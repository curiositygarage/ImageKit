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
class ExampleRobolectricTest {

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
    val resized = BitmapProcessor.resizeToDimensions(sample, 600, 600, maintainAspectRatio = false)
    assertEquals(600, resized.width)
    assertEquals(600, resized.height)
  }

  @Test
  fun `test raw photo target byte compression under 500KB`() {
    val rawPhoto = SampleImages.createHighResDeskRawSample()
    assertNotNull(rawPhoto)
    val targetBytes = 500 * 1024L
    val (compressed, format, quality) = BitmapProcessor.compressToTargetBytes(
      bitmap = rawPhoto,
      maxBytes = targetBytes,
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
  }
}
