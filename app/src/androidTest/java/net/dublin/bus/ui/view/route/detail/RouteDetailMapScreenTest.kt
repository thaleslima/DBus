package net.dublin.bus.ui.view.route.detail

import android.Manifest
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
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
import net.dublin.bus.ui.view.utilities.MockServer
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherInboundResponse200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherInboundResponse2_200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherResponse500
import net.dublin.bus.ui.view.utilities.TestUtils
import net.dublin.bus.ui.view.utilities.TestUtils.sleep
import org.hamcrest.Matchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RouteDetailMapScreenTest {
    private val device = UiDevice.getInstance(getInstrumentation())

    @get:Rule
    val activityTestRule = ActivityTestRule(RouteDetailActivity::class.java, false, false)

    @get:Rule
    var locationPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    @Throws(Exception::class)
    fun setup() {
        MockServer.start()
        IdlingRegistry.getInstance().register()
    }

    @After
    @Throws(Exception::class)
    fun after() {
        MockServer.shutdown()
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun loadMap_clickMarker_DisplayedInUi() {
        setDispatcherInboundResponse200()
        launchActivity()

        if (!BuildConfig.MOCK_MAP) return

        //Change map
        onView(withId(R.id.route_change_map_list_view)).perform(click())
        sleep()

        //Click marker
        clickMarker("stopNumber3544")

        //Check marker selected
        sleep()
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()))
        onView(withId(R.id.route_detail_map_description_stop_view)).check(matches(withText(startsWith("Beechfield Mnr, Shanganagh Road"))))
        onView(withId(R.id.route_detail_map_routes_view)).check(matches(withText(startsWith("40, 40B, 40D"))))

        //Change list
        sleep()
        onView(withId(R.id.route_change_map_list_view)).perform(click())
    }

    @Test
    fun loadMap_clickSecondMarker_DisplayedInUi() {
        setDispatcherInboundResponse200()
        launchActivity()

        if (!BuildConfig.MOCK_MAP) return

        //Change map
        onView(withId(R.id.route_change_map_list_view)).perform(click())

        //Click marker 1
        clickMarker("stopNumber3544")

        //Click marker 2
        setDispatcherInboundResponse2_200()
        clickMarker("stopNumber3552")

        //Check second marker selected
        sleep()
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()))
        onView(withId(R.id.route_detail_map_description_stop_view)).check(matches(withText(startsWith("Shanganagh Road, Hazelwood"))))
        onView(withId(R.id.route_detail_map_routes_view)).check(matches(withText(startsWith("20D, 27, 40"))))

        //Change list
        sleep()
        onView(withId(R.id.route_change_map_list_view)).perform(click())
    }

    private fun clickMarker(description: String) {
        val marker = device.findObject(UiSelector().descriptionContains(description))
        try {
            marker.click()
            sleep()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun loadMap_clickMarker_rotateScreen_DisplayedInUi() {
        setDispatcherInboundResponse200()
        launchActivity()

        if (!BuildConfig.MOCK_MAP) return

        //Change map
        onView(withId(R.id.route_change_map_list_view)).perform(click())

        //Click marker
        clickMarker("stopNumber3544")

        //Rotate Screen
        device.setOrientationLeft()

        //Check marker selected
        sleep()
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()))
        onView(withId(R.id.route_detail_map_description_stop_view)).check(matches(withText(startsWith("Beechfield Mnr, Shanganagh Road"))))
        onView(withId(R.id.route_detail_map_routes_view)).check(matches(withText(startsWith("40, 40B, 40D"))))

        //Change list
        sleep()
        onView(withId(R.id.route_change_map_list_view)).perform(click())

        //Rotate Screen
        device.setOrientationNatural()
    }

    @Test
    fun loadMap_clickMarker_errorConnection_ShowsErrorUi() {
        setDispatcherInboundResponse200()
        launchActivity()

        if (!BuildConfig.MOCK_MAP) return

        //Change map
        onView(withId(R.id.route_change_map_list_view)).perform(click())

        //Click marker
        setDispatcherResponse500()
        clickMarker("stopNumber3544")

        //Check message error route
        sleep()
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()))
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.route_detail_error)
        onView(withId(R.id.route_detail_map_routes_view)).check(matches(withText(startsWith(text))))

        //Change list
        sleep()
        onView(withId(R.id.route_change_map_list_view)).perform(click())
    }

    @Test
    fun loadMap_clickMarker_rotateScreen_errorConnection_ShowsErrorUi() {
        setDispatcherInboundResponse200()
        launchActivity()

        if (!BuildConfig.MOCK_MAP) return

        //Change map
        onView(withId(R.id.route_change_map_list_view)).perform(click())

        //Click marker
        setDispatcherResponse500()
        clickMarker("stopNumber3544")

        //Rotate screen
        device.setOrientationLeft()

        //Check message error route
        sleep()
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()))
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.route_detail_error)
        onView(withId(R.id.route_detail_map_routes_view)).check(matches(withText(startsWith(text))))

        //Change list
        sleep()
        onView(withId(R.id.route_change_map_list_view)).perform(click())

        //Rotate screen
        device.setOrientationNatural()
    }


    private fun launchActivity() {
        val intent = Intent()
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_NUMBER, ROUTE_NUMBER)
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_OUT_TOWARDS, ROUTE_OUT_TOWARDS)
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_IN_TOWARDS, ROUTE_IN_TOWARDS)
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_CODE, ROUTE_CODE)

        activityTestRule.launchActivity(intent)
        TestUtils.sleepLong()
    }

    companion object {
        private const val ROUTE_NUMBER = "7b"
        private const val ROUTE_IN_TOWARDS = "route_inbound_towards"
        private const val ROUTE_OUT_TOWARDS = "route_outbound_towards"
        private const val ROUTE_CODE = "113"
    }
}
