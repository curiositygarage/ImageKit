package com.curiositygarage.imagekit.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.curiositygarage.imagekit.model.Adjustments
import com.curiositygarage.imagekit.model.AspectRatioPreset
import com.curiositygarage.imagekit.model.EditTool
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.FilterPreset
import com.curiositygarage.imagekit.model.MarkupItem
import com.curiositygarage.imagekit.model.MarkupType
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.model.ResizeScale
import com.curiositygarage.imagekit.model.SavedImageItem
import com.curiositygarage.imagekit.model.WebUploadPreset
import com.curiositygarage.imagekit.util.BitmapProcessor
import com.curiositygarage.imagekit.util.SampleImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

data class EditorUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val activeTool: EditTool? = null,
    val originalBitmap: Bitmap? = null,
    val currentBaseBitmap: Bitmap? = null, // After crop & orientation
    val previewBitmap: Bitmap? = null,    // After filters, adjustments & markups
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val showCompare: Boolean = false,
    val statusMessage: String? = null,

    // Edit Parameters
    val selectedFilter: FilterPreset = FilterPreset.ORIGINAL,
    val adjustments: Adjustments = Adjustments(),
    val selectedAspectRatio: AspectRatioPreset = AspectRatioPreset.FREE,
    val cropRectNorm: androidx.compose.ui.geometry.Rect = androidx.compose.ui.geometry.Rect(0f, 0f, 1f, 1f),
    val markups: List<MarkupItem> = emptyList(),
    val activeMarkupType: MarkupType = MarkupType.BLACKOUT,

    // Web & HTML Upload Optimizer
    val selectedWebPreset: WebUploadPreset? = null,
    val resizeScale: ResizeScale = ResizeScale.FULL,
    val exportFormat: ExportFormat = ExportFormat.WEBP,
    val exportQuality: Int = 85,
    val customWidth: Int = 0,
    val customHeight: Int = 0,
    val maintainAspectRatio: Boolean = true,
    val isCustomDimensionMode: Boolean = false,
    val selectedTargetBytes: Long? = null,
    val estimatedSizeBytes: Long = 0L,
    val originalEstimatedSizeBytes: Long = 0L,

    // Library & Batch
    val savedImages: List<SavedImageItem> = emptyList(),
    val batchBitmaps: List<Pair<String, Bitmap>> = emptyList(),
    val batchProgress: Float = 0f,
    val isBatchProcessing: Boolean = false,

    // Undo / Redo
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)

private data class HistorySnapshot(
    val baseBitmap: Bitmap,
    val filter: FilterPreset,
    val adjustments: Adjustments,
    val markups: List<MarkupItem>
)

class ImageEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<HistorySnapshot>()
    private val redoStack = mutableListOf<HistorySnapshot>()

    init {
        Timber.tag(TAG).i("ImageEditorViewModel initialized, preloading default sample")
        loadSampleImage("receipt")
    }

    fun setTab(tab: NavigationTab) {
        Timber.tag(TAG).d("Tab changed to: %s", tab.name)
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setActiveTool(tool: EditTool?) {
        Timber.tag(TAG).d("Active tool changed to: %s", tool?.name ?: "None")
        _uiState.update { it.copy(activeTool = tool) }
    }

    fun loadSampleImage(sampleType: String) {
        viewModelScope.launch {
            Timber.tag(TAG).i("Loading sample image: %s", sampleType)
            _uiState.update { it.copy(isLoading = true) }
            val bitmap = withContext(Dispatchers.Default) {
                when (sampleType) {
                    "receipt" -> SampleImages.createReceiptSample()
                    "passport" -> SampleImages.createPassportPortraitSample()
                    "raw_photo" -> SampleImages.createHighResDeskRawSample()
                    "print" -> SampleImages.create3DPrintSample()
                    "shipping" -> SampleImages.createShippingLabelSample()
                    else -> SampleImages.createReceiptSample()
                }
            }
            setNewImage(bitmap)
            val desc = when (sampleType) {
                "passport" -> "Loaded Passport Portrait Sample"
                "raw_photo" -> "Loaded 8.4MB Raw Camera Photo Sample"
                "receipt" -> "Loaded Hardware Receipt Sample"
                "shipping" -> "Loaded Shipping Label Sample"
                else -> "Loaded Sample"
            }
            _uiState.update { it.copy(isLoading = false, statusMessage = desc) }
        }
    }

    fun loadImageFromUri(uri: Uri) {
        viewModelScope.launch {
            Timber.tag(TAG).i("Loading user image from URI")
            _uiState.update { it.copy(isLoading = true) }
            val context = getApplication<Application>()
            val bitmap = BitmapProcessor.loadBitmapFromUri(context, uri)
            if (bitmap != null) {
                setNewImage(bitmap)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentTab = NavigationTab.STUDIO,
                        statusMessage = "Image loaded (${bitmap.width}×${bitmap.height} px)"
                    )
                }
            } else {
                Timber.tag(TAG).w("Failed to load user image from URI")
                _uiState.update { it.copy(isLoading = false, statusMessage = "Could not load image") }
            }
        }
    }

    private fun setNewImage(bitmap: Bitmap) {
        undoStack.clear()
        redoStack.clear()
        val origEstBytes = BitmapProcessor.estimateFileSize(bitmap, ExportFormat.JPEG, 95)
        Timber.tag(TAG).i("New image configured: %dx%d px (~%d bytes)", bitmap.width, bitmap.height, origEstBytes)
        _uiState.update {
            it.copy(
                originalBitmap = bitmap,
                currentBaseBitmap = bitmap,
                previewBitmap = bitmap,
                selectedFilter = FilterPreset.ORIGINAL,
                adjustments = Adjustments(),
                markups = emptyList(),
                cropRectNorm = androidx.compose.ui.geometry.Rect(0f, 0f, 1f, 1f),
                selectedWebPreset = null,
                selectedTargetBytes = null,
                isCustomDimensionMode = false,
                customWidth = bitmap.width,
                customHeight = bitmap.height,
                originalEstimatedSizeBytes = origEstBytes,
                canUndo = false,
                canRedo = false
            )
        }
        updatePreview()
    }

    private fun saveHistory() {
        val base = _uiState.value.currentBaseBitmap ?: return
        undoStack.add(
            HistorySnapshot(
                baseBitmap = base,
                filter = _uiState.value.selectedFilter,
                adjustments = _uiState.value.adjustments,
                markups = _uiState.value.markups.toList()
            )
        )
        if (undoStack.size > 15) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
        _uiState.update { it.copy(canUndo = true, canRedo = false) }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val currentSnapshot = HistorySnapshot(
            baseBitmap = _uiState.value.currentBaseBitmap ?: return,
            filter = _uiState.value.selectedFilter,
            adjustments = _uiState.value.adjustments,
            markups = _uiState.value.markups
        )
        redoStack.add(currentSnapshot)

        val previous = undoStack.removeAt(undoStack.size - 1)
        Timber.tag(TAG).d("Undo performed. Remaining history depth: %d", undoStack.size)
        _uiState.update {
            it.copy(
                currentBaseBitmap = previous.baseBitmap,
                selectedFilter = previous.filter,
                adjustments = previous.adjustments,
                markups = previous.markups,
                canUndo = undoStack.isNotEmpty(),
                canRedo = true
            )
        }
        updatePreview()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val currentSnapshot = HistorySnapshot(
            baseBitmap = _uiState.value.currentBaseBitmap ?: return,
            filter = _uiState.value.selectedFilter,
            adjustments = _uiState.value.adjustments,
            markups = _uiState.value.markups
        )
        undoStack.add(currentSnapshot)

        val next = redoStack.removeAt(redoStack.size - 1)
        Timber.tag(TAG).d("Redo performed. Remaining redo depth: %d", redoStack.size)
        _uiState.update {
            it.copy(
                currentBaseBitmap = next.baseBitmap,
                selectedFilter = next.filter,
                adjustments = next.adjustments,
                markups = next.markups,
                canUndo = true,
                canRedo = redoStack.isNotEmpty()
            )
        }
        updatePreview()
    }

    fun resetAllEdits() {
        val orig = _uiState.value.originalBitmap ?: return
        Timber.tag(TAG).i("Resetting all edits to original base image")
        saveHistory()
        _uiState.update {
            it.copy(
                currentBaseBitmap = orig,
                selectedFilter = FilterPreset.ORIGINAL,
                adjustments = Adjustments(),
                markups = emptyList(),
                selectedAspectRatio = AspectRatioPreset.FREE,
                cropRectNorm = androidx.compose.ui.geometry.Rect(0f, 0f, 1f, 1f),
                statusMessage = "Reset to original image"
            )
        }
        updatePreview()
    }

    // --- Transform Tools ---

    fun rotate(degrees: Float) {
        val current = _uiState.value.currentBaseBitmap ?: return
        saveHistory()
        viewModelScope.launch {
            val rotated = withContext(Dispatchers.Default) {
                BitmapProcessor.rotateBitmap(current, degrees)
            }
            _uiState.update { it.copy(currentBaseBitmap = rotated) }
            updatePreview()
        }
    }

    fun flip(horizontal: Boolean, vertical: Boolean) {
        val current = _uiState.value.currentBaseBitmap ?: return
        saveHistory()
        viewModelScope.launch {
            val flipped = withContext(Dispatchers.Default) {
                BitmapProcessor.flipBitmap(current, horizontal, vertical)
            }
            _uiState.update { it.copy(currentBaseBitmap = flipped) }
            updatePreview()
        }
    }

    fun setAspectRatio(preset: AspectRatioPreset) {
        _uiState.update { it.copy(selectedAspectRatio = preset) }
        val base = _uiState.value.currentBaseBitmap ?: return
        val imgRatio = base.width.toFloat() / base.height.toFloat()

        val newRect = when {
            preset == AspectRatioPreset.ORIGINAL || preset.ratio == -1.0f -> {
                androidx.compose.ui.geometry.Rect(0f, 0f, 1f, 1f)
            }
            preset == AspectRatioPreset.FREE || preset.ratio == null -> {
                // Freeform manual crop mode
                if (_uiState.value.cropRectNorm.width > 0.98f && _uiState.value.cropRectNorm.height > 0.98f) {
                    androidx.compose.ui.geometry.Rect(0.08f, 0.08f, 0.92f, 0.92f)
                } else {
                    _uiState.value.cropRectNorm
                }
            }
            else -> {
                val targetRatio = preset.ratio
                if (imgRatio > targetRatio) {
                    // Image is wider than target ratio: crop sides
                    val normWidth = targetRatio / imgRatio
                    val left = (1f - normWidth) / 2f
                    androidx.compose.ui.geometry.Rect(left, 0f, (left + normWidth).coerceAtMost(1f), 1f)
                } else {
                    // Image is taller than target ratio: crop top/bottom
                    val normHeight = imgRatio / targetRatio
                    val top = (1f - normHeight) / 2f
                    androidx.compose.ui.geometry.Rect(0f, top, 1f, (top + normHeight).coerceAtMost(1f))
                }
            }
        }

        _uiState.update { it.copy(cropRectNorm = newRect) }
    }

    fun updateCropRect(rect: androidx.compose.ui.geometry.Rect) {
        _uiState.update { 
            it.copy(
                cropRectNorm = rect,
                selectedAspectRatio = AspectRatioPreset.FREE
            ) 
        }
    }

    fun resetCropRect() {
        _uiState.update {
            it.copy(
                cropRectNorm = androidx.compose.ui.geometry.Rect(0f, 0f, 1f, 1f),
                selectedAspectRatio = AspectRatioPreset.ORIGINAL
            )
        }
    }

    fun applyCrop() {
        val current = _uiState.value.currentBaseBitmap ?: return
        val crop = _uiState.value.cropRectNorm
        if (crop.left <= 0.01f && crop.top <= 0.01f && crop.right >= 0.99f && crop.bottom >= 0.99f) {
            return
        }
        saveHistory()
        viewModelScope.launch {
            val cropped = withContext(Dispatchers.Default) {
                BitmapProcessor.cropBitmap(current, crop.left, crop.top, crop.right, crop.bottom)
            }
            _uiState.update {
                it.copy(
                    currentBaseBitmap = cropped,
                    cropRectNorm = androidx.compose.ui.geometry.Rect(0f, 0f, 1f, 1f),
                    statusMessage = "Cropped ${cropped.width}×${cropped.height}"
                )
            }
            updatePreview()
        }
    }

    // --- Adjustments & Filters ---

    fun setFilter(filter: FilterPreset) {
        saveHistory()
        _uiState.update { it.copy(selectedFilter = filter) }
        updatePreview()
    }

    fun updateAdjustments(adjustments: Adjustments) {
        _uiState.update { it.copy(adjustments = adjustments) }
        updatePreview()
    }

    fun commitAdjustments() {
        saveHistory()
    }

    // --- Markups & Redactions ---

    fun setActiveMarkupType(type: MarkupType) {
        _uiState.update { it.copy(activeMarkupType = type) }
    }

    fun addMarkup(item: MarkupItem) {
        saveHistory()
        _uiState.update { it.copy(markups = it.markups + item) }
        updatePreview()
    }

    fun removeMarkup(id: String) {
        saveHistory()
        _uiState.update { it.copy(markups = it.markups.filter { m -> m.id != id }) }
        updatePreview()
    }

    fun clearMarkups() {
        if (_uiState.value.markups.isEmpty()) return
        saveHistory()
        _uiState.update { it.copy(markups = emptyList()) }
        updatePreview()
    }

    // --- Web & HTML Upload Optimizer Settings ---

    fun applyWebUploadPreset(preset: WebUploadPreset) {
        saveHistory()
        val base = _uiState.value.currentBaseBitmap
        val w = preset.targetWidth ?: base?.width ?: 800
        val h = preset.targetHeight ?: base?.height ?: 800

        Timber.tag(TAG).i("Applying web preset: %s (target=%dx%d, maxBytes=%s)", preset.title, w, h, preset.maxBytes)
        _uiState.update {
            it.copy(
                selectedWebPreset = preset,
                exportFormat = preset.defaultFormat,
                exportQuality = preset.defaultQuality,
                isCustomDimensionMode = preset.targetWidth != null,
                customWidth = w,
                customHeight = h,
                selectedTargetBytes = preset.maxBytes,
                statusMessage = "Applied preset: ${preset.title}"
            )
        }
        updatePreview()
    }

    fun setCustomDimensionMode(enabled: Boolean) {
        _uiState.update {
            val base = it.currentBaseBitmap
            it.copy(
                isCustomDimensionMode = enabled,
                customWidth = if (it.customWidth <= 0) (base?.width ?: 800) else it.customWidth,
                customHeight = if (it.customHeight <= 0) (base?.height ?: 600) else it.customHeight
            )
        }
        updatePreview()
    }

    fun setCustomDimensions(width: Int, height: Int, maintainAspect: Boolean) {
        _uiState.update {
            it.copy(
                customWidth = width,
                customHeight = height,
                maintainAspectRatio = maintainAspect
            )
        }
        updatePreview()
    }

    fun setTargetByteSize(targetBytes: Long?) {
        Timber.tag(TAG).d("Setting target byte size budget: %s", targetBytes)
        _uiState.update { it.copy(selectedTargetBytes = targetBytes) }
        updatePreview()
    }

    fun setResizeScale(scale: ResizeScale) {
        _uiState.update { it.copy(resizeScale = scale, isCustomDimensionMode = false, selectedWebPreset = null) }
        updatePreview()
    }

    fun setExportFormat(format: ExportFormat) {
        _uiState.update { it.copy(exportFormat = format) }
        updatePreview()
    }

    fun setExportQuality(quality: Int) {
        _uiState.update { it.copy(exportQuality = quality) }
        updatePreview()
    }

    fun setCompareOriginal(show: Boolean) {
        _uiState.update { it.copy(showCompare = show) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    private fun updatePreview() {
        viewModelScope.launch {
            val base = _uiState.value.currentBaseBitmap ?: return@launch
            val filter = _uiState.value.selectedFilter
            val adjustments = _uiState.value.adjustments
            val markups = _uiState.value.markups
            val isCustomDim = _uiState.value.isCustomDimensionMode
            val cWidth = _uiState.value.customWidth
            val cHeight = _uiState.value.customHeight
            val keepAspect = _uiState.value.maintainAspectRatio
            val scale = _uiState.value.resizeScale
            val targetBytes = _uiState.value.selectedTargetBytes
            var format = _uiState.value.exportFormat
            var quality = _uiState.value.exportQuality

            val rendered = withContext(Dispatchers.Default) {
                val filtered = BitmapProcessor.applyFilterAndAdjustments(base, filter, adjustments)
                val marked = BitmapProcessor.applyMarkups(filtered, markups)

                val scaled = if (isCustomDim && cWidth > 0 && cHeight > 0) {
                    BitmapProcessor.resizeToDimensions(marked, cWidth, cHeight, keepAspect)
                } else {
                    BitmapProcessor.resizeBitmap(marked, scale)
                }

                if (targetBytes != null && targetBytes > 0) {
                    val (autoScaled, autoFmt, autoQ) = BitmapProcessor.compressToTargetBytes(scaled, targetBytes, format)
                    format = autoFmt
                    quality = autoQ
                    autoScaled
                } else {
                    scaled
                }
            }

            val estSize = withContext(Dispatchers.Default) {
                BitmapProcessor.estimateFileSize(rendered, format, quality)
            }

            _uiState.update {
                it.copy(
                    previewBitmap = rendered,
                    estimatedSizeBytes = estSize,
                    exportFormat = format,
                    exportQuality = quality
                )
            }
        }
    }

    fun saveEditedImage() {
        val preview = _uiState.value.previewBitmap ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val context = getApplication<Application>()
            val origBytes = _uiState.value.originalEstimatedSizeBytes
            Timber.tag(TAG).i("Saving edited image to device: %dx%d, format=%s", preview.width, preview.height, _uiState.value.exportFormat)
            val item = BitmapProcessor.saveImageToDevice(
                context = context,
                bitmap = preview,
                format = _uiState.value.exportFormat,
                quality = _uiState.value.exportQuality,
                originalSizeBytes = origBytes,
                baseName = "ImageKit_Edit"
            )
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    savedImages = listOf(item) + it.savedImages,
                    statusMessage = "Saved to Gallery & Library! (${formatBytes(item.sizeBytes)})"
                )
            }
        }
    }

    // --- Batch Processing ---
    fun addBatchSampleImages() {
        viewModelScope.launch {
            val s1 = "Receipt" to SampleImages.createReceiptSample()
            val s2 = "3D_Part" to SampleImages.create3DPrintSample()
            val s3 = "Shipping_Label" to SampleImages.createShippingLabelSample()
            Timber.tag(TAG).i("Added 3 demo samples to batch queue")
            _uiState.update { it.copy(batchBitmaps = it.batchBitmaps + listOf(s1, s2, s3)) }
        }
    }

    fun clearBatch() {
        _uiState.update { it.copy(batchBitmaps = emptyList()) }
    }

    fun runBatchConvert(scale: ResizeScale, format: ExportFormat, quality: Int) {
        val list = _uiState.value.batchBitmaps
        if (list.isEmpty()) return

        viewModelScope.launch {
            Timber.tag(TAG).i("Starting batch conversion for %d images", list.size)
            _uiState.update { it.copy(isBatchProcessing = true, batchProgress = 0f) }
            val context = getApplication<Application>()
            val newSaved = mutableListOf<SavedImageItem>()

            for (i in list.indices) {
                val (name, bmp) = list[i]
                val resized = BitmapProcessor.resizeBitmap(bmp, scale)
                val saved = BitmapProcessor.saveImageToDevice(
                    context = context,
                    bitmap = resized,
                    format = format,
                    quality = quality,
                    baseName = "Batch_${name}"
                )
                newSaved.add(saved)
                _uiState.update { it.copy(batchProgress = (i + 1).toFloat() / list.size) }
            }

            Timber.tag(TAG).i("Batch processing completed: %d files saved", newSaved.size)
            _uiState.update {
                it.copy(
                    isBatchProcessing = false,
                    savedImages = newSaved + it.savedImages,
                    statusMessage = "Batch completed! Processed ${list.size} images.",
                    currentTab = NavigationTab.LIBRARY
                )
            }
        }
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format("%.2f MB", bytes.toDouble() / (1024 * 1024))
        }
    }

    companion object {
        private const val TAG = "EditorVM"
    }
}
