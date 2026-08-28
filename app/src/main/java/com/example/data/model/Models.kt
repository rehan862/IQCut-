package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "New Video Project",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val durationMs: Long = 15000L,
    val aspectRatio: String = "9:16",
    val resolution: String = "1080p",
    val fps: Int = 30,
    val thumbnailUri: String = "",
    val clipsJson: String = "[]",
    val audioTracksJson: String = "[]",
    val textOverlaysJson: String = "[]",
    val effectLayersJson: String = "[]",
    val stickersJson: String = "[]",
    val canvasBgColorHex: String = "#0B0B10",
    val canvasBlurBg: Boolean = true
)

data class ClipItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Clip",
    val mediaUri: String = "",
    val drawableRes: Int = 0,
    val durationMs: Long = 5000L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 5000L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val rotation: Int = 0,
    val isImage: Boolean = false,
    val filterPreset: String = "None",
    val filterIntensity: Float = 0.8f,
    val transitionType: String = "None",
    val transitionDurationMs: Long = 500L
) {
    val effectiveDurationMs: Long
        get() = ((trimEndMs - trimStartMs).coerceAtLeast(500L) / speed.coerceAtLeast(0.1f)).toLong()
}

data class AudioTrack(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Background Music",
    val artist: String = "IQCut Sound Lab",
    val audioUri: String = "",
    val durationMs: Long = 15000L,
    val startOffsetMs: Long = 0L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 15000L,
    val volume: Float = 0.8f,
    val fadeInMs: Long = 500L,
    val fadeOutMs: Long = 1000L,
    val isVoiceOver: Boolean = false
)

data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "Add Title",
    val startMs: Long = 0L,
    val endMs: Long = 3000L,
    val posX: Float = 0.0f, // -1.0 (left) to 1.0 (right), 0 is center
    val posY: Float = 0.0f, // -1.0 (top) to 1.0 (bottom), 0 is center
    val fontSize: Int = 24,
    val colorHex: String = "#FFFFFF",
    val bgColorHex: String = "#00000000",
    val strokeColorHex: String = "#6C3BFF",
    val strokeWidth: Float = 0.0f,
    val glowColorHex: String = "#00D9FF",
    val hasGlow: Boolean = true,
    val fontFamilyType: String = "Bold Sans",
    val animationType: String = "Fade"
)

data class EffectLayer(
    val id: String = UUID.randomUUID().toString(),
    val effectType: String = "Glitch",
    val name: String = "Cyber Glitch",
    val startMs: Long = 0L,
    val endMs: Long = 3000L,
    val intensity: Float = 0.7f
)

data class StickerItem(
    val id: String = UUID.randomUUID().toString(),
    val emojiOrIcon: String = "✨",
    val name: String = "Sparkles",
    val startMs: Long = 0L,
    val endMs: Long = 4000L,
    val posX: Float = 0.5f,
    val posY: Float = -0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f
)

data class VideoTemplate(
    val id: String,
    val title: String,
    val category: String,
    val durationMs: Long,
    val aspectRatio: String,
    val thumbnailRes: Int,
    val musicTitle: String,
    val description: String,
    val tags: List<String>,
    val clips: List<ClipItem>,
    val texts: List<TextOverlay> = emptyList(),
    val effects: List<EffectLayer> = emptyList()
)

data class AiCaptionItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val speaker: String = "Speaker 1"
)

enum class CanvasAspectRatio(val label: String, val ratio: Float, val iconLabel: String) {
    RATIO_9_16("9:16", 9f / 16f, "TikTok / Reels"),
    RATIO_16_9("16:9", 16f / 9f, "YouTube"),
    RATIO_1_1("1:1", 1f, "Instagram"),
    RATIO_4_5("4:5", 4f / 5f, "Feed"),
    RATIO_ORIGINAL("Original", 9f / 16f, "Full Frame")
}
