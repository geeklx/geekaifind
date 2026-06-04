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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import kotlin.math.cos
import kotlin.math.sin

// Traditional Chinese Calligraphy and Painting Mineral Pigments
val TraditionalInkBlack = Color(0xD51D242B)     // 黛黑 / 深冷水墨
val TraditionalVermillion = Color(0xD5BC3F2F)    // 朱砂 / 宫廷红
val TraditionalGold = Color(0xB8B9914A)          // 鎏金 / 暗金色
val TraditionalJade = Color(0xD53A6849)          // 石绿 / 苍秀青松竹翠
val TraditionalIndigo = Color(0xD5244E68)        // 靛青 / 青花瓷
val TraditionalOchre = Color(0xD5825330)         // 赭石 / 古木枝干矿色
val TraditionalWhite = Color(0xDCE8DEC7)         // 宣纸折纸白 / 米黄
val TraditionalInkHalo = Color(0x1C181D24)       // 水墨在宣纸上的渗湿晕染光圈

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

        val bgImageRes = remember(level.backgroundType) {
            when (level.backgroundType) {
                BackgroundStyle.INK_WASH_GOLD -> com.example.R.drawable.img_bg_ink_wash_gold_1780555350219
                BackgroundStyle.MISTY_JADE -> com.example.R.drawable.img_bg_misty_jade_1780555369325
                BackgroundStyle.MIDNIGHT_INDIGO -> com.example.R.drawable.img_bg_midnight_indigo_1780555386334
                BackgroundStyle.IMPERIAL_RED -> com.example.R.drawable.img_bg_imperial_red_1780555403738
                BackgroundStyle.SCHOLAR_BROWN -> com.example.R.drawable.img_bg_scholar_brown_1780555421689
            }
        }

        Image(
            painter = painterResource(id = bgImageRes),
            contentDescription = "Ancient Chinese scroll painting background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
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
 * Draws the specific spot-the-difference element procedurally and cleanly with traditional brushwork rules!
 */
private fun DrawScope.drawDifferenceItem(
    type: DifferenceType,
    center: Offset,
    isModifiedImage: Boolean,
    isSolved: Boolean,
    drawScope: DrawScope
) {
    val showModifiedState = isModifiedImage && !isSolved

    // Render a subtle wet-ink bleeding wash background (晕染效果)
    // This creates an organic shadow underneath each element so it blends perfectly into the antique scroll 
    drawCircle(
        color = TraditionalInkHalo,
        radius = 32f,
        center = center
    )
    drawCircle(
        color = TraditionalInkHalo.copy(alpha = TraditionalInkHalo.alpha * 0.4f),
        radius = 42f,
        center = center
    )

    when (type) {
        DifferenceType.QING_OFFICIAL_HAT -> {
            // Hat base cap (黛黑与朱砂红色)
            drawArc(TraditionalInkBlack, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(center.x - 24f, center.y - 12f), size = Size(48f, 24f))
            drawOval(TraditionalInkBlack, topLeft = Offset(center.x - 28f, center.y + 6f), size = Size(56f, 10f))
            drawArc(TraditionalVermillion, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(center.x - 12f, center.y - 16f), size = Size(24f, 12f))
            
            // Gold bead at center (鎏金珠顶)
            drawCircle(color = TraditionalGold, radius = 5.5f, center = Offset(center.x, center.y - 17f))

            if (!showModifiedState) {
                // Original: Peacock Feather Plume dangles to the right (双眼花翎)
                drawLine(TraditionalJade, Offset(center.x, center.y - 15f), Offset(center.x + 28f, center.y - 6f), strokeWidth = 3.5f, cap = StrokeCap.Round)
                drawCircle(TraditionalGold, radius = 4.5f, center = Offset(center.x + 28f, center.y - 6f))
                drawCircle(TraditionalIndigo, radius = 2.5f, center = Offset(center.x + 28f, center.y - 6f))
            } else {
                // Modified: Peacock Feather is shortened/missing dangles (缺顶戴花翎)
                drawLine(TraditionalOchre, Offset(center.x, center.y - 15f), Offset(center.x + 8f, center.y - 14f), strokeWidth = 3.5f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.PALACE_LANTERN -> {
            // Lantern rope & support structures in Ochre brown 
            drawLine(TraditionalOchre, Offset(center.x, center.y - 30f), Offset(center.x, center.y - 16f), strokeWidth = 2.5f)
            
            if (!showModifiedState) {
                // Soft warm glow aura for the lit palace lantern (红灯火光阴影)
                drawCircle(TraditionalGold.copy(alpha = 0.35f), radius = 28f, center = center)
            } else {
                // Dim unlit mask (未点亮红灯)
                drawCircle(TraditionalInkBlack.copy(alpha = 0.25f), radius = 22f, center = center)
            }

            // Traditional hexagonal palace lantern body
            val bodyPath = Path().apply {
                moveTo(center.x - 8f, center.y - 16f)
                lineTo(center.x + 8f, center.y - 16f)
                lineTo(center.x + 16f, center.y)
                lineTo(center.x + 8f, center.y + 16f)
                lineTo(center.x - 8f, center.y + 16f)
                lineTo(center.x - 16f, center.y)
                close()
            }
            drawPath(color = if (!showModifiedState) TraditionalVermillion else TraditionalVermillion.copy(alpha = 0.5f), path = bodyPath)
            drawPath(color = TraditionalInkBlack, path = bodyPath, style = Stroke(width = 1.5f))

            drawLine(TraditionalInkBlack, Offset(center.x - 10f, center.y - 16f), Offset(center.x + 10f, center.y - 16f), strokeWidth = 3f)
            drawLine(TraditionalInkBlack, Offset(center.x - 10f, center.y + 16f), Offset(center.x + 10f, center.y + 16f), strokeWidth = 3f)

            if (!showModifiedState) {
                // Golden tassel (流苏穗子)
                drawLine(TraditionalGold, Offset(center.x, center.y + 16f), Offset(center.x, center.y + 32f), strokeWidth = 2.5f)
                drawCircle(color = TraditionalVermillion, radius = 4f, center = Offset(center.x, center.y + 32f))
            }
        }

        DifferenceType.CLASSIC_FAN -> {
            // Silk fan outline in White & Gold (鎏金团扇)
            drawCircle(TraditionalWhite, radius = 22f, center = center)
            drawCircle(TraditionalGold, radius = 22f, center = center, style = Stroke(width = 1.8f))
            drawLine(TraditionalOchre, Offset(center.x, center.y + 22f), Offset(center.x, center.y + 42f), strokeWidth = 3.5f, cap = StrokeCap.Round)

            if (!showModifiedState) {
                // Original: Beautiful ink painted plum flower bough (水墨梅花)
                drawLine(TraditionalInkBlack, Offset(center.x - 12f, center.y + 6f), Offset(center.x + 10f, center.y - 6f), strokeWidth = 2f)
                drawCircle(TraditionalVermillion, radius = 4f, center = Offset(center.x + 2f, center.y - 2f))
                drawCircle(TraditionalVermillion, radius = 3f, center = Offset(center.x - 8f, center.y + 3f))
            }
        }

        DifferenceType.BRONZE_BELL -> {
            val bellPath = Path().apply {
                moveTo(center.x - 14f, center.y + 12f)
                cubicTo(center.x - 14f, center.y - 16f, center.x + 14f, center.y - 16f, center.x + 14f, center.y + 12f)
                close()
            }
            drawPath(color = TraditionalOchre, path = bellPath)
            drawPath(color = TraditionalInkBlack, path = bellPath, style = Stroke(width = 2f))
            drawArc(TraditionalInkBlack, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(center.x - 6f, center.y - 20f), size = Size(12f, 12f), style = Stroke(width = 2.5f))

            if (!showModifiedState) {
                // Bell clapper (铜钟摆锤)
                drawCircle(TraditionalVermillion, radius = 4.5f, center = Offset(center.x, center.y + 16f))
                drawLine(TraditionalInkBlack, Offset(center.x, center.y + 6f), Offset(center.x, center.y + 14f), strokeWidth = 2.5f)
            }
        }

        DifferenceType.INK_BUTTERFLY -> {
            val wingColor = if (!showModifiedState) TraditionalVermillion.copy(alpha = 0.85f) else TraditionalIndigo.copy(alpha = 0.85f)
            // Left wings path using double brush wash arcs
            val leftWing = Path().apply {
                moveTo(center.x, center.y)
                cubicTo(center.x - 16f, center.y - 16f, center.x - 20f, center.y, center.x - 8f, center.y + 4f)
                close()
            }
            drawPath(color = wingColor, path = leftWing)
            drawPath(color = TraditionalInkBlack, path = leftWing, style = Stroke(width = 1.5f))
            
            // Right wings path
            val rightWing = Path().apply {
                moveTo(center.x, center.y)
                cubicTo(center.x + 16f, center.y - 16f, center.x + 20f, center.y, center.x + 8f, center.y + 4f)
                close()
            }
            drawPath(color = wingColor, path = rightWing)
            drawPath(color = TraditionalInkBlack, path = rightWing, style = Stroke(width = 1.5f))

            // Body and antennae
            drawLine(TraditionalInkBlack, Offset(center.x, center.y - 10f), Offset(center.x, center.y + 8f), strokeWidth = 3.5f, cap = StrokeCap.Round)
            drawLine(TraditionalInkBlack, Offset(center.x, center.y - 10f), Offset(center.x - 6f, center.y - 16f), strokeWidth = 1f)
            drawLine(TraditionalInkBlack, Offset(center.x, center.y - 10f), Offset(center.x + 6f, center.y - 16f), strokeWidth = 1f)
        }

        DifferenceType.SOARING_CRANE -> {
            // Elegant flying immortal white crane (仙鹤高飞)
            drawLine(TraditionalWhite, Offset(center.x - 24f, center.y + 6f), Offset(center.x + 24f, center.y - 8f), strokeWidth = 4f, cap = StrokeCap.Round)

            val leftWingDetail = Path().apply {
                moveTo(center.x - 4f, center.y - 2f)
                quadraticTo(center.x - 16f, center.y - 26f, center.x - 12f, if (!showModifiedState) center.y - 32f else center.y - 12f)
            }
            drawPath(color = TraditionalWhite, path = leftWingDetail, style = Stroke(width = 4f, cap = StrokeCap.Round))
            
            // Wingtip ink trim
            drawLine(TraditionalInkBlack, Offset(center.x - 24f, center.y + 6f), Offset(center.x - 14f, center.y + 4f), strokeWidth = 3f)

            if (!showModifiedState) {
                // Vermillion crown on top (丹顶鹤朱砂顶点缀)
                drawCircle(color = TraditionalVermillion, radius = 3.5f, center = Offset(center.x + 18f, center.y - 6f))
            } else {
                // Normal plain crown head
                drawCircle(color = TraditionalInkBlack, radius = 2.5f, center = Offset(center.x + 18f, center.y - 6f))
            }
        }

        DifferenceType.TEA_CUP -> {
            // Blue-and-white porcelain tea cup (青花瓷盖碗)
            val bowlPath = Path().apply {
                moveTo(center.x - 14f, center.y - 10f)
                lineTo(center.x + 14f, center.y - 10f)
                quadraticTo(center.x + 12f, center.y + 8f, center.x, center.y + 12f)
                quadraticTo(center.x - 12f, center.y + 8f, center.x - 14f, center.y - 10f)
                close()
            }
            drawPath(color = TraditionalWhite, path = bowlPath)
            drawPath(color = TraditionalIndigo, path = bowlPath, style = Stroke(width = 2f))
            drawLine(TraditionalIndigo, Offset(center.x - 6f, center.y + 12f), Offset(center.x + 6f, center.y + 12f), strokeWidth = 3f, cap = StrokeCap.Round)

            if (!showModifiedState) {
                // Original: Splied curl of hot aromatic steam (香气飘走)
                drawLine(TraditionalGold.copy(alpha = 0.6f), Offset(center.x, center.y - 12f), Offset(center.x - 3f, center.y - 22f), strokeWidth = 2f, cap = StrokeCap.Round)
                drawLine(TraditionalGold.copy(alpha = 0.6f), Offset(center.x + 4f, center.y - 12f), Offset(center.x + 2f, center.y - 24f), strokeWidth = 2f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.SCROLL_BOOK -> {
            // Jade/wood scroll rollers (画轴卷轴)
            drawLine(TraditionalOchre, Offset(center.x - 22f, center.y - 16f), Offset(center.x - 22f, center.y + 16f), strokeWidth = 4.5f, cap = StrokeCap.Round)

            if (!showModifiedState) {
                drawLine(TraditionalOchre, Offset(center.x + 22f, center.y - 16f), Offset(center.x + 22f, center.y + 16f), strokeWidth = 4.5f, cap = StrokeCap.Round)
                drawRect(color = TraditionalWhite, topLeft = Offset(center.x - 20f, center.y - 12f), size = Size(40f, 24f))
                
                // Traditional Vermillion study ink seal stamp (朱砂小方印)
                drawRect(color = TraditionalVermillion, topLeft = Offset(center.x - 10f, center.y - 5f), size = Size(7f, 7f))
            } else {
                drawRect(color = TraditionalWhite, topLeft = Offset(center.x - 20f, center.y - 12f), size = Size(20f, 24f))
                drawLine(TraditionalOchre, Offset(center.x, center.y - 16f), Offset(center.x, center.y + 16f), strokeWidth = 4.5f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.FLOWER_LOTUS -> {
            // Elegant lotus stalks (荷花并蒂)
            drawLine(color = TraditionalJade, start = Offset(center.x, center.y), end = Offset(center.x, center.y + 24f), strokeWidth = 3f)
            
            // Soft red lotus petals (朱砂红晕染荷花)
            drawCircle(color = TraditionalVermillion.copy(alpha = 0.5f), radius = 10f, center = center)
            drawCircle(color = TraditionalVermillion.copy(alpha = 0.7f), radius = 6f, center = Offset(center.x - 8f, center.y - 2f))
            drawCircle(color = TraditionalVermillion.copy(alpha = 0.7f), radius = 6f, center = Offset(center.x + 8f, center.y - 2f))

            if (!showModifiedState) {
                // Rich green lotus pad (石绿荷叶)
                drawOval(color = TraditionalJade, topLeft = Offset(center.x - 22f, center.y + 6f), size = Size(18f, 10f))
            } else {
                // Withered yellowing lotus leaf (落叶悲秋)
                drawOval(color = TraditionalOchre, topLeft = Offset(center.x - 22f, center.y + 6f), size = Size(16f, 8f))
            }
        }

        DifferenceType.ANCIENT_COIN -> {
            // Antique copper coin (开元通宝/乾隆通宝古钱币)
            drawCircle(color = TraditionalGold, radius = 18f, center = center)
            drawCircle(color = TraditionalInkBlack, radius = 18f, center = center, style = Stroke(width = 1.8f))

            if (!showModifiedState) {
                // Square center bore lineart (方孔)
                drawRect(color = TraditionalInkBlack, topLeft = Offset(center.x - 5f, center.y - 5f), size = Size(10f, 10f))
            } else {
                // Round center bore variant (圆孔)
                drawCircle(color = TraditionalInkBlack, radius = 5f, center = center)
            }
        }

        DifferenceType.SPLASH_FISH -> {
            // Red Vermillion Carp (朱砂锦鲤)
            val fishRed = TraditionalVermillion
            val body = Path().apply {
                moveTo(center.x - 14f, center.y + 14f)
                quadraticTo(center.x + 4f, center.y - 12f, center.x + 14f, center.y - 6f)
                lineTo(center.x + 8f, center.y)
                quadraticTo(center.x, center.y + 10f, center.x - 14f, center.y + 14f)
            }
            drawPath(color = fishRed, path = body)

            // Carp ink fins
            drawLine(TraditionalInkBlack, Offset(center.x + 8f, center.y - 3f), Offset(center.x + 16f, center.y - 14f), strokeWidth = 1.5f)

            if (!showModifiedState) {
                // Beautiful water dynamic ripples (水花跃动)
                drawArc(color = TraditionalIndigo, startAngle = 30f, sweepAngle = 120f, useCenter = false, topLeft = Offset(center.x - 16f, center.y + 8f), size = Size(32f, 12f), style = Stroke(width = 2f, cap = StrokeCap.Round))
            } else {
                // Still water surface lines
                drawLine(color = TraditionalIndigo, start = Offset(center.x - 14f, center.y + 12f), end = Offset(center.x + 14f, center.y + 12f), strokeWidth = 1.5f)
            }
        }

        DifferenceType.CLOUDS -> {
            // Traditional Double-hook cloud outline (祥云纹双钩)
            val cloudOutline = if (!showModifiedState) TraditionalGold else TraditionalInkBlack
            val cloudPath = Path().apply {
                moveTo(center.x - 20f, center.y + 4f)
                quadraticTo(center.x - 10f, center.y - 12f, center.x, center.y - 2f)
                quadraticTo(center.x + 12f, center.y - 14f, center.x + 22f, center.y + 2f)
                quadraticTo(center.x + 10f, center.y + 12f, center.x - 6f, center.y + 8f)
                quadraticTo(center.x - 14f, center.y + 14f, center.x - 20f, center.y + 4f)
                close()
            }
            drawPath(color = if (!showModifiedState) TraditionalWhite.copy(alpha = 0.95f) else TraditionalWhite.copy(alpha = 0.6f), path = cloudPath)
            drawPath(color = cloudOutline, path = cloudPath, style = Stroke(width = 2f))
        }

        DifferenceType.TREE_BRANCH -> {
            // Brush-textured Pine/Willow branch (古松虬枝)
            drawLine(TraditionalOchre, Offset(center.x - 20f, center.y - 12f), Offset(center.x + 14f, center.y + 10f), strokeWidth = 3.5f, cap = StrokeCap.Round)
            if (!showModifiedState) {
                // Rich pine needle clusters in Jade green (绿松针)
                drawCircle(TraditionalJade, radius = 6f, center = Offset(center.x - 8f, center.y - 2f))
                drawCircle(TraditionalJade, radius = 6f, center = Offset(center.x + 4f, center.y + 6f))
                drawCircle(TraditionalInkBlack, radius = 2.5f, center = Offset(center.x - 8f, center.y - 2f))
            } else {
                // Bare twigs layout
                drawCircle(TraditionalJade, radius = 4f, center = Offset(center.x + 4f, center.y + 6f))
            }
        }

        DifferenceType.PAGODA -> {
            // Distant mountain pagoda tower (远山佛塔)
            val silhouette = TraditionalInkBlack
            val baseRect1 = Path().apply {
                moveTo(center.x - 15f, center.y + 12f)
                lineTo(center.x + 15f, center.y + 12f)
                lineTo(center.x + 10f, center.y + 2f)
                lineTo(center.x - 10f, center.y + 2f)
                close()
            }
            drawPath(color = silhouette, path = baseRect1)

            if (!showModifiedState) {
                // Golden top tower spire (鎏金水瓶顶)
                drawLine(TraditionalGold, Offset(center.x, center.y + 2f), Offset(center.x, center.y - 16f), strokeWidth = 3f, cap = StrokeCap.Round)
            } else {
                // Short stone spire (常石顶)
                drawLine(silhouette, Offset(center.x, center.y + 2f), Offset(center.x, center.y - 8f), strokeWidth = 3f, cap = StrokeCap.Round)
            }
        }

        DifferenceType.INCENSE_BURNER -> {
            // Antique Bronze incense burner (宣德炉香)
            val burnerCol = TraditionalOchre
            val outlineCol = TraditionalInkBlack
            drawLine(outlineCol, Offset(center.x - 10f, center.y), Offset(center.x - 12f, center.y + 14f), 3.5f)
            drawLine(outlineCol, Offset(center.x + 10f, center.y), Offset(center.x + 12f, center.y + 14f), 3.5f)
            drawCircle(color = burnerCol, radius = 14f, center = center)
            drawCircle(color = outlineCol, radius = 14f, center = center, style = Stroke(width = 1.8f))

            if (!showModifiedState) {
                // Soft elegant trailing coils of smoke (香炉袅袅轻烟)
                val smokePath = Path().apply {
                    moveTo(center.x, center.y - 8f)
                    cubicTo(center.x - 6f, center.y - 18f, center.x + 6f, center.y - 24f, center.x, center.y - 36f)
                }
                drawPath(path = smokePath, color = TraditionalWhite.copy(alpha = 0.6f), style = Stroke(width = 2.2f, cap = StrokeCap.Round))
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
    // Elegant mineral pigment seal stamp ring showing discovered coordinates (朱砂御印)
    drawScope.drawCircle(
        color = TraditionalVermillion,
        radius = 32f,
        center = offset,
        style = Stroke(
            width = 3.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f, 8f, 10f), 0f)
        )
    )

    // Vermillion central cross mark representing historic court verification
    drawScope.drawCircle(
        color = TraditionalVermillion.copy(alpha = 0.15f),
        radius = 29f,
        center = offset
    )
}
