package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MediaPickerScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IQCutViewModel

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    HOME,
    PROJECTS,
    TEMPLATES,
    AI_TOOLS,
    SETTINGS,
    MEDIA_PICKER,
    EDITOR,
    EXPORT
}

@Composable
fun AppNavigation(
    viewModel: IQCutViewModel = viewModel()
) {
    val onboardingDone by viewModel.onboardingCompleted.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val activeClips by viewModel.activeClips.collectAsState()
    val activeAudioTracks by viewModel.activeAudioTracks.collectAsState()
    val activeTexts by viewModel.activeTexts.collectAsState()
    val activeEffects by viewModel.activeEffects.collectAsState()
    val activeStickers by viewModel.activeStickers.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val selectedTextId by viewModel.selectedTextId.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val canvasRatio by viewModel.canvasAspectRatio.collectAsState()
    val isCanvasBlur by viewModel.canvasBlurBg.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackPositionMs by viewModel.playbackPositionMs.collectAsState()
    val totalDurationMs by viewModel.totalDurationMs.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val showBottomBar = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.PROJECTS,
        AppScreen.TEMPLATES,
        AppScreen.AI_TOOLS,
        AppScreen.SETTINGS
    )

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                IQCutBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it },
                    onCreateClick = { currentScreen = AppScreen.MEDIA_PICKER }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = if (onboardingDone) AppScreen.HOME else AppScreen.ONBOARDING
                        }
                    )
                }
                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        onFinished = {
                            viewModel.completeOnboarding()
                            currentScreen = AppScreen.HOME
                        }
                    )
                }
                AppScreen.HOME -> {
                    HomeScreen(
                        projects = projects,
                        onNewProjectClick = { currentScreen = AppScreen.MEDIA_PICKER },
                        onOpenProject = { project ->
                            viewModel.loadProject(project)
                            currentScreen = AppScreen.EDITOR
                        },
                        onDeleteProject = { viewModel.deleteProject(it) },
                        onDuplicateProject = { viewModel.duplicateProject(it) },
                        onRenameProject = { id, name -> viewModel.renameProject(id, name) },
                        onNavigateToProjects = { currentScreen = AppScreen.PROJECTS },
                        onNavigateToTemplates = { currentScreen = AppScreen.TEMPLATES },
                        onNavigateToAiTools = { currentScreen = AppScreen.AI_TOOLS },
                        onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }
                AppScreen.MEDIA_PICKER -> {
                    MediaPickerScreen(
                        onNavigateBack = { currentScreen = AppScreen.HOME },
                        onCreateProject = { clips, name ->
                            viewModel.createProjectFromClips(clips, name)
                            currentScreen = AppScreen.EDITOR
                        }
                    )
                }
                AppScreen.EDITOR -> {
                    EditorScreen(
                        project = activeProject,
                        clips = activeClips,
                        audioTracks = activeAudioTracks,
                        texts = activeTexts,
                        effects = activeEffects,
                        stickers = activeStickers,
                        selectedClipId = selectedClipId,
                        selectedTextId = selectedTextId,
                        activeTab = activeTab,
                        canvasRatio = canvasRatio,
                        isCanvasBlur = isCanvasBlur,
                        isPlaying = isPlaying,
                        playbackPositionMs = playbackPositionMs,
                        totalDurationMs = totalDurationMs,
                        canUndo = canUndo,
                        canRedo = canRedo,
                        onNavigateBack = {
                            viewModel.saveActiveProject()
                            currentScreen = AppScreen.HOME
                        },
                        onNavigateToExport = {
                            currentScreen = AppScreen.EXPORT
                        },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSeek = { viewModel.seekTo(it) },
                        onSelectClip = { viewModel.selectClip(it) },
                        onSetActiveTab = { viewModel.setActiveTab(it) },
                        onUndo = { viewModel.undo() },
                        onRedo = { viewModel.redo() },
                        onSplitClip = { viewModel.splitCurrentClip() },
                        onDeleteClip = { viewModel.deleteSelectedClip() },
                        onDuplicateClip = { viewModel.duplicateSelectedClip() },
                        onSpeedChange = { viewModel.setClipSpeed(it) },
                        onVolumeChange = { viewModel.setClipVolume(it) },
                        onRotateClip = { viewModel.rotateSelectedClip() },
                        onApplyFilter = { name, intensity -> viewModel.setClipFilter(name, intensity) },
                        onApplyTransition = { type, dur -> viewModel.setClipTransition(type, dur) },
                        onAddAudio = { viewModel.addAudioTrack(it) },
                        onDeleteAudio = { viewModel.deleteAudioTrack(it) },
                        onAudioVolumeChange = { id, vol -> viewModel.updateAudioVolume(id, vol) },
                        onAddText = { text, color -> viewModel.addTextOverlay(text, color) },
                        onUpdateText = { viewModel.updateTextOverlay(it) },
                        onDeleteText = { viewModel.deleteTextOverlay(it) },
                        onAddEffect = { type, name -> viewModel.addEffectLayer(type, name) },
                        onDeleteEffect = { viewModel.deleteEffectLayer(it) },
                        onAddSticker = { emoji, name -> viewModel.addSticker(emoji, name) },
                        onDeleteSticker = { viewModel.deleteSticker(it) },
                        onRatioChange = { viewModel.setCanvasRatio(it) },
                        onToggleCanvasBlur = { viewModel.toggleCanvasBlur() }
                    )
                }
                AppScreen.PROJECTS -> {
                    ProjectsScreen(
                        projects = projects,
                        onOpenProject = { project ->
                            viewModel.loadProject(project)
                            currentScreen = AppScreen.EDITOR
                        },
                        onNewProject = { currentScreen = AppScreen.MEDIA_PICKER },
                        onDeleteProject = { viewModel.deleteProject(it) },
                        onDuplicateProject = { viewModel.duplicateProject(it) },
                        onRenameProject = { id, name -> viewModel.renameProject(id, name) },
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
                AppScreen.TEMPLATES -> {
                    TemplatesScreen(
                        onSelectTemplate = { template ->
                            viewModel.loadTemplate(template)
                            currentScreen = AppScreen.EDITOR
                        },
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
                AppScreen.AI_TOOLS -> {
                    AiToolsScreen(
                        aiState = aiState,
                        hasActiveProject = activeProject != null,
                        onNavigateBack = { currentScreen = AppScreen.HOME },
                        onOpenEditor = { currentScreen = AppScreen.EDITOR },
                        onRunCaptions = { viewModel.runAiCaptions() },
                        onRunBgRemoval = { viewModel.runAiBackgroundRemoval() },
                        onRunEnhance = { viewModel.runSmartEnhance() },
                        onRunSilenceDetection = { viewModel.runSilenceDetection() }
                    )
                }
                AppScreen.EXPORT -> {
                    ExportScreen(
                        project = activeProject,
                        exportState = exportState,
                        onStartExport = { res, fps, q -> viewModel.startExport(res, fps, q) },
                        onResetExport = { viewModel.resetExportState() },
                        onNavigateBack = { currentScreen = AppScreen.EDITOR },
                        onNavigateHome = { currentScreen = AppScreen.HOME },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }
                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        onNavigateBack = { currentScreen = AppScreen.HOME },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun IQCutBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onCreateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(68.dp)
            .background(DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME) },
                testTag = "nav_home"
            )

            // Projects
            BottomNavItem(
                icon = Icons.Default.Folder,
                label = "Projects",
                isSelected = currentScreen == AppScreen.PROJECTS,
                onClick = { onNavigate(AppScreen.PROJECTS) },
                testTag = "nav_projects"
            )

            // Center Floating Create Button
            Box(
                modifier = Modifier
                    .offset(y = (-14).dp)
                    .size(54.dp)
                    .shadow(12.dp, CircleShape, spotColor = ElectricPurple)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(ElectricPurple, NeonCyan))
                    )
                    .clickable(onClick = onCreateClick)
                    .testTag("nav_center_create"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Project",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Templates
            BottomNavItem(
                icon = Icons.Default.GridView,
                label = "Templates",
                isSelected = currentScreen == AppScreen.TEMPLATES,
                onClick = { onNavigate(AppScreen.TEMPLATES) },
                testTag = "nav_templates"
            )

            // AI Tools
            BottomNavItem(
                icon = Icons.Default.AutoAwesome,
                label = "AI Studio",
                isSelected = currentScreen == AppScreen.AI_TOOLS,
                onClick = { onNavigate(AppScreen.AI_TOOLS) },
                testTag = "nav_ai_studio"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NeonCyan else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NeonCyan else TextSecondary
        )
    }
}
