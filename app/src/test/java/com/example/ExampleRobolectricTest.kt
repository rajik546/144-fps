package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.engine.VirtualizationFeasibilityEngine
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
    assertEquals("V-Phone 144", appName)
  }

  @Test
  fun `verify feasibility investigation runs and provides safe memory floor`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = VirtualizationFeasibilityEngine(context)
    val report = engine.runInvestigation()

    assertNotNull(report)
    assertTrue("Total RAM should be positive", report.totalRamMb > 0)
    assertTrue("Safe allocatable RAM should be calculated", report.safeAllocatableRamMb >= 1024)
    assertNotNull(report.recommendedEngineMode)
    assertNotNull(report.technicalVerdict)
  }

  @Test
  fun `verify device profile structure supports 144Hz preference`() {
    val profile = DeviceProfile(
      id = 1,
      profileName = "ASUS ROG Phone 8",
      manufacturer = "ASUS",
      brand = "ASUS",
      model = "ROG Phone 8",
      deviceName = "ASUS_AI2401",
      displayRefreshRate = 144,
      isDefault = false
    )
    assertEquals(144, profile.displayRefreshRate)
    assertEquals("ROG Phone 8", profile.model)
  }
}
