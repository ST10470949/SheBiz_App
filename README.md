# SheBiz - Full-Stack Mobile-First Platform for Women

**SheBiz** is a production-quality platform where women discover opportunities, find mentors, learn skills, and run thriving small businesses in South Africa.

Built with native Jetpack Compose on Android with a premium 3D design system, South African Rand (ZAR) financial tracking, interactive 3D hero canvas, dynamic opportunity match scoring, and atomic inventory management.

---

## 1. Firebase Backend Setup

### Prerequisites
- Node.js 18+
- Firebase CLI (`npm install -g firebase-tools`)

### Step-by-Step Instructions
1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/) and create a project named `shebiz-app`.

2. **Enable Authentication**:
   - In **Authentication > Sign-in method**:
     - Enable **Email/Password**.
     - Enable **Google** sign-in provider.

3. **Create Cloud Firestore**:
   - In **Firestore Database**: Create database in production mode.
   - Select cloud region (e.g. `europe-west1` or `southafrica-north`).

4. **Create Cloud Storage**:
   - In **Storage**: Get started in production mode for storing user CVs (`.pdf`) and product images.

5. **Install Trigger Email Extension**:
   - In Firebase Extensions, install **Trigger Email from Firestore** (`firebase/firestore-send-email`).
   - Set mail collection to `mail`.
   - Provide SMTP credentials (e.g. SendGrid API key or Gmail App Password).

6. **Set Gemini AI Secret**:
   - Set your Gemini API key in Cloud Secret Manager:
     ```bash
     firebase functions:secrets:set GEMINI_API_KEY
     ```

7. **Deploy Rules & Cloud Functions**:
   ```bash
   firebase login
   firebase use <your-project-id>
   firebase deploy --only firestore:rules,storage:rules
   cd functions && npm install && cd ..
   firebase deploy --only functions
   ```

8. **Seed Demo Content**:
   ```bash
   node seed.js
   ```

---

## 2. Design System Tokens & Aesthetics

- **Colours**:
  - Primary Purple: `#6938FF`
  - Coral Pink: `#F0527D`
  - Gold Accent: `#F7BB43`
  - Growth Green: `#1F9D6B`
  - Warning Red: `#D93F4B`
  - Dark Surface: `#15131A`
  - Light Background: `#FAF8FC`
  - Lavender Container: `#EFE9FF`
  - Blush Container: `#FCE8EF`
  - Cream Container: `#FFF4D6`
  - Border: `#ECE8F5`
- **Currency**: South African Rand, formatted as `R12,450` or `R350`.
- **3D Interactive Features**:
  - **3D Hero Canvas**: Renders 3 glowing interactive spheres (Purple, Coral, Gold) with realistic diffuse studio lighting, specular gloss highlights, orbital drift animation, touch drag response, and white animated network lines.
  - **3D Pillow Buttons**: Highlight bevel top edge, soft drop shadow, and pressed sink effect.
  - **Glossy Spherical Avatars**: Spherical gloss rendering in lavender/blush/cream tones.
  - **Floating Cards**: Layered ambient and spot shadows.

---

## 3. End-to-End Test Checklist

- [x] **Sign Up (`/register`)**: Full name, valid South African phone (`+27`), password strength meter, 6 interest checkboxes, terms confirmation.
- [x] **Receive & Enter Code (`/verify-email`)**: 6 separate OTP input boxes with auto-advance, numeric keypad, paste support, and fallback link mode.
- [x] **Wrong Code**: Error state indicating remaining attempts and locking after 5 invalid attempts.
- [x] **Resend Code**: 60-second countdown cooldown timer.
- [x] **Login (`/login`)**: Email/password authentication, show/hide eye toggle, remember me, forgot password modal, and Continue with Google.
- [x] **One-Tap Demo Mode**: Instant access as Abongile Lathitha (founder of Abo's Bakery, Gqeberha).
- [x] **Dynamic Match %**: Automatically calculates match % (58% - 98%) comparing opportunity skills & category against user profile.
- [x] **Apply for Opportunity**: Detailed modal with attached CV check and motivation statement; updates status to "Applied" and triggers real-time notification.
- [x] **Request Mentor**: Select mentor, specify growth goals, and trigger 10-second auto-approval demo with push notification.
- [x] **Real-Time Mentor Chat**: Interactive message exchange with typing indicator and simulated response.
- [x] **Record Sale**: Select product (Chocolate Cake, Cupcakes, etc.), quantity stepper, live total calculation, atomic stock deduction, and live customer history recording.
- [x] **Operating Expenses**: Log ingredients and packaging costs with date picker and "Export CSV" trigger.
- [x] **Business Analytics**: Monthly performance grouped bar chart, profit split donut (Profit %, Ingredients %, Packaging %), best-selling products leaderboard, and Growth Insights card.
- [x] **Learning Academy**: 4 courses with 5+ complete lessons, lesson guide viewer, key takeaways, and "Mark as Complete" updating the dashboard skills pathway.
- [x] **AI Assistant**: Context-aware guidance incorporating Abo's Bakery, user skills, and South African market realities, with copy and rating feedback.
- [x] **Profile Edit**: Update personal details, interactive 0-100 skill proficiency sliders, Nelson Mandela University education records, and achievements.
- [x] **Log Out**: Modal confirmation redirecting safely to the landing screen.
