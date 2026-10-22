package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StockStatus
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D Hero Canvas rendering three glowing interactive 3D spheres (Purple, Pink, Gold)
 * with studio lighting, realistic glossy highlights, orbital floating drift,
 * interactive touch drag response, and animated white network lines with pulse nodes.
 */
@Composable
fun SheBiz3DHeroCanvas(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_3d")

    // Animations for the 3 spheres
    val timePurple by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time_purple"
    )

    val timePink by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time_pink"
    )

    val timeGold by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time_gold"
    )

    val linePulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "line_pulse"
    )

    // Touch interaction offset
    var touchOffsetX by remember { mutableFloatStateOf(0f) }
    var touchOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF20163B),
                        Color(0xFF15131A)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        touchOffsetX = 0f
                        touchOffsetY = 0f
                    },
                    onDragCancel = {
                        touchOffsetX = 0f
                        touchOffsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    touchOffsetX = (touchOffsetX + dragAmount.x * 0.4f).coerceIn(-40f, 40f)
                    touchOffsetY = (touchOffsetY + dragAmount.y * 0.4f).coerceIn(-30f, 30f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("hero_3d_canvas")) {
            val width = size.width
            val height = size.height

            // Background soft 3D decorative rounded shapes
            drawRoundRect(
                color = SheBizLavender.copy(alpha = 0.08f),
                topLeft = Offset(width * 0.1f, height * 0.15f),
                size = androidx.compose.ui.geometry.Size(width * 0.45f, height * 0.65f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
            )
            drawRoundRect(
                color = SheBizBlush.copy(alpha = 0.08f),
                topLeft = Offset(width * 0.48f, height * 0.1f),
                size = androidx.compose.ui.geometry.Size(width * 0.42f, height * 0.60f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
            )

            // Sphere 1: Purple Sphere (Center-Left)
            val pX = width * 0.32f + cos(timePurple) * 14f + touchOffsetX * 0.8f
            val pY = height * 0.52f + sin(timePurple * 1.2f) * 12f + touchOffsetY * 0.8f
            val pRadius = width * 0.18f

            // Sphere 2: Pink / Coral Sphere (Center-Right)
            val kX = width * 0.68f + cos(timePink * 0.9f) * 16f + touchOffsetX * 1.1f
            val kY = height * 0.44f + sin(timePink) * 15f + touchOffsetY * 1.1f
            val kRadius = width * 0.15f

            // Sphere 3: Golden-Yellow Sphere (Top-Center)
            val gX = width * 0.50f + sin(timeGold) * 18f + touchOffsetX * 0.9f
            val gY = height * 0.28f + cos(timeGold * 0.8f) * 10f + touchOffsetY * 0.9f
            val gRadius = width * 0.12f

            // Connecting Network Lines
            val path = Path().apply {
                moveTo(pX, pY)
                lineTo(gX, gY)
                lineTo(kX, kY)
                lineTo(pX, pY)
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.35f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), linePulse * 20f)
                )
            )

            // Animated light packet along lines
            val packet1 = Offset(pX + (gX - pX) * linePulse, pY + (gY - pY) * linePulse)
            val packet2 = Offset(gX + (kX - gX) * linePulse, gY + (kY - gY) * linePulse)
            val packet3 = Offset(kX + (pX - kX) * linePulse, kY + (pY - kY) * linePulse)

            drawCircle(Color.White, radius = 4f, center = packet1)
            drawCircle(Color.White, radius = 4f, center = packet2)
            drawCircle(Color.White, radius = 4f, center = packet3)

            // Connecting nodes
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 5f, center = Offset(pX, pY))
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 5f, center = Offset(kX, kY))
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 5f, center = Offset(gX, gY))

            // Draw Spheres with 3D Studio Lighting & Specular Highlights
            draw3DSphere(
                center = Offset(pX, pY),
                radius = pRadius,
                baseColor = SheBizPrimaryPurple,
                highlightColor = Color(0xFFC7B2FF),
                shadowColor = Color(0xFF261066)
            )

            draw3DSphere(
                center = Offset(kX, kY),
                radius = kRadius,
                baseColor = SheBizCoral,
                highlightColor = Color(0xFFFFB3C6),
                shadowColor = Color(0xFF6B152B)
            )

            draw3DSphere(
                center = Offset(gX, gY),
                radius = gRadius,
                baseColor = SheBizGold,
                highlightColor = Color(0xFFFFF3B8),
                shadowColor = Color(0xFF7A5408)
            )
        }

        // Overlay text / badge inside hero
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(Color(0x77000000), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(SheBizGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Interactive 3D Network • Touch to rotate",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Custom 3D Sphere Renderer with studio diffuse lighting and specular sheen
 */
private fun DrawScope.draw3DSphere(
    center: Offset,
    radius: Float,
    baseColor: Color,
    highlightColor: Color,
    shadowColor: Color
) {
    // 1. Soft contact & diffuse drop shadow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent),
            center = Offset(center.x + radius * 0.1f, center.y + radius * 0.25f),
            radius = radius * 1.35f
        ),
        radius = radius * 1.35f,
        center = Offset(center.x + radius * 0.1f, center.y + radius * 0.25f)
    )

    // 2. Base Sphere with 3D spherical radial lighting gradient
    val lightSource = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                highlightColor,
                baseColor,
                shadowColor
            ),
            center = lightSource,
            radius = radius * 1.4f
        ),
        radius = radius,
        center = center
    )

    // 3. Crisp specular glossy highlight (studio light reflection)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.90f),
                Color.White.copy(alpha = 0.40f),
                Color.Transparent
            ),
            center = Offset(center.x - radius * 0.32f, center.y - radius * 0.32f),
            radius = radius * 0.45f
        ),
        radius = radius * 0.45f,
        center = Offset(center.x - radius * 0.32f, center.y - radius * 0.32f)
    )

    // 4. Subtle ambient rim light opposite the main light
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                highlightColor.copy(alpha = 0.25f)
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

/**
 * Signature 3D Pillow Button
 * Subtle gradient, highlight edge, elevated shadow, pressed sink animation
 */
@Composable
fun PillowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    gradient: List<Color> = listOf(SheBizPrimaryPurple, Color(0xFF5522E8)),
    textColor: Color = Color.White,
    enabled: Boolean = true,
    testTag: String = "pillow_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val translationY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        label = "pillow_press"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        label = "pillow_shadow"
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .testTag(testTag)
            .offset(y = translationY)
            .shadow(
                elevation = if (enabled) shadowElevation else 1.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = gradient.first().copy(alpha = 0.3f),
                spotColor = gradient.first().copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (enabled) {
                        Brush.verticalGradient(gradient)
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFFCCC8D6), Color(0xFFB5B0C2)))
                    }
                )
                // Highlight bevel top edge
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 20.dp, vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * 3D Floating Card with layered shadows and border
 */
@Composable
fun Floating3DCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SheBizSurface,
    borderColor: Color = SheBizBorder,
    elevation: Dp = 6.dp,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    testTag: String = "floating_3d_card",
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .testTag(testTag)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x1A6938FF),
                spotColor = Color(0x1F15131A)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .clickable { onClick() }
    } else {
        modifier
            .testTag(testTag)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x1A6938FF),
                spotColor = Color(0x1F15131A)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
    }

    Column(
        modifier = cardModifier.padding(18.dp),
        content = content
    )
}

/**
 * Glossy 3D Avatar Sphere in lavender/blush/cream tones with initials
 */
@Composable
fun GlossySphereAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    tone: String = "lavender" // lavender, blush, cream, purple
) {
    val (baseColor, highlightColor, textColor) = when (tone) {
        "blush" -> Triple(SheBizBlush, Color.White, SheBizCoral)
        "cream" -> Triple(SheBizCream, Color.White, Color(0xFF9E6500))
        "purple" -> Triple(SheBizPrimaryPurple, Color(0xFFC7B2FF), Color.White)
        else -> Triple(SheBizLavender, Color.White, SheBizPrimaryPurple)
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape, ambientColor = baseColor.copy(alpha = 0.4f))
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(highlightColor, baseColor),
                    center = Offset(20f, 20f),
                    radius = 90f
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.36f).sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Stock Status Pill (0 = Out of stock red, 1-2 = Low stock gold, 3+ = Available green)
 */
@Composable
fun StockStatusBadge(status: StockStatus, count: Int? = null) {
    val (bgColor, textColor, label) = when (status) {
        StockStatus.AVAILABLE -> Triple(
            SheBizGreen.copy(alpha = 0.12f),
            SheBizGreen,
            if (count != null) "$count in stock" else "Available"
        )
        StockStatus.LOW_STOCK -> Triple(
            SheBizGold.copy(alpha = 0.16f),
            Color(0xFFB37400),
            if (count != null) "Low stock ($count)" else "Low stock"
        )
        StockStatus.OUT_OF_STOCK -> Triple(
            SheBizRed.copy(alpha = 0.12f),
            SheBizRed,
            "Out of stock"
        )
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Top App Header with Logo, Title, Notification Bell and User Chip with Dropdown Menu
 */
@Composable
fun SheBizTopHeader(
    title: String,
    subtitle: String,
    unreadNotificationCount: Int,
    userName: String,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SheBizBackground)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo: Overlapping purple & coral circles + SheBiz text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
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
                    fontSize = 20.sp,
                    color = SheBizHeading,
                    letterSpacing = (-0.5).sp
                )
            }

            // Right actions: Notification Bell + User Profile Chip
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notification Bell with Badge
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("notification_bell_button")
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = SheBizHeading
                        )
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(SheBizCoral, CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadNotificationCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // User Chip with Avatar + Name + Dropdown
                Box {
                    Surface(
                        onClick = { menuExpanded = true },
                        shape = RoundedCornerShape(100.dp),
                        color = SheBizLavender,
                        modifier = Modifier.testTag("user_chip_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlossySphereAvatar(
                                initials = "AL",
                                size = 28.dp,
                                tone = "purple"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = userName.split(" ").firstOrNull() ?: "Member",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SheBizHeading,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Member",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SheBizPrimaryPurple
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "User Menu",
                                tint = SheBizPrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(SheBizSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Profile") },
                            leadingIcon = { Icon(Icons.Outlined.Person, null, tint = SheBizPrimaryPurple) },
                            onClick = {
                                menuExpanded = false
                                onProfileClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Account Settings") },
                            leadingIcon = { Icon(Icons.Outlined.Settings, null, tint = SheBizPrimaryPurple) },
                            onClick = {
                                menuExpanded = false
                                onSettingsClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Help & Support") },
                            leadingIcon = { Icon(Icons.Outlined.HelpOutline, null, tint = SheBizPrimaryPurple) },
                            onClick = {
                                menuExpanded = false
                                onHelpClick()
                            }
                        )
                        HorizontalDivider(color = SheBizBorder)
                        DropdownMenuItem(
                            text = { Text("Log Out", color = SheBizRed) },
                            leadingIcon = { Icon(Icons.Outlined.Logout, null, tint = SheBizRed) },
                            onClick = {
                                menuExpanded = false
                                onLogoutClick()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Page Title & Subtitle
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = SheBizHeading
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = SheBizBody
        )
    }
}

/**
 * SheBiz Bottom Navigation Bar with 5 tabs + Raised Floating 3D Sparkle AI Assistant button
 */
@Composable
fun SheBizBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAiClick: () -> Unit
) {
    val items = listOf(
        Pair("Home", Icons.Filled.Home to Icons.Outlined.Home),
        Pair("Opportunities", Icons.Filled.Work to Icons.Outlined.WorkOutline),
        Pair("Mentors", Icons.Filled.Groups to Icons.Outlined.Groups),
        Pair("Business", Icons.Filled.Storefront to Icons.Outlined.Storefront),
        Pair("Profile", Icons.Filled.Person to Icons.Outlined.PersonOutline)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, ambientColor = Color(0x1F000000), spotColor = Color(0x336938FF)),
        color = SheBizSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                // Main 5 Navigation Items
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onTabSelected(index) }
                                .testTag("nav_tab_${item.first.lowercase()}"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) SheBizLavender else Color.Transparent)
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.second.first else item.second.second,
                                    contentDescription = item.first,
                                    tint = if (isSelected) SheBizPrimaryPurple else SheBizBody,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.first,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SheBizPrimaryPurple else SheBizBody,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Floating 3D AI Sparkle Button floating above the bar on the right side
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-12).dp, y = (-24).dp)
                ) {
                    FloatingAiSparkleButton(onClick = onAiClick)
                }
            }
        }
    }
}

/**
 * 3D AI Sparkle Floating Button
 */
@Composable
fun FloatingAiSparkleButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier
            .size(52.dp)
            .shadow(10.dp, CircleShape, ambientColor = SheBizCoral.copy(alpha = 0.5f), spotColor = SheBizPrimaryPurple)
            .testTag("floating_ai_assistant_button")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SheBizCoral,
                            SheBizPrimaryPurple,
                            SheBizGold
                        )
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "SheBiz AI Assistant",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
