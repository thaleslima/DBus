package net.dublin.bus.ui.view.route.detail

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import net.dublin.bus.R
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherResponse500
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherRouteInbound200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherRouteOutbound200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherRouteOutboundNoItems200
import net.dublin.bus.ui.view.utilities.MockServer.shutdown
import net.dublin.bus.ui.view.utilities.MockServer.start
import net.dublin.bus.ui.view.utilities.TestUtils
import net.dublin.bus.ui.view.utilities.TestUtils.checkRecyclerHasDescendant
import net.dublin.bus.ui.view.utilities.TestUtils.sleep
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RouteDetailScreenTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(RouteDetailActivity::class.java, false, false)

    @Before
    @Throws(Exception::class)
    fun setup() {
        start()
        IdlingRegistry.getInstance().register()
    }

    @After
    @Throws(Exception::class)
    fun after() {
        shutdown()
        IdlingRegistry.getInstance().unregister()
    }

    private fun loadInbound_DisplayedInUi(launchActivity: Boolean) {
        if (launchActivity) {
            setDispatcherRouteInbound200()
            launchActivity()
        }

        onView(withId(R.id.route_detail_progress_bar_view)).check(matches(not(isDisplayed())))

        //Check Title
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_IN_TOWARDS))))
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("57 stops"))))

        //Check list items
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "3544")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "Beechfield Mnr, Shanganagh Road")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 1, "3552")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 1, "Shanganagh Road, Rathsallagh")

        //Check menu (change direction)
        onView(withId(R.id.route_change_direction_view)).perform(click())
        onView(withText(ROUTE_IN_TOWARDS)).check(matches(isDisplayed()))
        onView(withText(ROUTE_OUT_TOWARDS)).check(matches(isDisplayed()))
    }

    @Test
    fun loadInbound_DisplayedInUi() {
        loadInbound_DisplayedInUi(true)
    }

    @Test
    fun loadInbound_rotateScreen_DisplayedInUi() {
        setDispatcherRouteInbound200()
        launchActivity()

        //Rotate Screen
        val device = UiDevice.getInstance(getInstrumentation())
        device.setOrientationLeft()

        //Check data after rotate
        loadInbound_DisplayedInUi(false)

        //Rotate Screen
        device.setOrientationNatural()
    }

    private fun changeToOutbound_DisplayedInUi(rotate: Boolean) {
        setDispatcherRouteInbound200()
        launchActivity()

        val device = UiDevice.getInstance(getInstrumentation())

        setDispatcherRouteOutbound200()

        //Change direction
        onView(withId(R.id.route_change_direction_view)).perform(click())
        onView(withText(ROUTE_OUT_TOWARDS)).perform(click())

        //Rotate Screen
        if (rotate) {
            device.setOrientationLeft()
        }

        sleep()
        //Check Title
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))))
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))))

        //Check list items
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "4962")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "Mountjoy Square, Mountjoy Sq Nth")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 1, "6059")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 1, "O'Connell St, North Earl Street")

        //Rotate Screen
        if (rotate) {
            device.setOrientationNatural()
        }
    }

    @Test
    fun changeToOutbound_DisplayedInUi() {
        changeToOutbound_DisplayedInUi(false)
    }

    @Test
    fun changeToOutbound_rotateScreen_DisplayedInUi() {
        changeToOutbound_DisplayedInUi(true)
    }

    @Test
    fun loadInbound_withNoItems_DisplayedInUi() {
        setDispatcherRouteOutboundNoItems200()
        launchActivity()

        onView(withId(R.id.route_detail_progress_bar_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("0 stops"))))
    }

    @Test
    fun loadOutbound_withoutInbound_DisplayedInUi() {
        setDispatcherRouteOutbound200()
        launchActivity2()

        //Check Title
        onView(withId(R.id.route_detail_progress_bar_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))))
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))))

        //Check list items
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "4962")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "Mountjoy Square, Mountjoy Sq Nth")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 1, "6059")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 1, "O'Connell St, North Earl Street")

        //Check menu (change direction) without inbound
        onView(withId(R.id.route_change_direction_view)).perform(click())
        onView(withText(ROUTE_OUT_TOWARDS)).check(matches(isDisplayed()))
        onView(withText(ROUTE_IN_TOWARDS)).check(doesNotExist())
    }

    @Test
    fun errorConnection_ShowsErrorUi() {
        setDispatcherResponse500()
        launchActivity()

        //Check message error
        sleep()
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.error_message)
        onView(withText(text)).check(matches(isDisplayed()))
    }

    @Test
    fun errorConnection_retry_DisplayedInUi() {
        setDispatcherResponse500()
        launchActivity()

        //Click Retry
        sleep()
        setDispatcherRouteInbound200()
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.title_retry)
        onView(withText(text)).perform(click())
        sleep()

        //Check reload data
        loadInbound_DisplayedInUi(false)
    }

    @Test
    fun changeToOutbound_errorConnection_retry_DisplayedInUi() {
        setDispatcherRouteInbound200()
        launchActivity()

        //Change to Outbound
        setDispatcherResponse500()
        onView(withId(R.id.route_change_direction_view)).perform(click())
        onView(withText(ROUTE_OUT_TOWARDS)).perform(click())

        //Click Retry
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.title_retry)
        sleep()
        setDispatcherRouteOutbound200()
        onView(withText(text)).perform(click())

        sleep()
        //Check Title
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))))
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))))

        //Check list items
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "4962")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "Mountjoy Square, Mountjoy Sq Nth")
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

    private fun launchActivity2() {
        val intent = Intent()
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_NUMBER, ROUTE_NUMBER)
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_OUT_TOWARDS, ROUTE_OUT_TOWARDS)
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_IN_TOWARDS, "")

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
