package net.dublin.bus.ui.view.realtime

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import net.dublin.bus.R
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherNoItemsResponse200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherRealTimeResponse200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherResponse500
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherTimeWarningNoItemsResponse200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherTimeWarningWithItemsResponse200
import net.dublin.bus.ui.view.utilities.MockServer.shutdown
import net.dublin.bus.ui.view.utilities.MockServer.start
import net.dublin.bus.ui.view.utilities.TestUtils.checkRecyclerHasDescendant
import net.dublin.bus.ui.view.utilities.TestUtils.sleep
import net.dublin.bus.ui.view.utilities.TestUtils.swipeRefresh
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RealTimeScreenTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(RealTimeActivity::class.java, false, false)

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

    private fun loadData_DisplayedInUi(launchActivity: Boolean) {
        if (launchActivity) {
            setDispatcherRealTimeResponse200()
            launchActivity()
        }

        sleep()
        onView(withId(R.id.real_progress_bar_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))))
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))))
        onView(withId(R.id.real_message_empty_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_line_note_view)).check(matches(not<View>(isDisplayed())))

        checkRecyclerHasDescendant(R.id.real_recycler_view, 0, "38")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 0, "Damastown via Corduff")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 0, "3 min")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 1, "46a")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 1, "Phoenix Pk via Donnybrook")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 1, "9 min")
    }

    @Test
    fun loadData_DisplayedInUi() {
        loadData_DisplayedInUi(true)
    }

    @Test
    fun serviceWithError_UpdateSwipe_DisplayedInUi() {
        setDispatcherResponse500()
        launchActivity()
        setDispatcherRealTimeResponse200()
        swipeRefresh(R.id.real_swipe_refresh_layout)
        loadData_DisplayedInUi(false)
    }

    @Test
    fun loadData_updateSwipe_apiWithError_showsErrorUi() {
        loadData_DisplayedInUi(true)
        setDispatcherResponse500()

        swipeRefresh(R.id.real_swipe_refresh_layout)
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.error_message)
        onView(withText(text)).check(matches(isDisplayed()))
        loadData_DisplayedInUi(false)
    }

    @Test
    fun noItemsAndWarmingMessage_updateSwipe_DisplayedInUi() {
        setDispatcherTimeWarningNoItemsResponse200()
        launchActivity()

        //Check no items and warming message
        onView(withId(R.id.real_progress_bar_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()))
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()))
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_error_message)
        onView(withText(text)).check(matches(isDisplayed()))
        onView(withText(WARNING)).check(matches(isDisplayed()))

        //Reload Data and check api with items
        setDispatcherRealTimeResponse200()
        swipeRefresh(R.id.real_swipe_refresh_layout)
        loadData_DisplayedInUi(false)
    }


    @Test
    fun withItemsAndWarmingMessage_DisplayedInUi() {
        setDispatcherTimeWarningWithItemsResponse200()
        launchActivity()

        onView(withId(R.id.real_progress_bar_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))))
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))))
        onView(withId(R.id.real_message_empty_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()))

        //Check warning
        onView(withText(startsWith(WARNING_2))).check(matches(isDisplayed()))

        //Check items
        checkRecyclerHasDescendant(R.id.real_recycler_view, 0, "1")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 0, "Shanard Road via O'Connell Street")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 0, "2 min")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 1, "11")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 1, "St Pappin's Rd via Drumcondra")
        checkRecyclerHasDescendant(R.id.real_recycler_view, 1, "16 min")
    }

    @Test
    fun withItemsAndWarmingMessage_updateSwipe_DisplayedInUi() {
        setDispatcherTimeWarningWithItemsResponse200()
        launchActivity()

        //Check warning
        onView(withId(R.id.real_progress_bar_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))))
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))))
        onView(withId(R.id.real_message_empty_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()))

        //Reload data without warming
        setDispatcherRealTimeResponse200()
        swipeRefresh(R.id.real_swipe_refresh_layout)
        loadData_DisplayedInUi(false)
    }

    @Test
    fun noItemsAndWarmingMessage_DisplayedInUi() {
        setDispatcherTimeWarningNoItemsResponse200()
        launchActivity()

        onView(withId(R.id.real_progress_bar_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()))
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()))
    }

    @Test
    fun noItems_showsMessageUi() {
        setDispatcherNoItemsResponse200()
        launchActivity()

        onView(withId(R.id.real_progress_bar_view)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))))
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))))
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()))

        val messageErrorRequired = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_error_message)
        onView(withText(messageErrorRequired)).check(matches(isDisplayed()))
    }

    @Test
    fun errorConnection_ShowsErrorUi() {
        setDispatcherResponse500()
        launchActivity()

        sleep()
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.error_message)
        onView(withText(text)).check(matches(isDisplayed()))
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()))
    }

    @Test
    fun errorConnection_retry_DisplayedInUi() {
        setDispatcherResponse500()
        launchActivity()

        setDispatcherRealTimeResponse200()
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.title_retry)
        sleep()
        onView(withText(text)).perform(click())
        loadData_DisplayedInUi(false)
    }

    private fun launchActivity() {
        val intent = Intent()
        intent.putExtra(RealTimeActivity.EXTRA_DESCRIPTION, STOP_DESCRIPTION)
        intent.putExtra(RealTimeActivity.EXTRA_STOP_NUMBER, STOP_NUMBER)
        activityTestRule.launchActivity(intent)
        sleep()
    }

    companion object {
        private const val STOP_NUMBER = "2"
        private const val STOP_DESCRIPTION = "TEST 2"

        private const val WARNING = "Due to weather conditions, there will be no Dublin Bus services Thurs. 1st or Fri. 2nd March. See dublinbus.ie for updates."
        private const val WARNING_2 = "Red Weather Alert - Dublin Bus services are experiencing significant delays due to weather conditions."
    }
}
