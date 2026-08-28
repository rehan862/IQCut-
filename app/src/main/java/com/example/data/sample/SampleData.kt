package com.example.data.sample

import com.example.R
import com.example.data.model.AudioTrack
import com.example.data.model.ClipItem
import com.example.data.model.EffectLayer
import com.example.data.model.ProjectEntity
import com.example.data.model.StickerItem
import com.example.data.model.TextOverlay
import com.example.data.model.VideoTemplate
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object SampleData {

    val sampleMediaLibrary = listOf(
        ClipItem(
            id = "media_travel_1",
            name = "Mountain Drone 4K",
            drawableRes = R.drawable.thumb_travel_1787899372157,
            durationMs = 8000L,
            trimStartMs = 0L,
            trimEndMs = 8000L,
            speed = 1.0f,
            filterPreset = "Cinematic"
        ),
        ClipItem(
            id = "media_cyber_1",
            name = "Cyberpunk City Nights",
            drawableRes = R.drawable.thumb_cyberpunk_1787899390969,
            durationMs = 6500L,
            trimStartMs = 0L,
            trimEndMs = 6500L,
            speed = 1.0f,
            filterPreset = "Cool"
        ),
        ClipItem(
            id = "media_sunset_1",
            name = "Golden Coast Sunset",
            drawableRes = R.drawable.thumb_sunset_1787899404052,
            durationMs = 7000L,
            trimStartMs = 0L,
            trimEndMs = 7000L,
            speed = 1.0f,
            filterPreset = "Warm"
        ),
        ClipItem(
            id = "media_edit_1",
            name = "Creative Studio Session",
            drawableRes = R.drawable.iqcut_onboard_edit_1787899332060,
            durationMs = 5000L,
            trimStartMs = 0L,
            trimEndMs = 5000L,
            speed = 1.0f,
            filterPreset = "Vibrant"
        ),
        ClipItem(
            id = "media_audio_1",
            name = "Soundwaves Visualizer",
            drawableRes = R.drawable.iqcut_onboard_music_1787899346962,
            durationMs = 6000L,
            trimStartMs = 0L,
            trimEndMs = 6000L,
            speed = 1.0f,
            filterPreset = "Vintage"
        )
    )

    val sampleAudioTracks = listOf(
        AudioTrack(
            id = "audio_1",
            title = "Midnight Horizon",
            artist = "Neon Dreams",
            durationMs = 18000L,
            volume = 0.85f
        ),
        AudioTrack(
            id = "audio_2",
            title = "Cyber Pulse 2088",
            artist = "RetroSynth",
            durationMs = 24000L,
            volume = 0.9f
        ),
        AudioTrack(
            id = "audio_3",
            title = "Lo-Fi Coffee Sunset",
            artist = "Acoustic Vibes",
            durationMs = 30000L,
            volume = 0.75f
        ),
        AudioTrack(
            id = "audio_4",
            title = "Epic Cinematic Rise",
            artist = "Orchestral Lab",
            durationMs = 20000L,
            volume = 0.8f
        ),
        AudioTrack(
            id = "audio_5",
            title = "Upbeat Summer Vlog",
            artist = "Feel Good Club",
            durationMs = 15000L,
            volume = 0.8f
        )
    )

    val sampleTemplates = listOf(
        VideoTemplate(
            id = "tpl_1",
            title = "Cinematic Travel Reel",
            category = "Trending",
            durationMs = 15000L,
            aspectRatio = "9:16",
            thumbnailRes = R.drawable.thumb_travel_1787899372157,
            musicTitle = "Midnight Horizon",
            description = "High-energy fast cuts with cinematic color grading and smooth zoom transitions.",
            tags = listOf("#Travel", "#Reels", "#4K", "#Trending"),
            clips = listOf(
                sampleMediaLibrary[0].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 5000),
                sampleMediaLibrary[2].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 5000, transitionType = "Zoom"),
                sampleMediaLibrary[1].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 5000, transitionType = "Fade")
            ),
            texts = listOf(
                TextOverlay(text = "EXPLORE THE UNKNOWN", startMs = 500, endMs = 4500, posY = -0.6f, colorHex = "#00D9FF"),
                TextOverlay(text = "Summer 2026", startMs = 5200, endMs = 9500, posY = 0.6f, colorHex = "#FFFFFF")
            ),
            effects = listOf(
                EffectLayer(effectType = "FilmGrain", name = "35mm Grain", startMs = 0, endMs = 15000, intensity = 0.4f)
            )
        ),
        VideoTemplate(
            id = "tpl_2",
            title = "Cyberpunk Beat Sync",
            category = "Gaming",
            durationMs = 12000L,
            aspectRatio = "9:16",
            thumbnailRes = R.drawable.thumb_cyberpunk_1787899390969,
            musicTitle = "Cyber Pulse 2088",
            description = "Neon glow styling, glitch flashes, RGB split effects synced to bass drops.",
            tags = listOf("#Gaming", "#Neon", "#Cyberpunk", "#BeatSync"),
            clips = listOf(
                sampleMediaLibrary[1].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 6000),
                sampleMediaLibrary[0].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 6000, transitionType = "Slide")
            ),
            texts = listOf(
                TextOverlay(text = "LEVEL UP", startMs = 0, endMs = 3000, posY = 0.0f, fontSize = 32, colorHex = "#00D9FF", strokeColorHex = "#6C3BFF", strokeWidth = 2.0f)
            ),
            effects = listOf(
                EffectLayer(effectType = "Glitch", name = "Cyber Glitch", startMs = 1000, endMs = 4000, intensity = 0.8f),
                EffectLayer(effectType = "RGBShift", name = "RGB Split", startMs = 6000, endMs = 9000, intensity = 0.7f)
            )
        ),
        VideoTemplate(
            id = "tpl_3",
            title = "Golden Sunset Vlog",
            category = "Vlog",
            durationMs = 14000L,
            aspectRatio = "9:16",
            thumbnailRes = R.drawable.thumb_sunset_1787899404052,
            musicTitle = "Lo-Fi Coffee Sunset",
            description = "Warm golden tones, soft light leaks, and minimalist subtitle captions.",
            tags = listOf("#Vlog", "#Sunset", "#Aesthetic", "#LoFi"),
            clips = listOf(
                sampleMediaLibrary[2].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 7000),
                sampleMediaLibrary[3].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 7000, transitionType = "Blur")
            ),
            texts = listOf(
                TextOverlay(text = "Golden Hour Memories", startMs = 1000, endMs = 6000, posY = 0.5f, colorHex = "#FFE5A3")
            ),
            effects = listOf(
                EffectLayer(effectType = "LightLeak", name = "Warm Flare", startMs = 0, endMs = 7000, intensity = 0.6f)
            )
        ),
        VideoTemplate(
            id = "tpl_4",
            title = "Pro Creator Showcase",
            category = "Business",
            durationMs = 16000L,
            aspectRatio = "16:9",
            thumbnailRes = R.drawable.iqcut_onboard_edit_1787899332060,
            musicTitle = "Epic Cinematic Rise",
            description = "Clean presentation template with lower-third title cards and subtle motion blur.",
            tags = listOf("#Business", "#Showcase", "#YouTube", "#16x9"),
            clips = listOf(
                sampleMediaLibrary[3].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 8000),
                sampleMediaLibrary[1].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 8000, transitionType = "Fade")
            ),
            texts = listOf(
                TextOverlay(text = "IQCut Studio Edition", startMs = 0, endMs = 4000, posY = -0.5f, colorHex = "#FFFFFF")
            )
        )
    )

    fun createInitialProjects(): List<ProjectEntity> {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        val p1Clips = listOf(
            sampleMediaLibrary[0].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 6500),
            sampleMediaLibrary[2].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 5500, transitionType = "Fade")
        )
        val p1Texts = listOf(
            TextOverlay(id = UUID.randomUUID().toString(), text = "SUMMER TRIP", startMs = 500, endMs = 4000, posY = -0.5f, colorHex = "#00D9FF")
        )

        val p2Clips = listOf(
            sampleMediaLibrary[0].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 8000)
        )
        val p2Texts = listOf(
            TextOverlay(id = UUID.randomUUID().toString(), text = "PEAK HORIZON", startMs = 1000, endMs = 5000, posY = 0.4f, colorHex = "#FFFFFF")
        )

        val p3Clips = listOf(
            sampleMediaLibrary[2].copy(id = UUID.randomUUID().toString(), trimStartMs = 0, trimEndMs = 7000)
        )

        return listOf(
            ProjectEntity(
                id = "proj_travel_vlog",
                name = "Travel Vlog",
                createdAt = now - (oneDay * 3),
                lastModified = now - 3600000L,
                durationMs = 12000L,
                aspectRatio = "9:16",
                resolution = "1080p",
                fps = 60,
                thumbnailUri = "res://${R.drawable.thumb_travel_1787899372157}",
                clipsJson = serializeClips(p1Clips),
                audioTracksJson = serializeAudioTracks(listOf(sampleAudioTracks[0])),
                textOverlaysJson = serializeTexts(p1Texts),
                effectLayersJson = serializeEffects(listOf(EffectLayer(effectType = "FilmGrain", name = "35mm Grain", startMs = 0, endMs = 12000, intensity = 0.35f))),
                stickersJson = "[]"
            ),
            ProjectEntity(
                id = "proj_mountains",
                name = "Mountains",
                createdAt = now - (oneDay * 4),
                lastModified = now - (oneDay * 1),
                durationMs = 8000L,
                aspectRatio = "9:16",
                resolution = "4K",
                fps = 60,
                thumbnailUri = "res://${R.drawable.thumb_travel_1787899372157}",
                clipsJson = serializeClips(p2Clips),
                audioTracksJson = serializeAudioTracks(listOf(sampleAudioTracks[3])),
                textOverlaysJson = serializeTexts(p2Texts),
                effectLayersJson = "[]",
                stickersJson = "[]"
            ),
            ProjectEntity(
                id = "proj_sunset_edit",
                name = "Sunset Edit",
                createdAt = now - (oneDay * 5),
                lastModified = now - (oneDay * 2),
                durationMs = 7000L,
                aspectRatio = "9:16",
                resolution = "1080p",
                fps = 30,
                thumbnailUri = "res://${R.drawable.thumb_sunset_1787899404052}",
                clipsJson = serializeClips(p3Clips),
                audioTracksJson = serializeAudioTracks(listOf(sampleAudioTracks[2])),
                textOverlaysJson = "[]",
                effectLayersJson = serializeEffects(listOf(EffectLayer(effectType = "LightLeak", name = "Warm Leak", startMs = 0, endMs = 7000, intensity = 0.5f))),
                stickersJson = "[]"
            )
        )
    }

    // JSON serialization helpers
    fun serializeClips(clips: List<ClipItem>): String {
        val array = JSONArray()
        clips.forEach { clip ->
            val obj = JSONObject().apply {
                put("id", clip.id)
                put("name", clip.name)
                put("mediaUri", clip.mediaUri)
                put("drawableRes", clip.drawableRes)
                put("durationMs", clip.durationMs)
                put("trimStartMs", clip.trimStartMs)
                put("trimEndMs", clip.trimEndMs)
                put("speed", clip.speed.toDouble())
                put("volume", clip.volume.toDouble())
                put("rotation", clip.rotation)
                put("isImage", clip.isImage)
                put("filterPreset", clip.filterPreset)
                put("filterIntensity", clip.filterIntensity.toDouble())
                put("transitionType", clip.transitionType)
                put("transitionDurationMs", clip.transitionDurationMs)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeClips(json: String): List<ClipItem> {
        if (json.isBlank() || json == "[]") return emptyList()
        val list = mutableListOf<ClipItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ClipItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        name = obj.optString("name", "Clip"),
                        mediaUri = obj.optString("mediaUri", ""),
                        drawableRes = obj.optInt("drawableRes", R.drawable.thumb_travel_1787899372157),
                        durationMs = obj.optLong("durationMs", 5000L),
                        trimStartMs = obj.optLong("trimStartMs", 0L),
                        trimEndMs = obj.optLong("trimEndMs", 5000L),
                        speed = obj.optDouble("speed", 1.0).toFloat(),
                        volume = obj.optDouble("volume", 1.0).toFloat(),
                        rotation = obj.optInt("rotation", 0),
                        isImage = obj.optBoolean("isImage", false),
                        filterPreset = obj.optString("filterPreset", "None"),
                        filterIntensity = obj.optDouble("filterIntensity", 0.8).toFloat(),
                        transitionType = obj.optString("transitionType", "None"),
                        transitionDurationMs = obj.optLong("transitionDurationMs", 500L)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeAudioTracks(tracks: List<AudioTrack>): String {
        val array = JSONArray()
        tracks.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("artist", t.artist)
                put("audioUri", t.audioUri)
                put("durationMs", t.durationMs)
                put("startOffsetMs", t.startOffsetMs)
                put("trimStartMs", t.trimStartMs)
                put("trimEndMs", t.trimEndMs)
                put("volume", t.volume.toDouble())
                put("fadeInMs", t.fadeInMs)
                put("fadeOutMs", t.fadeOutMs)
                put("isVoiceOver", t.isVoiceOver)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeAudioTracks(json: String): List<AudioTrack> {
        if (json.isBlank() || json == "[]") return emptyList()
        val list = mutableListOf<AudioTrack>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AudioTrack(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        title = obj.optString("title", "Track"),
                        artist = obj.optString("artist", "IQCut"),
                        audioUri = obj.optString("audioUri", ""),
                        durationMs = obj.optLong("durationMs", 15000L),
                        startOffsetMs = obj.optLong("startOffsetMs", 0L),
                        trimStartMs = obj.optLong("trimStartMs", 0L),
                        trimEndMs = obj.optLong("trimEndMs", 15000L),
                        volume = obj.optDouble("volume", 0.8).toFloat(),
                        fadeInMs = obj.optLong("fadeInMs", 500L),
                        fadeOutMs = obj.optLong("fadeOutMs", 1000L),
                        isVoiceOver = obj.optBoolean("isVoiceOver", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeTexts(texts: List<TextOverlay>): String {
        val array = JSONArray()
        texts.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("text", t.text)
                put("startMs", t.startMs)
                put("endMs", t.endMs)
                put("posX", t.posX.toDouble())
                put("posY", t.posY.toDouble())
                put("fontSize", t.fontSize)
                put("colorHex", t.colorHex)
                put("bgColorHex", t.bgColorHex)
                put("strokeColorHex", t.strokeColorHex)
                put("strokeWidth", t.strokeWidth.toDouble())
                put("glowColorHex", t.glowColorHex)
                put("hasGlow", t.hasGlow)
                put("fontFamilyType", t.fontFamilyType)
                put("animationType", t.animationType)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeTexts(json: String): List<TextOverlay> {
        if (json.isBlank() || json == "[]") return emptyList()
        val list = mutableListOf<TextOverlay>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TextOverlay(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        text = obj.optString("text", "Text"),
                        startMs = obj.optLong("startMs", 0L),
                        endMs = obj.optLong("endMs", 3000L),
                        posX = obj.optDouble("posX", 0.0).toFloat(),
                        posY = obj.optDouble("posY", 0.0).toFloat(),
                        fontSize = obj.optInt("fontSize", 24),
                        colorHex = obj.optString("colorHex", "#FFFFFF"),
                        bgColorHex = obj.optString("bgColorHex", "#00000000"),
                        strokeColorHex = obj.optString("strokeColorHex", "#6C3BFF"),
                        strokeWidth = obj.optDouble("strokeWidth", 0.0).toFloat(),
                        glowColorHex = obj.optString("glowColorHex", "#00D9FF"),
                        hasGlow = obj.optBoolean("hasGlow", true),
                        fontFamilyType = obj.optString("fontFamilyType", "Bold Sans"),
                        animationType = obj.optString("animationType", "Fade")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeEffects(effects: List<EffectLayer>): String {
        val array = JSONArray()
        effects.forEach { e ->
            val obj = JSONObject().apply {
                put("id", e.id)
                put("effectType", e.effectType)
                put("name", e.name)
                put("startMs", e.startMs)
                put("endMs", e.endMs)
                put("intensity", e.intensity.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeEffects(json: String): List<EffectLayer> {
        if (json.isBlank() || json == "[]") return emptyList()
        val list = mutableListOf<EffectLayer>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    EffectLayer(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        effectType = obj.optString("effectType", "Glitch"),
                        name = obj.optString("name", "Effect"),
                        startMs = obj.optLong("startMs", 0L),
                        endMs = obj.optLong("endMs", 3000L),
                        intensity = obj.optDouble("intensity", 0.7).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeStickers(stickers: List<StickerItem>): String {
        val array = JSONArray()
        stickers.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("emojiOrIcon", s.emojiOrIcon)
                put("name", s.name)
                put("startMs", s.startMs)
                put("endMs", s.endMs)
                put("posX", s.posX.toDouble())
                put("posY", s.posY.toDouble())
                put("scale", s.scale.toDouble())
                put("rotation", s.rotation.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeStickers(json: String): List<StickerItem> {
        if (json.isBlank() || json == "[]") return emptyList()
        val list = mutableListOf<StickerItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    StickerItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        emojiOrIcon = obj.optString("emojiOrIcon", "✨"),
                        name = obj.optString("name", "Sticker"),
                        startMs = obj.optLong("startMs", 0L),
                        endMs = obj.optLong("endMs", 4000L),
                        posX = obj.optDouble("posX", 0.0).toFloat(),
                        posY = obj.optDouble("posY", 0.0).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat(),
                        rotation = obj.optDouble("rotation", 0.0).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
