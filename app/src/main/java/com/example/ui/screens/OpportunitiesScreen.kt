package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.SheBizRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OpportunitiesScreen(
    repository: SheBizRepository,
    selectedOppFromNav: Opportunity? = null,
    onClearSelectedOpp: () -> Unit = {}
) {
    val opportunities by repository.opportunities.collectAsState()
    val savedIds by repository.savedOpportunityIds.collectAsState()
    val applications by repository.applications.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Explore, 1 = My Applications, 2 = Saved
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }
    var sortByMatch by remember { mutableStateOf(true) } // true = Match %, false = Closing date

    var viewingOpportunity by remember { mutableStateOf<Opportunity?>(selectedOppFromNav) }
    var applyingOpportunity by remember { mutableStateOf<Opportunity?>(null) }
    var applicationMotivation by remember { mutableStateOf("") }
    var selectedCvName by remember { mutableStateOf("Abongile_Lathitha_CV.pdf") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Technology", "Marketing", "Business", "Finance", "Leadership")
    val types = listOf("All", "Job", "Internship", "Learnership", "Scholarship", "Programme")
    val locations = listOf("All", "Gqeberha", "Johannesburg", "Cape Town", "South Africa")

    // Filter & Sort
    val filteredOpportunities = remember(opportunities, searchQuery, selectedCategory, selectedType, selectedLocation, sortByMatch) {
        opportunities.filter { opp ->
            val matchQuery = searchQuery.isBlank() ||
                opp.title.contains(searchQuery, ignoreCase = true) ||
                opp.company.contains(searchQuery, ignoreCase = true) ||
                opp.skills.any { it.contains(searchQuery, ignoreCase = true) }

            val matchCategory = selectedCategory == "All" || opp.category.equals(selectedCategory, ignoreCase = true)
            val matchType = selectedType == "All" || opp.type.equals(selectedType, ignoreCase = true)
            val matchLocation = selectedLocation == "All" || opp.location.contains(selectedLocation, ignoreCase = true)

            matchQuery && matchCategory && matchType && matchLocation
        }.sortedWith { a, b ->
            if (sortByMatch) {
                val matchA = repository.calculateMatchPercentage(a)
                val matchB = repository.calculateMatchPercentage(b)
                matchB.compareTo(matchA)
            } else {
                a.daysLeft.compareTo(b.daysLeft)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
    ) {
        // Tab Switcher (Explore / My Applications / Saved)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .background(SheBizLavender, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            listOf("Explore (18)", "Applications (${applications.size})", "Saved (${savedIds.size})").forEachIndexed { index, title ->
                val isSelected = activeTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SheBizPrimaryPurple else Color.Transparent)
                        .clickable { activeTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else SheBizHeading
                    )
                }
            }
        }

        if (activeTab == 0) {
            // EXPLORE TAB
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)
            ) {
                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by role, company, or skill (e.g. HTML, Marketing)...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = SheBizPrimaryPurple) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("opp_search_input")
                )

                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = if (isSelected) "All" else cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = if (isSelected && cat != "All") {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SheBizPrimaryPurple,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                    }
                }

                // Sub-Filters & Sort Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredOpportunities.size} opportunities found",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SheBizBody
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { sortByMatch = !sortByMatch }) {
                            Icon(
                                if (sortByMatch) Icons.Default.Sort else Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = SheBizPrimaryPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (sortByMatch) "Sort: Match %" else "Sort: Closing Date",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SheBizPrimaryPurple
                            )
                        }
                    }
                }

                // List of Opportunities
                if (filteredOpportunities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.SearchOff, null, tint = SheBizBody, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No opportunities found", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                            Text("Try adjusting your search keywords or filter tags.", color = SheBizBody, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedCategory = "All"
                                    selectedType = "All"
                                    selectedLocation = "All"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple)
                            ) {
                                Text("Reset Filters")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredOpportunities, key = { it.id }) { opp ->
                            val matchPct = repository.calculateMatchPercentage(opp)
                            val isSaved = savedIds.contains(opp.id)
                            val hasApplied = applications.any { it.opportunityId == opp.id }

                            Floating3DCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 3.dp,
                                onClick = { viewingOpportunity = opp },
                                testTag = "opp_card_${opp.id}"
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(SheBizLavender, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                opp.type,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SheBizPrimaryPurple
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${opp.daysLeft} days left",
                                            fontSize = 11.sp,
                                            color = if (opp.daysLeft <= 18) SheBizRed else SheBizBody,
                                            fontWeight = if (opp.daysLeft <= 18) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }

                                    // Bookmark toggle
                                    IconButton(
                                        onClick = { repository.toggleSaveOpportunity(opp.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Save",
                                            tint = if (isSaved) SheBizPrimaryPurple else SheBizBody
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = opp.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SheBizHeading
                                )
                                Text(
                                    text = "${opp.company} • ${opp.location}",
                                    fontSize = 13.sp,
                                    color = SheBizBody
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Skills tags
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    opp.skills.take(3).forEach { skill ->
                                        Box(
                                            modifier = Modifier
                                                .background(SheBizSurfaceVariant, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(skill, fontSize = 10.sp, color = SheBizBody, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Match % badge
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    if (matchPct >= 80) SheBizGreen else SheBizPrimaryPurple,
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "$matchPct% Match for your profile",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (matchPct >= 80) SheBizGreen else SheBizPrimaryPurple
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (hasApplied) {
                                            Box(
                                                modifier = Modifier
                                                    .background(SheBizGreen.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text("Applied", color = SheBizGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text(
                                                text = "View details →",
                                                color = SheBizPrimaryPurple,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeTab == 1) {
            // MY APPLICATIONS TAB
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Tracked Applications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SheBizHeading
                )
                Text(
                    text = "Status updates from recruiters and funding committees.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SheBizBody
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (applications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No applications submitted yet.", color = SheBizBody)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(applications) { app ->
                            Floating3DCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(app.opportunityTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
                                        Text(app.company, fontSize = 12.sp, color = SheBizBody)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when (app.status) {
                                                    "Submitted" -> SheBizLavender
                                                    "In review" -> SheBizCream
                                                    "Shortlisted" -> SheBizGreen.copy(alpha = 0.15f)
                                                    else -> SheBizBlush
                                                },
                                                RoundedCornerShape(100.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            app.status,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = when (app.status) {
                                                "Submitted" -> SheBizPrimaryPurple
                                                "In review" -> Color(0xFF9E6500)
                                                "Shortlisted" -> SheBizGreen
                                                else -> SheBizRed
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Attached CV: ${app.cvName}",
                                    fontSize = 11.sp,
                                    color = SheBizBody
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\"${app.motivation}\"",
                                    fontSize = 12.sp,
                                    color = SheBizHeading,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { repository.withdrawApplication(app.id) }) {
                                        Text("Withdraw", color = SheBizRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // SAVED OPPORTUNITIES TAB
            val savedList = opportunities.filter { savedIds.contains(it.id) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Saved Bookmarks (${savedList.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SheBizHeading
                )
                Spacer(modifier = Modifier.height(14.dp))

                if (savedList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No saved opportunities yet. Tap bookmark to save.", color = SheBizBody)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(savedList) { opp ->
                            Floating3DCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { viewingOpportunity = opp }
                            ) {
                                Text(opp.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
                                Text("${opp.company} • ${opp.location}", fontSize = 12.sp, color = SheBizBody)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${opp.daysLeft} days left", fontSize = 11.sp, color = SheBizCoral, fontWeight = FontWeight.Bold)
                                    Text("View →", color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Opportunity Detail Dialog
    viewingOpportunity?.let { opp ->
        val matchPct = repository.calculateMatchPercentage(opp)
        val hasApplied = applications.any { it.opportunityId == opp.id }
        val isSaved = savedIds.contains(opp.id)

        Dialog(
            onDismissRequest = {
                viewingOpportunity = null
                onClearSelectedOpp()
            },
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
                    // Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewingOpportunity = null
                            onClearSelectedOpp()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SheBizHeading)
                        }

                        IconButton(onClick = { repository.toggleSaveOpportunity(opp.id) }) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) SheBizPrimaryPurple else SheBizBody
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(SheBizLavender, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(opp.type, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(SheBizGreen.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("$matchPct% Match", color = SheBizGreen, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(opp.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = SheBizHeading)
                    Text("${opp.company} • ${opp.location}", fontSize = 14.sp, color = SheBizBody)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Info Strip
                    Floating3DCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Closing Date", fontSize = 10.sp, color = SheBizBody)
                                Text(opp.closingDate, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizHeading)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Days Left", fontSize = 10.sp, color = SheBizBody)
                                Text("${opp.daysLeft} days", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizCoral)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Funding / Stipend", fontSize = 10.sp, color = SheBizBody)
                                Text(opp.stipendOrSalary, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SheBizGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(opp.description, style = MaterialTheme.typography.bodyMedium, color = SheBizBody, lineHeight = 22.sp)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("Requirements", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(6.dp))
                    opp.requirements.forEach { req ->
                        Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.CheckCircle, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(16.dp).offset(y = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(req, fontSize = 13.sp, color = SheBizHeading)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("Relevant Skills", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        opp.skills.forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .background(SheBizLavender, RoundedCornerShape(100.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(skill, color = SheBizPrimaryPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (hasApplied) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = SheBizGreen.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Check, null, tint = SheBizGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Application Already Submitted", color = SheBizGreen, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        PillowButton(
                            text = "Apply Now",
                            onClick = {
                                viewingOpportunity = null
                                applyingOpportunity = opp
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "opp_apply_now_button"
                        )
                    }
                }
            }
        }
    }

    // Application Submission Modal
    applyingOpportunity?.let { opp ->
        var isSubmitting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { applyingOpportunity = null },
            title = {
                Text(
                    text = "Apply for ${opp.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Company: ${opp.company} • ${opp.location}",
                        fontSize = 12.sp,
                        color = SheBizBody
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // CV Attachment Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SheBizLavender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AttachFile, null, tint = SheBizPrimaryPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(selectedCvName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SheBizHeading)
                                Text("PDF • 240 KB • Verified", fontSize = 10.sp, color = SheBizBody)
                            }
                            Text("Attached", color = SheBizGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Motivation Statement", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = applicationMotivation,
                        onValueChange = { applicationMotivation = it },
                        placeholder = { Text("Explain why you are an ideal fit for this opportunity...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("app_motivation_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mot = applicationMotivation.ifBlank {
                            "I am thrilled to apply for this role. With my background and commitment to continuous growth through SheBiz, I look forward to contributing meaningfully."
                        }
                        isSubmitting = true
                        repository.applyForOpportunity(opp.id, mot, selectedCvName)
                        applyingOpportunity = null
                        applicationMotivation = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("app_submit_confirm_button")
                ) {
                    Text("Submit Application", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { applyingOpportunity = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
