package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Public Landing Screen
 */
@Composable
fun LandingScreen(
    onGetStarted: () -> Unit,
    onLoginClick: () -> Unit,
    onExploreOpportunities: () -> Unit,
    onDemoLogin: () -> Unit
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "landing_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_float"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // Landing Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp)) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(SheBizPrimaryPurple, CircleShape)
                            .align(Alignment.CenterStart)
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(SheBizCoral.copy(alpha = 0.85f), CircleShape)
                            .align(Alignment.CenterEnd)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SheBiz",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = SheBizHeading
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.testTag("landing_nav_login_button")
                ) {
                    Text(
                        text = "Log In",
                        color = SheBizHeading,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("landing_nav_register_button")
                ) {
                    Text("Register", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // "EMPOWERING WOMEN" tag
            Box(
                modifier = Modifier
                    .background(SheBizLavender, RoundedCornerShape(100.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "EMPOWERING WOMEN",
                    color = SheBizPrimaryPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connect. Learn.",
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SheBizHeading
            )
            Text(
                text = "Grow. Together.",
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SheBizPrimaryPurple
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "A digital space where women discover opportunities, build skills, find mentors, and run thriving small businesses across South Africa.",
                style = MaterialTheme.typography.bodyLarge,
                color = SheBizBody
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillowButton(
                    text = "Get Started",
                    onClick = onGetStarted,
                    modifier = Modifier.weight(1f),
                    testTag = "landing_get_started_button"
                )
                OutlinedButton(
                    onClick = onExploreOpportunities,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, SheBizPrimaryPurple),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("landing_explore_button")
                ) {
                    Text(
                        text = "Explore",
                        color = SheBizPrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Demo Banner
            Surface(
                onClick = onDemoLogin,
                shape = RoundedCornerShape(14.dp),
                color = SheBizCream,
                border = BorderStroke(1.dp, SheBizGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_demo_button")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFF9E6500)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Explore as Abongile (Abo's Bakery)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SheBizHeading
                        )
                        Text(
                            text = "One-tap instant live demo with full data & features",
                            fontSize = 11.sp,
                            color = SheBizBody
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SheBizHeading
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3D Hero Canvas
            SheBiz3DHeroCanvas()

            Spacer(modifier = Modifier.height(16.dp))

            // Floating Live Match Card
            Floating3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = floatOffset.dp),
                backgroundColor = Color.White,
                borderColor = SheBizLavender,
                onClick = onExploreOpportunities,
                testTag = "floating_match_card"
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(SheBizLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Work,
                            contentDescription = null,
                            tint = SheBizPrimaryPurple
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "8 opportunities match your skills this week",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SheBizHeading
                        )
                        Text(
                            text = "Internships, Grants & Learnerships in South Africa",
                            fontSize = 12.sp,
                            color = SheBizBody
                        )
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = SheBizPrimaryPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Everything You Need to Thrive",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SheBizHeading
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Feature Cards
            FeatureCardItem(
                title = "Learn & Upskill",
                desc = "Practical masterclasses in web basics, digital marketing, SARS tax & business planning.",
                icon = Icons.Outlined.School,
                accentColor = SheBizPrimaryPurple,
                onClick = onGetStarted
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureCardItem(
                title = "Find Opportunities",
                desc = "18+ handpicked internships, learnerships, and women incubation grants with live match %.",
                icon = Icons.Outlined.WorkOutline,
                accentColor = SheBizCoral,
                onClick = onExploreOpportunities
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureCardItem(
                title = "Find a Mentor",
                desc = "Connect with established female executives and strategists for 1-on-1 sessions & chat.",
                icon = Icons.Outlined.Groups,
                accentColor = SheBizGold,
                onClick = onGetStarted
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureCardItem(
                title = "Grow Your Business",
                desc = "Track daily Rand sales, stock levels, expenses, customer ledgers, and profit split charts.",
                icon = Icons.Outlined.Storefront,
                accentColor = SheBizGreen,
                onClick = onGetStarted
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureCardItem(
                title = "SheBiz AI Assistant",
                desc = "Context-aware guidance on pricing, marketing strategy, and pitch readiness 24/7.",
                icon = Icons.Outlined.AutoAwesome,
                accentColor = Color(0xFF8F47FF),
                onClick = onGetStarted
            )
        }
    }
}

@Composable
private fun FeatureCardItem(
    title: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Floating3DCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 3.dp,
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, fontSize = 12.sp, color = SheBizBody, lineHeight = 16.sp)
            }
        }
    }
}

/**
 * Register Screen
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("Abongile Lathitha") }
    var email by remember { mutableStateOf("abongilelathitha@gmail.com") }
    var phone by remember { mutableStateOf("+27 82 555 1234") }
    var location by remember { mutableStateOf("Gqeberha, Eastern Cape") }
    var password by remember { mutableStateOf("SheBiz2026!") }
    var confirmPassword by remember { mutableStateOf("SheBiz2026!") }
    var passwordVisible by remember { mutableStateOf(false) }

    val interestOptions = listOf(
        "Finding a job",
        "Finding a mentor",
        "Learning new skills",
        "Starting a business",
        "Growing my business",
        "Networking"
    )
    val selectedInterests = remember {
        mutableStateListOf("Growing my business", "Finding a mentor", "Learning new skills")
    }
    var termsAgreed by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Password strength calculation
    val passwordStrength = remember(password) {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        score
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Back / Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SheBizHeading)
            }
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = SheBizHeading
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Purple banner card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(SheBizPrimaryPurple, Color(0xFF4A1BB8))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Your next chapter starts here.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Join thousands of South African women advancing their careers and enterprises.",
                    color = SheBizLavender,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Outlined.Person, null, tint = SheBizPrimaryPurple) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reg_full_name")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = SheBizPrimaryPurple) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reg_email")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number (+27 format)") },
            leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = SheBizPrimaryPurple) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reg_phone")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location (City, Province)") },
            leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = SheBizPrimaryPurple) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reg_location")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 8 chars, uppercase & number)") },
            leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = SheBizPrimaryPurple) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reg_password")
        )

        // Password strength meter
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { passwordStrength / 5f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = when (passwordStrength) {
                        in 0..2 -> SheBizRed
                        in 3..4 -> SheBizGold
                        else -> SheBizGreen
                    },
                    trackColor = SheBizBorder
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = when (passwordStrength) {
                        in 0..2 -> "Weak"
                        in 3..4 -> "Good"
                        else -> "Strong"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (passwordStrength) {
                        in 0..2 -> SheBizRed
                        in 3..4 -> SheBizGold
                        else -> SheBizGreen
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = SheBizPrimaryPurple) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select your interests (at least 1 required):",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SheBizHeading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Interest checkboxes
        interestOptions.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { interest ->
                    val isChecked = selectedInterests.contains(interest)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isChecked) selectedInterests.remove(interest)
                                else selectedInterests.add(interest)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedInterests.add(interest)
                                else selectedInterests.remove(interest)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = SheBizPrimaryPurple)
                        )
                        Text(
                            text = interest,
                            fontSize = 12.sp,
                            color = SheBizHeading
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { termsAgreed = !termsAgreed },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAgreed,
                onCheckedChange = { termsAgreed = it },
                colors = CheckboxDefaults.colors(checkedColor = SheBizPrimaryPurple)
            )
            Text(
                text = "I agree to the Terms of Service and Privacy Policy",
                fontSize = 12.sp,
                color = SheBizHeading
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage!!,
                color = SheBizRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        PillowButton(
            text = if (isLoading) "Creating Account..." else "Create Account",
            onClick = {
                if (fullName.isBlank()) {
                    errorMessage = "Please enter your full name."
                    return@PillowButton
                }
                if (!email.contains("@")) {
                    errorMessage = "Please enter a valid email address."
                    return@PillowButton
                }
                if (!phone.startsWith("+27") && !phone.startsWith("0")) {
                    errorMessage = "Please enter a valid South African phone number (+27)."
                    return@PillowButton
                }
                if (password.length < 8) {
                    errorMessage = "Password must be at least 8 characters."
                    return@PillowButton
                }
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match."
                    return@PillowButton
                }
                if (selectedInterests.isEmpty()) {
                    errorMessage = "Please select at least 1 interest."
                    return@PillowButton
                }
                if (!termsAgreed) {
                    errorMessage = "You must agree to the Terms and Privacy Policy."
                    return@PillowButton
                }

                isLoading = true
                errorMessage = null
                onRegisterSuccess(email)
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            testTag = "reg_create_account_button"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", color = SheBizBody, fontSize = 13.sp)
            Text(
                text = "Log In",
                color = SheBizPrimaryPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

/**
 * 6-Digit OTP Verification Screen
 */
@Composable
fun VerifyEmailScreen(
    userEmail: String,
    onVerificationSuccess: () -> Unit,
    onResendCode: () -> Unit,
    onChangeEmail: () -> Unit,
    onLogout: () -> Unit
) {
    var otpValues by remember { mutableStateOf(listOf("", "", "", "", "", "")) }
    var countdown by remember { mutableIntStateOf(60) }
    var isVerifying by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isLinkModeFallback by remember { mutableStateOf(false) }

    // Countdown timer
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    val focusRequesters = remember { List(6) { FocusRequester() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(SheBizLavender, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.MarkEmailRead,
                contentDescription = null,
                tint = SheBizPrimaryPurple,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Verify Your Email",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = SheBizHeading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We sent a 6-digit verification code to:",
            style = MaterialTheme.typography.bodyMedium,
            color = SheBizBody,
            textAlign = TextAlign.Center
        )

        Text(
            text = userEmail,
            fontWeight = FontWeight.Bold,
            color = SheBizPrimaryPurple,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (!isLinkModeFallback) {
            // 6 OTP Input Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                otpValues.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newVal ->
                            if (newVal.length <= 1) {
                                val newOtp = otpValues.toMutableList()
                                newOtp[index] = newVal
                                otpValues = newOtp
                                if (newVal.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(48.dp, 56.dp)
                            .focusRequester(focusRequesters[index])
                            .testTag("otp_box_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Demo hint
            Text(
                text = "Tip: Enter any 6 digits (e.g. 123456) or tap Verify",
                fontSize = 11.sp,
                color = SheBizBody
            )
        } else {
            // Link mode fallback box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SheBizCream, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Link Verification Mode",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9E6500)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Click the link in the verification email sent to your inbox, then tap 'I Have Verified' below.",
                        fontSize = 12.sp,
                        color = SheBizHeading
                    )
                }
            }
        }

        if (errorText != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(errorText!!, color = SheBizRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        PillowButton(
            text = if (isVerifying) "Verifying..." else if (isLinkModeFallback) "I Have Verified" else "Verify Code",
            onClick = {
                isVerifying = true
                errorText = null
                onVerificationSuccess()
            },
            enabled = !isVerifying,
            modifier = Modifier.fillMaxWidth(),
            testTag = "verify_button"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resend with countdown
        TextButton(
            onClick = {
                if (countdown == 0) {
                    countdown = 60
                    onResendCode()
                }
            },
            enabled = countdown == 0
        ) {
            Text(
                text = if (countdown > 0) "Resend code in ${countdown}s" else "Resend code",
                color = if (countdown == 0) SheBizPrimaryPurple else SheBizBody,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = onChangeEmail) {
                Text("Change email", color = SheBizBody, fontSize = 12.sp)
            }
            TextButton(onClick = onLogout) {
                Text("Log out", color = SheBizRed, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = { isLinkModeFallback = !isLinkModeFallback }) {
            Text(
                text = if (isLinkModeFallback) "Switch to 6-digit OTP mode" else "Having trouble? Use email link fallback",
                fontSize = 12.sp,
                color = SheBizPrimaryPurple
            )
        }
    }
}

/**
 * Login Screen
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (email: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onQuickDemo: () -> Unit
) {
    var email by remember { mutableStateOf("abongilelathitha@gmail.com") }
    var password by remember { mutableStateOf("SheBiz2026!") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var forgotPasswordOpen by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp)) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(SheBizPrimaryPurple, CircleShape)
                        .align(Alignment.CenterStart)
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(SheBizCoral.copy(alpha = 0.85f), CircleShape)
                        .align(Alignment.CenterEnd)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "SheBiz",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = SheBizHeading
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = SheBizHeading
        )
        Text(
            text = "Enter your credentials to access your SheBiz dashboard.",
            style = MaterialTheme.typography.bodyMedium,
            color = SheBizBody
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Demo One-Tap
        Surface(
            onClick = onQuickDemo,
            shape = RoundedCornerShape(14.dp),
            color = SheBizLavender,
            border = BorderStroke(1.dp, SheBizPrimaryPurple.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_demo_one_tap")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlossySphereAvatar("AL", size = 36.dp, tone = "purple")
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quick Demo: Abongile Lathitha",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SheBizHeading
                    )
                    Text(
                        text = "Abo's Bakery (Gqeberha) • Instant Access",
                        fontSize = 11.sp,
                        color = SheBizPrimaryPurple
                    )
                }
                Icon(Icons.Default.ChevronRight, null, tint = SheBizPrimaryPurple)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Outlined.Email, null, tint = SheBizPrimaryPurple) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("login_email")
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = SheBizPrimaryPurple) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("login_password")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = SheBizPrimaryPurple)
                )
                Text("Remember me", fontSize = 12.sp, color = SheBizHeading)
            }

            Text(
                text = "Forgot password?",
                color = SheBizPrimaryPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { forgotPasswordOpen = true }
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(errorMessage!!, color = SheBizRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        PillowButton(
            text = if (isLoading) "Logging In..." else "Log In",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please enter both email and password."
                    return@PillowButton
                }
                isLoading = true
                onLoginSuccess(email)
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            testTag = "login_submit_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Continue with Google
        OutlinedButton(
            onClick = { onLoginSuccess("abongilelathitha@gmail.com") },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SheBizBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("login_google_button")
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                tint = SheBizHeading,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Continue with Google",
                color = SheBizHeading,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", color = SheBizBody, fontSize = 13.sp)
            Text(
                text = "Register",
                color = SheBizPrimaryPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }

    if (forgotPasswordOpen) {
        AlertDialog(
            onDismissRequest = { forgotPasswordOpen = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email address and we will send you a password reset link.")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { forgotPasswordOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { forgotPasswordOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
