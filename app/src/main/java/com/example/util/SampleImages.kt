package com.curiositygarage.imagekit.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

object SampleImages {

    fun createReceiptSample(): Bitmap {
        val width = 800
        val height = 1200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Desk wood/slate background
        val bgPaint = Paint().apply { color = Color.rgb(40, 44, 52) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Paper receipt with slight shadow and warm white texture
        val shadowPaint = Paint().apply { color = Color.argb(120, 0, 0, 0) }
        canvas.drawRoundRect(RectF(100f, 80f, 720f, 1140f), 16f, 16f, shadowPaint)

        val paperPaint = Paint().apply { color = Color.rgb(248, 246, 240) }
        canvas.drawRoundRect(RectF(90f, 70f, 710f, 1130f), 12f, 12f, paperPaint)

        // Receipt header
        val textPaint = Paint().apply {
            color = Color.rgb(30, 30, 30)
            isAntiAlias = true
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("HOBBY SUPPLY & HARDWARE", 400f, 160f, textPaint)

        textPaint.textSize = 22f
        textPaint.isFakeBoldText = false
        canvas.drawText("Desk & Workshop Essentials", 400f, 200f, textPaint)
        canvas.drawText("Invoice #HB-84920 • 2026-08-25", 400f, 235f, textPaint)

        // Divider line
        val linePaint = Paint().apply {
            color = Color.rgb(180, 180, 180)
            strokeWidth = 2f
        }
        canvas.drawLine(140f, 265f, 660f, 265f, linePaint)

        // Items list
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = 24f
        val items = listOf(
            "1x PLA+ Filament Spool 1.75mm (Grey)" to "$24.99",
            "1x Soldering Tip Cleaner Wire Sponge" to "$6.50",
            "1x Digital Caliper 150mm Stainless" to "$18.25",
            "1x M3 Hex Screw Assortment Kit 300pc" to "$12.40",
            "2x ESD Anti-Static Tweezers Set" to "$8.80",
            "1x Desk Silicone Solder Heat Mat" to "$15.00",
            "1x USB-C Logic Analyzer Cable" to "$9.95"
        )

        var y = 320f
        for ((item, price) in items) {
            textPaint.color = Color.rgb(40, 40, 40)
            canvas.drawText(item, 140f, y, textPaint)
            val pricePaint = Paint(textPaint).apply {
                textAlign = Paint.Align.RIGHT
                isFakeBoldText = true
            }
            canvas.drawText(price, 660f, y, pricePaint)
            y += 55f
        }

        canvas.drawLine(140f, y + 20f, 660f, y + 20f, linePaint)
        y += 70f

        val totalPaint = Paint().apply {
            color = Color.rgb(20, 20, 20)
            textSize = 28f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("SUBTOTAL", 140f, y, totalPaint)
        canvas.drawText("$95.89", 570f, y, totalPaint)
        y += 45f
        canvas.drawText("TAX (8.5%)", 140f, y, totalPaint)
        canvas.drawText("$8.15", 590f, y, totalPaint)
        y += 50f
        totalPaint.textSize = 34f
        totalPaint.color = Color.rgb(0, 75, 125)
        canvas.drawText("TOTAL PAID", 140f, y, totalPaint)
        canvas.drawText("$104.04", 530f, y, totalPaint)

        // Barcode at bottom
        val barcodePaint = Paint().apply { color = Color.rgb(40, 40, 40) }
        var bx = 220f
        while (bx < 580f) {
            val barW = (if ((bx.toInt() % 7) == 0) 6f else 3f)
            canvas.drawRect(bx, 980f, bx + barW, 1050f, barcodePaint)
            bx += barW + if ((bx.toInt() % 5) == 0) 7f else 4f
        }

        return bitmap
    }

    fun create3DPrintSample(): Bitmap {
        val width = 1000
        val height = 750
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark workbench mat with millimeter grid lines
        val matPaint = Paint().apply { color = Color.rgb(28, 35, 42) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), matPaint)

        val gridPaint = Paint().apply {
            color = Color.rgb(45, 55, 66)
            strokeWidth = 1.5f
        }
        for (x in 0..width step 40) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }
        for (y in 0..height step 40) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }

        // Geometric 3D printed mechanical bracket model
        val shadowPaint = Paint().apply { color = Color.argb(100, 10, 10, 15) }
        canvas.drawOval(RectF(280f, 480f, 760f, 620f), shadowPaint)

        val bodyPaint = Paint().apply {
            color = Color.rgb(0, 150, 214)
            isAntiAlias = true
        }
        val path = Path().apply {
            moveTo(360f, 240f)
            lineTo(640f, 200f)
            lineTo(720f, 380f)
            lineTo(620f, 520f)
            lineTo(320f, 500f)
            lineTo(260f, 350f)
            close()
        }
        canvas.drawPath(path, bodyPaint)

        // Facets and highlights
        val facetPaint = Paint().apply {
            color = Color.rgb(60, 185, 245)
            isAntiAlias = true
        }
        val topPath = Path().apply {
            moveTo(360f, 240f)
            lineTo(640f, 200f)
            lineTo(560f, 320f)
            lineTo(300f, 330f)
            close()
        }
        canvas.drawPath(topPath, facetPaint)

        // Screw holes
        val holePaint = Paint().apply {
            color = Color.rgb(18, 22, 28)
            isAntiAlias = true
        }
        canvas.drawCircle(420f, 280f, 25f, holePaint)
        canvas.drawCircle(540f, 420f, 35f, holePaint)
        canvas.drawCircle(360f, 420f, 20f, holePaint)

        // Annotations
        val labelPaint = Paint().apply {
            color = Color.rgb(208, 228, 255)
            textSize = 28f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("3D PROTOTYPE: SERVO BRACKET v2.1", 50f, 60f, labelPaint)

        val subPaint = Paint().apply {
            color = Color.rgb(160, 175, 195)
            textSize = 20f
            isAntiAlias = true
        }
        canvas.drawText("Material: PETG | Infill: 30% Gyroid | Tolerance: ±0.15mm", 50f, 95f, subPaint)

        return bitmap
    }

    fun createShippingLabelSample(): Bitmap {
        val width = 800
        val height = 1000
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Cardboard background
        val bgPaint = Paint().apply { color = Color.rgb(198, 156, 109) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // White shipping label sticker
        val labelBg = Paint().apply { color = Color.WHITE }
        canvas.drawRoundRect(RectF(60f, 60f, 740f, 940f), 16f, 16f, labelBg)

        val border = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        canvas.drawRoundRect(RectF(80f, 80f, 720f, 920f), 8f, 8f, border)

        val textPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 36f
            isFakeBoldText = true
        }
        canvas.drawText("PRIORITY EXPRESS", 110f, 140f, textPaint)

        textPaint.textSize = 22f
        textPaint.isFakeBoldText = false
        canvas.drawText("FROM:", 110f, 190f, textPaint)
        canvas.drawText("Home Office Maker Studio", 110f, 220f, textPaint)
        canvas.drawText("742 Evergreen Terrace, Suite B", 110f, 250f, textPaint)
        canvas.drawText("Springfield, OR 97477", 110f, 280f, textPaint)

        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 2f
        }
        canvas.drawLine(110f, 310f, 690f, 310f, linePaint)

        textPaint.textSize = 24f
        textPaint.isFakeBoldText = true
        canvas.drawText("SHIP TO [CONFIDENTIAL]:", 110f, 360f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText("Alex Morgan (Personal Workbench)", 110f, 400f, textPaint)
        canvas.drawText("1042 Industrial Parkway, Bay 4", 110f, 435f, textPaint)
        canvas.drawText("Austin, TX 78701", 110f, 470f, textPaint)
        canvas.drawText("Direct Phone: (512) 555-0198", 110f, 505f, textPaint)

        canvas.drawLine(110f, 535f, 690f, 535f, linePaint)

        textPaint.textSize = 30f
        textPaint.isFakeBoldText = true
        canvas.drawText("TRACKING: 9400 1102 0088 3921 9012 44", 110f, 590f, textPaint)

        // Big barcode
        val barcodePaint = Paint().apply { color = Color.BLACK }
        var bx = 120f
        while (bx < 680f) {
            val barW = (if ((bx.toInt() % 11) < 4) 8f else 4f)
            canvas.drawRect(bx, 620f, bx + barW, 760f, barcodePaint)
            bx += barW + if ((bx.toInt() % 6) == 0) 8f else 5f
        }

        // Stamp badge
        val stampPaint = Paint().apply {
            color = Color.rgb(200, 30, 30)
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(RectF(480f, 800f, 690f, 880f), 8f, 8f, stampPaint)
        val stampText = Paint().apply {
            color = Color.rgb(200, 30, 30)
            textSize = 26f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("FRAGILE", 585f, 850f, stampText)

        return bitmap
    }

    // Portrait photo ideal for demonstrating Passport 600x600 px cropping & <240KB web upload compression
    fun createPassportPortraitSample(): Bitmap {
        val width = 1200
        val height = 1600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Off-white / light blue studio wall backdrop
        val bgPaint = Paint().apply { color = Color.rgb(225, 232, 240) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Gradient lighting vignette on backdrop
        val vignettePaint = Paint().apply { color = Color.argb(40, 0, 30, 60) }
        canvas.drawCircle(600f, 600f, 750f, vignettePaint)

        // Shoulder silhouette (dark blue blazer)
        val clothingPaint = Paint().apply {
            color = Color.rgb(28, 44, 68)
            isAntiAlias = true
        }
        val shoulderPath = Path().apply {
            moveTo(200f, 1600f)
            quadTo(350f, 1050f, 600f, 1020f)
            quadTo(850f, 1050f, 1000f, 1600f)
            close()
        }
        canvas.drawPath(shoulderPath, clothingPaint)

        // White collared shirt inner V
        val shirtPaint = Paint().apply {
            color = Color.rgb(250, 252, 255)
            isAntiAlias = true
        }
        val shirtPath = Path().apply {
            moveTo(500f, 1020f)
            lineTo(600f, 1250f)
            lineTo(700f, 1020f)
            close()
        }
        canvas.drawPath(shirtPath, shirtPaint)

        // Neck
        val skinPaint = Paint().apply {
            color = Color.rgb(238, 192, 164)
            isAntiAlias = true
        }
        canvas.drawRect(520f, 850f, 680f, 1030f, skinPaint)

        // Head oval
        canvas.drawOval(RectF(400f, 450f, 800f, 920f), skinPaint)

        // Hair (Dark charcoal neat crop)
        val hairPaint = Paint().apply {
            color = Color.rgb(45, 35, 30)
            isAntiAlias = true
        }
        val hairPath = Path().apply {
            moveTo(390f, 600f)
            quadTo(400f, 380f, 600f, 370f)
            quadTo(800f, 380f, 810f, 600f)
            quadTo(760f, 480f, 600f, 470f)
            quadTo(440f, 480f, 390f, 600f)
            close()
        }
        canvas.drawPath(hairPath, hairPaint)

        // Eyes & Eyebrows
        val eyePaint = Paint().apply {
            color = Color.rgb(40, 30, 25)
            isAntiAlias = true
        }
        canvas.drawOval(RectF(480f, 620f, 540f, 650f), eyePaint)
        canvas.drawOval(RectF(660f, 620f, 720f, 650f), eyePaint)

        // Eyebrows
        val browPaint = Paint().apply {
            color = Color.rgb(50, 40, 35)
            strokeWidth = 10f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        canvas.drawLine(470f, 600f, 550f, 595f, browPaint)
        canvas.drawLine(650f, 595f, 730f, 600f, browPaint)

        // Smile
        val mouthPaint = Paint().apply {
            color = Color.rgb(190, 110, 95)
            strokeWidth = 8f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        val mouthPath = Path().apply {
            moveTo(540f, 780f)
            quadTo(600f, 820f, 660f, 780f)
        }
        canvas.drawPath(mouthPath, mouthPaint)

        // Top info banner for demo
        val tagBg = Paint().apply { color = Color.argb(180, 0, 0, 0) }
        canvas.drawRoundRect(RectF(50f, 50f, 1150f, 150f), 20f, 20f, tagBg)
        val tagText = Paint().apply {
            color = Color.WHITE
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("RAW PORTRAIT (1200×1600 px • ~3.8 MB)", 600f, 110f, tagText)

        return bitmap
    }

    // High resolution raw desk workbench photo (2400x1600 px) to showcase drastic HTML/Web shrinking
    fun createHighResDeskRawSample(): Bitmap {
        val width = 2400
        val height = 1600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Heavy walnut wooden desk texture
        val woodBg = Paint().apply { color = Color.rgb(65, 48, 38) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), woodBg)

        // Wood grain planks
        val grainPaint = Paint().apply {
            color = Color.rgb(55, 38, 28)
            strokeWidth = 6f
        }
        for (y in 0..height step 180) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), grainPaint)
        }

        // Cutting mat in center
        val matPaint = Paint().apply { color = Color.rgb(32, 60, 48) }
        canvas.drawRoundRect(RectF(300f, 300f, 2100f, 1400f), 24f, 24f, matPaint)

        // Grid on cutting mat
        val gridPaint = Paint().apply {
            color = Color.rgb(45, 85, 65)
            strokeWidth = 3f
        }
        for (x in 300..2100 step 80) {
            canvas.drawLine(x.toFloat(), 300f, x.toFloat(), 1400f, gridPaint)
        }
        for (y in 300..1400 step 80) {
            canvas.drawLine(300f, y.toFloat(), 2100f, y.toFloat(), gridPaint)
        }

        // Metal ruler
        val rulerPaint = Paint().apply { color = Color.rgb(180, 185, 195) }
        canvas.drawRoundRect(RectF(400f, 1150f, 2000f, 1280f), 10f, 10f, rulerPaint)

        // Soldering iron station
        val toolBg = Paint().apply { color = Color.rgb(20, 24, 30) }
        canvas.drawRoundRect(RectF(450f, 450f, 950f, 950f), 30f, 30f, toolBg)
        val dialPaint = Paint().apply { color = Color.rgb(0, 180, 216) }
        canvas.drawCircle(700f, 700f, 120f, dialPaint)

        // Brass brass tip cleaner
        val brassPaint = Paint().apply { color = Color.rgb(212, 175, 55) }
        canvas.drawCircle(1300f, 650f, 140f, brassPaint)

        // Circuit PCB board
        val pcbPaint = Paint().apply { color = Color.rgb(0, 110, 60) }
        canvas.drawRoundRect(RectF(1550f, 500f, 2000f, 950f), 16f, 16f, pcbPaint)

        // Raw Camera Exif banner
        val exifBg = Paint().apply { color = Color.argb(220, 15, 18, 24) }
        canvas.drawRoundRect(RectF(80f, 80f, 2320f, 220f), 24f, 24f, exifBg)

        val exifTitle = Paint().apply {
            color = Color.rgb(208, 228, 255)
            textSize = 52f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("📸 RAW WORKBENCH PHOTO (2400×1600 px • 8.4 MB RAW)", 140f, 155f, exifTitle)

        val exifSub = Paint().apply {
            color = Color.rgb(160, 180, 200)
            textSize = 34f
            isAntiAlias = true
        }
        canvas.drawText("Too large for standard HTML forms (>2MB limit). Tap 'Web Optimizer' to shrink to <500KB instantly!", 140f, 200f, exifSub)

        return bitmap
    }
}
