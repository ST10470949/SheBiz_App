package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.SheBizRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MentorshipScreen(
    repository: SheBizRepository
) {
    val mentors by repository.mentors.collectAsState()
    val activeMentorship by repository.activeMentorship.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    var viewingMentor by remember { mutableStateOf<Mentor?>(null) }
    var requestingMentor by remember { mutableStateOf<Mentor?>(null) }
    var requestGoal by remember { mutableStateOf("") }
    var requestTime by remember { mutableStateOf("Tuesdays 17:00") }

    var bookingSessionForMentor by remember { mutableStateOf<MentorshipConnection?>(null) }
    var bookedDateText by remember { mutableStateOf("Thursday at 16:30") }

    var isChatOpen by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    var isMentorTyping by remember { mutableStateOf(false) }

    var isCallModalOpen by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val categories = listOf("All", "Technology", "Business", "Marketing", "Finance", "Leadership", "Career Development")

    val filteredMentors = remember(mentors, selectedCategory) {
        if (selectedCategory == "All") mentors
        else mentors.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .padding(bottom = 72.dp)
    ) {
        // Active Mentorship Card (if connected)
        activeMentorship?.let { connection ->
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Floating3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SheBizLavender.copy(alpha = 0.6f),
                    borderColor = SheBizPrimaryPurple.copy(alpha = 0.3f),
                    elevation = 3.dp,
                    testTag = "active_mentorship_card"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlossySphereAvatar(
                                initials = connection.mentorName.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                size = 44.dp,
                                tone = "purple"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Connected with ${connection.mentorName}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = SheBizHeading
                                )
                                Text(
                                    text = connection.mentorTitle,
                                    fontSize = 12.sp,
                                    color = SheBizBody
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(SheBizGreen.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(connection.status, color = SheBizGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Event, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Next Check-in: ${connection.nextCheckIn}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SheBizHeading
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isChatOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("open_mentor_chat_button")
                        ) {
                            Icon(Icons.Outlined.Chat, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Messages", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { bookingSessionForMentor = connection },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, SheBizPrimaryPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reschedule", fontSize = 12.sp, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { isCallModalOpen = true },
                            modifier = Modifier
                                .background(SheBizGreen, RoundedCornerShape(10.dp))
                                .size(40.dp)
                                .testTag("join_mentor_call_button")
                        ) {
                            Icon(Icons.Default.Videocam, null, tint = Color.White)
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SheBizPrimaryPurple,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp)
                )
            }
        }

        // Mentor List
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMentors, key = { it.id }) { mentor ->
                Floating3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 3.dp,
                    onClick = { viewingMentor = mentor },
                    testTag = "mentor_card_${mentor.id}"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlossySphereAvatar(
                            initials = mentor.initials,
                            size = 54.dp,
                            tone = when (mentor.category) {
                                "Technology" -> "purple"
                                "Marketing" -> "blush"
                                "Business" -> "cream"
                                else -> "lavender"
                            }
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mentor.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = SheBizHeading
                                )
                                Box(
                                    modifier = Modifier
                                        .background(SheBizGreen.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Available",
                                        color = SheBizGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "${mentor.title} • ${mentor.yearsExperience} yrs exp",
                                fontSize = 12.sp,
                                color = SheBizBody
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = SheBizGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${mentor.rating} (${mentor.menteeCount} mentees)",
                                    fontSize = 11.sp,
                                    color = SheBizHeading,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mentor.bio,
                        fontSize = 12.sp,
                        color = SheBizBody,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Skills tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        mentor.skills.take(3).forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .background(SheBizLavender, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(skill, fontSize = 10.sp, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "View Profile →",
                            color = SheBizPrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Full Mentor Profile Dialog
    viewingMentor?.let { mentor ->
        Dialog(
            onDismissRequest = { viewingMentor = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SheBizBackground)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                color = SheBizBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    IconButton(onClick = { viewingMentor = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SheBizHeading)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlossySphereAvatar(
                            initials = mentor.initials,
                            size = 72.dp,
                            tone = "purple"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(mentor.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = SheBizHeading)
                            Text(mentor.title, fontSize = 14.sp, color = SheBizBody)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = SheBizGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${mentor.rating} Rating • ${mentor.menteeCount} Mentees", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SheBizHeading)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bio
                    Text("About", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(mentor.bio, style = MaterialTheme.typography.bodyMedium, color = SheBizBody, lineHeight = 22.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Expertise
                    Text("Expertise & Skills", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        mentor.skills.forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .background(SheBizLavender, RoundedCornerShape(100.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(skill, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Languages
                    Text("Languages Spoken", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(mentor.languages.joinToString(", "), fontSize = 13.sp, color = SheBizBody)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Availability Slots
                    Text("Typical Availability Slots", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(8.dp))
                    mentor.availability.forEach { slot ->
                        Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(slot, fontSize = 13.sp, color = SheBizHeading)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Action Buttons
                    PillowButton(
                        text = "Request Mentorship",
                        onClick = {
                            val target = viewingMentor
                            viewingMentor = null
                            requestingMentor = target
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "request_mentorship_open_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            viewingMentor = null
                            isChatOpen = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, SheBizPrimaryPurple),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Outlined.Chat, null, tint = SheBizPrimaryPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Message", color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Request Mentorship Modal (with 10-second auto-approval demo simulation)
    requestingMentor?.let { mentor ->
        AlertDialog(
            onDismissRequest = { requestingMentor = null },
            title = { Text("Request Mentorship with ${mentor.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    Text("Describe what you hope to achieve with ${mentor.name}:", fontSize = 12.sp, color = SheBizBody)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = requestGoal,
                        onValueChange = { requestGoal = it },
                        placeholder = { Text("e.g. Guidance on optimizing bakery ingredient costs and scaling custom cake orders...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preferred Check-in Slot:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = requestTime,
                        onValueChange = { requestTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Note: Auto-approves in 10s for live demonstration.", fontSize = 11.sp, color = SheBizPrimaryPurple)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val goal = requestGoal.ifBlank { "Scale Abo's Bakery production and optimize margins" }
                        repository.requestMentorship(mentor, goal, requestTime)
                        requestingMentor = null
                        requestGoal = ""

                        // Auto approve after 10 seconds
                        coroutineScope.launch {
                            delay(10000)
                            repository.autoApproveMentorship()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("submit_mentorship_request_button")
                ) {
                    Text("Send Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { requestingMentor = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reschedule / Book Session Modal
    bookingSessionForMentor?.let { connection ->
        AlertDialog(
            onDismissRequest = { bookingSessionForMentor = null },
            title = { Text("Book Session with ${connection.mentorName}") },
            text = {
                Column {
                    Text("Select preferred day and time for your next 1-on-1 check-in:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = bookedDateText,
                        onValueChange = { bookedDateText = it },
                        label = { Text("Date & Time") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.bookSession(bookedDateText)
                        bookingSessionForMentor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                ) {
                    Text("Confirm Check-in")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingSessionForMentor = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Real-Time Chat Dialog
    if (isChatOpen) {
        val listState = rememberLazyListState()

        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }

        Dialog(
            onDismissRequest = { isChatOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SheBizBackground)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                color = SheBizBackground
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SheBizSurface)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { isChatOpen = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close", tint = SheBizHeading)
                        }
                        GlossySphereAvatar("NK", size = 36.dp, tone = "purple")
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                activeMentorship?.mentorName ?: "Naledi K.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SheBizHeading
                            )
                            Text(
                                if (isMentorTyping) "typing..." else "Online • Active Mentorship",
                                fontSize = 11.sp,
                                color = if (isMentorTyping) SheBizPrimaryPurple else SheBizGreen
                            )
                        }
                        IconButton(onClick = { isCallModalOpen = true }) {
                            Icon(Icons.Default.Videocam, null, tint = SheBizPrimaryPurple)
                        }
                    }

                    HorizontalDivider(color = SheBizBorder)

                    // Messages List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { msg ->
                            val isMe = !msg.isMentor
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isMe) 16.dp else 4.dp,
                                                bottomEnd = if (isMe) 4.dp else 16.dp
                                            )
                                        )
                                        .background(if (isMe) SheBizPrimaryPurple else SheBizSurface)
                                        .border(
                                            if (isMe) 0.dp else 1.dp,
                                            if (isMe) Color.Transparent else SheBizBorder,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = if (isMe) Color.White else SheBizHeading,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Input Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SheBizSurface)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInputText,
                            onValueChange = { chatInputText = it },
                            placeholder = { Text("Message mentor...") },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mentor_chat_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatInputText.isNotBlank()) {
                                    val text = chatInputText
                                    chatInputText = ""
                                    repository.sendChatMessage(text)

                                    // Simulated live mentor reply
                                    coroutineScope.launch {
                                        delay(1500)
                                        isMentorTyping = true
                                        delay(2500)
                                        isMentorTyping = false
                                        repository.receiveMentorReply("Thanks for the update Abongile! I am reviewing your packaging and pricing points. Let's dig into that during our session tomorrow!")
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(SheBizPrimaryPurple, CircleShape)
                                .testTag("mentor_chat_send_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    // Video Call Simulator Modal
    if (isCallModalOpen) {
        AlertDialog(
            onDismissRequest = { isCallModalOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, null, tint = SheBizPrimaryPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Starting Call...")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GlossySphereAvatar("NK", size = 64.dp, tone = "purple")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Connecting with Naledi K...", fontWeight = FontWeight.Bold, color = SheBizHeading)
                    Text("Encrypted 1-on-1 SheBiz Mentorship Video Room", fontSize = 11.sp, color = SheBizBody)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = SheBizGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { isCallModalOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizRed)
                ) {
                    Text("End Call")
                }
            }
        )
    }
}
