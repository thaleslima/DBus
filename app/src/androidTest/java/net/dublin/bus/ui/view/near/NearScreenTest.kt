package net.dublin.bus.ui.view.near

import android.Manifest
import android.content.Intent
import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import net.dublin.bus.BuildConfig
import net.dublin.bus.R
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.utilities.TestUtils
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters.NAME_ASCENDING
import java.io.UnsupportedEncodingException

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(NAME_ASCENDING)
@LargeTest
class NearScreenTest {
    private val device = UiDevice.getInstance(getInstrumentation())

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

    @get:Rule
    var locationPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    @Throws(InterruptedException::class, UnsupportedEncodingException::class)
    fun changeMapMarker_DisplayedInUi() {
        launchActivity()
        SystemClock.sleep(500)
        onView(withId(R.id.navigation_near)).perform(click())


        if (BuildConfig.MOCK_MAP) {
            SystemClock.sleep(1000)
            onView(withText("O'Connell St, O'Connell Bridge")).check(matches(isDisplayed()))
            onView(withText("58 m")).check(matches(isDisplayed()))

            val marker = device.findObject(UiSelector().descriptionContains("stopNumber274"))
            try {
                marker.click()
            } catch (e: UiObjectNotFoundException) {
                e.printStackTrace()
            }
        }

        //SystemClock.sleep(1500);
        //onView(withText("O'Connell St, Henry Street")).check(matches(isDisplayed()));
        //onView(withText("334 m")).check(matches(isDisplayed()));
    }

    private fun launchActivity() {
        val intent = Intent()
        activityTestRule.launchActivity(intent)
        TestUtils.sleepLong()
    }
}
