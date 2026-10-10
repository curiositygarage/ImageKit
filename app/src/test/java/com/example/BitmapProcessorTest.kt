package com.curiositygarage.imagekit

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.curiositygarage.imagekit.model.Adjustments
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.FilterPreset
import com.curiositygarage.imagekit.util.BitmapProcessor
import com.curiositygarage.imagekit.util.SampleImages
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BitmapProcessorTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ImageKit", appName)
  }

  @Test
  fun `test passport dimension resizing`() {
    val sample = SampleImages.createPassportPortraitSample()
    assertNotNull(sample)
    val resized = BitmapProcessor.resizeToDimensions(sample, 600, 600, maintainAspect = false)
    assertEquals(600, resized.width)
    assertEquals(600, resized.height)
  }

  @Test
  fun `test raw photo target byte compression under 500KB`() {
    val rawPhoto = SampleImages.createHighResDeskRawSample()
    assertNotNull(rawPhoto)
    val targetBytes = 500 * 1024L
    val (compressed, format, quality) = BitmapProcessor.compressToTargetBytes(
      source = rawPhoto,
      targetBytes = targetBytes,
      preferredFormat = ExportFormat.WEBP
    )
    assertNotNull(compressed)
    val estSize = BitmapProcessor.estimateFileSize(compressed, format, quality)
    assertTrue("Estimated size $estSize should be <= $targetBytes", estSize <= targetBytes)
  }

  @Test
  fun `test document clean and magic color filter`() {
    val receipt = SampleImages.createReceiptSample()
    assertNotNull(receipt)
    val filtered = BitmapProcessor.applyFilterAndAdjustments(
      receipt,
      FilterPreset.DOC_CLEAN,
      Adjustments()
    )
    assertNotNull(filtered)
    assertEquals(receipt.width, filtered.width)
    assertEquals(receipt.height, filtered.height)
  }

  @Test
  fun `test geometry rotation and flip operations`() {
    val src = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
    val rotated = BitmapProcessor.rotateBitmap(src, 90f)
    assertEquals(200, rotated.width)
    assertEquals(400, rotated.height)

    val flipped = BitmapProcessor.flipBitmap(src, horizontal = true, vertical = false)
    assertEquals(400, flipped.width)
    assertEquals(200, flipped.height)
  }

  @Test
  fun `test crop bitmap with normalized coordinates`() {
    val src = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    val cropped = BitmapProcessor.cropBitmap(src, 0.1f, 0.1f, 0.9f, 0.9f)
    assertEquals(400, cropped.width)
    assertEquals(400, cropped.height)
  }
}
