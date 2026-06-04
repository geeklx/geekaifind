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
            drawSceneryBackground(level.backgroundType, size)

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
 * Draws elegant atmospheric backdrop graphics for traditional Chinese feel
 */
private fun DrawScope.drawSceneryBackground(style: BackgroundStyle, size: Size) {
    val inkColor = when (style) {
        BackgroundStyle.MIDNIGHT_INDIGO -> Color(0x33FFFFFF)
        else -> Color(0x1F2B1E17)
    }

    // Draw misty ink mountains in background
    val mountainPath1 = Path().apply {
        moveTo(0f, size.height * 0.8f)
        cubicTo(
            size.width * 0.2f, size.height * 0.5f,
            size.width * 0.4f, size.height * 0.9f,
            size.width * 0.6f, size.height * 0.6f
        )
        lineTo(size.width * 0.8f, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(mountainPath1, color = inkColor)

    val mountainPath2 = Path().apply {
        moveTo(size.width * 0.3f, size.height)
        cubicTo(
            size.width * 0.5f, size.height * 0.45f,
            size.width * 0.7f, size.height * 0.85f,
            size.width, size.height * 0.5f
        )
        lineTo(size.width, size.height)
        lineTo(size.width * 0.3f, size.height)
        close()
    }
    drawPath(mountainPath2, color = inkColor.copy(alpha = inkColor.alpha * 0.7f))

    // Draw solar/lunar wash in upper corner
    val sunColor = when (style) {
        BackgroundStyle.MIDNIGHT_INDIGO -> Color(0xFFFFF3B0).copy(alpha = 0.25f)
        BackgroundStyle.IMPERIAL_RED -> Color(0xFFD94E34).copy(alpha = 0.15f)
        else -> Color(0xFFE56B55).copy(alpha = 0.12f)
    }
    drawCircle(
        color = sunColor,
        radius = size.height * 0.18f,
        center = Offset(size.width * 0.82f, size.height * 0.25f)
    )

    // Draw a subtle border inside the frame mimicking traditional scroll framing
    val frameColor = when (style) {
        BackgroundStyle.MIDNIGHT_INDIGO -> Color(0x3DFFFFFF)
        else -> Color(0x277A502C)
    }
    drawRect(
        color = frameColor,
        topLeft = Offset(8f, 8f),
        size = Size(size.width - 16f, size.height - 16f),
        style = Stroke(width = 2f)
    )
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
    val textIndicator = if (isModified) "變" else "本" // Classic glyph placeholders
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
 * Draws the specific spot-the-difference element procedurally!
 */
private fun DrawScope.drawDifferenceItem(
    type: DifferenceType,
    center: Offset,
    isModifiedImage: Boolean,
    isSolved: Boolean,
    drawScope: DrawScope
) {
    // Note: If solving, we usually make them identical (or show original on both for clarity, or draw original state).
    // Let's implement original vs modified draw mechanics!
    val showModifiedState = isModifiedImage && !isSolved

    when (type) {
        DifferenceType.QING_OFFICIAL_HAT -> {
            // Draw a Qing dynasty imperial hat
            val darkBlue = Color(0xFF14213D)
            val crimsonRed = Color(0xFF9E2A2B)
            val brightGold = Color(0xFFFCA311)

            // Hat base
            drawArc(
                color = darkBlue,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - 24f, center.y - 12f),
                size = Size(48f, 24f)
            )

            // Hat rim
            drawOval(
                color = darkBlue,
                topLeft = Offset(center.x - 30f, center.y + 6f),
                size = Size(60f, 10f)
            )

            // Red tassel dome atop
            drawArc(
                color = crimsonRed,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(center.x - 12f, center.y - 16f),
                size = Size(24f, 12f)
            )

            // Golden topper bead
            drawCircle(color = brightGold, radius = 5f, center = Offset(center.x, center.y - 17f))

            if (!showModifiedState) {
                // Original: Peacock Feather Plume dangling out (Green stick with a beautiful circle tail)
                val featherGreen = Color(0xFF1B4332)
                drawLine(
                    color = featherGreen,
                    start = Offset(center.x, center.y - 15f),
                    end = Offset(center.x + 28f, center.y - 6f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color(0xFF40916C),
                    radius = 4f,
                    center = Offset(center.x + 28f, center.y - 6f)
                )
                drawCircle(
                    color = Color(0xFF52B788),
                    radius = 2f,
                    center = Offset(center.x + 28f, center.y - 6f)
                )
            } else {
                // Modified: Peacock Feather is MISSING or is short blue bead!
                val alteredBlue = Color(0xFF00B4D8)
                drawLine(
                    color = alteredBlue,
                    start = Offset(center.x, center.y - 15f),
                    end = Offset(center.x + 12f, center.y - 12f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        DifferenceType.PALACE_LANTERN -> {
            // Draw classical red hanging lantern
            val frameColor = Color(0xFF4E2C15)
            val clothColor = Color(0xFFD90429)
            val tasselColor = Color(0xFFFFB703)

            // Hanging rope
            drawLine(
                color = frameColor,
                start = Offset(center.x, center.y - 30f),
                end = Offset(center.x, center.y - 16f),
                strokeWidth = 2f
            )

            // If Original (lighted): Draw a beautiful gentle neon glow in background!
            if (!showModifiedState) {
                drawCircle(
                    color = Color(0xFFFFEA70).copy(alpha = 0.5f),
                    radius = 28f,
                    center = center
                )
            } else {
                // Darkened background for unlit lantern
                drawCircle(
                    color = Color(0x33000000),
                    radius = 24f,
                    center = center
                )
            }

            // Hexagonal red lantern body
            val bodyPath = Path().apply {
                moveTo(center.x - 8f, center.y - 16f)
                lineTo(center.x + 8f, center.y - 16f)
                lineTo(center.x + 16f, center.y)
                lineTo(center.x + 8f, center.y + 16f)
                lineTo(center.x - 8f, center.y + 16f)
                lineTo(center.x - 16f, center.y)
                close()
            }
            drawPath(
                color = if (!showModifiedState) clothColor else Color(0xFF8B2635),
                path = bodyPath
            )

            // Wooden cap/bottom frames
            drawLine(
                color = frameColor,
                start = Offset(center.x - 10f, center.y - 16f),
                end = Offset(center.x + 10f, center.y - 16f),
                strokeWidth = 3f
            )
            drawLine(
                color = frameColor,
                start = Offset(center.x - 10f, center.y + 16f),
                end = Offset(center.x + 10f, center.y + 16f),
                strokeWidth = 3f
            )

            // Tassels
            if (!showModifiedState) {
                // Original: Golden flowing details
                drawLine(
                    color = tasselColor,
                    start = Offset(center.x, center.y + 16f),
                    end = Offset(center.x, center.y + 32f),
                    strokeWidth = 2f
                )
                drawCircle(color = tasselColor, radius = 3f, center = Offset(center.x, center.y + 32f))
            } else {
                // Modified: Missing hanging tassel!
            }
        }

        DifferenceType.CLASSIC_FAN -> {
            val paperColor = Color(0xFFFCF6BD)
            val woodColor = Color(0xFFD62828)

            // Draw circular fan
            drawCircle(color = paperColor, radius = 22f, center = center)
            drawCircle(color = Color(0xFFE9C46A), radius = 22f, center = center, style = Stroke(width = 1.5f))

            // Wooden handle
            drawLine(
                color = woodColor,
                start = Offset(center.x, center.y + 22f),
                end = Offset(center.x, center.y + 42f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            if (!showModifiedState) {
                // Original: Paint a delicate ink branch of plum blossom!
                drawLine(
                    color = Color(0xFF4A2511),
                    start = Offset(center.x - 12f, center.y + 8f),
                    end = Offset(center.x + 10f, center.y - 8f),
                    strokeWidth = 2f
                )
                // Blooming pink plum dots
                drawCircle(color = Color(0xFFE63946), radius = 3f, center = Offset(center.x - 4f, center.y + 2f))
                drawCircle(color = Color(0xFFE63946), radius = 2f, center = Offset(center.x + 4f, center.y - 4f))
            } else {
                // Modified: The fan is empty / blank paper!
            }
        }

        DifferenceType.BRONZE_BELL -> {
            val bronzeCol = Color(0xFFCD7F32)
            val ironCol = Color(0xFF2F3E46)

            // Bell main body shape (U-shaped upside down)
            val bellPath = Path().apply {
                moveTo(center.x - 14f, center.y + 12f)
                cubicTo(
                    center.x - 14f, center.y - 16f,
                    center.x + 14f, center.y - 16f,
                    center.x + 14f, center.y + 12f
                )
                close()
            }
            drawPath(color = bronzeCol, path = bellPath)
            drawPath(color = ironCol, path = bellPath, style = Stroke(width = 2f))

            // Upper hanger loop
            drawArc(
                color = ironCol,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - 6f, center.y - 20f),
                size = Size(12f, 12f),
                style = Stroke(width = 2.5f)
            )

            // Striking hammer inside
            if (!showModifiedState) {
                // Original: has the hammer dangle
                drawCircle(color = Color(0xFF4A2511), radius = 4f, center = Offset(center.x, center.y + 16f))
                drawLine(
                    color = Color(0xFF4AD511),
                    start = Offset(center.x, center.y + 6f),
                    end = Offset(center.x, center.y + 14f),
                    strokeWidth = 2f
                )
            } else {
                // Modified: Missing hammer!
            }
        }

        DifferenceType.INK_BUTTERFLY -> {
            val inkBlack = Color(0xFF264653)
            val wingColor = if (!showModifiedState) Color(0xFFE76F51) else Color(0xFF2E86AB)

            // Draw classical chinese paint ink butterfly
            // Left wings
            val leftWing = Path().apply {
                moveTo(center.x, center.y)
                cubicTo(
                    center.x - 16f, center.y - 16f,
                    center.x - 20f, center.y,
                    center.x - 8f, center.y + 4f
                )
                close()
            }
            drawPath(color = wingColor, path = leftWing)
            drawPath(color = inkBlack, path = leftWing, style = Stroke(width = 1.5f))

            // Right wings
            val rightWing = Path().apply {
                moveTo(center.x, center.y)
                cubicTo(
                    center.x + 16f, center.y - 16f,
                    center.x + 20f, center.y,
                    center.x + 8f, center.y + 4f
                )
                close()
            }
            drawPath(color = wingColor, path = rightWing)
            drawPath(color = inkBlack, path = rightWing, style = Stroke(width = 1.5f))

            // Butterfly body line
            drawLine(
                color = inkBlack,
                start = Offset(center.x, center.y - 10f),
                end = Offset(center.x, center.y + 8f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Antennas
            drawLine(
                color = inkBlack,
                start = Offset(center.x, center.y - 8f),
                end = Offset(center.x - 8f, center.y - 15f),
                strokeWidth = 1f
            )
            drawLine(
                color = inkBlack,
                start = Offset(center.x, center.y - 8f),
                end = Offset(center.x + 8f, center.y - 15f),
                strokeWidth = 1f
            )
        }

        DifferenceType.SOARING_CRANE -> {
            val craneWhite = Color(0xFFF8F9FA)
            val customCrimson = Color(0xFFE63946)

            // Draw stylized flying white crane in Chinese brush look (V-shape wing curve)
            val bodyPath = Path().apply {
                moveTo(center.x - 24f, center.y + 6f) // tail
                lineTo(center.x + 18f, center.y - 6f) // head neck base
                lineTo(center.x + 28f, center.y - 10f) // long peak beak
            }
            drawPath(color = craneWhite, path = bodyPath, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

            // Wings (Original: Large majestic widespread wing, Modified: shorter lower wings)
            val leftWingDetail = Path().apply {
                moveTo(center.x - 4f, center.y - 2f)
                quadraticTo(
                    center.x - 16f, center.y - 28f,
                    center.x - 12f, if (!showModifiedState) center.y - 34f else center.y - 12f
                )
            }
            drawPath(color = craneWhite, path = leftWingDetail, style = Stroke(width = 4f, cap = StrokeCap.Round))

            val rightWingDetail = Path().apply {
                moveTo(center.x - 6f, center.y + 2f)
                quadraticTo(
                    center.x + 12f, center.y + 26f,
                    center.x + 16f, if (!showModifiedState) center.y + 32f else center.y + 12f
                )
            }
            drawPath(color = craneWhite, path = rightWingDetail, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

            // Red crowned spot on head
            if (!showModifiedState) {
                drawCircle(color = customCrimson, radius = 3.5f, center = Offset(center.x + 20f, center.y - 7f))
            } else {
                // Modified: crane with black/yellow crown instead of red crown!
                drawCircle(color = Color.DarkGray, radius = 2.5f, center = Offset(center.x + 20f, center.y - 7f))
            }
        }

        DifferenceType.TEA_CUP -> {
            // Antique fine china teacup
            val ceramicWhite = Color(0xFFF7F5F0)
            val cobaltBlue = Color(0xFF1D3557)

            // Cup bowl body
            val bowlPath = Path().apply {
                moveTo(center.x - 14f, center.y - 10f)
                lineTo(center.x + 14f, center.y - 10f)
                quadraticTo(center.x + 12f, center.y + 8f, center.x, center.y + 12f)
                quadraticTo(center.x - 12f, center.y + 8f, center.x - 14f, center.y - 10f)
                close()
            }
            drawPath(color = ceramicWhite, path = bowlPath)
            drawPath(color = cobaltBlue, path = bowlPath, style = Stroke(width = 2f))

            // Stand rim base
            drawLine(
                color = cobaltBlue,
                start = Offset(center.x - 6f, center.y + 12f),
                end = Offset(center.x + 6f, center.y + 12f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Cobalt floral paint pattern
            drawCircle(color = cobaltBlue, radius = 3.5f, center = Offset(center.x, center.y))

            if (!showModifiedState) {
                // Original: 3 lovely steam lines curling upward
                val steamColor = Color(0xFFADB5BD)
                val strokeW = 1.5f
                drawLine(
                    color = steamColor,
                    start = Offset(center.x - 6f, center.y - 14f),
                    end = Offset(center.x - 8f, center.y - 24f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = steamColor,
                    start = Offset(center.x, center.y - 14f),
                    end = Offset(center.x + 2f, center.y - 26f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = steamColor,
                    start = Offset(center.x + 6f, center.y - 14f),
                    end = Offset(center.x + 4f, center.y - 24f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            } else {
                // Modified: No hot steam! Cold tea.
            }
        }

        DifferenceType.SCROLL_BOOK -> {
            val paperBg = Color(0xFFFAE19C)
            val woodBrn = Color(0xFF6B4E3D)

            // Horizontal opened scroll painting
            // Left scroll wooden roller
            drawLine(
                color = woodBrn,
                start = Offset(center.x - 22f, center.y - 16f),
                end = Offset(center.x - 22f, center.y + 16f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            if (!showModifiedState) {
                // Original: standard fully opened scroll canvas
                // Right scroll wooden roller
                drawLine(
                    color = woodBrn,
                    start = Offset(center.x + 22f, center.y - 16f),
                    end = Offset(center.x + 22f, center.y + 16f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Paper backing
                drawRect(
                    color = paperBg,
                    topLeft = Offset(center.x - 20f, center.y - 12f),
                    size = Size(40f, 24f)
                )
                // Red seal stamp ornament (small red square)
                drawRect(
                    color = Color(0xFFB22222),
                    topLeft = Offset(center.x - 12f, center.y - 5f),
                    size = Size(6f, 6f)
                )
                // Small ink stroke lines (representing writing)
                drawLine(Color.DarkGray, Offset(center.x + 2f, center.y - 6f), Offset(center.x + 2f, center.y + 6f), 1.5f)
                drawLine(Color.DarkGray, Offset(center.x + 8f, center.y - 4f), Offset(center.x + 8f, center.y + 4f), 1.5f)
            } else {
                // Modified: Scroll is partially rolled up!
                drawRect(
                    color = paperBg,
                    topLeft = Offset(center.x - 20f, center.y - 12f),
                    size = Size(20f, 24f)
                )
                // Wooden bar shifted inward
                drawLine(
                    color = woodBrn,
                    start = Offset(center.x, center.y - 16f),
                    end = Offset(center.x, center.y + 16f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }

        DifferenceType.FLOWER_LOTUS -> {
            val petalPink = Color(0xFFFFB3C1)
            val stemGreen = Color(0xFF70E000)

            // Stem
            drawLine(
                color = stemGreen,
                start = Offset(center.x, center.y),
                end = Offset(center.x, center.y + 24f),
                strokeWidth = 2.5f
            )

            // Pink lotus petals outline
            drawCircle(color = petalPink, radius = 10f, center = center)

            // Overlapping side petals
            drawCircle(color = Color(0xFFFF85A1), radius = 6f, center = Offset(center.x - 8f, center.y - 2f))
            drawCircle(color = Color(0xFFFF85A1), radius = 6f, center = Offset(center.x + 8f, center.y - 2f))

            if (!showModifiedState) {
                // Original: Green lotus leaf is floating right next to it
                drawOval(
                    color = Color(0xFF38B000),
                    topLeft = Offset(center.x - 22f, center.y + 6f),
                    size = Size(18f, 10f)
                )
            } else {
                // Modified: Leaf is missing or turned completely dry withered yellow!
                drawOval(
                    color = Color(0xFFE9C46A),
                    topLeft = Offset(center.x - 22f, center.y + 6f),
                    size = Size(18f, 10f)
                )
            }
        }

        DifferenceType.ANCIENT_COIN -> {
            val goldBronze = Color(0xFFE9C46A)
            val darkRim = Color(0xFF4A3728)

            // Draw Chinese copper coin with round exterior
            drawCircle(color = goldBronze, radius = 18f, center = center)
            drawCircle(color = darkRim, radius = 18f, center = center, style = Stroke(width = 2f))

            if (!showModifiedState) {
                // Original: Standard classic square cut-out hole in center
                drawRect(
                    color = darkRim,
                    topLeft = Offset(center.x - 4.5f, center.y - 4.5f),
                    size = Size(9f, 9f)
                )
            } else {
                // Modified: Center hole is completely CIRCULAR or filled in!
                drawCircle(
                    color = darkRim,
                    radius = 4.5f,
                    center = center
                )
            }
        }

        DifferenceType.SPLASH_FISH -> {
            val fishRed = Color(0xFFD9381E)
            val splashColor = Color(0xFF0077B6)

            // Leap curved body shape
            val body = Path().apply {
                moveTo(center.x - 14f, center.y + 14f)
                quadraticTo(center.x + 4f, center.y - 12f, center.x + 14f, center.y - 6f)
                lineTo(center.x + 8f, center.y)
                quadraticTo(center.x, center.y + 10f, center.x - 14f, center.y + 14f)
            }
            drawPath(color = fishRed, path = body)

            // Tail fins
            val tail = Path().apply {
                moveTo(center.x - 14f, center.y + 14f)
                lineTo(center.x - 24f, center.y + 10f)
                lineTo(center.x - 20f, center.y + 20f)
                close()
            }
            drawPath(color = fishRed, path = tail)

            if (!showModifiedState) {
                // Original: Two cute splash arcs underneath
                drawArc(
                    color = splashColor,
                    startAngle = 30f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(center.x - 20f, center.y + 8f),
                    size = Size(40f, 14f),
                    style = Stroke(width = 2f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = splashColor,
                    startAngle = 15f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - 12f, center.y + 14f),
                    size = Size(24f, 10f),
                    style = Stroke(width = 1.5f)
                )
            } else {
                // Modified: Splash ripples are flat lines!
                drawLine(
                    color = splashColor,
                    start = Offset(center.x - 20f, center.y + 16f),
                    end = Offset(center.x + 20f, center.y + 16f),
                    strokeWidth = 1.5f
                )
            }
        }

        DifferenceType.CLOUDS -> {
            val cloudOutline = if (!showModifiedState) Color(0xFFE9C46A) else Color(0xFF4A4E69)

            // Drawn stylized auspicious scrolls (祥云)
            val cloudPath = Path().apply {
                moveTo(center.x - 20f, center.y + 4f)
                quadraticTo(center.x - 10f, center.y - 12f, center.x, center.y - 2f)
                quadraticTo(center.x + 12f, center.y - 14f, center.x + 22f, center.y + 2f)
                quadraticTo(center.x + 10f, center.y + 12f, center.x - 6f, center.y + 8f)
                quadraticTo(center.x - 14f, center.y + 14f, center.x - 20f, center.y + 4f)
                close()
            }
            drawPath(
                color = if (!showModifiedState) Color(0xFFFFFFFF).copy(alpha = 0.85f) else Color(0xFFDFE2DB).copy(alpha = 0.6f),
                path = cloudPath
            )
            drawPath(
                color = cloudOutline,
                path = cloudPath,
                style = Stroke(width = 2f)
            )
        }

        DifferenceType.TREE_BRANCH -> {
            val woodColor = Color(0xFF4A3728)
            val leafColor = Color(0xFF4F772D)

            // Drooping branch line
            drawLine(
                color = woodColor,
                start = Offset(center.x - 20f, center.y - 12f),
                end = Offset(center.x + 14f, center.y + 10f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            if (!showModifiedState) {
                // Original: multiple green leaf bundles
                drawCircle(color = leafColor, radius = 6f, center = Offset(center.x - 8f, center.y - 2f))
                drawCircle(color = leafColor, radius = 6f, center = Offset(center.x + 4f, center.y + 6f))
                drawCircle(color = leafColor, radius = 5f, center = Offset(center.x + 14f, center.y + 11f))
                drawCircle(color = Color(0xFF90A955), radius = 4f, center = Offset(center.x - 15f, center.y - 8f))
            } else {
                // Modified: severely pruned, missing leaves!
                drawCircle(color = leafColor, radius = 4f, center = Offset(center.x + 14f, center.y + 11f))
            }
        }

        DifferenceType.PAGODA -> {
            val silhouette = Color(0xFF2B2D42)

            // Pagoda layers base (step levels)
            val baseRect1 = Path().apply {
                moveTo(center.x - 18f, center.y + 16f)
                lineTo(center.x + 18f, center.y + 16f)
                lineTo(center.x + 12f, center.y + 4f)
                lineTo(center.x - 12f, center.y + 4f)
                close()
            }
            val baseRect2 = Path().apply {
                moveTo(center.x - 11f, center.y + 4f)
                lineTo(center.x + 11f, center.y + 4f)
                lineTo(center.x + 7f, center.y - 8f)
                lineTo(center.x - 7f, center.y - 8f)
                close()
            }
            drawPath(color = silhouette, path = baseRect1)
            drawPath(color = silhouette, path = baseRect2)

            if (!showModifiedState) {
                // Original: Pagoda tall golden spindle needle tip pointing up
                drawLine(
                    color = Color(0xFFE9C46A),
                    start = Offset(center.x, center.y - 8f),
                    end = Offset(center.x, center.y - 28f),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
                drawCircle(color = Color(0xFFE9C46A), radius = 3f, center = Offset(center.x, center.y - 22f))
            } else {
                // Modified: broken tip
                drawLine(
                    color = silhouette,
                    start = Offset(center.x, center.y - 8f),
                    end = Offset(center.x, center.y - 14f),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
            }
        }

        DifferenceType.INCENSE_BURNER -> {
            val burnerCol = Color(0xFF9B7E46)
            val outlineCol = Color(0xFF422F13)

            // Three-legged traditional incense urn container
            // Left & Right feet
            drawLine(outlineCol, Offset(center.x - 12f, center.y), Offset(center.x - 15f, center.y + 16f), 4f)
            drawLine(outlineCol, Offset(center.x + 12f, center.y), Offset(center.x + 15f, center.y + 16f), 4f)
            drawLine(outlineCol, Offset(center.x, center.y), Offset(center.x, center.y + 16f), 4f) // Center foot

            // Urn body circle
            drawCircle(color = burnerCol, radius = 16f, center = center)
            drawCircle(color = outlineCol, radius = 16f, center = center, style = Stroke(width = 2f))

            // Two handles
            drawArc(
                color = outlineCol,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - 22f, center.y - 10f),
                size = Size(10f, 20f),
                style = Stroke(width = 2.5f)
            )
            drawArc(
                color = outlineCol,
                startAngle = 270f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x + 12f, center.y - 10f),
                size = Size(10f, 20f),
                style = Stroke(width = 2.5f)
            )

            if (!showModifiedState) {
                // Original: incense fumes curling upwards
                val smokeBrush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x66CAE9FF), Color(0xAA90E0EF))
                )
                val smokePath = Path().apply {
                    moveTo(center.x, center.y - 8f)
                    cubicTo(center.x - 8f, center.y - 24f, center.x + 8f, center.y - 32f, center.x, center.y - 48f)
                }
                drawPath(path = smokePath, brush = smokeBrush, style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                // Modified: No smoke!
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
