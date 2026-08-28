package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.ClipItem
import com.example.data.model.EffectLayer
import com.example.data.model.StickerItem
import com.example.data.model.TextOverlay
import com.example.data.sample.SampleData
import com.example.ui.components.formatDurationReadable
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceContainerHigh
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ElectricPurpleDark
import com.example.ui.theme.ElectricPurpleLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

// 1. EDIT TOOL PANEL
@Composable
fun EditToolPanel(
    selectedClip: ClipItem?,
    onSplit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onRotate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedSlider by remember { mutableStateOf(false) }
    var showVolumeSlider by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (selectedClip != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clip: ${selectedClip.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "Duration: ${formatDurationReadable(selectedClip.effectiveDurationMs)} (${selectedClip.speed}x)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            // Quick Action Buttons
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    EditorActionButton(
                        icon = Icons.Default.ContentCut,
                        label = "Split",
                        onClick = onSplit,
                        testTag = "panel_action_split"
                    )
                }
                item {
                    EditorActionButton(
                        icon = Icons.Default.Speed,
                        label = "Speed",
                        isActive = showSpeedSlider,
                        onClick = {
                            showSpeedSlider = !showSpeedSlider
                            showVolumeSlider = false
                        },
                        testTag = "panel_action_speed"
                    )
                }
                item {
                    EditorActionButton(
                        icon = Icons.Default.VolumeUp,
                        label = "Volume",
                        isActive = showVolumeSlider,
                        onClick = {
                            showVolumeSlider = !showVolumeSlider
                            showSpeedSlider = false
                        },
                        testTag = "panel_action_volume"
                    )
                }
                item {
                    EditorActionButton(
                        icon = Icons.Default.RotateRight,
                        label = "Rotate",
                        onClick = onRotate,
                        testTag = "panel_action_rotate"
                    )
                }
                item {
                    EditorActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "Duplicate",
                        onClick = onDuplicate,
                        testTag = "panel_action_duplicate"
                    )
                }
                item {
                    EditorActionButton(
                        icon = Icons.Default.Delete,
                        label = "Delete",
                        iconColor = Color(0xFFFF453A),
                        onClick = onDelete,
                        testTag = "panel_action_delete"
                    )
                }
            }

            // Speed presets
            AnimatedVisibility(visible = showSpeedSlider) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Clip Speed Ramping: ${selectedClip.speed}x",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    val speedPresets = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        speedPresets.forEach { sp ->
                            val isSelected = selectedClip.speed == sp
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan else DarkSurfaceContainerHigh)
                                    .clickable { onSpeedChange(sp) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${sp}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Volume Slider
            AnimatedVisibility(visible = showVolumeSlider) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Clip Audio Volume: ${(selectedClip.volume * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Slider(
                        value = selectedClip.volume,
                        onValueChange = { onVolumeChange(it) },
                        valueRange = 0f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = DarkSurfaceBorder
                        )
                    )
                }
            }
        } else {
            Text(
                text = "Select a clip on the timeline to edit split, speed, volume, and cuts.",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// 2. AUDIO TOOL PANEL
@Composable
fun AudioToolPanel(
    audioTracks: List<AudioTrack>,
    onAddTrack: (AudioTrack) -> Unit,
    onDeleteTrack: (String) -> Unit,
    onVolumeChange: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRecordingVoiceOver by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Audio & Music Library",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Voice Over Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isRecordingVoiceOver) Color(0xFFFF3B30) else DarkSurfaceVariant)
                    .clickable {
                        isRecordingVoiceOver = !isRecordingVoiceOver
                        if (isRecordingVoiceOver) {
                            onAddTrack(
                                AudioTrack(
                                    id = UUID.randomUUID().toString(),
                                    title = "Voice-over Record #${(1..99).random()}",
                                    artist = "Microphone",
                                    durationMs = 6000L,
                                    volume = 1.0f,
                                    isVoiceOver = true
                                )
                            )
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isRecordingVoiceOver) "Recording..." else "Record Voice",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Curated Soundtrack items
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(SampleData.sampleAudioTracks) { track ->
                val isAdded = audioTracks.any { it.title == track.title }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAdded) ElectricPurpleDark else DarkSurfaceVariant)
                        .border(1.dp, if (isAdded) NeonCyan else DarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            if (!isAdded) {
                                onAddTrack(track.copy(id = UUID.randomUUID().toString()))
                            }
                        }
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.width(130.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = track.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "${track.artist} • ${formatDurationReadable(track.durationMs)}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Active Audio Tracks List with Volume control
        if (audioTracks.isNotEmpty()) {
            Text(
                text = "Active Tracks on Project:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            audioTracks.forEach { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = track.title,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onDeleteTrack(track.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color(0xFFFF453A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// 3. TEXT TOOL PANEL
@Composable
fun TextToolPanel(
    texts: List<TextOverlay>,
    selectedTextId: String?,
    onAddText: (String, String) -> Unit,
    onUpdateText: (TextOverlay) -> Unit,
    onDeleteText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newTextInput by remember { mutableStateOf("New Text Overlay") }
    var selectedColor by remember { mutableStateOf("#00D9FF") }
    val colors = listOf("#FFFFFF", "#00D9FF", "#6C3BFF", "#FFE500", "#FF2E93", "#00E676", "#FF9500", "#000000")
    val fontStyles = listOf("Bold Sans", "Display Neon", "Serif Elegant", "Cyberpunk", "Handwriting")

    val selectedOverlay = texts.find { it.id == selectedTextId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Text & Titles",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Button(
                onClick = { onAddText(newTextInput, selectedColor) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Text", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Text input field
        OutlinedTextField(
            value = if (selectedOverlay != null) selectedOverlay.text else newTextInput,
            onValueChange = { input ->
                if (selectedOverlay != null) {
                    onUpdateText(selectedOverlay.copy(text = input))
                } else {
                    newTextInput = input
                }
            },
            label = { Text("Text content", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Color Palette
        Text(text = "Text Color:", fontSize = 11.sp, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(colors) { hex ->
                val color = parseHexColor(hex)
                val isSelected = (selectedOverlay?.colorHex ?: selectedColor) == hex
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) NeonCyan else DarkSurfaceBorder,
                            shape = CircleShape
                        )
                        .clickable {
                            selectedColor = hex
                            if (selectedOverlay != null) {
                                onUpdateText(selectedOverlay.copy(colorHex = hex))
                            }
                        }
                )
            }
        }

        // Font selection
        Text(text = "Font Style:", fontSize = 11.sp, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(fontStyles) { font ->
                val isSelected = selectedOverlay?.fontFamilyType == font
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                        .clickable {
                            if (selectedOverlay != null) {
                                onUpdateText(selectedOverlay.copy(fontFamilyType = font))
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = font,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else TextPrimary
                    )
                }
            }
        }
    }
}

// 4. FILTERS TOOL PANEL
@Composable
fun FiltersToolPanel(
    selectedClip: ClipItem?,
    onApplyFilter: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        Pair("None", "Original"),
        Pair("Cinematic", "Teal & Orange"),
        Pair("Warm", "Sunset Glow"),
        Pair("Cool", "Cyber Chill"),
        Pair("Vintage", "90s Nostalgia"),
        Pair("Mono", "Noir Black"),
        Pair("Vibrant", "Pop Vibrant"),
        Pair("Golden", "Golden Hour")
    )

    var intensity by remember { mutableFloatStateOf(selectedClip?.filterIntensity ?: 0.85f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Original Filter Presets",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filters) { (filterKey, filterDesc) ->
                val isSelected = selectedClip?.filterPreset == filterKey
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ElectricPurpleDark else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) NeonCyan else DarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            onApplyFilter(filterKey, intensity)
                        }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = filterKey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonCyan else TextPrimary
                        )
                        Text(
                            text = filterDesc,
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        if (selectedClip != null && selectedClip.filterPreset != "None") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Intensity: ${(intensity * 100).toInt()}%", fontSize = 11.sp, color = TextSecondary)
            }
            Slider(
                value = intensity,
                onValueChange = {
                    intensity = it
                    onApplyFilter(selectedClip.filterPreset, it)
                },
                valueRange = 0f..1.0f,
                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
            )
        }
    }
}

// 5. EFFECTS TOOL PANEL
@Composable
fun EffectsToolPanel(
    effects: List<EffectLayer>,
    onAddEffect: (String, String) -> Unit,
    onDeleteEffect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val effectList = listOf(
        Pair("Glitch", "Cyber Glitch"),
        Pair("RGBShift", "Chromatic Aberration"),
        Pair("Shake", "Camera Shake"),
        Pair("FilmGrain", "35mm Film Grain"),
        Pair("LightLeak", "Anamorphic Leak"),
        Pair("Blur", "Radial Soft Blur")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Visual VFX Overlays",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(effectList) { (type, name) ->
                val isAdded = effects.any { it.effectType == type }
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAdded) ElectricPurpleDark else DarkSurfaceVariant)
                        .border(1.dp, if (isAdded) NeonCyan else DarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            if (!isAdded) onAddEffect(type, name)
                        }
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Text(text = type, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text(text = name, fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }
        }

        if (effects.isNotEmpty()) {
            Text(text = "Active Effects on Project:", fontSize = 11.sp, color = TextSecondary)
            effects.forEach { effect ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = effect.name, fontSize = 12.sp, color = TextPrimary)
                    IconButton(onClick = { onDeleteEffect(effect.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF453A), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 6. TRANSITIONS TOOL PANEL
@Composable
fun TransitionsToolPanel(
    selectedClip: ClipItem?,
    onApplyTransition: (String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val transitions = listOf("None", "Fade", "Slide", "Zoom", "Blur", "Iris")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Between-Clips Transition",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(transitions) { trans ->
                val isSelected = selectedClip?.transitionType == trans
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                        .clickable { onApplyTransition(trans, 500L) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = trans,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else TextPrimary
                    )
                }
            }
        }
    }
}

// 7. STICKERS TOOL PANEL
@Composable
fun StickersToolPanel(
    stickers: List<StickerItem>,
    onAddSticker: (String, String) -> Unit,
    onDeleteSticker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stickerList = listOf(
        Pair("✨", "Sparkles"),
        Pair("🔥", "Fire"),
        Pair("💜", "Neon Heart"),
        Pair("👍", "Like"),
        Pair("⭐", "Star"),
        Pair("🚀", "Rocket"),
        Pair("💯", "100"),
        Pair("🎬", "Cinema")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Creative Stickers & Overlays",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(stickerList) { (emoji, name) ->
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .clickable { onAddSticker(emoji, name) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }
        }

        if (stickers.isNotEmpty()) {
            Text(text = "Active Stickers:", fontSize = 11.sp, color = TextSecondary)
            stickers.forEach { s ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${s.emojiOrIcon} ${s.name}", fontSize = 12.sp, color = TextPrimary)
                    IconButton(onClick = { onDeleteSticker(s.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF453A), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 8. CANVAS TOOL PANEL
@Composable
fun CanvasToolPanel(
    currentRatio: CanvasAspectRatio,
    isBlurBg: Boolean,
    onRatioChange: (CanvasAspectRatio) -> Unit,
    onToggleBlur: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ratios = CanvasAspectRatio.values()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Canvas Aspect Ratio",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ratios) { ratio ->
                val isSelected = currentRatio == ratio
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ElectricPurpleDark else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) NeonCyan else DarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .clickable { onRatioChange(ratio) }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = ratio.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonCyan else TextPrimary
                        )
                        Text(
                            text = ratio.iconLabel,
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Blurred Video Background", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "Fills canvas edges with ambient blur", fontSize = 10.sp, color = TextSecondary)
            }
            Switch(
                checked = isBlurBg,
                onCheckedChange = { onToggleBlur() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonCyan,
                    uncheckedTrackColor = DarkSurfaceBorder
                )
            )
        }
    }
}

@Composable
fun EditorActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    iconColor: Color = Color.White,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isActive) ElectricPurple else DarkSurfaceVariant)
                .border(1.dp, if (isActive) NeonCyan else DarkSurfaceBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isActive) NeonCyan else TextSecondary,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        Color.White
    }
}
