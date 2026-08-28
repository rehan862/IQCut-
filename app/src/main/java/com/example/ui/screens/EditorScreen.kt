package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AudioTrack
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.ClipItem
import com.example.data.model.EffectLayer
import com.example.data.model.ProjectEntity
import com.example.data.model.StickerItem
import com.example.data.model.TextOverlay
import com.example.ui.components.formatTimecode
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ElectricPurpleDark
import com.example.ui.theme.ElectricPurpleLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.EditorTab

@Composable
fun EditorScreen(
    project: ProjectEntity?,
    clips: List<ClipItem>,
    audioTracks: List<AudioTrack>,
    texts: List<TextOverlay>,
    effects: List<EffectLayer>,
    stickers: List<StickerItem>,
    selectedClipId: String?,
    selectedTextId: String?,
    activeTab: EditorTab,
    canvasRatio: CanvasAspectRatio,
    isCanvasBlur: Boolean,
    isPlaying: Boolean,
    playbackPositionMs: Long,
    totalDurationMs: Long,
    canUndo: Boolean,
    canRedo: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToExport: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSelectClip: (String) -> Unit,
    onSetActiveTab: (EditorTab) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSplitClip: () -> Unit,
    onDeleteClip: () -> Unit,
    onDuplicateClip: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onRotateClip: () -> Unit,
    onApplyFilter: (String, Float) -> Unit,
    onApplyTransition: (String, Long) -> Unit,
    onAddAudio: (AudioTrack) -> Unit,
    onDeleteAudio: (String) -> Unit,
    onAudioVolumeChange: (String, Float) -> Unit,
    onAddText: (String, String) -> Unit,
    onUpdateText: (TextOverlay) -> Unit,
    onDeleteText: (String) -> Unit,
    onAddEffect: (String, String) -> Unit,
    onDeleteEffect: (String) -> Unit,
    onAddSticker: (String, String) -> Unit,
    onDeleteSticker: (String) -> Unit,
    onRatioChange: (CanvasAspectRatio) -> Unit,
    onToggleCanvasBlur: () -> Unit
) {
    var isFullscreenPreview by remember { mutableStateOf(false) }

    // Identify active clip at current playback position
    var accumulatedTime = 0L
    var activeClip = clips.firstOrNull()
    for (c in clips) {
        val clipDur = c.effectiveDurationMs
        if (playbackPositionMs >= accumulatedTime && playbackPositionMs <= accumulatedTime + clipDur) {
            activeClip = c
            break
        }
        accumulatedTime += clipDur
    }

    val selectedClip = clips.find { it.id == selectedClipId } ?: activeClip

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. TOP TOOLBAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("editor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = project?.name ?: "Video Editor",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )

                // Undo / Redo buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onUndo,
                        enabled = canUndo,
                        modifier = Modifier.testTag("editor_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) Color.White else TextMuted
                        )
                    }

                    IconButton(
                        onClick = onRedo,
                        enabled = canRedo,
                        modifier = Modifier.testTag("editor_redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) Color.White else TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Export Button
                    Box(
                        modifier = Modifier
                            .testTag("editor_export_button")
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(listOf(ElectricPurple, NeonCyan))
                            )
                            .clickable(onClick = onNavigateToExport)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.IosShare,
                                contentDescription = "Export",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Export",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 2. VIDEO PREVIEW CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
                    .background(Color(0xFF07070A))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background blur simulation
                if (isCanvasBlur && activeClip != null) {
                    Image(
                        painter = painterResource(id = activeClip.drawableRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(24.dp)
                            .alpha(0.35f),
                        contentScale = ContentScale.Crop
                    )
                }

                // Main Aspect Ratio Framed Container
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(canvasRatio.ratio)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (activeClip != null) {
                        // Apply Color Matrix Filter
                        val colorFilter = remember(activeClip.filterPreset, activeClip.filterIntensity) {
                            createFilterColorMatrix(activeClip.filterPreset, activeClip.filterIntensity)
                        }

                        Image(
                            painter = painterResource(id = activeClip.drawableRes),
                            contentDescription = activeClip.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(activeClip.rotation.toFloat()),
                            contentScale = ContentScale.Crop,
                            colorFilter = colorFilter
                        )

                        // Live VFX Overlays
                        effects.forEach { effect ->
                            if (playbackPositionMs in effect.startMs..effect.endMs) {
                                LiveEffectOverlay(effect = effect)
                            }
                        }

                        // Live Text Overlays
                        texts.forEach { overlay ->
                            if (playbackPositionMs in overlay.startMs..overlay.endMs) {
                                LiveTextOverlayItem(overlay = overlay)
                            }
                        }

                        // Live Stickers
                        stickers.forEach { s ->
                            if (playbackPositionMs in s.startMs..s.endMs) {
                                Text(
                                    text = s.emojiOrIcon,
                                    fontSize = (32 * s.scale).sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset {
                                            IntOffset(
                                                (s.posX * 120).toInt(),
                                                (s.posY * 120).toInt()
                                            )
                                        }
                                )
                            }
                        }
                    } else {
                        Text("No Media", color = TextMuted)
                    }

                    // Fullscreen Toggle in Preview Corner
                    IconButton(
                        onClick = { isFullscreenPreview = !isFullscreenPreview },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 3. PLAYBACK CONTROLS BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timecode
                Text(
                    text = "${formatTimecode(playbackPositionMs)} / ${formatTimecode(totalDurationMs, false)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )

                // Play / Pause Button Center
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { onSeek((playbackPositionMs - 1000L).coerceAtLeast(0L)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "-1s",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .testTag("editor_play_pause_button")
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(listOf(ElectricPurple, NeonCyan))
                            )
                            .clickable(onClick = onTogglePlayPause),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onSplitClip,
                        modifier = Modifier
                            .testTag("editor_quick_split_button")
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Split",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Aspect Ratio indicator
                Text(
                    text = canvasRatio.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurface)
                        .clickable { onSetActiveTab(EditorTab.CANVAS) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // 4. MULTI-TRACK TIMELINE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(DarkBackground)
                    .padding(vertical = 4.dp)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val timelineWidthPx = constraints.maxWidth.toFloat()
                    val totalMs = totalDurationMs.coerceAtLeast(1000L)
                    val playheadRatio = (playbackPositionMs.toFloat() / totalMs).coerceIn(0f, 1f)
                    val playheadOffset = timelineWidthPx * playheadRatio

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        // Time ruler marks
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        ) {
                            val stepPx = size.width / 6f
                            for (i in 0..6) {
                                val x = i * stepPx
                                drawLine(
                                    color = DarkSurfaceBorder,
                                    start = Offset(x, 0f),
                                    end = Offset(x, 8f),
                                    strokeWidth = 1.5f
                                )
                            }
                        }

                        // Track 1: Video Clips Track
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            clips.forEach { clip ->
                                val weightRatio = (clip.effectiveDurationMs.toFloat() / totalMs).coerceAtLeast(0.05f)
                                val isSelected = clip.id == selectedClipId

                                Box(
                                    modifier = Modifier
                                        .weight(weightRatio)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) ElectricPurpleDark else DarkSurface)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.5.dp,
                                            color = if (isSelected) NeonCyan else DarkSurfaceBorder,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onSelectClip(clip.id) }
                                ) {
                                    Image(
                                        painter = painterResource(id = clip.drawableRes),
                                        contentDescription = clip.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Transition badge if set
                                    if (clip.transitionType != "None") {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(NeonCyan),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Shuffle,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }

                                    // Name & Speed label
                                    Text(
                                        text = "${clip.name} (${clip.speed}x)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(2.dp)
                                            .background(Color.Black.copy(alpha = 0.6f))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // Track 2: Audio Waveform Track
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF101C24))
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = audioTracks.firstOrNull()?.title ?: "Background Audio Track",
                                fontSize = 9.sp,
                                color = NeonCyanLight,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Track 3: Text / Overlays Bar
                        if (texts.isNotEmpty() || effects.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1C1329))
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = ElectricPurpleLight,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${texts.size} Text Overlays • ${effects.size} VFX",
                                    fontSize = 8.sp,
                                    color = ElectricPurpleLight
                                )
                            }
                        }
                    }

                    // Interactive Scrub Area & Glowing Playhead Line
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(totalMs) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                                        onSeek((newProgress * totalMs).toLong())
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                                        onSeek((newProgress * totalMs).toLong())
                                    }
                                )
                            }
                    ) {
                        // Glowing Playhead Needle
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(playheadOffset.toInt(), 0) }
                                .fillMaxHeight()
                                .width(2.5.dp)
                                .background(NeonCyan)
                                .shadow(8.dp, spotColor = NeonCyan)
                        )

                        // Playhead Top Handle
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(playheadOffset.toInt() - 14, 0) }
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                    }
                }
            }

            // 5. EDITING TOOLBAR TABS
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = EditorTab.values()
                items(tabs) { tab ->
                    val isSelected = activeTab == tab
                    val icon = when (tab) {
                        EditorTab.EDIT -> Icons.Default.ContentCut
                        EditorTab.AUDIO -> Icons.Default.MusicNote
                        EditorTab.TEXT -> Icons.Default.TextFields
                        EditorTab.FILTERS -> Icons.Default.AutoAwesome
                        EditorTab.EFFECTS -> Icons.Default.GraphicEq
                        EditorTab.TRANSITIONS -> Icons.Default.Shuffle
                        EditorTab.STICKERS -> Icons.Default.Mood
                        EditorTab.CANVAS -> Icons.Default.AspectRatio
                    }

                    Row(
                        modifier = Modifier
                            .testTag("tab_${tab.name.lowercase()}")
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElectricPurple else Color.Transparent)
                            .clickable { onSetActiveTab(tab) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            // 6. CONTEXT TOOL PANEL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .background(DarkSurface)
            ) {
                when (activeTab) {
                    EditorTab.EDIT -> {
                        EditToolPanel(
                            selectedClip = selectedClip,
                            onSplit = onSplitClip,
                            onDelete = onDeleteClip,
                            onDuplicate = onDuplicateClip,
                            onSpeedChange = onSpeedChange,
                            onVolumeChange = onVolumeChange,
                            onRotate = onRotateClip
                        )
                    }
                    EditorTab.AUDIO -> {
                        AudioToolPanel(
                            audioTracks = audioTracks,
                            onAddTrack = onAddAudio,
                            onDeleteTrack = onDeleteAudio,
                            onVolumeChange = onAudioVolumeChange
                        )
                    }
                    EditorTab.TEXT -> {
                        TextToolPanel(
                            texts = texts,
                            selectedTextId = selectedTextId,
                            onAddText = onAddText,
                            onUpdateText = onUpdateText,
                            onDeleteText = onDeleteText
                        )
                    }
                    EditorTab.FILTERS -> {
                        FiltersToolPanel(
                            selectedClip = selectedClip,
                            onApplyFilter = onApplyFilter
                        )
                    }
                    EditorTab.EFFECTS -> {
                        EffectsToolPanel(
                            effects = effects,
                            onAddEffect = onAddEffect,
                            onDeleteEffect = onDeleteEffect
                        )
                    }
                    EditorTab.TRANSITIONS -> {
                        TransitionsToolPanel(
                            selectedClip = selectedClip,
                            onApplyTransition = onApplyTransition
                        )
                    }
                    EditorTab.STICKERS -> {
                        StickersToolPanel(
                            stickers = stickers,
                            onAddSticker = onAddSticker,
                            onDeleteSticker = onDeleteSticker
                        )
                    }
                    EditorTab.CANVAS -> {
                        CanvasToolPanel(
                            currentRatio = canvasRatio,
                            isBlurBg = isCanvasBlur,
                            onRatioChange = onRatioChange,
                            onToggleBlur = onToggleCanvasBlur
                        )
                    }
                }
            }
        }
    }
}

// Live Text Overlay Item rendering on canvas
@Composable
fun LiveTextOverlayItem(overlay: TextOverlay) {
    val color = remember(overlay.colorHex) { parseHexColor(overlay.colorHex) }
    val strokeColor = remember(overlay.strokeColorHex) { parseHexColor(overlay.strokeColorHex) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = overlay.text,
            fontSize = overlay.fontSize.sp,
            fontWeight = FontWeight.Black,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset {
                    IntOffset(
                        (overlay.posX * 150).toInt(),
                        (overlay.posY * 220).toInt()
                    )
                }
                .shadow(if (overlay.hasGlow) 10.dp else 0.dp, spotColor = NeonCyan)
                .background(
                    if (overlay.bgColorHex != "#00000000") parseHexColor(overlay.bgColorHex) else Color.Transparent,
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Live VFX Overlays
@Composable
fun LiveEffectOverlay(effect: EffectLayer) {
    when (effect.effectType) {
        "Glitch" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                NeonCyan.copy(alpha = 0.25f),
                                Color.Transparent,
                                ElectricPurple.copy(alpha = 0.25f)
                            )
                        )
                    )
            )
        }
        "FilmGrain" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.08f))
            )
        }
        "LightLeak" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFF9500).copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = 400f
                        )
                    )
            )
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NeonCyan.copy(alpha = 0.12f))
            )
        }
    }
}

// Helper to construct ColorMatrix based on filter preset
fun createFilterColorMatrix(preset: String, intensity: Float): ColorFilter? {
    if (preset == "None") return null

    val matrix = when (preset) {
        "Cinematic" -> floatArrayOf(
            1.1f, 0.0f, 0.0f, 0.0f, 10f * intensity,
            0.0f, 1.0f, 0.0f, 0.0f, 0f,
            0.0f, 0.0f, 1.2f, 0.0f, 20f * intensity,
            0.0f, 0.0f, 0.0f, 1.0f, 0f
        )
        "Warm" -> floatArrayOf(
            1.2f, 0.0f, 0.0f, 0.0f, 25f * intensity,
            0.0f, 1.05f, 0.0f, 0.0f, 10f * intensity,
            0.0f, 0.0f, 0.85f, 0.0f, -10f * intensity,
            0.0f, 0.0f, 0.0f, 1.0f, 0f
        )
        "Cool" -> floatArrayOf(
            0.85f, 0.0f, 0.0f, 0.0f, -10f * intensity,
            0.0f, 1.05f, 0.0f, 0.0f, 5f * intensity,
            0.0f, 0.0f, 1.3f, 0.0f, 30f * intensity,
            0.0f, 0.0f, 0.0f, 1.0f, 0f
        )
        "Mono" -> {
            val lumR = 0.2126f
            val lumG = 0.7152f
            val lumB = 0.0722f
            floatArrayOf(
                lumR, lumG, lumB, 0.0f, 0f,
                lumR, lumG, lumB, 0.0f, 0f,
                lumR, lumG, lumB, 0.0f, 0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0f
            )
        }
        "Vibrant" -> floatArrayOf(
            1.2f, 0.0f, 0.0f, 0.0f, 0f,
            0.0f, 1.2f, 0.0f, 0.0f, 0f,
            0.0f, 0.0f, 1.2f, 0.0f, 0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0f
        )
        else -> null
    }

    return matrix?.let { ColorFilter.colorMatrix(ColorMatrix(it)) }
}
