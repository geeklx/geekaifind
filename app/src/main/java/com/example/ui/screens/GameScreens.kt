package com.example.ui.screens

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.LevelDefinition
import com.example.model.LevelDefinitions
import com.example.ui.components.SpotTheDifferenceCanvas
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGameApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Screen router
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    GameViewModel.Screen.Home -> HomeScreen(
                        onPlayClick = { viewModel.navigateTo(GameViewModel.Screen.LevelSelector) },
                        onInfoClick = { viewModel.navigateTo(GameViewModel.Screen.Info) }
                    )
                    GameViewModel.Screen.LevelSelector -> LevelSelectorScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(GameViewModel.Screen.Home) }
                    )
                    GameViewModel.Screen.GamePlay -> GamePlayScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(GameViewModel.Screen.LevelSelector) }
                    )
                    GameViewModel.Screen.Info -> InfoScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(GameViewModel.Screen.Home) }
                    )
                }
            }

            // Elegant overlay toast banner for statuses
            statusMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImperialRed.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp, start = 32.dp, end = 32.dp)
                        .animateContentSize()
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 1. HOME SCREEN - Traditional Qing splash page with custom generated scroll artwork
 */
@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_bounce")
    val bannerOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Title Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "GeekAIFind",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = ImperialRed,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "清 朝 谜 案 · 八 股 找 茬",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = CalligraphyInk.copy(alpha = 0.7f),
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .background(PalaceGold.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = "Classic",
                    tint = ImperialRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "二十关古典成语大挑战",
                    color = ImperialRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Center visual art frame with custom generated masterwork background
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1.2f)
                .offset(y = bannerOffset.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(3.dp, DarkWood, RoundedCornerShape(16.dp))
                .background(SoftParchment)
        ) {
            // Async loading of the amazing Qing Scholar design banner
            Image(
                painter = painterResource(id = com.example.R.drawable.img_home_banner_1780535994180),
                contentDescription = "Qing Dynasty imperial scroll painting",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Ink mist decorative brush gradient bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xE61E1A17))
                        )
                    )
            )

            Text(
                text = "明察秋毫 · 得意洋洋",
                color = SoftParchment,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(14.dp),
                textAlign = TextAlign.Center
            )
        }

        // Action Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(containerColor = ImperialRed),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(52.dp)
                    .border(2.dp, PalaceGold, RoundedCornerShape(24.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "开始找茬",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onInfoClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkWood),
                border = BorderStroke(1.5.dp, DarkWood),
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HelpOutline, contentDescription = "Info", tint = DarkWood)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "游戏指南",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * 2. LEVEL SELECTOR SCREEN - Lists 20 levels. Passing a level unlocks the next one.
 */
@Composable
fun LevelSelectorScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val progressList by viewModel.allProgress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(SoftParchment, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = CalligraphyInk)
            }

            Text(
                text = "衙门关卡选择",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CalligraphyInk,
                fontFamily = FontFamily.Serif
            )

            IconButton(
                onClick = { viewModel.navigateTo(GameViewModel.Screen.Info) },
                modifier = Modifier.background(SoftParchment, CircleShape)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = CalligraphyInk)
            }
        }

        HorizontalDivider(color = DarkWood.copy(alpha = 0.2f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of 20 levels
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(LevelDefinitions.levels) { level ->
                // Look up matching progress records from Room Database
                val progress = progressList.firstOrNull { it.levelId == level.id }
                val isUnlocked = progress?.unlocked ?: (level.id == 1)
                val isCompleted = progress?.completed ?: false
                val bestTime = progress?.bestTimeSeconds ?: -1

                LevelCard(
                    level = level,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    bestTime = bestTime,
                    onClick = {
                        if (isUnlocked) {
                            viewModel.selectLevel(level.id)
                        } else {
                            viewModel.triggerStatus("此关暂未解锁，需通过上一关。")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LevelCard(
    level: LevelDefinition,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    bestTime: Int,
    onClick: () -> Unit
) {
    val cardBg = if (isUnlocked) SoftParchment else Color(0xFFE5DECE)
    val cardBorderCol = if (isUnlocked) ImperialRed else Color(0x665C3D2E)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                color = cardBorderCol,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Vertical water-stamp level indicator background
            Text(
                text = "${level.id}",
                fontSize = 62.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isUnlocked) ImperialRed.copy(alpha = 0.08f) else Color.DarkGray.copy(alpha = 0.04f),
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Header level pinyin
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "第 ${level.id} 关",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isUnlocked) ImperialRed else Color.Gray
                    )

                    if (isCompleted) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Solved",
                            tint = JadeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (!isUnlocked) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Locked",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Idiom Name
                Text(
                    text = level.idiom,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) CalligraphyInk else Color.Gray,
                    fontFamily = FontFamily.Serif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = level.pinyin,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stars/Record footer
                if (isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Solved stars container
                        Row {
                            repeat(3) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "Star",
                                    tint = PalaceGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        if (bestTime > 0) {
                            Text(
                                text = "${bestTime}秒",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkWood
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (isUnlocked) "点击开始解谜" else "尚被禁闭锁",
                        fontSize = 11.sp,
                        color = if (isUnlocked) JadeGreen else Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * 3. GAME PLAY SCREEN - Dual contrast image canvas. Uses Adaptive landscape/vertical splitting.
 */
@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val level by viewModel.activeLevel.collectAsState()
    val discoveredIds by viewModel.discoveredIds.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val showDialog by viewModel.showCompletionDialog.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(SoftParchment, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit to selector", tint = CalligraphyInk)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "第 ${level.id} 关 · ${level.idiom}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalligraphyInk,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = level.pinyin,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Stats Dashboard (Timer & Solutions)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(SoftParchment, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "Timer",
                        tint = ImperialRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${timerSeconds}秒",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImperialRed
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Solved points",
                        tint = PalaceGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${discoveredIds.size}/4",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalligraphyInk
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.radialGradient(listOf(Color(0xFFFCFDFD), PaperBg)))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Adaptive design
            if (isLandscape) {
                // Wide screen side-by-side painting split
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "【原本上卷】",
                            fontSize = 12.sp,
                            color = JadeGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        SpotTheDifferenceCanvas(
                            level = level,
                            isModified = false,
                            discoveredIds = discoveredIds,
                            onTap = { x, y -> viewModel.handleTapCoordinates(x, y) }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "【改编下画 - 点击此处找不同】",
                            fontSize = 12.sp,
                            color = ImperialRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        SpotTheDifferenceCanvas(
                            level = level,
                            isModified = true,
                            discoveredIds = discoveredIds,
                            onTap = { x, y -> viewModel.handleTapCoordinates(x, y) }
                        )
                    }
                }
            } else {
                // Compact Vertical stacked layouts
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "原画 (上卷)",
                        fontSize = 11.sp,
                        color = JadeGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(JadeGreen.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )

                    SpotTheDifferenceCanvas(
                        level = level,
                        isModified = false,
                        discoveredIds = discoveredIds,
                        onTap = { x, y -> viewModel.handleTapCoordinates(x, y) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "改画 (下卷 · 点击下方寻找 4 处不同)",
                        fontSize = 11.sp,
                        color = ImperialRed,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(ImperialRed.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )

                    SpotTheDifferenceCanvas(
                        level = level,
                        isModified = true,
                        discoveredIds = discoveredIds,
                        onTap = { x, y -> viewModel.handleTapCoordinates(x, y) }
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Imperial Decree scroll level complete dialog popup !
    if (showDialog) {
        ImperialDecreeDialog(
            level = level,
            solveTime = timerSeconds,
            onNext = { viewModel.nextLevel() },
            onDismiss = { onBack() }
        )
    }
}

/**
 * High quality imperial decree dialog popup styled after vertical scrolls
 */
@Composable
fun ImperialDecreeDialog(
    level: LevelDefinition,
    solveTime: Int,
    onNext: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SoftParchment),
            border = BorderStroke(3.dp, PalaceGold),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .border(2.dp, ImperialRed, RoundedCornerShape(17.dp))
                    .padding(24.dp)
            ) {
                // Imperial double dragon seal background pattern
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRect(
                        color = ImperialRed.copy(alpha = 0.03f),
                        topLeft = Offset.Zero,
                        size = size
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imperial stamp
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Royal approved",
                        tint = ImperialRed,
                        modifier = Modifier.size(54.dp)
                    )

                    Text(
                        text = "奉天承运 · 皇帝诏曰",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImperialRed,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "通关圣谕",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalligraphyInk,
                        fontFamily = FontFamily.Serif
                    )

                    HorizontalDivider(color = ImperialRed, thickness = 1.5.dp, modifier = Modifier.fillMaxWidth(0.5f))

                    Text(
                        text = "爱卿眼力卓群，明察秋毫！仅耗时【${solveTime}秒】即成功破解《${level.idiom}》之改卷迷局，特赐通关金牌！",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = CalligraphyInk,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )

                    // Explanation block about the idiom story
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PaperBg),
                        border = BorderStroke(1.dp, CalligraphyInk.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "【成语故事】",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImperialRed,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = level.story,
                                fontSize = 12.sp,
                                color = CharcoalGrey,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }

                    Row {
                        repeat(3) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Completed Star",
                                tint = PalaceGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, CalligraphyInk),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CalligraphyInk),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "返回关卡")
                        }

                        Button(
                            onClick = onNext,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialRed),
                            modifier = Modifier
                                .weight(1.2f)
                                .border(1.dp, PalaceGold, RoundedCornerShape(20.dp))
                        ) {
                            Text(text = "启程下一关", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. INFO SCREEN - Settings, guide, reset progress
 */
@Composable
fun InfoScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    var showConfirmReset by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(SoftParchment, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back", tint = CalligraphyInk)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "衙门传记与指南",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CalligraphyInk,
                fontFamily = FontFamily.Serif
            )
        }

        HorizontalDivider(color = DarkWood.copy(alpha = 0.2f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Guide text block
        Card(
            colors = CardDefaults.cardColors(containerColor = SoftParchment),
            border = BorderStroke(1.5.dp, PalaceGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "📜 游戏规则",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ImperialRed,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "1. 下图改编自上面的原画，共包含【4 处不同】。\n\n" +
                            "2. 通过手指点击下卷上的可疑地方进行探寻。一旦敲定，会勾勒朱红印章作为喜报！\n\n" +
                            "3. 共有20道成语御关，每破开一关，即通晓该成语之清代传奇，并自动解密释放下一关！",
                    fontSize = 14.sp,
                    color = CalligraphyInk,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Clear Progress options
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperBg),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "危险区域",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ImperialRed,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "重新初始化所有关卡解锁数据，历史最佳通关时间将全部抹除。",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (!showConfirmReset) {
                    Button(
                        onClick = { showConfirmReset = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "重置游戏进度", color = Color.White)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "确定要铲除所有的功名吗？",
                            color = ImperialRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { showConfirmReset = false },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "手下留情")
                            }
                            Button(
                                onClick = {
                                    viewModel.resetGameProgress()
                                    showConfirmReset = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ImperialRed),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "确认剥夺")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Footer version info
        Text(
            text = "GeekAIFind v1.0.0\n开发组 liangxiaogeek6@gmail.com",
            color = Color.Gray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
