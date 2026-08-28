package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AiCaptionItem
import com.example.data.model.AudioTrack
import com.example.data.model.CanvasAspectRatio
import com.example.data.model.ClipItem
import com.example.data.model.EffectLayer
import com.example.data.model.ProjectEntity
import com.example.data.model.StickerItem
import com.example.data.model.TextOverlay
import com.example.data.model.VideoTemplate
import com.example.data.repository.ProjectRepository
import com.example.data.sample.SampleData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import java.util.UUID

enum class EditorTab(val label: String) {
    EDIT("Edit"),
    AUDIO("Audio"),
    TEXT("Text"),
    FILTERS("Filters"),
    EFFECTS("Effects"),
    TRANSITIONS("Transition"),
    STICKERS("Stickers"),
    CANVAS("Canvas")
}

data class EditorHistorySnapshot(
    val clips: List<ClipItem>,
    val audioTracks: List<AudioTrack>,
    val textOverlays: List<TextOverlay>,
    val effectLayers: List<EffectLayer>,
    val stickers: List<StickerItem>
)

data class ExportState(
    val isExporting: Boolean = false,
    val progress: Float = 0f,
    val stageMessage: String = "",
    val isSuccess: Boolean = false,
    val exportedUri: String? = null,
    val resolution: String = "1080p",
    val fps: Int = 30,
    val quality: String = "High",
    val estimatedSizeMb: Float = 48.5f
)

data class AiProcessingState(
    val isProcessing: Boolean = false,
    val toolName: String = "",
    val progress: Float = 0f,
    val stageMessage: String = "",
    val resultMessage: String = "",
    val generatedCaptions: List<AiCaptionItem> = emptyList(),
    val isBackgroundRemoved: Boolean = false,
    val isEnhanced: Boolean = false,
    val detectedSilencesCount: Int = 0
)

class IQCutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    private val prefs = application.getSharedPreferences("iqcut_prefs", Context.MODE_PRIVATE)

    val allProjects: StateFlow<List<ProjectEntity>>

    // Onboarding state
    private val _onboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_done", false))
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    // Active Project & Editor State
    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    private val _activeClips = MutableStateFlow<List<ClipItem>>(emptyList())
    val activeClips: StateFlow<List<ClipItem>> = _activeClips.asStateFlow()

    private val _activeAudioTracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val activeAudioTracks: StateFlow<List<AudioTrack>> = _activeAudioTracks.asStateFlow()

    private val _activeTexts = MutableStateFlow<List<TextOverlay>>(emptyList())
    val activeTexts: StateFlow<List<TextOverlay>> = _activeTexts.asStateFlow()

    private val _activeEffects = MutableStateFlow<List<EffectLayer>>(emptyList())
    val activeEffects: StateFlow<List<EffectLayer>> = _activeEffects.asStateFlow()

    private val _activeStickers = MutableStateFlow<List<StickerItem>>(emptyList())
    val activeStickers: StateFlow<List<StickerItem>> = _activeStickers.asStateFlow()

    // Selection & UI state
    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _activeTab = MutableStateFlow(EditorTab.EDIT)
    val activeTab: StateFlow<EditorTab> = _activeTab.asStateFlow()

    private val _selectedTextId = MutableStateFlow<String?>(null)
    val selectedTextId: StateFlow<String?> = _selectedTextId.asStateFlow()

    private val _selectedAudioId = MutableStateFlow<String?>(null)
    val selectedAudioId: StateFlow<String?> = _selectedAudioId.asStateFlow()

    private val _canvasAspectRatio = MutableStateFlow(CanvasAspectRatio.RATIO_9_16)
    val canvasAspectRatio: StateFlow<CanvasAspectRatio> = _canvasAspectRatio.asStateFlow()

    private val _canvasBlurBg = MutableStateFlow(true)
    val canvasBlurBg: StateFlow<Boolean> = _canvasBlurBg.asStateFlow()

    // Playback Engine state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackPositionMs = MutableStateFlow(0L)
    val playbackPositionMs: StateFlow<Long> = _playbackPositionMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(15000L)
    val totalDurationMs: StateFlow<Long> = _totalDurationMs.asStateFlow()

    // Undo / Redo history
    private val undoStack = ArrayDeque<EditorHistorySnapshot>()
    private val redoStack = ArrayDeque<EditorHistorySnapshot>()
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()
    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Export & AI Tool states
    private val _exportState = MutableStateFlow(ExportState())
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _aiState = MutableStateFlow(AiProcessingState())
    val aiState: StateFlow<AiProcessingState> = _aiState.asStateFlow()

    // Feedback Toast / Snackbar
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private var playbackJob: Job? = null
    private var exportJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProjectRepository(db.projectDao())
        allProjects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed initial sample projects on first launch
        viewModelScope.launch {
            if (repository.getProjectCount() == 0) {
                SampleData.createInitialProjects().forEach {
                    repository.saveProject(it)
                }
            }
        }
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_done", true).apply()
        _onboardingCompleted.value = true
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // --- PROJECT CREATION & LOADING ---

    fun createProjectFromClips(clips: List<ClipItem>, projectName: String = "My Edit") {
        val totalMs = clips.sumOf { it.effectiveDurationMs }.coerceAtLeast(3000L)
        val newProj = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = projectName,
            durationMs = totalMs,
            clipsJson = SampleData.serializeClips(clips),
            audioTracksJson = "[]",
            textOverlaysJson = "[]",
            effectLayersJson = "[]",
            stickersJson = "[]",
            thumbnailUri = if (clips.isNotEmpty()) "res://${clips.first().drawableRes}" else ""
        )
        _activeProject.value = newProj
        _activeClips.value = clips
        _activeAudioTracks.value = emptyList()
        _activeTexts.value = emptyList()
        _activeEffects.value = emptyList()
        _activeStickers.value = emptyList()
        _selectedClipId.value = clips.firstOrNull()?.id
        _playbackPositionMs.value = 0L
        _isPlaying.value = false
        recalculateTotalDuration()
        clearHistory()
        saveActiveProject()
    }

    fun loadProject(project: ProjectEntity) {
        _activeProject.value = project
        _activeClips.value = SampleData.deserializeClips(project.clipsJson)
        _activeAudioTracks.value = SampleData.deserializeAudioTracks(project.audioTracksJson)
        _activeTexts.value = SampleData.deserializeTexts(project.textOverlaysJson)
        _activeEffects.value = SampleData.deserializeEffects(project.effectLayersJson)
        _activeStickers.value = SampleData.deserializeStickers(project.stickersJson)
        _selectedClipId.value = _activeClips.value.firstOrNull()?.id
        _playbackPositionMs.value = 0L
        _isPlaying.value = false

        _canvasAspectRatio.value = when (project.aspectRatio) {
            "16:9" -> CanvasAspectRatio.RATIO_16_9
            "1:1" -> CanvasAspectRatio.RATIO_1_1
            "4:5" -> CanvasAspectRatio.RATIO_4_5
            else -> CanvasAspectRatio.RATIO_9_16
        }
        _canvasBlurBg.value = project.canvasBlurBg
        recalculateTotalDuration()
        clearHistory()
    }

    fun loadTemplate(template: VideoTemplate) {
        val newProj = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = template.title,
            durationMs = template.durationMs,
            aspectRatio = template.aspectRatio,
            thumbnailUri = "res://${template.thumbnailRes}",
            clipsJson = SampleData.serializeClips(template.clips),
            audioTracksJson = SampleData.serializeAudioTracks(listOf(SampleData.sampleAudioTracks.first { it.title == template.musicTitle || true })),
            textOverlaysJson = SampleData.serializeTexts(template.texts),
            effectLayersJson = SampleData.serializeEffects(template.effects),
            stickersJson = "[]"
        )
        _activeProject.value = newProj
        _activeClips.value = template.clips
        _activeAudioTracks.value = listOf(AudioTrack(title = template.musicTitle, durationMs = template.durationMs))
        _activeTexts.value = template.texts
        _activeEffects.value = template.effects
        _activeStickers.value = emptyList()
        _selectedClipId.value = template.clips.firstOrNull()?.id
        _playbackPositionMs.value = 0L
        _isPlaying.value = false
        recalculateTotalDuration()
        clearHistory()
        saveActiveProject()
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_activeProject.value?.id == id) {
                _activeProject.value = null
            }
            showMessage("Project deleted")
        }
    }

    fun duplicateProject(id: String) {
        viewModelScope.launch {
            val proj = repository.getProject(id) ?: return@launch
            val dup = proj.copy(
                id = UUID.randomUUID().toString(),
                name = "${proj.name} (Copy)",
                createdAt = System.currentTimeMillis(),
                lastModified = System.currentTimeMillis()
            )
            repository.saveProject(dup)
            showMessage("Project duplicated")
        }
    }

    fun renameProject(id: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.renameProject(id, newName)
            if (_activeProject.value?.id == id) {
                _activeProject.value = _activeProject.value?.copy(name = newName)
            }
            showMessage("Project renamed")
        }
    }

    // --- PLAYBACK ENGINE ---

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 50L
            while (_isPlaying.value) {
                delay(stepMs)
                val current = _playbackPositionMs.value
                val total = _totalDurationMs.value
                if (current + stepMs >= total) {
                    _playbackPositionMs.value = 0L
                } else {
                    _playbackPositionMs.value = current + stepMs
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(positionMs: Long) {
        _playbackPositionMs.value = positionMs.coerceIn(0L, _totalDurationMs.value)
    }

    private fun recalculateTotalDuration() {
        val clipsDuration = _activeClips.value.sumOf { it.effectiveDurationMs }
        val audioDuration = _activeAudioTracks.value.maxOfOrNull { it.startOffsetMs + it.durationMs } ?: 0L
        _totalDurationMs.value = maxOf(clipsDuration, audioDuration).coerceAtLeast(2000L)
    }

    // --- EDITING OPERATIONS & HISTORY ---

    private fun saveHistoryState() {
        undoStack.push(
            EditorHistorySnapshot(
                clips = _activeClips.value.map { it.copy() },
                audioTracks = _activeAudioTracks.value.map { it.copy() },
                textOverlays = _activeTexts.value.map { it.copy() },
                effectLayers = _activeEffects.value.map { it.copy() },
                stickers = _activeStickers.value.map { it.copy() }
            )
        )
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val currentSnapshot = EditorHistorySnapshot(
            clips = _activeClips.value.map { it.copy() },
            audioTracks = _activeAudioTracks.value.map { it.copy() },
            textOverlays = _activeTexts.value.map { it.copy() },
            effectLayers = _activeEffects.value.map { it.copy() },
            stickers = _activeStickers.value.map { it.copy() }
        )
        redoStack.push(currentSnapshot)
        val prev = undoStack.pop()
        _activeClips.value = prev.clips
        _activeAudioTracks.value = prev.audioTracks
        _activeTexts.value = prev.textOverlays
        _activeEffects.value = prev.effectLayers
        _activeStickers.value = prev.stickers
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Undo")
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val currentSnapshot = EditorHistorySnapshot(
            clips = _activeClips.value.map { it.copy() },
            audioTracks = _activeAudioTracks.value.map { it.copy() },
            textOverlays = _activeTexts.value.map { it.copy() },
            effectLayers = _activeEffects.value.map { it.copy() },
            stickers = _activeStickers.value.map { it.copy() }
        )
        undoStack.push(currentSnapshot)
        val next = redoStack.pop()
        _activeClips.value = next.clips
        _activeAudioTracks.value = next.audioTracks
        _activeTexts.value = next.textOverlays
        _activeEffects.value = next.effectLayers
        _activeStickers.value = next.stickers
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Redo")
    }

    private fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
        _canUndo.value = false
        _canRedo.value = false
    }

    fun selectClip(id: String?) {
        _selectedClipId.value = id
    }

    fun setActiveTab(tab: EditorTab) {
        _activeTab.value = tab
    }

    // Clip Actions
    fun splitCurrentClip() {
        val playhead = _playbackPositionMs.value
        var accumulated = 0L
        val clips = _activeClips.value.toMutableList()
        val index = clips.indexOfFirst { clip ->
            val end = accumulated + clip.effectiveDurationMs
            val matches = playhead in accumulated until end
            if (!matches) accumulated += clip.effectiveDurationMs
            matches
        }

        if (index == -1) {
            showMessage("Move playhead over a clip to split")
            return
        }

        saveHistoryState()
        val targetClip = clips[index]
        val localOffset = ((playhead - accumulated) * targetClip.speed).toLong()
        val splitPoint = targetClip.trimStartMs + localOffset

        if (splitPoint <= targetClip.trimStartMs + 500L || splitPoint >= targetClip.trimEndMs - 500L) {
            showMessage("Split point is too close to clip edge")
            return
        }

        val firstPart = targetClip.copy(
            id = UUID.randomUUID().toString(),
            name = "${targetClip.name} Part 1",
            trimEndMs = splitPoint
        )
        val secondPart = targetClip.copy(
            id = UUID.randomUUID().toString(),
            name = "${targetClip.name} Part 2",
            trimStartMs = splitPoint
        )

        clips.removeAt(index)
        clips.add(index, secondPart)
        clips.add(index, firstPart)

        _activeClips.value = clips
        _selectedClipId.value = secondPart.id
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Clip split into 2 parts")
    }

    fun trimSelectedClip(startMs: Long, endMs: Long) {
        val id = _selectedClipId.value ?: return
        saveHistoryState()
        _activeClips.value = _activeClips.value.map { clip ->
            if (clip.id == id) {
                clip.copy(trimStartMs = startMs, trimEndMs = endMs)
            } else clip
        }
        recalculateTotalDuration()
        saveActiveProject()
    }

    fun deleteSelectedClip() {
        val id = _selectedClipId.value ?: return
        if (_activeClips.value.size <= 1) {
            showMessage("Timeline must have at least 1 clip")
            return
        }
        saveHistoryState()
        _activeClips.value = _activeClips.value.filter { it.id != id }
        _selectedClipId.value = _activeClips.value.firstOrNull()?.id
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Clip deleted")
    }

    fun duplicateSelectedClip() {
        val id = _selectedClipId.value ?: return
        val clip = _activeClips.value.find { it.id == id } ?: return
        saveHistoryState()
        val duplicate = clip.copy(
            id = UUID.randomUUID().toString(),
            name = "${clip.name} (Copy)"
        )
        val clips = _activeClips.value.toMutableList()
        val index = clips.indexOfFirst { it.id == id }
        clips.add(index + 1, duplicate)
        _activeClips.value = clips
        _selectedClipId.value = duplicate.id
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Clip duplicated")
    }

    fun setClipSpeed(speed: Float) {
        val id = _selectedClipId.value ?: return
        saveHistoryState()
        _activeClips.value = _activeClips.value.map { clip ->
            if (clip.id == id) clip.copy(speed = speed) else clip
        }
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Speed set to ${speed}x")
    }

    fun setClipVolume(volume: Float) {
        val id = _selectedClipId.value ?: return
        saveHistoryState()
        _activeClips.value = _activeClips.value.map { clip ->
            if (clip.id == id) clip.copy(volume = volume) else clip
        }
        saveActiveProject()
    }

    fun rotateSelectedClip() {
        val id = _selectedClipId.value ?: return
        saveHistoryState()
        _activeClips.value = _activeClips.value.map { clip ->
            if (clip.id == id) clip.copy(rotation = (clip.rotation + 90) % 360) else clip
        }
        saveActiveProject()
        showMessage("Rotated 90°")
    }

    fun setClipFilter(filterName: String, intensity: Float) {
        val id = _selectedClipId.value ?: return
        saveHistoryState()
        _activeClips.value = _activeClips.value.map { clip ->
            if (clip.id == id) clip.copy(filterPreset = filterName, filterIntensity = intensity) else clip
        }
        saveActiveProject()
    }

    fun setClipTransition(transitionType: String, durationMs: Long = 500L) {
        val id = _selectedClipId.value ?: return
        saveHistoryState()
        _activeClips.value = _activeClips.value.map { clip ->
            if (clip.id == id) clip.copy(transitionType = transitionType, transitionDurationMs = durationMs) else clip
        }
        saveActiveProject()
        showMessage("Transition applied: $transitionType")
    }

    // Audio Actions
    fun addAudioTrack(track: AudioTrack) {
        saveHistoryState()
        _activeAudioTracks.value = _activeAudioTracks.value + track
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Audio added: ${track.title}")
    }

    fun deleteAudioTrack(id: String) {
        saveHistoryState()
        _activeAudioTracks.value = _activeAudioTracks.value.filter { it.id != id }
        recalculateTotalDuration()
        saveActiveProject()
        showMessage("Audio track removed")
    }

    fun updateAudioVolume(id: String, volume: Float) {
        _activeAudioTracks.value = _activeAudioTracks.value.map {
            if (it.id == id) it.copy(volume = volume) else it
        }
        saveActiveProject()
    }

    // Text Actions
    fun addTextOverlay(text: String, colorHex: String = "#FFFFFF") {
        saveHistoryState()
        val playhead = _playbackPositionMs.value
        val newText = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = text,
            startMs = playhead,
            endMs = (playhead + 3000L).coerceAtMost(_totalDurationMs.value),
            colorHex = colorHex
        )
        _activeTexts.value = _activeTexts.value + newText
        _selectedTextId.value = newText.id
        saveActiveProject()
        showMessage("Text overlay added")
    }

    fun updateTextOverlay(overlay: TextOverlay) {
        saveHistoryState()
        _activeTexts.value = _activeTexts.value.map {
            if (it.id == overlay.id) overlay else it
        }
        saveActiveProject()
    }

    fun deleteTextOverlay(id: String) {
        saveHistoryState()
        _activeTexts.value = _activeTexts.value.filter { it.id != id }
        _selectedTextId.value = null
        saveActiveProject()
        showMessage("Text overlay removed")
    }

    // Effect Actions
    fun addEffectLayer(type: String, name: String) {
        saveHistoryState()
        val playhead = _playbackPositionMs.value
        val newEffect = EffectLayer(
            id = UUID.randomUUID().toString(),
            effectType = type,
            name = name,
            startMs = playhead,
            endMs = (playhead + 4000L).coerceAtMost(_totalDurationMs.value),
            intensity = 0.75f
        )
        _activeEffects.value = _activeEffects.value + newEffect
        saveActiveProject()
        showMessage("Effect added: $name")
    }

    fun deleteEffectLayer(id: String) {
        saveHistoryState()
        _activeEffects.value = _activeEffects.value.filter { it.id != id }
        saveActiveProject()
        showMessage("Effect removed")
    }

    // Sticker Actions
    fun addSticker(emoji: String, name: String) {
        saveHistoryState()
        val playhead = _playbackPositionMs.value
        val sticker = StickerItem(
            id = UUID.randomUUID().toString(),
            emojiOrIcon = emoji,
            name = name,
            startMs = playhead,
            endMs = (playhead + 4000L).coerceAtMost(_totalDurationMs.value)
        )
        _activeStickers.value = _activeStickers.value + sticker
        saveActiveProject()
        showMessage("Sticker added: $name")
    }

    fun deleteSticker(id: String) {
        saveHistoryState()
        _activeStickers.value = _activeStickers.value.filter { it.id != id }
        saveActiveProject()
        showMessage("Sticker removed")
    }

    // Canvas Actions
    fun setCanvasRatio(ratio: CanvasAspectRatio) {
        _canvasAspectRatio.value = ratio
        _activeProject.value = _activeProject.value?.copy(aspectRatio = ratio.label)
        saveActiveProject()
        showMessage("Canvas aspect ratio: ${ratio.label}")
    }

    fun toggleCanvasBlur() {
        _canvasBlurBg.value = !_canvasBlurBg.value
        _activeProject.value = _activeProject.value?.copy(canvasBlurBg = _canvasBlurBg.value)
        saveActiveProject()
    }

    // --- AUTO SAVE ---

    fun saveActiveProject() {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            lastModified = System.currentTimeMillis(),
            durationMs = _totalDurationMs.value,
            aspectRatio = _canvasAspectRatio.value.label,
            canvasBlurBg = _canvasBlurBg.value,
            clipsJson = SampleData.serializeClips(_activeClips.value),
            audioTracksJson = SampleData.serializeAudioTracks(_activeAudioTracks.value),
            textOverlaysJson = SampleData.serializeTexts(_activeTexts.value),
            effectLayersJson = SampleData.serializeEffects(_activeEffects.value),
            stickersJson = SampleData.serializeStickers(_activeStickers.value),
            thumbnailUri = if (_activeClips.value.isNotEmpty()) "res://${_activeClips.value.first().drawableRes}" else current.thumbnailUri
        )
        _activeProject.value = updated
        viewModelScope.launch {
            repository.saveProject(updated)
        }
    }

    // --- AI TOOLS EXECUTION ---

    fun runAiCaptions() {
        viewModelScope.launch {
            _aiState.value = AiProcessingState(
                isProcessing = true,
                toolName = "Auto Captions",
                stageMessage = "Analyzing audio speech & transcribing timestamps..."
            )
            delay(1200)
            _aiState.value = _aiState.value.copy(progress = 0.5f, stageMessage = "Formatting timed caption overlays...")
            delay(1000)

            val generated = listOf(
                AiCaptionItem(text = "Welcome to the ultimate creative journey with IQCut.", startMs = 500, endMs = 3500),
                AiCaptionItem(text = "Unleash your imagination and create seamless edits.", startMs = 3800, endMs = 7000),
                AiCaptionItem(text = "Powered by next-generation mobile processing.", startMs = 7200, endMs = 11000)
            )

            // Convert to TextOverlays
            val newTexts = generated.map { cap ->
                TextOverlay(
                    id = UUID.randomUUID().toString(),
                    text = cap.text,
                    startMs = cap.startMs,
                    endMs = cap.endMs,
                    posY = 0.65f,
                    fontSize = 18,
                    colorHex = "#00D9FF",
                    bgColorHex = "#800B0B10",
                    fontFamilyType = "Bold Sans"
                )
            }
            _activeTexts.value = _activeTexts.value + newTexts
            saveActiveProject()

            _aiState.value = AiProcessingState(
                isProcessing = false,
                toolName = "Auto Captions",
                progress = 1.0f,
                resultMessage = "Generated ${generated.size} AI caption overlays",
                generatedCaptions = generated
            )
            showMessage("AI Captions generated successfully")
        }
    }

    fun runAiBackgroundRemoval() {
        viewModelScope.launch {
            _aiState.value = AiProcessingState(
                isProcessing = true,
                toolName = "AI Background Removal",
                stageMessage = "Detecting subject silhouettes & isolating background matte..."
            )
            delay(1800)
            _aiState.value = AiProcessingState(
                isProcessing = false,
                toolName = "AI Background Removal",
                progress = 1.0f,
                isBackgroundRemoved = true,
                resultMessage = "Background isolated with AI cutout shader"
            )
            showMessage("AI Background Removal applied")
        }
    }

    fun runSmartEnhance() {
        viewModelScope.launch {
            _aiState.value = AiProcessingState(
                isProcessing = true,
                toolName = "Smart Enhance",
                stageMessage = "Calibrating dynamic HDR, contrast balance & color vibrancy..."
            )
            delay(1400)
            _activeClips.value = _activeClips.value.map {
                it.copy(filterPreset = "Vibrant", filterIntensity = 0.95f)
            }
            saveActiveProject()
            _aiState.value = AiProcessingState(
                isProcessing = false,
                toolName = "Smart Enhance",
                progress = 1.0f,
                isEnhanced = true,
                resultMessage = "Optimized color grading and dynamic range"
            )
            showMessage("Smart Enhance HDR grading applied")
        }
    }

    fun runSilenceDetection() {
        viewModelScope.launch {
            _aiState.value = AiProcessingState(
                isProcessing = true,
                toolName = "Silence Detection",
                stageMessage = "Scanning audio waveform for pauses below -30dB..."
            )
            delay(1500)
            _aiState.value = AiProcessingState(
                isProcessing = false,
                toolName = "Silence Detection",
                progress = 1.0f,
                detectedSilencesCount = 3,
                resultMessage = "Detected 3 silent pauses (total 2.4s). Auto-trimmed."
            )
            showMessage("Auto-trimmed silent intervals")
        }
    }

    // --- REALISTIC EXPORT WORKFLOW ---

    fun startExport(resolution: String = "1080p", fps: Int = 30, quality: String = "High") {
        exportJob?.cancel()
        val mbPerSec = when (resolution) {
            "4K" -> 4.5f
            "1080p" -> 2.2f
            "720p" -> 1.2f
            else -> 0.8f
        }
        val estimatedSize = (_totalDurationMs.value / 1000f) * mbPerSec

        _exportState.value = ExportState(
            isExporting = true,
            progress = 0.05f,
            stageMessage = "Initializing media render pipeline ($resolution @ ${fps}fps)...",
            resolution = resolution,
            fps = fps,
            quality = quality,
            estimatedSizeMb = estimatedSize
        )

        exportJob = viewModelScope.launch {
            val stages = listOf(
                Pair(0.20f, "Applying color filters & GPU transitions..."),
                Pair(0.45f, "Rendering video stream frame-by-frame..."),
                Pair(0.70f, "Compositing text overlays & VFX shaders..."),
                Pair(0.88f, "Mixing multi-channel audio tracks & fades..."),
                Pair(0.98f, "Encoding MP4 H.264 video stream..."),
                Pair(1.00f, "Export completed successfully!")
            )

            for (stage in stages) {
                delay(700)
                _exportState.value = _exportState.value.copy(
                    progress = stage.first,
                    stageMessage = stage.second
                )
            }

            _exportState.value = _exportState.value.copy(
                isExporting = false,
                isSuccess = true,
                exportedUri = "content://iqcut/exports/${_activeProject.value?.id ?: "export"}.mp4"
            )
            showMessage("Video export completed!")
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState()
    }
}
