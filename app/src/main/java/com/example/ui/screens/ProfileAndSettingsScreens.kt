package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SheBizRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

// ====================== PROFILE SCREEN ======================
@Composable
fun ProfileScreen(
    repository: SheBizRepository,
    onOpenSettings: () -> Unit
) {
    val user by repository.currentUser.collectAsState()

    var editFullName by remember(user) { mutableStateOf(user.fullName) }
    var editLocation by remember(user) { mutableStateOf(user.location) }
    var editPhone by remember(user) { mutableStateOf(user.phone) }
    var editEmail by remember(user) { mutableStateOf(user.email) }

    var isPhotoModalOpen by remember { mutableStateOf(false) }
    var isAddSkillOpen by remember { mutableStateOf(false) }
    var newSkillName by remember { mutableStateOf("") }
    var newSkillLevel by remember { mutableFloatStateOf(75f) }

    var isAddEduOpen by remember { mutableStateOf(false) }
    var newEduInst by remember { mutableStateOf("") }
    var newEduQual by remember { mutableStateOf("") }
    var newEduYear by remember { mutableStateOf("2024") }

    var isAddAchieveOpen by remember { mutableStateOf(false) }
    var newAchieveTitle by remember { mutableStateOf("") }
    var newAchieveDesc by remember { mutableStateOf("") }

    var toastNotice by remember { mutableStateOf<String?>(null) }
    val isDirty = editFullName != user.fullName || editLocation != user.location || editPhone != user.phone

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .verticalScroll(scrollState)
            .padding(20.dp)
            .padding(bottom = 72.dp)
    ) {
        // Profile Header Card
        Floating3DCard(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    GlossySphereAvatar("AL", size = 68.dp, tone = "purple")
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(SheBizCoral, CircleShape)
                            .clickable { isPhotoModalOpen = true }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(user.fullName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = SheBizHeading)
                    Text(user.location, fontSize = 12.sp, color = SheBizBody)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(SheBizGreen.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${user.profileComplete}% Complete",
                                color = SheBizGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = SheBizBody)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { isPhotoModalOpen = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Change Photo", fontSize = 11.sp, color = SheBizPrimaryPurple)
                }
                Button(
                    onClick = {
                        repository.updateUserProfile(
                            user.copy(
                                fullName = editFullName,
                                location = editLocation,
                                phone = editPhone
                            )
                        )
                        toastNotice = "Profile updated successfully!"
                    },
                    enabled = isDirty,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    modifier = Modifier.weight(1f).testTag("save_profile_button")
                ) {
                    Text("Save Changes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (toastNotice != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = SheBizGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, null, tint = SheBizGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(toastNotice!!, color = SheBizGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Personal Information
        Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
        Spacer(modifier = Modifier.height(10.dp))

        Floating3DCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = editFullName,
                onValueChange = { editFullName = it },
                label = { Text("Full Name") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("edit_full_name")
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = editLocation,
                onValueChange = { editLocation = it },
                label = { Text("Location") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = editPhone,
                onValueChange = { editPhone = it },
                label = { Text("Phone (+27)") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = editEmail,
                onValueChange = {},
                readOnly = true,
                label = { Text("Email (Verified)") },
                trailingIcon = { Icon(Icons.Default.Verified, null, tint = SheBizGreen) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Skills Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Skills & Proficiencies", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
            TextButton(onClick = { isAddSkillOpen = true }) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = SheBizPrimaryPurple)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Skill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
            }
        }

        Floating3DCard(modifier = Modifier.fillMaxWidth()) {
            user.skills.forEachIndexed { index, skill ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(skill.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${skill.level}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
                            IconButton(onClick = { repository.deleteSkill(index) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, null, tint = SheBizBody, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { skill.level / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = SheBizPrimaryPurple,
                        trackColor = SheBizBorder
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Education Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Education", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
            TextButton(onClick = { isAddEduOpen = true }) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = SheBizPrimaryPurple)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
            }
        }

        user.education.forEach { edu ->
            Floating3DCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 1.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SheBizLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.School, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(edu.institution, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                        Text("${edu.qualification} • Class of ${edu.year}", fontSize = 12.sp, color = SheBizBody)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Achievements Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Achievements & Awards", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
            TextButton(onClick = { isAddAchieveOpen = true }) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = SheBizPrimaryPurple)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
            }
        }

        user.achievements.forEach { achieve ->
            Floating3DCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 1.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SheBizCream, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.EmojiEvents, null, tint = Color(0xFF9E6500), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(achieve.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                        Text(achieve.description, fontSize = 12.sp, color = SheBizBody)
                    }
                }
            }
        }
    }

    // Add Skill Modal
    if (isAddSkillOpen) {
        AlertDialog(
            onDismissRequest = { isAddSkillOpen = false },
            title = { Text("Add New Skill") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newSkillName,
                        onValueChange = { newSkillName = it },
                        label = { Text("Skill Name (e.g. Baking, SEO, Budgeting)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Proficiency Level: ${newSkillLevel.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = newSkillLevel,
                        onValueChange = { newSkillLevel = it },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(thumbColor = SheBizPrimaryPurple, activeTrackColor = SheBizPrimaryPurple)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSkillName.isNotBlank()) {
                            repository.addSkill(newSkillName, newSkillLevel.toInt())
                            isAddSkillOpen = false
                            newSkillName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                ) {
                    Text("Add Skill")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddSkillOpen = false }) { Text("Cancel") }
            }
        )
    }

    // Add Education Modal
    if (isAddEduOpen) {
        AlertDialog(
            onDismissRequest = { isAddEduOpen = false },
            title = { Text("Add Education") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newEduInst,
                        onValueChange = { newEduInst = it },
                        label = { Text("Institution (e.g. Nelson Mandela University)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newEduQual,
                        onValueChange = { newEduQual = it },
                        label = { Text("Qualification (e.g. Diploma in Culinary Arts)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newEduYear,
                        onValueChange = { newEduYear = it },
                        label = { Text("Year of Completion") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEduInst.isNotBlank()) {
                            repository.addEducation(newEduInst, newEduQual, newEduYear)
                            isAddEduOpen = false
                            newEduInst = ""
                            newEduQual = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddEduOpen = false }) { Text("Cancel") }
            }
        )
    }

    // Add Achievement Modal
    if (isAddAchieveOpen) {
        AlertDialog(
            onDismissRequest = { isAddAchieveOpen = false },
            title = { Text("Add Achievement") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newAchieveTitle,
                        onValueChange = { newAchieveTitle = it },
                        label = { Text("Award or Milestone Title") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAchieveDesc,
                        onValueChange = { newAchieveDesc = it },
                        label = { Text("Description") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAchieveTitle.isNotBlank()) {
                            repository.addAchievement(newAchieveTitle, newAchieveDesc)
                            isAddAchieveOpen = false
                            newAchieveTitle = ""
                            newAchieveDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddAchieveOpen = false }) { Text("Cancel") }
            }
        )
    }

    // Change Photo Modal
    if (isPhotoModalOpen) {
        AlertDialog(
            onDismissRequest = { isPhotoModalOpen = false },
            title = { Text("Profile Photo") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    GlossySphereAvatar("AL", size = 80.dp, tone = "purple")
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Using 3D Glossy Sphere Avatar (Purple Tone)", fontSize = 12.sp, color = SheBizBody)
                }
            },
            confirmButton = {
                Button(
                    onClick = { isPhotoModalOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

// ====================== NOTIFICATIONS MODAL ======================
@Composable
fun NotificationsDialog(
    repository: SheBizRepository,
    onDismiss: () -> Unit
) {
    val notifications by repository.notifications.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            shape = RoundedCornerShape(24.dp),
            color = SheBizSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
                    Row {
                        TextButton(onClick = { repository.markAllNotificationsRead() }) {
                            Text("Mark all read", fontSize = 11.sp, color = SheBizPrimaryPurple)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notifications) { notif ->
                        Surface(
                            onClick = { repository.markNotificationRead(notif.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (notif.read) SheBizSurfaceVariant else SheBizLavender.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.dp,
                                if (notif.read) SheBizBorder else SheBizPrimaryPurple.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            when (notif.type) {
                                                "application" -> SheBizPrimaryPurple
                                                "business" -> SheBizCoral
                                                "mentor" -> SheBizGreen
                                                else -> SheBizGold
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (notif.type) {
                                            "application" -> Icons.Default.Work
                                            "business" -> Icons.Default.Storefront
                                            "mentor" -> Icons.Default.Groups
                                            else -> Icons.Default.Notifications
                                        },
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(notif.body, fontSize = 11.sp, color = SheBizBody, lineHeight = 15.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(notif.timeAgo, fontSize = 10.sp, color = SheBizBody.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================== HELP & SUPPORT MODAL ======================
@Composable
fun HelpSupportDialog(
    onDismiss: () -> Unit
) {
    var expandedFaq by remember { mutableIntStateOf(-1) }
    var supportMessage by remember { mutableStateOf("") }
    var messageSentNotice by remember { mutableStateOf(false) }

    val faqs = listOf(
        Pair("How do I record a sale for Abo's Bakery?", "Navigate to the Business tab and tap 'Record Sale'. Select the product, adjust quantity, and tap Record Sale. Stock is automatically decremented and financial totals update instantly."),
        Pair("How are opportunity match percentages calculated?", "Match percentages compare the required skills and categories of the job or grant against your saved profile skills, education, and career interests dynamically."),
        Pair("Can I connect with multiple mentors?", "Yes! You can request mentorship with any available mentor in the directory. You can book 1-on-1 video sessions and exchange real-time messages."),
        Pair("What currency does SheBiz support?", "SheBiz is built for South Africa and natively formats all business turnover, product pricing, and grant awards in South African Rand (ZAR, formatted as R12,450).")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
            shape = RoundedCornerShape(24.dp),
            color = SheBizSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Help & Support", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                Spacer(modifier = Modifier.height(8.dp))

                faqs.forEachIndexed { index, (q, a) ->
                    val isExpanded = expandedFaq == index
                    Surface(
                        onClick = { expandedFaq = if (isExpanded) -1 else index },
                        shape = RoundedCornerShape(12.dp),
                        color = SheBizSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(q, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SheBizHeading, modifier = Modifier.weight(1f))
                                Icon(
                                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    null,
                                    tint = SheBizPrimaryPurple
                                )
                            }
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(a, fontSize = 11.sp, color = SheBizBody, lineHeight = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Contact Support Team", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = supportMessage,
                    onValueChange = { supportMessage = it },
                    placeholder = { Text("How can we assist you today?", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (messageSentNotice) {
                    Text("Support ticket submitted! We will respond within 24 hours.", color = SheBizGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (supportMessage.isNotBlank()) {
                            messageSentNotice = true
                            supportMessage = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Support Message")
                }
            }
        }
    }
}

// ====================== ACCOUNT SETTINGS MODAL ======================
@Composable
fun AccountSettingsDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English (South Africa)") }
    var deleteConfirmOpen by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = SheBizSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Account Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notifications Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Push & In-App Notifications", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
                        Text("Alerts for applications, sales and mentors", fontSize = 11.sp, color = SheBizBody)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SheBizPrimaryPurple)
                    )
                }

                HorizontalDivider(color = SheBizBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Language
                Text("Regional Language", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = SheBizBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Delete Account
                TextButton(
                    onClick = { deleteConfirmOpen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Account & Data", color = SheBizRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (deleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { deleteConfirmOpen = false },
            title = { Text("Delete SheBiz Account?") },
            text = { Text("This will permanently remove your profile, Abo's Bakery inventory records, sales ledgers, and chat history. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteConfirmOpen = false
                        onDismiss()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
