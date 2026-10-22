package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SheBizRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun DashboardScreen(
    repository: SheBizRepository,
    onNavigateToTab: (Int) -> Unit,
    onOpenAiWithPrompt: (String) -> Unit,
    onSelectOpportunity: (Opportunity) -> Unit,
    onOpenLearning: () -> Unit
) {
    val user by repository.currentUser.collectAsState()
    val opportunities by repository.opportunities.collectAsState()
    val activeMentorship by repository.activeMentorship.collectAsState()
    val products by repository.products.collectAsState()
    val monthlySales = repository.getMonthlySales()
    val overallSkillsProgress = repository.getOverallSkillsProgress()

    // Greeting based on time of day
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val firstName = user.fullName.split(" ").firstOrNull() ?: "Abongile"

    // High matching opportunities (match > 60%)
    val matchingOpportunities = remember(opportunities, user) {
        opportunities.map { opp -> opp to repository.calculateMatchPercentage(opp) }
            .sortedByDescending { it.second }
    }
    val highMatchCount = matchingOpportunities.count { it.second >= 60 }
    val topThreeOpportunities = matchingOpportunities.take(3)

    var quickAiPrompt by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 80.dp)
    ) {
        // Welcome Header Banner
        Text(
            text = "$greeting, $firstName ✨",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = SheBizHeading
        )
        Text(
            text = "Here is what's happening with your business and career today.",
            style = MaterialTheme.typography.bodyMedium,
            color = SheBizBody
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 4 Live Stat Cards (2x2 Grid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Skills Progress
            StatGridCard(
                title = "Skills Progress",
                value = "$overallSkillsProgress%",
                subtext = "4 modules active",
                icon = Icons.Outlined.School,
                accentColor = SheBizPrimaryPurple,
                modifier = Modifier.weight(1f),
                onClick = onOpenLearning
            )
            // 2. Matching Jobs
            StatGridCard(
                title = "Matching Jobs",
                value = "$highMatchCount",
                subtext = "Above 60% match",
                icon = Icons.Outlined.WorkOutline,
                accentColor = SheBizCoral,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(1) } // Opportunities Tab
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 3. Business Sales
            StatGridCard(
                title = "Business Sales",
                value = SheBizRepository.formatRand(monthlySales),
                subtext = "Abo's Bakery (Oct)",
                icon = Icons.Outlined.MonetizationOn,
                accentColor = SheBizGreen,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(3) } // Business Tab
            )
            // 4. Mentorship Status
            StatGridCard(
                title = "Mentorship",
                value = if (activeMentorship != null) "Active" else "None",
                subtext = activeMentorship?.mentorName ?: "Find mentor",
                icon = Icons.Outlined.Groups,
                accentColor = SheBizGold,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(2) } // Mentorship Tab
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "Ask SheBiz AI" Card with input
        Floating3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SheBizLavender.copy(alpha = 0.5f),
            borderColor = SheBizPrimaryPurple.copy(alpha = 0.25f),
            elevation = 4.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SheBizPrimaryPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Ask SheBiz AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SheBizHeading
                    )
                    Text(
                        text = "Instant guidance for Abo's Bakery, grant pitches & skills",
                        fontSize = 11.sp,
                        color = SheBizBody
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickAiPrompt,
                    onValueChange = { quickAiPrompt = it },
                    placeholder = { Text("e.g. How can I scale my cake sales in Gqeberha?", fontSize = 12.sp) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_quick_ai_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val prompt = quickAiPrompt.ifBlank { "How can I scale cake sales for Abo's Bakery in Gqeberha?" }
                        quickAiPrompt = ""
                        onOpenAiWithPrompt(prompt)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(SheBizPrimaryPurple, RoundedCornerShape(12.dp))
                        .testTag("dashboard_quick_ai_send")
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recommended Opportunities (Top 3 by match %)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Recommended for You",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SheBizHeading
                )
                Text(
                    text = "Ranked dynamically by your skills & interests",
                    style = MaterialTheme.typography.bodySmall,
                    color = SheBizBody
                )
            }
            TextButton(onClick = { onNavigateToTab(1) }) {
                Text(
                    text = "View all (${opportunities.size})",
                    color = SheBizPrimaryPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        topThreeOpportunities.forEach { (opp, matchPct) ->
            Floating3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 2.dp,
                onClick = { onSelectOpportunity(opp) },
                testTag = "rec_opp_${opp.id}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(SheBizLavender, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    opp.type,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SheBizPrimaryPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${opp.daysLeft} days left",
                                fontSize = 11.sp,
                                color = SheBizBody
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = opp.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SheBizHeading
                        )
                        Text(
                            text = "${opp.company} • ${opp.location}",
                            fontSize = 12.sp,
                            color = SheBizBody
                        )
                    }

                    // Dynamic Match % Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (matchPct >= 80) SheBizGreen.copy(alpha = 0.12f)
                                else SheBizPrimaryPurple.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$matchPct%",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (matchPct >= 80) SheBizGreen else SheBizPrimaryPurple
                            )
                            Text(
                                text = "MATCH",
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (matchPct >= 80) SheBizGreen else SheBizPrimaryPurple
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Your Progress Section
        Text(
            text = "Your Progress",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = SheBizHeading
        )
        Spacer(modifier = Modifier.height(12.dp))

        Floating3DCard(modifier = Modifier.fillMaxWidth()) {
            ProgressRowItem(
                label = "Complete Your Profile",
                percentage = user.profileComplete,
                color = SheBizPrimaryPurple,
                onClick = { onNavigateToTab(4) } // Profile
            )
            HorizontalDivider(color = SheBizBorder, modifier = Modifier.padding(vertical = 12.dp))
            ProgressRowItem(
                label = "Skills Pathway",
                percentage = overallSkillsProgress,
                color = SheBizCoral,
                onClick = onOpenLearning
            )
            HorizontalDivider(color = SheBizBorder, modifier = Modifier.padding(vertical = 12.dp))
            ProgressRowItem(
                label = "Business Setup (Abo's Bakery)",
                percentage = 85,
                color = SheBizGreen,
                onClick = { onNavigateToTab(3) } // Business
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // This Week Section (3 Tiles)
        Text(
            text = "This Week",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = SheBizHeading
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ThisWeekTile(
                count = "${opportunities.size}",
                title = "New Opportunities",
                subtitle = "In your area",
                icon = Icons.Outlined.Work,
                accent = SheBizPrimaryPurple,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(1) }
            )
            ThisWeekTile(
                count = "6",
                title = "Top Mentors",
                subtitle = "Available now",
                icon = Icons.Outlined.PersonPin,
                accent = SheBizCoral,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(2) }
            )
            ThisWeekTile(
                count = "4",
                title = "Skill Courses",
                subtitle = "Free for you",
                icon = Icons.Outlined.MenuBook,
                accent = SheBizGold,
                modifier = Modifier.weight(1f),
                onClick = onOpenLearning
            )
        }
    }
}

@Composable
private fun StatGridCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Floating3DCard(
        modifier = modifier,
        elevation = 3.dp,
        onClick = onClick,
        testTag = "stat_card_${title.lowercase().replace(" ", "_")}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Icon(Icons.Default.ArrowOutward, null, tint = SheBizBody.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = SheBizHeading)
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SheBizHeading)
        Text(subtext, fontSize = 10.sp, color = SheBizBody)
    }
}

@Composable
private fun ProgressRowItem(
    label: String,
    percentage: Int,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
            Text("$percentage%", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = SheBizBorder
        )
    }
}

@Composable
private fun ThisWeekTile(
    count: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = SheBizSurface,
        border = BorderStroke(1.dp, SheBizBorder),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(accent.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = SheBizHeading)
            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SheBizHeading, maxLines = 1)
            Text(subtitle, fontSize = 9.sp, color = SheBizBody, maxLines = 1)
        }
    }
}
