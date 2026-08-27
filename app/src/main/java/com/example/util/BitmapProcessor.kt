package com.curiositygarage.imagekit.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.curiositygarage.imagekit.model.Adjustments
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.FilterPreset
import com.curiositygarage.imagekit.model.MarkupItem
import com.curiositygarage.imagekit.model.MarkupType
import com.curiositygarage.imagekit.model.ResizeScale
import com.curiositygarage.imagekit.model.SavedImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object BitmapProcessor {

    suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val input: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(input, null, options)
            input?.close()

            // Downsample if mega large (> 3000px) to prevent OOM
            var inSampleSize = 1
            val maxDim = max(options.outWidth, options.outHeight)
            while (maxDim / inSampleSize > 3200) {
                inSampleSize *= 2
            }

            val loadOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val newInput: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(newInput, null, loadOptions)
            newInput?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return source
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun flipBitmap(source: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix().apply {
            postScale(
                if (horizontal) -1f else 1f,
                if (vertical) -1f else 1f,
                source.width / 2f,
                source.height / 2f
            )
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun cropBitmap(source: Bitmap, leftNorm: Float, topNorm: Float, rightNorm: Float, bottomNorm: Float): Bitmap {
        val left = (leftNorm.coerceIn(0f, 1f) * source.width).toInt().coerceIn(0, source.width - 1)
        val top = (topNorm.coerceIn(0f, 1f) * source.height).toInt().coerceIn(0, source.height - 1)
        val right = (rightNorm.coerceIn(0f, 1f) * source.width).toInt().coerceIn(left + 1, source.width)
        val bottom = (bottomNorm.coerceIn(0f, 1f) * source.height).toInt().coerceIn(top + 1, source.height)

        val cropWidth = max(1, right - left)
        val cropHeight = max(1, bottom - top)

        return Bitmap.createBitmap(source, left, top, cropWidth, cropHeight)
    }

    fun applyFilterAndAdjustments(
        source: Bitmap,
        preset: FilterPreset,
        adjustments: Adjustments
    ): Bitmap {
        // First handle special pixel-based algorithms if needed
        var base = when (preset) {
            FilterPreset.DOC_CLEAN -> applyDocumentCleanFilter(source)
            FilterPreset.WHITEBOARD -> applyWhiteboardFilter(source)
            FilterPreset.MAGIC_COLOR -> applyMagicColorFilter(source)
            else -> source
        }

        // Apply ColorMatrix based filter and adjustments
        val colorMatrix = ColorMatrix()

        // 1. Preset filter matrix
        when (preset) {
            FilterPreset.AUTO_ENHANCE -> {
                val m = ColorMatrix(floatArrayOf(
                    1.12f, 0f, 0f, 0f, 10f,
                    0f, 1.12f, 0f, 0f, 10f,
                    0f, 0f, 1.12f, 0f, 10f,
                    0f, 0f, 0f, 1f, 0f
                ))
                val sat = ColorMatrix().apply { setSaturation(1.15f) }
                m.postConcat(sat)
                colorMatrix.postConcat(m)
            }
            FilterPreset.BW_CONTRAST -> {
                val sat = ColorMatrix().apply { setSaturation(0f) }
                val contrastMatrix = getContrastMatrix(35f)
                sat.postConcat(contrastMatrix)
                colorMatrix.postConcat(sat)
            }
            FilterPreset.VINTAGE_WARM -> {
                val m = ColorMatrix(floatArrayOf(
                    1.1f, 0f, 0f, 0f, 15f,
                    0f, 1.02f, 0f, 0f, 5f,
                    0f, 0f, 0.88f, 0f, -10f,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(m)
            }
            FilterPreset.COOL_SLATE -> {
                val m = ColorMatrix(floatArrayOf(
                    0.88f, 0f, 0f, 0f, -5f,
                    0f, 1.02f, 0f, 0f, 5f,
                    0f, 0f, 1.15f, 0f, 20f,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(m)
            }
            FilterPreset.VIVID -> {
                val sat = ColorMatrix().apply { setSaturation(1.4f) }
                val cont = getContrastMatrix(15f)
                sat.postConcat(cont)
                colorMatrix.postConcat(sat)
            }
            FilterPreset.INVERT -> {
                val m = ColorMatrix(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(m)
            }
            else -> {}
        }

        // 2. Adjustments: Brightness
        if (adjustments.brightness != 0f) {
            val b = adjustments.brightness * 1.5f
            val brightMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, b,
                0f, 1f, 0f, 0f, b,
                0f, 0f, 1f, 0f, b,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(brightMatrix)
        }

        // 3. Contrast
        if (adjustments.contrast != 0f) {
            colorMatrix.postConcat(getContrastMatrix(adjustments.contrast))
        }

        // 4. Saturation
        if (adjustments.saturation != 0f) {
            val satFactor = (adjustments.saturation + 100f) / 100f
            val satMatrix = ColorMatrix().apply { setSaturation(satFactor.coerceAtLeast(0f)) }
            colorMatrix.postConcat(satMatrix)
        }

        // 5. Warmth (Red vs Blue shift)
        if (adjustments.warmth != 0f) {
            val w = adjustments.warmth
            val warmMatrix = ColorMatrix(floatArrayOf(
                1f + (w * 0.003f), 0f, 0f, 0f, w * 0.5f,
                0f, 1f + (w * 0.001f), 0f, 0f, 0f,
                0f, 0f, 1f - (w * 0.003f), 0f, -w * 0.5f,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(warmMatrix)
        }

        // Render result bitmap
        val output = Bitmap.createBitmap(base.width, base.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        canvas.drawBitmap(base, 0f, 0f, paint)

        // 6. Vignette overlay if requested
        if (adjustments.vignette > 0f) {
            applyVignetteToCanvas(canvas, base.width, base.height, adjustments.vignette)
        }

        return output
    }

    private fun getContrastMatrix(contrast: Float): ColorMatrix {
        val scale = (contrast + 100f) / 100f
        val translate = (-0.5f * scale + 0.5f) * 255f
        return ColorMatrix(floatArrayOf(
            scale, 0f, 0f, 0f, translate,
            0f, scale, 0f, 0f, translate,
            0f, 0f, scale, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        ))
    }

    private fun applyVignetteToCanvas(canvas: Canvas, width: Int, height: Int, strength: Float) {
        val radius = max(width, height) * 0.8f
        val alpha = ((strength / 100f) * 220).toInt().coerceIn(0, 255)
        val gradient = RadialGradient(
            width / 2f,
            height / 2f,
            radius,
            intArrayOf(Color.TRANSPARENT, Color.argb(alpha / 3, 0, 0, 0), Color.argb(alpha, 0, 0, 0)),
            floatArrayOf(0.4f, 0.75f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = gradient
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    // High performance document thresholding cleaner for receipts & invoices
    fun applyDocumentCleanFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF

            // Perceived luminance
            val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

            // Dynamic document high-pass curve: clean paper to crisp white, darken ink to dark charcoal
            val cleanVal = when {
                lum > 175 -> 255
                lum < 90 -> (lum * 0.4).toInt()
                else -> {
                    val factor = (lum - 90f) / (175f - 90f)
                    val smooth = factor * factor * (3 - 2 * factor)
                    (36 + smooth * (255 - 36)).toInt()
                }
            }
            pixels[i] = Color.rgb(cleanVal, cleanVal, cleanVal)
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    // Whiteboard enhancement filter
    fun applyWhiteboardFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            var r = (color shr 16) and 0xFF
            var g = (color shr 8) and 0xFF
            var b = color and 0xFF

            // Boost ink contrast while whitening gray background
            val maxC = max(r, max(g, b))
            val minC = min(r, min(g, b))
            val saturation = if (maxC > 0) (maxC - minC).toFloat() / maxC else 0f

            if (saturation < 0.15f && minC > 140) {
                // Whitish/gray background -> push to pure white
                r = 255
                g = 255
                b = 255
            } else {
                // Colored or dark ink -> amplify contrast
                r = (r * 0.85).toInt().coerceIn(0, 255)
                g = (g * 0.85).toInt().coerceIn(0, 255)
                b = (b * 0.85).toInt().coerceIn(0, 255)
            }
            pixels[i] = Color.rgb(r, g, b)
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    // Magic Color filter - clears murky gray background paper while making color ink/stamps pop
    fun applyMagicColorFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            var r = (color shr 16) and 0xFF
            var g = (color shr 8) and 0xFF
            var b = color and 0xFF

            val maxC = max(r, max(g, b))
            val minC = min(r, min(g, b))
            val saturation = if (maxC > 0) (maxC - minC).toFloat() / maxC else 0f
            val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

            if (saturation < 0.18f && lum > 145) {
                // Background paper with faint shadows -> push to pure white
                val factor = (lum - 145f) / (255f - 145f)
                val boost = (235 + factor * 20).toInt().coerceIn(0, 255)
                r = boost
                g = boost
                b = boost
            } else if (saturation > 0.25f) {
                // Colored inks/stamps/logos -> saturate and sharpen
                r = min(255, (r * 1.2f).toInt())
                g = min(255, (g * 1.2f).toInt())
                b = min(255, (b * 1.2f).toInt())
            } else {
                // Dark text ink -> deepen contrast
                r = (r * 0.75f).toInt()
                g = (g * 0.75f).toInt()
                b = (b * 0.75f).toInt()
            }

            pixels[i] = Color.rgb(r, g, b)
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    // Render Redactions & Markups on top of bitmap
    fun applyMarkups(source: Bitmap, markups: List<MarkupItem>): Bitmap {
        if (markups.isEmpty()) return source

        val output = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val w = source.width.toFloat()
        val h = source.height.toFloat()

        for (item in markups) {
            when (item.type) {
                MarkupType.BLACKOUT -> {
                    val rectF = RectF(
                        item.rect.left * w,
                        item.rect.top * h,
                        item.rect.right * w,
                        item.rect.bottom * h
                    )
                    val paint = Paint().apply {
                        color = Color.BLACK
                        style = Paint.Style.FILL
                    }
                    canvas.drawRoundRect(rectF, 8f, 8f, paint)
                }
                MarkupType.PIXELATE -> {
                    val left = (item.rect.left * w).toInt().coerceIn(0, source.width - 1)
                    val top = (item.rect.top * h).toInt().coerceIn(0, source.height - 1)
                    val right = (item.rect.right * w).toInt().coerceIn(left + 1, source.width)
                    val bottom = (item.rect.bottom * h).toInt().coerceIn(top + 1, source.height)
                    val boxW = right - left
                    val boxH = bottom - top

                    if (boxW > 10 && boxH > 10) {
                        val pixelBlock = max(8, min(boxW, boxH) / 12)
                        val smallW = max(1, boxW / pixelBlock)
                        val smallH = max(1, boxH / pixelBlock)
                        val sub = Bitmap.createBitmap(output, left, top, boxW, boxH)
                        val scaledSmall = Bitmap.createScaledBitmap(sub, smallW, smallH, false)
                        val pixelated = Bitmap.createScaledBitmap(scaledSmall, boxW, boxH, false)
                        canvas.drawBitmap(pixelated, left.toFloat(), top.toFloat(), null)
                    }
                }
                MarkupType.BLUR_BOX -> {
                    val rectF = RectF(
                        item.rect.left * w,
                        item.rect.top * h,
                        item.rect.right * w,
                        item.rect.bottom * h
                    )
                    // Elegant frosted semi-transparent mask
                    val paint = Paint().apply {
                        color = Color.argb(220, 220, 225, 230)
                        style = Paint.Style.FILL
                    }
                    canvas.drawRoundRect(rectF, 10f, 10f, paint)
                    val borderPaint = Paint().apply {
                        color = Color.argb(180, 100, 110, 125)
                        style = Paint.Style.STROKE
                        strokeWidth = 2f
                    }
                    canvas.drawRoundRect(rectF, 10f, 10f, borderPaint)
                }
                MarkupType.HIGHLIGHT -> {
                    val rectF = RectF(
                        item.rect.left * w,
                        item.rect.top * h,
                        item.rect.right * w,
                        item.rect.bottom * h
                    )
                    val paint = Paint().apply {
                        color = Color.argb(100, 255, 235, 59) // Translucent yellow highlight
                        style = Paint.Style.FILL
                    }
                    canvas.drawRoundRect(rectF, 6f, 6f, paint)
                }
                MarkupType.ARROW -> {
                    val startX = item.startOffset.x * w
                    val startY = item.startOffset.y * h
                    val endX = item.endOffset.x * w
                    val endY = item.endOffset.y * h

                    val arrowPaint = Paint().apply {
                        color = Color.rgb(220, 40, 40) // Bold red callout
                        strokeWidth = max(4f, w * 0.006f)
                        style = Paint.Style.STROKE
                        strokeCap = Paint.Cap.ROUND
                        isAntiAlias = true
                    }
                    canvas.drawLine(startX, startY, endX, endY, arrowPaint)

                    // Arrow head
                    val angle = atan2((endY - startY).toDouble(), (endX - startX).toDouble())
                    val headLen = max(24f, w * 0.03f)
                    val p1X = endX - headLen * cos(angle - Math.PI / 6).toFloat()
                    val p1Y = endY - headLen * sin(angle - Math.PI / 6).toFloat()
                    val p2X = endX - headLen * cos(angle + Math.PI / 6).toFloat()
                    val p2Y = endY - headLen * sin(angle + Math.PI / 6).toFloat()

                    val headPath = Path().apply {
                        moveTo(endX, endY)
                        lineTo(p1X, p1Y)
                        moveTo(endX, endY)
                        lineTo(p2X, p2Y)
                    }
                    canvas.drawPath(headPath, arrowPaint)
                }
                MarkupType.TEXT -> {
                    val x = item.rect.left * w
                    val y = item.rect.top * h
                    val bgPaint = Paint().apply {
                        color = Color.argb(220, 0, 49, 92) // Deep navy badge
                        style = Paint.Style.FILL
                        isAntiAlias = true
                    }
                    val textPaint = Paint().apply {
                        color = Color.rgb(208, 228, 255) // Light cyan text
                        textSize = max(24f, w * 0.035f)
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                    val textBounds = Rect()
                    textPaint.getTextBounds(item.text, 0, item.text.length, textBounds)
                    val pad = 16f
                    val badgeRect = RectF(
                        x,
                        y,
                        x + textBounds.width() + pad * 2,
                        y + textBounds.height() + pad * 2
                    )
                    canvas.drawRoundRect(badgeRect, 12f, 12f, bgPaint)
                    canvas.drawText(item.text, x + pad, y + textBounds.height() + pad * 0.8f, textPaint)
                }
            }
        }

        return output
    }

    // Resize bitmap based on preset
    fun resizeBitmap(source: Bitmap, scale: ResizeScale): Bitmap {
        val origW = source.width
        val origH = source.height

        val (targetW, targetH) = when (scale) {
            ResizeScale.FULL -> return source
            ResizeScale.SCALE_75 -> (origW * 0.75f).toInt() to (origH * 0.75f).toInt()
            ResizeScale.SCALE_50 -> (origW * 0.5f).toInt() to (origH * 0.5f).toInt()
            ResizeScale.SCALE_25 -> (origW * 0.25f).toInt() to (origH * 0.25f).toInt()
            ResizeScale.HD_1080 -> {
                val maxDim = 1080f
                val ratio = min(maxDim / origW, maxDim / origH)
                if (ratio >= 1.0f) origW to origH else (origW * ratio).toInt() to (origH * ratio).toInt()
            }
            ResizeScale.HD_720 -> {
                val maxDim = 720f
                val ratio = min(maxDim / origW, maxDim / origH)
                if (ratio >= 1.0f) origW to origH else (origW * ratio).toInt() to (origH * ratio).toInt()
            }
            ResizeScale.WEB_800 -> {
                val maxDim = 800f
                val ratio = min(maxDim / origW, maxDim / origH)
                if (ratio >= 1.0f) origW to origH else (origW * ratio).toInt() to (origH * ratio).toInt()
            }
            ResizeScale.PASSPORT_600 -> {
                val maxDim = 600f
                val ratio = min(maxDim / origW, maxDim / origH)
                if (ratio >= 1.0f) origW to origH else (origW * ratio).toInt() to (origH * ratio).toInt()
            }
            ResizeScale.THUMB_400 -> {
                val maxDim = 400f
                val ratio = min(maxDim / origW, maxDim / origH)
                if (ratio >= 1.0f) origW to origH else (origW * ratio).toInt() to (origH * ratio).toInt()
            }
        }

        val w = max(1, targetW)
        val h = max(1, targetH)
        return Bitmap.createScaledBitmap(source, w, h, true)
    }

    // Resize bitmap to exact custom pixel dimensions
    fun resizeToDimensions(source: Bitmap, targetWidth: Int, targetHeight: Int, maintainAspect: Boolean = true): Bitmap {
        val origW = source.width
        val origH = source.height

        if (targetWidth <= 0 && targetHeight <= 0) return source

        val finalW: Int
        val finalH: Int

        if (maintainAspect) {
            val aspect = origW.toFloat() / origH.toFloat()
            if (targetWidth > 0 && targetHeight > 0) {
                // Fit within target bounds while preserving ratio
                val scale = min(targetWidth.toFloat() / origW, targetHeight.toFloat() / origH)
                finalW = max(1, (origW * scale).toInt())
                finalH = max(1, (origH * scale).toInt())
            } else if (targetWidth > 0) {
                finalW = max(1, targetWidth)
                finalH = max(1, (targetWidth / aspect).toInt())
            } else {
                finalH = max(1, targetHeight)
                finalW = max(1, (targetHeight * aspect).toInt())
            }
        } else {
            finalW = max(1, if (targetWidth > 0) targetWidth else origW)
            finalH = max(1, if (targetHeight > 0) targetHeight else origH)
        }

        return Bitmap.createScaledBitmap(source, finalW, finalH, true)
    }

    // Smart Auto-Fit Target Size Compressor: Iteratively finds optimal resolution & quality to strictly satisfy target byte budget
    fun compressToTargetBytes(
        source: Bitmap,
        targetBytes: Long,
        preferredFormat: ExportFormat = ExportFormat.WEBP
    ): Triple<Bitmap, ExportFormat, Int> {
        var currentBitmap = source
        var format = preferredFormat
        var quality = 85

        var estimated = estimateFileSize(currentBitmap, format, quality)
        if (estimated <= targetBytes) {
            return Triple(currentBitmap, format, quality)
        }

        // 1. Try lowering quality first down to 40%
        while (quality > 40 && estimated > targetBytes) {
            quality -= 10
            estimated = estimateFileSize(currentBitmap, format, quality)
        }

        if (estimated <= targetBytes) {
            return Triple(currentBitmap, format, quality)
        }

        // 2. If still over budget, downscale image resolution progressively
        var scaleFactor = 0.85f
        while (estimated > targetBytes && scaleFactor > 0.15f) {
            val newW = max(100, (source.width * scaleFactor).toInt())
            val newH = max(100, (source.height * scaleFactor).toInt())
            currentBitmap = Bitmap.createScaledBitmap(source, newW, newH, true)
            quality = 75
            estimated = estimateFileSize(currentBitmap, format, quality)

            if (estimated > targetBytes && quality > 45) {
                quality = 50
                estimated = estimateFileSize(currentBitmap, format, quality)
            }
            scaleFactor -= 0.15f
        }

        return Triple(currentBitmap, format, quality)
    }

    // Estimate file size in bytes
    fun estimateFileSize(bitmap: Bitmap, format: ExportFormat, quality: Int): Long {
        val stream = ByteArrayOutputStream()
        val compressFormat = when (format) {
            ExportFormat.JPEG -> Bitmap.CompressFormat.JPEG
            ExportFormat.PNG -> Bitmap.CompressFormat.PNG
            ExportFormat.WEBP -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
        }
        bitmap.compress(compressFormat, quality.coerceIn(10, 100), stream)
        return stream.size().toLong()
    }

    // Save edited bitmap to device / app local storage
    suspend fun saveImageToDevice(
        context: Context,
        bitmap: Bitmap,
        format: ExportFormat,
        quality: Int,
        originalSizeBytes: Long = 0L,
        baseName: String = "ImageKit_Edit"
    ): SavedImageItem = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val filename = "${baseName}_${timestamp}.${format.extension}"
        val compressFormat = when (format) {
            ExportFormat.JPEG -> Bitmap.CompressFormat.JPEG
            ExportFormat.PNG -> Bitmap.CompressFormat.PNG
            ExportFormat.WEBP -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
        }

        var savedUri: Uri? = null
        var localPath = ""
        var sizeBytes = 0L

        try {
            // Save to app internal files cache first for instant preview & library
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val cacheFile = File(exportDir, filename)
            FileOutputStream(cacheFile).use { out ->
                bitmap.compress(compressFormat, quality.coerceIn(10, 100), out)
            }
            localPath = cacheFile.absolutePath
            sizeBytes = cacheFile.length()

            // Also publish to MediaStore Pictures/ImageKit if on device
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, format.mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ImageKit")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val uri = context.contentResolver.insert(collection, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(compressFormat, quality.coerceIn(10, 100), out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }
                savedUri = uri
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        SavedImageItem(
            id = java.util.UUID.randomUUID().toString(),
            uri = savedUri,
            localPath = localPath,
            filename = filename,
            width = bitmap.width,
            height = bitmap.height,
            sizeBytes = sizeBytes,
            originalSizeBytes = originalSizeBytes,
            format = format,
            timestamp = timestamp
        )
    }
}
