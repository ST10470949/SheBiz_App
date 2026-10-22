package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
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

// ====================== LEARNING SCREEN ======================
@Composable
fun LearningScreen(
    repository: SheBizRepository,
    onBack: () -> Unit
) {
    val courses by repository.courses.collectAsState()
    val courseProgress by repository.courseProgress.collectAsState()
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var activeLessonIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .statusBarsPadding()
            .padding(bottom = 72.dp)
    ) {
        // Learning Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SheBizHeading)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text("SheBiz Academy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = SheBizHeading)
                Text("Bite-sized courses designed for women in business", fontSize = 11.sp, color = SheBizBody)
            }
        }

        // Skills Pathway Card
        val overall = repository.getOverallSkillsProgress()
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
            Floating3DCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SheBizLavender.copy(alpha = 0.5f),
                borderColor = SheBizPrimaryPurple.copy(alpha = 0.3f),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Your Skills Pathway", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                        Text("Accredited micro-credentials", fontSize = 11.sp, color = SheBizBody)
                    }
                    Text("$overall% Complete", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = SheBizPrimaryPurple)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { overall / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = SheBizPrimaryPurple,
                    trackColor = SheBizBorder
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Course List
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(courses, key = { it.id }) { course ->
                val completionPercent = repository.getCourseCompletionPercent(course.id)
                val completedCount = courseProgress[course.id]?.size ?: 0

                Floating3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 3.dp,
                    onClick = {
                        selectedCourse = course
                        activeLessonIndex = 0
                    },
                    testTag = "course_card_${course.id}"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SheBizLavender, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(course.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
                        }

                        Text(
                            "$completedCount / ${course.lessons.size} lessons",
                            fontSize = 11.sp,
                            color = SheBizBody,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(course.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(course.description, fontSize = 12.sp, color = SheBizBody, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { completionPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = if (completionPercent == 100) SheBizGreen else SheBizPrimaryPurple,
                        trackColor = SheBizBorder
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "$completionPercent% completed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (completionPercent == 100) SheBizGreen else SheBizPrimaryPurple
                        )

                        Text(
                            if (completionPercent == 0) "Start Course →" else if (completionPercent == 100) "Review →" else "Continue →",
                            color = SheBizPrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // Lesson Viewer Dialog
    selectedCourse?.let { course ->
        val currentLesson = course.lessons.getOrNull(activeLessonIndex) ?: course.lessons.first()
        val isLessonCompleted = courseProgress[course.id]?.contains(currentLesson.id) == true

        Dialog(
            onDismissRequest = { selectedCourse = null },
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
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedCourse = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close", tint = SheBizHeading)
                        }
                        Text(
                            "Lesson ${activeLessonIndex + 1} of ${course.lessons.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SheBizBody
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(course.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(currentLesson.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = SheBizHeading)
                    Text("Duration: ${currentLesson.duration}", fontSize = 12.sp, color = SheBizBody)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lesson Content Body
                    Floating3DCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Lesson Guide", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            currentLesson.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SheBizHeading,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Key Actionable Takeaways:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
                        Spacer(modifier = Modifier.height(6.dp))
                        currentLesson.keyTakeaways.forEach { takeaway ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = SheBizGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(takeaway, fontSize = 12.sp, color = SheBizHeading)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mark as complete button
                    Button(
                        onClick = {
                            repository.markLessonCompleted(course.id, currentLesson.id)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLessonCompleted) SheBizGreen else SheBizPrimaryPurple
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(
                            if (isLessonCompleted) Icons.Default.Check else Icons.Default.CheckCircleOutline,
                            null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isLessonCompleted) "Completed! (Tap to update)" else "Mark as Complete",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation Prev / Next
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { if (activeLessonIndex > 0) activeLessonIndex-- },
                            enabled = activeLessonIndex > 0,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("← Previous")
                        }

                        Button(
                            onClick = {
                                if (activeLessonIndex < course.lessons.size - 1) {
                                    activeLessonIndex++
                                } else {
                                    selectedCourse = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (activeLessonIndex < course.lessons.size - 1) "Next →" else "Finish Course")
                        }
                    }
                }
            }
        }
    }
}

// ====================== AI ASSISTANT SCREEN ======================
@Composable
fun AiAssistantScreen(
    repository: SheBizRepository,
    initialPrompt: String? = null,
    onBack: () -> Unit
) {
    val aiMessages by repository.aiMessages.collectAsState()
    var promptInput by remember { mutableStateOf(initialPrompt ?: "") }
    var isThinking by remember { mutableStateOf(false) }
    var clearDialogVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    // Auto-scroll when messages update
    LaunchedEffect(aiMessages.size) {
        if (aiMessages.isNotEmpty()) {
            listState.animateScrollToItem(aiMessages.size - 1)
        }
    }

    // Auto-send initial prompt if provided
    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            promptInput = ""
            isThinking = true
            delay(1200)
            repository.sendAiMessage(initialPrompt)
            isThinking = false
        }
    }

    val topicCards = listOf(
        Pair("Career", "How can I stand out in grant applications and interviews?"),
        Pair("Business", "What is the optimal pricing and margin for Abo's Bakery?"),
        Pair("Skills", "Which tech and financial skills match top job openings?"),
        Pair("Mentorship", "What questions should I ask in my next mentor check-in?")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // AI Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SheBizSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SheBizHeading)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Brush.linearGradient(listOf(SheBizCoral, SheBizPrimaryPurple)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("SheBiz AI Guide", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = SheBizHeading)
                    Text("Tailored to Abo's Bakery & your career", fontSize = 11.sp, color = SheBizBody)
                }
            }

            IconButton(onClick = { clearDialogVisible = true }) {
                Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear Chat", tint = SheBizBody)
            }
        }

        HorizontalDivider(color = SheBizBorder)

        // Chat messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Topic suggestion cards at the top
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        "What would you like guidance on today?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SheBizHeading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        topicCards.forEach { (topic, question) ->
                            Surface(
                                onClick = {
                                    promptInput = question
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = SheBizLavender,
                                border = BorderStroke(1.dp, SheBizPrimaryPurple.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lightbulb, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(topic, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
                                }
                            }
                        }
                    }
                }
            }

            items(aiMessages, key = { it.id }) { msg ->
                if (msg.isUser) {
                    // User message bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 290.dp)
                                .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                                .background(SheBizPrimaryPurple)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(msg.message, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                } else {
                    // Assistant message bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(modifier = Modifier.widthIn(max = 310.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                    .background(SheBizSurface)
                                    .border(1.dp, SheBizBorder, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    msg.message,
                                    color = SheBizHeading,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Action footer under each AI response
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TextButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(msg.message)) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, null, modifier = Modifier.size(14.dp), tint = SheBizBody)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", fontSize = 11.sp, color = SheBizBody)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { repository.rateAiMessage(msg.id, true) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.ThumbUp,
                                            null,
                                            tint = if (msg.helpful == true) SheBizGreen else SheBizBody,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { repository.rateAiMessage(msg.id, false) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.ThumbDown,
                                            null,
                                            tint = if (msg.helpful == false) SheBizRed else SheBizBody,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SheBizSurface,
                            border = BorderStroke(1.dp, SheBizBorder),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = SheBizPrimaryPurple
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SheBiz AI is thinking...", fontSize = 11.sp, color = SheBizBody)
                            }
                        }
                    }
                }
            }
        }

        // Guardrail Disclaimer Footer
        Text(
            text = "AI guidance supports your choices. Important decisions remain yours.",
            fontSize = 10.sp,
            color = SheBizBody,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Bottom Chat Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SheBizSurface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("Ask about grants, Abo's Bakery, skills...", fontSize = 12.sp) },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("ai_assistant_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            val prompt = promptInput
                            promptInput = ""
                            coroutineScope.launch {
                                isThinking = true
                                delay(1200)
                                repository.sendAiMessage(prompt)
                                isThinking = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(SheBizCoral, CircleShape)
                        .testTag("ai_assistant_send_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (clearDialogVisible) {
        AlertDialog(
            onDismissRequest = { clearDialogVisible = false },
            title = { Text("Clear Chat History?") },
            text = { Text("This will reset your conversation with SheBiz AI assistant.") },
            confirmButton = {
                Button(
                    onClick = {
                        repository.clearAiChat()
                        clearDialogVisible = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizRed)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { clearDialogVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
