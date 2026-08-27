package com.curiositygarage.imagekit.model

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

enum class NavigationTab(val label: String, val icon: String) {
    HOME("Home", "🏠"),
    STUDIO("Studio", "🖼️"),
    BATCH("Batch", "🛠️"),
    LIBRARY("Library", "📁")
}

enum class EditTool(val label: String, val description: String, val icon: String) {
    RESIZE_CONVERT("HTML & Web Optimizer", "Passport, web form & target size compressor", "🌐"),
    CROP_ROTATE("Crop & Orient", "Passport 2x2, Document A4, Rotate & Flip", "📐"),
    FILTERS("Doc & Lens Filter", "Receipt cleaner, whiteboard, magic color", "🧾"),
    AUTO_ENHANCE("Auto-Clarity", "One-tap desk exposure & shadow balance", "✨"),
    ADJUST("Adjustments", "Brightness, contrast, saturation & warmth", "🎛️"),
    REDACT_MARKUP("Privacy & Markup", "Blackout, pixelate & annotate", "🛡️")
}

enum class AspectRatioPreset(val label: String, val ratio: Float?) {
    FREE("Free", null),
    PASSPORT_2_2("Passport 2×2 (1:1)", 1.0f),
    VISA_3_4("Visa / ID (3:4)", 3f / 4f),
    DOC_A4("A4 / Letter", 1f / 1.414f),
    RECEIPT_STRIP("Receipt (1:2.5)", 1f / 2.5f),
    DESKTOP_16_9("16:9 Screen", 16f / 9f),
    PHOTO_4_3("4:3 Photo", 4f / 3f),
    PHOTO_3_2("3:2 Classic", 3f / 2f)
}

enum class WebUploadPreset(
    val title: String,
    val subtitle: String,
    val icon: String,
    val targetWidth: Int?,
    val targetHeight: Int?,
    val maxBytes: Long?,
    val defaultFormat: ExportFormat,
    val defaultQuality: Int
) {
    PASSPORT_ID(
        title = "Passport / Visa Photo",
        subtitle = "600×600 px • Under 240 KB (Gov/Visa Portal Spec)",
        icon = "🛂",
        targetWidth = 600,
        targetHeight = 600,
        maxBytes = 240 * 1024L,
        defaultFormat = ExportFormat.JPEG,
        defaultQuality = 85
    ),
    WEB_FORM_UPLOAD(
        title = "HTML / Web Form Upload",
        subtitle = "Max 1200px • Under 500 KB (Avoids 413 Payload Errors)",
        icon = "🌐",
        targetWidth = 1200,
        targetHeight = 1200,
        maxBytes = 500 * 1024L,
        defaultFormat = ExportFormat.WEBP,
        defaultQuality = 80
    ),
    SEARCH_SEO_THUMB(
        title = "Search & Thumbnail",
        subtitle = "Max 600px • Ultra-compact WebP (< 80 KB)",
        icon = "🔍",
        targetWidth = 600,
        targetHeight = 600,
        maxBytes = 80 * 1024L,
        defaultFormat = ExportFormat.WEBP,
        defaultQuality = 75
    ),
    DESKTOP_WEBAPP(
        title = "Desktop / WebApp (1080p)",
        subtitle = "1920×1080 FHD • Fast load (< 1 MB)",
        icon = "💻",
        targetWidth = 1920,
        targetHeight = 1080,
        maxBytes = 1024 * 1024L,
        defaultFormat = ExportFormat.WEBP,
        defaultQuality = 85
    ),
    AVATAR_PROFILE(
        title = "Profile / Avatar",
        subtitle = "400×400 px Square • Under 100 KB",
        icon = "👤",
        targetWidth = 400,
        targetHeight = 400,
        maxBytes = 100 * 1024L,
        defaultFormat = ExportFormat.JPEG,
        defaultQuality = 85
    ),
    EMAIL_LIGHT(
        title = "Email Attachment",
        subtitle = "Quick lightweight downscale (< 200 KB)",
        icon = "✉️",
        targetWidth = 800,
        targetHeight = 800,
        maxBytes = 200 * 1024L,
        defaultFormat = ExportFormat.JPEG,
        defaultQuality = 75
    )
}

enum class FilterPreset(val label: String, val description: String) {
    ORIGINAL("Original", "No filter applied"),
    DOC_CLEAN("Clean Document", "Binarize gray paper to pure white & darken receipt ink"),
    WHITEBOARD("Whiteboard Pop", "Strip room glare, enhance handwriting lines"),
    MAGIC_COLOR("Magic Color", "Vibrant colors with white paper cast removal"),
    AUTO_ENHANCE("Auto Clarity", "Dynamic exposure and shadow balance"),
    BW_CONTRAST("B&W Contrast", "High-contrast clean monochrome"),
    VIVID("Vivid Pop", "Boost saturation for hobby craft photos"),
    VINTAGE_WARM("Warm Craft", "Subtle warm wood tones"),
    COOL_SLATE("Cool Slate", "Modern sleek cyan/slate wash"),
    INVERT("Invert Schematic", "Dark mode blueprint inverter")
}

enum class MarkupType(val label: String, val icon: String) {
    BLACKOUT("Blackout Box", "⬛"),
    PIXELATE("Pixelate Box", "▫️"),
    BLUR_BOX("Blur Box", "🌫️"),
    HIGHLIGHT("Highlight", "🖍️"),
    ARROW("Arrow Callout", "➡️"),
    TEXT("Text Note", "🏷️")
}

data class MarkupItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: MarkupType,
    val rect: Rect = Rect(0.1f, 0.1f, 0.4f, 0.25f), // Normalized 0..1 coordinates
    val startOffset: Offset = Offset(0.2f, 0.2f),
    val endOffset: Offset = Offset(0.5f, 0.4f),
    val text: String = "NOTE",
    val colorArgb: Long = 0xFF000000
)

enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
    JPEG("jpg", "image/jpeg", "JPEG (Universal)"),
    WEBP("webp", "image/webp", "WebP (Modern Web / Smallest)"),
    PNG("png", "image/png", "PNG (Lossless)")
}

enum class ResizeScale(val label: String, val factor: Float) {
    FULL("100% (Original)", 1.0f),
    SCALE_75("75%", 0.75f),
    SCALE_50("50% (Half)", 0.5f),
    SCALE_25("25% (Email)", 0.25f),
    HD_1080("1080p FHD", -1080f),
    HD_720("720p HD", -720f),
    WEB_800("800px Web", -800f),
    PASSPORT_600("600px Passport", -600f),
    THUMB_400("400px Thumb", -400f)
}

data class TargetSizePreset(val label: String, val bytes: Long)

val TARGET_SIZE_PRESETS = listOf(
    TargetSizePreset("< 100 KB", 100 * 1024L),
    TargetSizePreset("< 240 KB (Passport)", 240 * 1024L),
    TargetSizePreset("< 500 KB (Web Form)", 500 * 1024L),
    TargetSizePreset("< 1 MB (Email)", 1024 * 1024L),
    TargetSizePreset("< 2 MB", 2 * 1024 * 1024L)
)

data class Adjustments(
    val brightness: Float = 0f,   // -100 to +100
    val contrast: Float = 0f,     // -100 to +100
    val saturation: Float = 0f,   // -100 to +100
    val warmth: Float = 0f,       // -100 to +100
    val vignette: Float = 0f,     // 0 to 100
    val sharpen: Float = 0f       // 0 to 100
) {
    val isDefault: Boolean
        get() = brightness == 0f && contrast == 0f && saturation == 0f && warmth == 0f && vignette == 0f && sharpen == 0f
}

data class SavedImageItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val localPath: String = "",
    val filename: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val sizeBytes: Long = 0L,
    val originalSizeBytes: Long = 0L,
    val format: ExportFormat = ExportFormat.JPEG,
    val timestamp: Long = System.currentTimeMillis()
)

