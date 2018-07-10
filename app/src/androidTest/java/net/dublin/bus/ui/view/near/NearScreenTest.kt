package net.dublin.bus.ui.view.near

import android.Manifest
import android.content.Intent
import android.os.SystemClock
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObjectNotFoundException
import android.support.test.uiautomator.UiSelector
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
