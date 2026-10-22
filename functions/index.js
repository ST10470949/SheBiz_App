const functions = require("firebase-functions");
const admin = require("firebase-admin");
const crypto = require("crypto");

admin.initializeApp();
const db = admin.firestore();

/**
 * 1. sendVerificationCode
 * Generates a 6-digit OTP, securely stores a SHA-256 hash in Firestore with 10-minute expiry,
 * and queues a branded email document to the "mail" collection for the Firebase Email Trigger extension.
 */
exports.sendVerificationCode = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const uid = context.auth.uid;
  const email = context.auth.token.email || data.email;
  if (!email) {
    throw new functions.https.HttpsError("invalid-argument", "Valid user email is required.");
  }

  const verificationRef = db.collection("emailVerifications").doc(uid);
  const existingDoc = await verificationRef.get();

  const now = Date.now();
  if (existingDoc.exists) {
    const prev = existingDoc.data();
    // Rate limit: max 1 per 60 seconds
    if (prev.lastSentAt && now - prev.lastSentAt < 60000) {
      const waitSec = Math.ceil((60000 - (now - prev.lastSentAt)) / 1000);
      throw new functions.https.HttpsError("resource-exhausted", `Please wait ${waitSec} seconds before requesting a new code.`);
    }
  }

  // Generate 6-digit cryptographic random code
  const code = Math.floor(100000 + crypto.randomInt(900000)).toString();
  const hash = crypto.createHash("sha256").update(code).digest("hex");

  const expiresAt = now + 10 * 60 * 1000; // 10 minutes

  await verificationRef.set({
    hash,
    expiresAt,
    attempts: 0,
    lastSentAt: now,
    email
  });

  // Write to "mail" collection for Firebase "Trigger Email from Firestore" extension
  await db.collection("mail").add({
    to: [email],
    message: {
      subject: "Your SheBiz Verification Code",
      text: `Your SheBiz verification code is: ${code}. This code expires in 10 minutes. If you did not request this, ignore this email.`,
      html: `
        <div style="font-family: Arial, sans-serif; background-color: #FAF8FC; padding: 24px; color: #15131A;">
          <div style="background-color: #6938FF; padding: 20px; border-radius: 12px; text-align: center;">
            <h1 style="color: #FFFFFF; margin: 0; font-size: 26px;">SheBiz</h1>
            <p style="color: #EFE9FF; margin: 4px 0 0 0; font-size: 14px;">Empowering Women in Career & Business</p>
          </div>
          <div style="background-color: #FFFFFF; padding: 24px; margin-top: 16px; border-radius: 16px; border: 1px solid #ECE8F5;">
            <h2 style="color: #15131A; font-size: 18px; margin-top: 0;">Verify Your Email Address</h2>
            <p style="color: #6B6777; font-size: 14px; line-height: 1.5;">Welcome to SheBiz! Please enter the 6-digit verification code below to activate your account:</p>
            <div style="text-align: center; margin: 24px 0;">
              <span style="display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #6938FF; background-color: #EFE9FF; padding: 12px 28px; border-radius: 12px;">${code}</span>
            </div>
            <p style="color: #6B6777; font-size: 12px; text-align: center;">This code expires in 10 minutes. If you did not request this code, you can safely ignore this email.</p>
          </div>
        </div>
      `
    }
  });

  return { success: true, message: "Verification code sent successfully." };
});

/**
 * 2. verifyCode
 * Validates the 6-digit OTP against the stored SHA-256 hash.
 * Enforces attempt limits (locks for 15 mins after 5 wrong attempts) and expiration.
 */
exports.verifyCode = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const uid = context.auth.uid;
  const inputCode = (data.code || "").trim();

  if (!inputCode || inputCode.length !== 6) {
    throw new functions.https.HttpsError("invalid-argument", "A 6-digit code is required.");
  }

  const verificationRef = db.collection("emailVerifications").doc(uid);
  const doc = await verificationRef.get();

  if (!doc.exists) {
    throw new functions.https.HttpsError("not-found", "No verification request found. Please request a new code.");
  }

  const record = doc.data();
  const now = Date.now();

  // Check lockout
  if (record.lockedUntil && now < record.lockedUntil) {
    const waitMin = Math.ceil((record.lockedUntil - now) / 60000);
    throw new functions.https.HttpsError("permission-denied", `Too many failed attempts. Account locked for ${waitMin} minutes.`);
  }

  // Check expiration
  if (now > record.expiresAt) {
    throw new functions.https.HttpsError("deadline-exceeded", "The verification code has expired. Please request a new one.");
  }

  // Verify hash
  const inputHash = crypto.createHash("sha256").update(inputCode).digest("hex");
  if (inputHash !== record.hash) {
    const attempts = (record.attempts || 0) + 1;
    const updateData = { attempts };
    if (attempts >= 5) {
      updateData.lockedUntil = now + 15 * 60 * 1000; // Lock for 15 minutes
    }
    await verificationRef.update(updateData);

    const remaining = Math.max(0, 5 - attempts);
    throw new functions.https.HttpsError("invalid-argument", `Invalid code. ${remaining} attempts remaining.`);
  }

  // Success: mark user as verified in Firebase Auth and Firestore
  await admin.auth().updateUser(uid, { emailVerified: true });
  await db.collection("users").doc(uid).set({ emailVerified: true }, { merge: true });
  await verificationRef.delete();

  return { success: true, message: "Email successfully verified." };
});

/**
 * 3. geminiAiProxy
 * Server-side Gemini API proxy that attaches user profile and business context.
 * The GEMINI_API_KEY is retrieved securely from Cloud Secret Manager or environment variables.
 */
exports.geminiAiProxy = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Authentication required for AI guidance.");
  }

  const uid = context.auth.uid;
  const prompt = (data.prompt || "").trim();
  if (!prompt) {
    throw new functions.https.HttpsError("invalid-argument", "Prompt is required.");
  }

  // Load user profile & business data for hyper-personalized context
  const userDoc = await db.collection("users").doc(uid).get();
  const userData = userDoc.exists ? userDoc.data() : {};

  const apiKey = process.env.GEMINI_API_KEY || functions.config().gemini?.key;
  if (!apiKey) {
    // Fall back to structured contextual guidance if server API key is not yet set
    return {
      reply: `[SheBiz AI Guide]: For your venture "${userData.businessName || "Abo's Bakery"}", focus on high-margin artisanal products, daily cash flow tracking in South African Rand, and leveraging mentorship sessions with Naledi K. to scale production.`,
      isFallback: true
    };
  }

  try {
    const { GoogleGenAI } = require("@google/genai");
    const ai = new GoogleGenAI({ apiKey });

    const systemInstruction = `You are SheBiz AI, an encouraging and expert career & small business advisor dedicated to empowering South African women. 
Context about the current user:
- Name: ${userData.fullName || "Abongile"}
- Business: ${userData.businessName || "Abo's Bakery"} in Gqeberha, Eastern Cape
- Currency: South African Rand (ZAR, format: R12,450)
- Skills: ${(userData.skills || []).map(s => s.name).join(", ") || "Baking, Planning, Marketing"}
Provide concise, practical, high-impact advice on business growth, pricing, funding, or mentorship. Never offer legal or medical guarantees.`;

    const response = await ai.models.generateContent({
      model: "gemini-2.5-flash",
      contents: prompt,
      config: {
        systemInstruction
      }
    });

    return {
      reply: response.text,
      isFallback: false
    };
  } catch (error) {
    console.error("Gemini API error:", error);
    return {
      reply: "I am having trouble reaching the AI service right now. Please try again shortly or consult our learning modules on business pricing and funding.",
      isFallback: true
    };
  }
});
