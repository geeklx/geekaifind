package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundStyle
import com.example.model.DifferenceDefinition
import com.example.model.DifferenceType
import com.example.model.LevelDefinition
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders the Spot the Difference Canvas with elegant Chinese painting backgrounds
 * and procedurally drawn elements that differ between original (top) and modified (bottom) states.
 */
@Composable
fun SpotTheDifferenceCanvas(
    level: LevelDefinition,
    isModified: Boolean,
    discoveredIds: Set<Int>,
    modifier: Modifier = Modifier,
    onTap: (Float, Float) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f) // Perfect classic landscape ratio
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF8A623A), RoundedCornerShape(12.dp))
    ) {
        val width = maxWidth
        val height = maxHeight

        // Dynamic Chinese-themed background gradients
        val backgroundBrush = remember(level.backgroundType) {
            getBackgroundBrush(level.backgroundType)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .pointerInput(level.id) {
                    detectTapGestures { offset ->
                        // Convert absolute tap offset to ratios (0f to 1f)
                        val xRatio = offset.x / size.width
                        val yRatio = offset.y / size.height
                        onTap(xRatio, yRatio)
                    }
                }
        ) {
            // 1. Draw elegant background decorations (Qing style ink mountains/rivers/scenery)
            drawSceneryBackground(level, size)

            // 1.5. Draw level-specific main scenic backdrop corresponding to the idiom
            drawIdiomMainScene(level.id, size, this)

            // 2. Draw vertical Chinese scroll badge for level context
            drawScrollBadge(level, size, isModified)

            // 3. Draw each difference interactive item
            level.differences.forEach { diff ->
                val isSolved = discoveredIds.contains(diff.id)
                val itemX = diff.x * size.width
                val itemY = diff.y * size.height

                // Draw the actual item based on solved state and top/bottom variation
                drawDifferenceItem(
                    type = diff.type,
                    center = Offset(itemX, itemY),
                    isModifiedImage = isModified,
                    isSolved = isSolved,
                    drawScope = this
                )

                // 4. Draw Red Ink Imperial Stamp Circle if solved!
                if (isSolved) {
                    drawSolvedStamp(Offset(itemX, itemY), this)
                }
            }
        }
    }
}

private fun getBackgroundBrush(style: BackgroundStyle): Brush {
    return when (style) {
        BackgroundStyle.INK_WASH_GOLD -> Brush.radialGradient(
            colors = listOf(Color(0xFFFCF8EB), Color(0xFFEADBBE)),
            radius = 1200f
        )
        BackgroundStyle.MISTY_JADE -> Brush.verticalGradient(
            colors = listOf(Color(0xFFE8F2EC), Color(0xFFC8DEC9))
        )
        BackgroundStyle.MIDNIGHT_INDIGO -> Brush.verticalGradient(
            colors = listOf(Color(0xFF1B263B), Color(0xFF0D1B2A))
        )
        BackgroundStyle.IMPERIAL_RED -> Brush.radialGradient(
            colors = listOf(Color(0xFFFFF0EC), Color(0xFFF3C0B2)),
            radius = 1000f
        )
        BackgroundStyle.SCHOLAR_BROWN -> Brush.verticalGradient(
            colors = listOf(Color(0xFFF5F0E6), Color(0xFFDECBB7))
        )
    }
}

/**
 * Draws elegant, varied, level-specific atmospheric backdrops reflecting traditional scroll art.
 */
private fun DrawScope.drawSceneryBackground(level: LevelDefinition, size: Size) {
    val style = level.backgroundType
    val inkColor = when (style) {
        BackgroundStyle.MIDNIGHT_INDIGO -> Color(0x44FFFFFF)
        else -> Color(0x1C2B1E17)
    }

    val w = size.width
    val h = size.height

    // 1. Level-specific dynamic mountain scenery styles
    when (level.id) {
        1, 9, 20 -> { // INK_WASH_GOLD values
            val p = Path().apply {
                moveTo(0f, h * 0.9f)
                quadraticTo(w * 0.3f, h * 0.6f, w * 0.6f, h * 0.78f)
                quadraticTo(w * 0.8f, h * 0.5f, w, h * 0.9f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(p, color = inkColor)
            for (i in 0..2) {
                drawCircle(Color(0x16D4AF37), radius = 80f + i * 40f, center = Offset(w * (0.2f + i * 0.3f), h * 0.25f))
            }
        }
        2, 6, 8, 11, 18 -> { // MISTY_JADE values
            val p = Path().apply {
                moveTo(0f, h)
                cubicTo(w * 0.15f, h * 0.55f, w * 0.45f, h * 0.88f, w * 0.72f, h * 0.65f)
                lineTo(w, h)
                close()
            }
            drawPath(p, color = inkColor)
            // Delicate bamboo silhouettes
            for (i in 0..3) {
                val bx = w * (0.05f + i * 0.08f)
                drawLine(inkColor.copy(alpha = 0.08f), Offset(bx, h), Offset(bx + 12f, h * 0.35f), strokeWidth = 6f)
            }
        }
        3, 12, 15 -> { // MIDNIGHT_INDIGO values
            val p = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.55f)
                lineTo(w * 0.35f, h * 0.48f)
                lineTo(w * 0.5f, h * 0.7f)
                lineTo(w * 0.8f, h * 0.42f)
                lineTo(w, h * 0.65f)
                lineTo(w, h)
                close()
            }
            drawPath(p, color = Color(0xFF0F172A).copy(alpha = 0.65f))
            // Twinkling night sky background stars
            for (i in 0..5) {
                drawCircle(Color.White.copy(alpha = 0.4f), radius = 2.5f, center = Offset(w * (0.1f + i * 0.15f + (i % 2) * 0.06f), h * (0.18f + (i % 3) * 0.07f)))
            }
        }
        else -> { // SCHOLAR_BROWN, IMPERIAL_RED, others
            drawRect(Color(0x084E2C15), topLeft = Offset.Zero, size = size)
            // Decorative window grid lines
            for (i in 0..2) {
                drawRect(
                    color = inkColor,
                    topLeft = Offset(w * (0.15f + i * 0.28f), 24f),
                    size = Size(w * 0.12f, h * 0.4f),
                    style = Stroke(width = 2f)
                )
            }
        }
    }

    // 2. Majestic Dynamic sun/moon based on authentic level theme
    val cellColor = when {
        level.id == 3 || level.id == 12 || level.id == 15 -> Color(0xEEE0E1DD) // Moon
        level.id == 20 -> Color(0xFFE56B55) // Huge rising sun
        style == BackgroundStyle.IMPERIAL_RED -> Color(0x2CD94E34)
        else -> Color(0x1AD94E34)
    }
    val radius = if (level.id == 20) h * 0.32f else h * 0.18f
    val center = if (level.id == 3 || level.id == 12) Offset(w * 0.22f, h * 0.28f) else Offset(w * 0.82f, h * 0.25f)

    drawCircle(color = cellColor, radius = radius, center = center)
    drawCircle(color = cellColor.copy(alpha = cellColor.alpha * 0.4f), radius = radius + 12f, center = center, style = Stroke(width = 3f))

    // 3. Traditional bounding border inside the scroll
    drawRect(
        color = if (style == BackgroundStyle.MIDNIGHT_INDIGO) Color(0x3AFFFFFF) else Color(0x1F8A623A),
        topLeft = Offset(10f, 10f),
        size = Size(w - 20f, h - 20f),
        style = Stroke(width = 1.5f)
    )
}

/**
 * Draws the level-specific main scenic backdrop corresponding to the idiom story,
 * providing a fully unique visual identity and rich theme for every single level.
 */
private fun drawIdiomMainScene(levelId: Int, size: Size, drawScope: DrawScope) {
    drawScope.apply {
        val w = size.width
        val h = size.height
        val inkColor = Color(0x3B2C1E17)
        val deskColor = Color(0xAA4E2C15)

        when (levelId) {
            1 -> { // 走马观花
                // Draw majestic horse & rider
                drawOval(Color(0xFF7A502C), Offset(w * 0.24f, h * 0.42f), Size(65f, 32f)) // body
                drawRect(Color(0xFF422F13), Offset(w * 0.32f, h * 0.44f), Size(18f, 35f)) // legs
                drawCircle(Color(0xFFDECBB7), 8f, Offset(w * 0.35f, h * 0.40f)) // head at hat center coords
                // Flowering garden pathway on the right side
                for (i in 0..5) {
                    drawCircle(Color(0x99FF85A1), 10f, Offset(w * (0.62f + i * 0.05f), h * (0.64f + (i % 2) * 0.04f)))
                }
            }
            2 -> { // 对牛弹琴
                // Cute grazing Cow at bottom-left close to the desk
                drawOval(Color(0xBB5C544E), Offset(w * 0.16f, h * 0.60f), Size(75f, 42f))
                drawCircle(Color(0xBB5C544E), 16f, Offset(w * 0.15f, h * 0.62f))
                // Scholar desk under elegant pine tree curve
                drawRoundRect(Color(0xAA1E3A8A), Offset(w * 0.42f, h * 0.52f), Size(30f, 30f), CornerRadius(5f)) // Scholar
                drawLine(Color(0xFF5C3D2E), Offset(w * 0.22f, h * 0.70f), Offset(w * 0.28f, h * 0.70f), 4f) // Table for Tea Cup
                // Gnarled old pine tree trunk curving to tree branch difference at (0.78f, 0.30f)
                drawLine(Color(0xFF5C3D2E), Offset(w * 0.65f, h * 0.82f), Offset(w * 0.76f, h * 0.32f), 8f, cap = StrokeCap.Round)
                drawCircle(Color(0x662E8B57), 24f, Offset(w * 0.78f, h * 0.30f))
                // Lotus park pond boundary
                drawArc(Color(0x1C0077B6), 0f, 360f, true, Offset(w * 0.80f, h * 0.66f), Size(w * 0.16f, h * 0.18f))
            }
            3 -> { // 掩耳盗铃
                // Temple roof beam holding hanging bronze bell at (0.55f, 0.25f)
                drawLine(Color(0xFF422F13), Offset(w * 0.45f, h * 0.24f), Offset(w * 0.65f, h * 0.24f), 6f)
                // Thief crouching at (0.68f, 0.60f)
                drawOval(Color(0xEE1E293B), Offset(w * 0.65f, h * 0.56f), Size(32f, 48f))
                drawCircle(Color(0xFFDECBB7), 10f, Offset(w * 0.68f, h * 0.60f)) // Head matching Hat coordinate
                // Courtyard ground level
                drawLine(inkColor, Offset(0f, h * 0.81f), Offset(w, h * 0.81f), 2f)
            }
            4 -> { // 纸上谈兵
                // Large tactics war-desk
                drawRoundRect(deskColor, Offset(w * 0.2f, h * 0.52f), Size(w * 0.6f, h * 0.36f), CornerRadius(8f))
                drawLine(inkColor, Offset(w * 0.18f, h * 0.1f), Offset(w * 0.18f, h * 0.45f), 2f) // window frame
            }
            5 -> { // 名落孙山
                // Imperial list wall
                drawRect(Color(0xFF5C3D2E), Offset(w * 0.40f, h * 0.18f), Size(w * 0.2f, h * 0.35f), style = Stroke(width = 4f))
                drawRect(Color(0xFFFFF0EC), Offset(w * 0.41f, h * 0.20f), Size(w * 0.18f, h * 0.31f))
                drawOval(Color(0xAA0284C7), Offset(w * 0.24f, h * 0.68f), Size(32f, 42f)) // Sad Scholar
            }
            6 -> { // 铁杵磨针
                // Grinding rock
                drawRoundRect(Color(0xFF707070), Offset(w * 0.42f, h * 0.60f), Size(70f, 40f), CornerRadius(10f))
                // Old lady grinding pestle at (0.48f, 0.50f)
                drawCircle(Color(0xFFDECBB7), 8f, Offset(w * 0.48f, h * 0.50f))
                drawOval(Color(0xDD7C2D12), Offset(w * 0.45f, h * 0.53f), Size(35f, 35f))
                // Refreshing mountain pool
                drawArc(Color(0x220077B6), 0f, 360f, true, Offset(w * 0.12f, h * 0.70f), Size(w * 0.25f, h * 0.15f))
            }
            7 -> { // 叶公好龙
                val p = Path().apply {
                    moveTo(w * 0.1f, h * 0.75f)
                    quadraticTo(w * 0.4f, h * 0.35f, w * 0.58f, h * 0.35f)
                    quadraticTo(w * 0.7f, h * 0.35f, w * 0.85f, h * 0.65f)
                }
                drawPath(p, Color(0x25D4AF37), style = Stroke(width = 24f, cap = StrokeCap.Round))
                // Elegant incense burner table
                drawRoundRect(deskColor, Offset(w * 0.18f, h * 0.60f), Size(70f, 25f), CornerRadius(4f))
            }
            8 -> { // 井底之蛙
                // Circular deep well stone wall border
                drawCircle(Color(0xFF333333), h * 0.40f, Offset(w * 0.5f, h * 0.5f), style = Stroke(width = 24f))
                drawCircle(Color(0x884F772D), 14f, Offset(w * 0.15f, h * 0.45f)) // Green moss
            }
            9 -> { // 画龙点睛
                // Master hall murals
                drawRoundRect(Color(0xFFF1E4C3), Offset(w * 0.25f, h * 0.20f), Size(w * 0.5f, h * 0.38f), CornerRadius(4f))
                drawRoundRect(Color(0xFF8B0000), Offset(w * 0.25f, h * 0.20f), Size(w * 0.5f, h * 0.38f), CornerRadius(4f), style = Stroke(width = 2f))
                // Painter standing at right
                drawOval(Color(0xAA1E293B), Offset(w * 0.74f, h * 0.58f), Size(28f, 40f))
                drawCircle(Color(0xFFDECBB7), 7f, Offset(w * 0.78f, h * 0.60f)) // Scholar head
            }
            10 -> { // 守株待兔
                // Thick rustic tree trunk
                drawLine(Color(0xFF6B4E3D), Offset(w * 0.28f, h * 0.22f), Offset(w * 0.28f, h * 0.9f), 12f)
                drawCircle(Color(0xFF4F772D), 18f, Offset(w * 0.28f, h * 0.22f)) // foliage
                drawOval(Color(0xAA735D49), Offset(w * 0.56f, h * 0.68f), Size(38f, 30f)) // sleeping peasant
            }
            11 -> { // 刻舟求剑
                // Elegant wooden boat shape centered at (0.50f, 0.58f)
                drawOval(Color(0xDD8A623A), Offset(w * 0.35f, h * 0.54f), Size(110f, 25f))
                drawLine(Color(0x330077B6), Offset(0f, h * 0.75f), Offset(w, h * 0.75f), 3f) // water level
            }
            12 -> { // 闻鸡起舞
                drawLine(Color.Gray, Offset(0f, h * 0.78f), Offset(w, h * 0.78f), 2f) // terrace path
                drawOval(Color(0xEE1E293B), Offset(w * 0.41f, h * 0.64f), Size(25f, 40f)) // swordsman
                drawCircle(Color(0xFFDECBB7), 8f, Offset(w * 0.44f, h * 0.68f)) // head at coords
            }
            13 -> { // 完璧归赵
                // Golden throne center podium
                drawRoundRect(Color(0xFF8B0000), Offset(w * 0.38f, h * 0.52f), Size(100f, 40f), CornerRadius(6f))
                drawRoundRect(Color(0xFFD4AF37), Offset(w * 0.38f, h * 0.52f), Size(100f, 40f), CornerRadius(6f), style = Stroke(width = 2f))
                drawOval(Color(0xCC1E293B), Offset(w * 0.68f, h * 0.58f), Size(28f, 45f)) // minister
            }
            14 -> { // 自相矛盾
                // Crossed antique weapon structures
                drawLine(Color(0xFF5C544E), Offset(w * 0.3f, h * 0.25f), Offset(w * 0.7f, h * 0.75f), 4f)
                drawCircle(Color(0xFFDECBB7), 8f, Offset(w * 0.82f, h * 0.68f)) // spectator head
            }
            15 -> { // 四面楚歌
                // Military tents structures on ground
                val path = Path().apply {
                    moveTo(w * 0.45f, h * 0.4f)
                    lineTo(w * 0.6f, h * 0.75f)
                    lineTo(w * 0.3f, h * 0.75f)
                    close()
                }
                drawPath(path, Color(0xCC374151))
            }
            16 -> { // 画饼充饥
                // Stand easel holding drawing board at (0.52f, 0.48f)
                drawRoundRect(Color(0xEEF5F0E6), Offset(w * 0.42f, h * 0.34f), Size(90f, 65f), CornerRadius(4f))
                drawRoundRect(Color(0xFF4E2C15), Offset(w * 0.42f, h * 0.34f), Size(90f, 65f), CornerRadius(4f), style = Stroke(width = 2f))
                drawCircle(Color(0xFFDECBB7), 7f, Offset(w * 0.70f, h * 0.60f)) // painter head
            }
            17 -> { // 名列前茅
                drawLine(Color.DarkGray, Offset(w * 0.15f, h * 0.2f), Offset(w * 0.15f, h * 0.85f), 3f) // flagpole
                drawRect(Color(0x55B22222), Offset(w * 0.05f, h * 0.25f), Size(60f, 45f)) // flag banner
            }
            18 -> { // 柳暗花明
                // Splendid arch stone bridge over river
                val p = Path().apply {
                    moveTo(w * 0.2f, h * 0.82f)
                    quadraticTo(w * 0.45f, h * 0.52f, w * 0.7f, h * 0.82f)
                }
                drawPath(p, Color(0xBB866141), style = Stroke(width = 12f))
            }
            19 -> { // 大智若愚
                // Cozy scholar library lattice bookcase grid lines back
                drawRect(Color(0x224E2C15), Offset(w * 0.15f, 20f), Size(w * 0.7f, h - 40f))
                drawLine(Color(0x224E2C15), Offset(w * 0.15f, h * 0.5f), Offset(w * 0.85f, h * 0.5f), 3f)
            }
            20 -> { // 一鸣惊人
                // Sunset mountain ridge rays
                for (i in 0..4) {
                    val angle = i * Math.PI / 4 + Math.PI / 8
                    drawLine(Color(0x33FCA311), Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f + cos(angle).toFloat() * 150f, h * 0.5f - sin(angle).toFloat() * 100f), 3f)
                }
            }
        }
    }
}

/**
 * Draws a gorgeous vertical chinese calligraphy label for authenticity
 */
private fun DrawScope.drawScrollBadge(level: LevelDefinition, size: Size, isModified: Boolean) {
    val scrollBg = Color(0xFFF1E4C3)
    val borderCol = Color(0xFF8B0000)

    // Small decorative label box at top left
    drawRoundRect(
        color = scrollBg,
        topLeft = Offset(24f, 24f),
        size = Size(40f, 90f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = borderCol,
        topLeft = Offset(24f, 24f),
        size = Size(40f, 90f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 1.5f)
    )

    // Paint indicator of which image is it (原图 Original vs 找茬 Modified)
    val indicatorColor = if (isModified) Color(0xFFB22222) else Color(0xFF2E8B57)

    drawCircle(
        color = indicatorColor,
        radius = 12f,
        center = Offset(44f, 44f)
    )

    // Draw three vertical hash lines on scroll side representing wood handles
    drawLine(
        color = Color(0x44000000),
        start = Offset(44f, 65f),
        end = Offset(44f, 100f),
        strokeWidth = 2f
    )
}

/**
 * Draws the specific spot-the-difference element procedurally and cleanly!
 */
private fun DrawScope.drawDifferenceItem(
    type: DifferenceType,
    center: Offset,
    isModifiedImage: Boolean,
    isSolved: Boolean,
    drawScope: DrawScope
) {
    val showModifiedState = isModifiedImage && !isSolved

    when (type) {
        DifferenceType.QING_OFFICIAL_HAT -> {
            val darkBlue = Color(0xFF14213D)
            val crimsonRed = Color(0xFF9E2A2B)
            val brightGold = Color(0xFFFCA311)
            // Hat base caps
            drawArc(darkBlue, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(center.x - 24f, center.y - 12f), size = Size(48f, 24f))
            drawOval(darkBlue, topLeft = Offset(center.x - 30f, center.y + 6f), size = Size(60f, 10f))
            drawArc(crimsonRed, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(center.x - 12f, center.y - 16f), size = Size(24f, 12f))
            drawCircle(color = brightGold, radius = 5f, center = Offset(center.x, center.y - 17f))

            if (!showModifiedState) {
                // Original: Peacock Feather Plume dangles to the right
                drawLine(Color(0xFF1B4332), Offset(center.x, center.y - 15f), Offset(center.x + 28f, center.y - 6f), strokeWidth = 3f, cap = StrokeCap.Round)
                drawCircle(Color(0xFF40916C), radius = 4f, center = Offset(center.x + 28f, center.y - 6f))
            } else {
                // Modified: Peacock Feather is shortened or missing
                drawLine(Color(0xFF00B4D8), Offset(center.x, center.y - 15f), Offset(center.x + 10f, center.y - 14f), strokeWidth = 3f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.PALACE_LANTERN -> {
            val frameColor = Color(0xFF4E2C15)
            val clothColor = Color(0xFFD90429)
            // Rope and base support frames
            drawLine(frameColor, Offset(center.x, center.y - 30f), Offset(center.x, center.y - 16f), strokeWidth = 2f)
            if (!showModifiedState) {
                drawCircle(Color(0xFFFFEA70).copy(alpha = 0.5f), radius = 28f, center = center) // Glowing!
            } else {
                drawCircle(Color(0x33000000), radius = 24f, center = center) // Dark/Dull unlit
            }
            // Traditional hexagonal body shape path
            val bodyPath = Path().apply {
                moveTo(center.x - 8f, center.y - 16f)
                lineTo(center.x + 8f, center.y - 16f)
                lineTo(center.x + 16f, center.y)
                lineTo(center.x + 8f, center.y + 16f)
                lineTo(center.x - 8f, center.y + 16f)
                lineTo(center.x - 16f, center.y)
                close()
            }
            drawPath(color = if (!showModifiedState) clothColor else Color(0xFF8B2635), path = bodyPath)
            drawLine(frameColor, Offset(center.x - 10f, center.y - 16f), Offset(center.x + 10f, center.y - 16f), strokeWidth = 3f)
            drawLine(frameColor, Offset(center.x - 10f, center.y + 16f), Offset(center.x + 10f, center.y + 16f), strokeWidth = 3f)

            if (!showModifiedState) {
                drawLine(Color(0xFFFFB703), Offset(center.x, center.y + 16f), Offset(center.x, center.y + 32f), strokeWidth = 2f)
                drawCircle(color = Color(0xFFFFB703), radius = 3.5f, center = Offset(center.x, center.y + 32f))
            }
        }

        DifferenceType.CLASSIC_FAN -> {
            drawCircle(Color(0xFFFCF6BD), radius = 22f, center = center)
            drawCircle(Color(0xFFE9C46A), radius = 22f, center = center, style = Stroke(width = 1.5f))
            drawLine(Color(0xFFD62828), Offset(center.x, center.y + 22f), Offset(center.x, center.y + 42f), strokeWidth = 3f, cap = StrokeCap.Round)

            if (!showModifiedState) {
                // Original: Beautiful ink painted plum flower bough
                drawLine(Color(0xFF4A2511), Offset(center.x - 12f, center.y + 6f), Offset(center.x + 10f, center.y - 6f), strokeWidth = 2f)
                drawCircle(Color(0xFFE63946), radius = 3.5f, center = Offset(center.x + 2f, center.y - 2f))
            }
        }

        DifferenceType.BRONZE_BELL -> {
            val bronzeCol = Color(0xFFCD7F32)
            val ironCol = Color(0xFF2F3E46)
            val bellPath = Path().apply {
                moveTo(center.x - 14f, center.y + 12f)
                cubicTo(center.x - 14f, center.y - 16f, center.x + 14f, center.y - 16f, center.x + 14f, center.y + 12f)
                close()
            }
            drawPath(color = bronzeCol, path = bellPath)
            drawPath(color = ironCol, path = bellPath, style = Stroke(width = 2f))
            drawArc(ironCol, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(center.x - 6f, center.y - 20f), size = Size(12f, 12f), style = Stroke(width = 2.5f))

            if (!showModifiedState) {
                drawCircle(Color(0xFF4A2511), radius = 4.5f, center = Offset(center.x, center.y + 16f))
                drawLine(Color.DarkGray, Offset(center.x, center.y + 6f), Offset(center.x, center.y + 14f), strokeWidth = 2f)
            }
        }

        DifferenceType.INK_BUTTERFLY -> {
            val inkBlack = Color(0xFF264653)
            val wingColor = if (!showModifiedState) Color(0xFFE76F51) else Color(0xFF2E86AB)
            // Left wings path
            val leftWing = Path().apply {
                moveTo(center.x, center.y)
                cubicTo(center.x - 16f, center.y - 16f, center.x - 20f, center.y, center.x - 8f, center.y + 4f)
                close()
            }
            drawPath(color = wingColor, path = leftWing)
            drawPath(color = inkBlack, path = leftWing, style = Stroke(width = 1.2f))
            // Right wings path
            val rightWing = Path().apply {
                moveTo(center.x, center.y)
                cubicTo(center.x + 16f, center.y - 16f, center.x + 20f, center.y, center.x + 8f, center.y + 4f)
                close()
            }
            drawPath(color = wingColor, path = rightWing)
            drawPath(color = inkBlack, path = rightWing, style = Stroke(width = 1.2f))

            drawLine(inkBlack, Offset(center.x, center.y - 10f), Offset(center.x, center.y + 8f), strokeWidth = 3f, cap = StrokeCap.Round)
        }

        DifferenceType.SOARING_CRANE -> {
            val craneWhite = Color(0xFFF8F9FA)
            val customCrimson = Color(0xFFE63946)
            drawLine(craneWhite, Offset(center.x - 24f, center.y + 6f), Offset(center.x + 24f, center.y - 8f), strokeWidth = 3.5f, cap = StrokeCap.Round)

            val leftWingDetail = Path().apply {
                moveTo(center.x - 4f, center.y - 2f)
                quadraticTo(center.x - 16f, center.y - 26f, center.x - 12f, if (!showModifiedState) center.y - 32f else center.y - 12f)
            }
            drawPath(color = craneWhite, path = leftWingDetail, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

            if (!showModifiedState) {
                drawCircle(color = customCrimson, radius = 3.5f, center = Offset(center.x + 18f, center.y - 6f))
            } else {
                drawCircle(color = Color.DarkGray, radius = 2.5f, center = Offset(center.x + 18f, center.y - 6f))
            }
        }

        DifferenceType.TEA_CUP -> {
            val ceramicWhite = Color(0xFFF7F5F0)
            val cobaltBlue = Color(0xFF1D3557)
            val bowlPath = Path().apply {
                moveTo(center.x - 14f, center.y - 10f)
                lineTo(center.x + 14f, center.y - 10f)
                quadraticTo(center.x + 12f, center.y + 8f, center.x, center.y + 12f)
                quadraticTo(center.x - 12f, center.y + 8f, center.x - 14f, center.y - 10f)
                close()
            }
            drawPath(color = ceramicWhite, path = bowlPath)
            drawPath(color = cobaltBlue, path = bowlPath, style = Stroke(width = 1.8f))
            drawLine(cobaltBlue, Offset(center.x - 6f, center.y + 12f), Offset(center.x + 6f, center.y + 12f), strokeWidth = 3f, cap = StrokeCap.Round)

            if (!showModifiedState) {
                // Original: Steam curls
                drawCircle(color = cobaltBlue, radius = 3.5f, center = Offset(center.x, center.y))
                drawLine(Color(0xFFADB5BD), Offset(center.x, center.y - 14f), Offset(center.x + 2f, center.y - 24f), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.SCROLL_BOOK -> {
            val paperBg = Color(0xFFFAE19C)
            val woodBrn = Color(0xFF6B4E3D)
            drawLine(woodBrn, Offset(center.x - 22f, center.y - 16f), Offset(center.x - 22f, center.y + 16f), strokeWidth = 4f, cap = StrokeCap.Round)

            if (!showModifiedState) {
                drawLine(woodBrn, Offset(center.x + 22f, center.y - 16f), Offset(center.x + 22f, center.y + 16f), strokeWidth = 4f, cap = StrokeCap.Round)
                drawRect(color = paperBg, topLeft = Offset(center.x - 20f, center.y - 12f), size = Size(40f, 24f))
                drawRect(color = Color(0xFFB22222), topLeft = Offset(center.x - 12f, center.y - 5f), size = Size(6f, 6f))
            } else {
                drawRect(color = paperBg, topLeft = Offset(center.x - 20f, center.y - 12f), size = Size(20f, 24f))
                drawLine(woodBrn, Offset(center.x, center.y - 16f), Offset(center.x, center.y + 16f), strokeWidth = 4f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.FLOWER_LOTUS -> {
            val petalPink = Color(0xFFFFB3C1)
            val stemGreen = Color(0xFF70E000)
            drawLine(color = stemGreen, start = Offset(center.x, center.y), end = Offset(center.x, center.y + 24f), strokeWidth = 2.5f)
            drawCircle(color = petalPink, radius = 10f, center = center)
            drawCircle(color = Color(0xFFFF85A1), radius = 6f, center = Offset(center.x - 8f, center.y - 2f))
            drawCircle(color = Color(0xFFFF85A1), radius = 6f, center = Offset(center.x + 8f, center.y - 2f))

            if (!showModifiedState) {
                drawOval(color = Color(0xFF38B000), topLeft = Offset(center.x - 22f, center.y + 6f), size = Size(18f, 10f))
            } else {
                drawOval(color = Color(0xFFE9C46A), topLeft = Offset(center.x - 22f, center.y + 6f), size = Size(18f, 10f)) // withered leaf
            }
        }

        DifferenceType.ANCIENT_COIN -> {
            val goldBronze = Color(0xFFE9C46A)
            val darkRim = Color(0xFF4A3728)
            drawCircle(color = goldBronze, radius = 18f, center = center)
            drawCircle(color = darkRim, radius = 18f, center = center, style = Stroke(width = 2f))

            if (!showModifiedState) {
                drawRect(color = darkRim, topLeft = Offset(center.x - 4.5f, center.y - 4.5f), size = Size(9f, 9f))
            } else {
                drawCircle(color = darkRim, radius = 4.5f, center = center)
            }
        }

        DifferenceType.SPLASH_FISH -> {
            val fishRed = Color(0xFFD9381E)
            val splashColor = Color(0xFF0077B6)
            val body = Path().apply {
                moveTo(center.x - 14f, center.y + 14f)
                quadraticTo(center.x + 4f, center.y - 12f, center.x + 14f, center.y - 6f)
                lineTo(center.x + 8f, center.y)
                quadraticTo(center.x, center.y + 10f, center.x - 14f, center.y + 14f)
            }
            drawPath(color = fishRed, path = body)

            if (!showModifiedState) {
                drawArc(color = splashColor, startAngle = 30f, sweepAngle = 120f, useCenter = false, topLeft = Offset(center.x - 16f, center.y + 8f), size = Size(32f, 12f), style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                drawLine(color = splashColor, start = Offset(center.x - 16f, center.y + 12f), end = Offset(center.x + 16f, center.y + 12f), strokeWidth = 1.5f)
            }
        }

        DifferenceType.CLOUDS -> {
            val cloudOutline = if (!showModifiedState) Color(0xFFE9C46A) else Color(0xFF4A4E69)
            val cloudPath = Path().apply {
                moveTo(center.x - 20f, center.y + 4f)
                quadraticTo(center.x - 10f, center.y - 12f, center.x, center.y - 2f)
                quadraticTo(center.x + 12f, center.y - 14f, center.x + 22f, center.y + 2f)
                quadraticTo(center.x + 10f, center.y + 12f, center.x - 6f, center.y + 8f)
                quadraticTo(center.x - 14f, center.y + 14f, center.x - 20f, center.y + 4f)
                close()
            }
            drawPath(color = if (!showModifiedState) Color.White.copy(alpha = 0.85f) else Color(0xFFDFE2DB).copy(alpha = 0.6f), path = cloudPath)
            drawPath(color = cloudOutline, path = cloudPath, style = Stroke(width = 2f))
        }

        DifferenceType.TREE_BRANCH -> {
            drawLine(Color(0xFF4A3728), Offset(center.x - 20f, center.y - 12f), Offset(center.x + 14f, center.y + 10f), strokeWidth = 3f, cap = StrokeCap.Round)
            if (!showModifiedState) {
                drawCircle(Color(0xFF4F772D), radius = 6f, center = Offset(center.x - 8f, center.y - 2f))
                drawCircle(Color(0xFF4F772D), radius = 6f, center = Offset(center.x + 4f, center.y + 6f))
            } else {
                drawCircle(Color(0xFF4F772D), radius = 4f, center = Offset(center.x + 4f, center.y + 6f))
            }
        }

        DifferenceType.PAGODA -> {
            val silhouette = Color(0xFF2B2D42)
            val baseRect1 = Path().apply {
                moveTo(center.x - 15f, center.y + 12f)
                lineTo(center.x + 15f, center.y + 12f)
                lineTo(center.x + 10f, center.y + 2f)
                lineTo(center.x - 10f, center.y + 2f)
                close()
            }
            drawPath(color = silhouette, path = baseRect1)

            if (!showModifiedState) {
                drawLine(Color(0xFFE9C46A), Offset(center.x, center.y + 2f), Offset(center.x, center.y - 16f), strokeWidth = 2.5f, cap = StrokeCap.Round)
            } else {
                drawLine(silhouette, Offset(center.x, center.y + 2f), Offset(center.x, center.y - 8f), strokeWidth = 2.5f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.INCENSE_BURNER -> {
            val burnerCol = Color(0xFF9B7E46)
            val outlineCol = Color(0xFF422F13)
            drawLine(outlineCol, Offset(center.x - 10f, center.y), Offset(center.x - 12f, center.y + 14f), 3.5f)
            drawLine(outlineCol, Offset(center.x + 10f, center.y), Offset(center.x + 12f, center.y + 14f), 3.5f)
            drawCircle(color = burnerCol, radius = 14f, center = center)
            drawCircle(color = outlineCol, radius = 14f, center = center, style = Stroke(width = 1.8f))

            if (!showModifiedState) {
                val smokePath = Path().apply {
                    moveTo(center.x, center.y - 8f)
                    cubicTo(center.x - 6f, center.y - 18f, center.x + 6f, center.y - 24f, center.x, center.y - 36f)
                }
                drawPath(path = smokePath, color = Color(0xBBCAE9FF), style = Stroke(width = 1.8f, cap = StrokeCap.Round))
            }
        }
    }
}

/**
 * Draws a beautiful Chinese vermillion red painting seal stamp highlighting found points
 */
private fun drawSolvedStamp(
    offset: Offset,
    drawScope: DrawScope
) {
    // Elegant red brush ring showing discovered coordinates
    drawScope.drawCircle(
        color = Color(0xFFC1121F), // Chinese seal red
        radius = 32f,
        center = offset,
        style = Stroke(
            width = 3.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f, 8f, 10f), 0f)
        )
    )

    // Vermillion central cross mark representing historical auditing
    drawScope.drawCircle(
        color = Color(0x27C1121F),
        radius = 29f,
        center = offset
    )
}
