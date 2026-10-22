package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.SheBizRepository
import com.example.model.Opportunity
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.SheBizTheme

enum class AppScreen {
    LANDING,
    LOGIN,
    REGISTER,
    VERIFY_EMAIL,
    MAIN_APP,
    LEARNING,
    AI_ASSISTANT
}

class MainActivity : ComponentActivity() {

    private val repository = SheBizRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SheBizTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.LANDING) }
                var selectedNavTab by remember { mutableIntStateOf(0) } // 0 = Home, 1 = Opps, 2 = Mentors, 3 = Business, 4 = Profile
                var selectedOppForDetail by remember { mutableStateOf<Opportunity?>(null) }
                var aiInitialPrompt by remember { mutableStateOf<String?>(null) }
                var verifyingEmail by remember { mutableStateOf("abongilelathitha@gmail.com") }

                var notificationsOpen by remember { mutableStateOf(false) }
                var helpSupportOpen by remember { mutableStateOf(false) }
                var accountSettingsOpen by remember { mutableStateOf(false) }

                val user by repository.currentUser.collectAsState()
                val notifications by repository.notifications.collectAsState()
                val unreadCount = notifications.count { !it.read }

                // System BackHandler
                BackHandler {
                    when {
                        notificationsOpen -> notificationsOpen = false
                        helpSupportOpen -> helpSupportOpen = false
                        accountSettingsOpen -> accountSettingsOpen = false
                        currentScreen == AppScreen.LEARNING -> currentScreen = AppScreen.MAIN_APP
                        currentScreen == AppScreen.AI_ASSISTANT -> currentScreen = AppScreen.MAIN_APP
                        currentScreen == AppScreen.MAIN_APP && selectedNavTab != 0 -> selectedNavTab = 0
                        currentScreen == AppScreen.LOGIN || currentScreen == AppScreen.REGISTER || currentScreen == AppScreen.VERIFY_EMAIL -> {
                            currentScreen = AppScreen.LANDING
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.LANDING -> {
                                LandingScreen(
                                    onGetStarted = { currentScreen = AppScreen.REGISTER },
                                    onLoginClick = { currentScreen = AppScreen.LOGIN },
                                    onExploreOpportunities = {
                                        selectedNavTab = 1
                                        currentScreen = AppScreen.MAIN_APP
                                    },
                                    onDemoLogin = {
                                        selectedNavTab = 0
                                        currentScreen = AppScreen.MAIN_APP
                                    }
                                )
                            }

                            AppScreen.REGISTER -> {
                                RegisterScreen(
                                    onRegisterSuccess = { email ->
                                        verifyingEmail = email
                                        currentScreen = AppScreen.VERIFY_EMAIL
                                    },
                                    onNavigateToLogin = { currentScreen = AppScreen.LOGIN }
                                )
                            }

                            AppScreen.VERIFY_EMAIL -> {
                                VerifyEmailScreen(
                                    userEmail = verifyingEmail,
                                    onVerificationSuccess = {
                                        currentScreen = AppScreen.MAIN_APP
                                    },
                                    onResendCode = {
                                        repository.addNotification("Verification Code Resent", "A fresh 6-digit code was sent to $verifyingEmail", "info")
                                    },
                                    onChangeEmail = { currentScreen = AppScreen.REGISTER },
                                    onLogout = { currentScreen = AppScreen.LANDING }
                                )
                            }

                            AppScreen.LOGIN -> {
                                LoginScreen(
                                    onLoginSuccess = { email ->
                                        currentScreen = AppScreen.MAIN_APP
                                    },
                                    onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                                    onQuickDemo = {
                                        selectedNavTab = 0
                                        currentScreen = AppScreen.MAIN_APP
                                    }
                                )
                            }

                            AppScreen.MAIN_APP -> {
                                Scaffold(
                                    topBar = {
                                        SheBizTopHeader(
                                            title = when (selectedNavTab) {
                                                0 -> "Dashboard"
                                                1 -> "Opportunities"
                                                2 -> "Mentorship"
                                                3 -> "My Business"
                                                else -> "Profile"
                                            },
                                            subtitle = when (selectedNavTab) {
                                                0 -> "Welcome to your career & enterprise portal"
                                                1 -> "Discover jobs, grants & learnerships"
                                                2 -> "1-on-1 female leadership guidance"
                                                3 -> "Manage Abo's Bakery sales & stock"
                                                else -> "Manage your skills & credentials"
                                            },
                                            unreadNotificationCount = unreadCount,
                                            userName = user.fullName,
                                            onNotificationsClick = { notificationsOpen = true },
                                            onProfileClick = { selectedNavTab = 4 },
                                            onSettingsClick = { accountSettingsOpen = true },
                                            onHelpClick = { helpSupportOpen = true },
                                            onLogoutClick = { currentScreen = AppScreen.LANDING }
                                        )
                                    },
                                    bottomBar = {
                                        SheBizBottomNavBar(
                                            selectedTab = selectedNavTab,
                                            onTabSelected = { selectedNavTab = it },
                                            onAiClick = {
                                                aiInitialPrompt = null
                                                currentScreen = AppScreen.AI_ASSISTANT
                                            }
                                        )
                                    }
                                ) { innerPadding ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        when (selectedNavTab) {
                                            0 -> DashboardScreen(
                                                repository = repository,
                                                onNavigateToTab = { selectedNavTab = it },
                                                onOpenAiWithPrompt = { prompt ->
                                                    aiInitialPrompt = prompt
                                                    currentScreen = AppScreen.AI_ASSISTANT
                                                },
                                                onSelectOpportunity = { opp ->
                                                    selectedOppForDetail = opp
                                                    selectedNavTab = 1
                                                },
                                                onOpenLearning = { currentScreen = AppScreen.LEARNING }
                                            )

                                            1 -> OpportunitiesScreen(
                                                repository = repository,
                                                selectedOppFromNav = selectedOppForDetail,
                                                onClearSelectedOpp = { selectedOppForDetail = null }
                                            )

                                            2 -> MentorshipScreen(repository = repository)

                                            3 -> BusinessScreen(repository = repository)

                                            4 -> ProfileScreen(
                                                repository = repository,
                                                onOpenSettings = { accountSettingsOpen = true }
                                            )
                                        }
                                    }
                                }
                            }

                            AppScreen.LEARNING -> {
                                LearningScreen(
                                    repository = repository,
                                    onBack = { currentScreen = AppScreen.MAIN_APP }
                                )
                            }

                            AppScreen.AI_ASSISTANT -> {
                                AiAssistantScreen(
                                    repository = repository,
                                    initialPrompt = aiInitialPrompt,
                                    onBack = {
                                        aiInitialPrompt = null
                                        currentScreen = AppScreen.MAIN_APP
                                    }
                                )
                            }
                        }
                    }
                }

                // Global Dialogs & Drawers
                if (notificationsOpen) {
                    NotificationsDialog(
                        repository = repository,
                        onDismiss = { notificationsOpen = false }
                    )
                }

                if (helpSupportOpen) {
                    HelpSupportDialog(
                        onDismiss = { helpSupportOpen = false }
                    )
                }

                if (accountSettingsOpen) {
                    AccountSettingsDialog(
                        onDismiss = { accountSettingsOpen = false },
                        onLogout = {
                            accountSettingsOpen = false
                            currentScreen = AppScreen.LANDING
                        }
                    )
                }
            }
        }
    }
}
