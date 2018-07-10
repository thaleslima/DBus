package net.dublin.bus.ui.view.search

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.dublin.bus.R
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.utilities.MockServer
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherRealTimeResponse200
import net.dublin.bus.ui.view.utilities.MockServer.setDispatcherRouteInbound200
import net.dublin.bus.ui.view.utilities.TestUtils
import net.dublin.bus.ui.view.utilities.TestUtils.checkRecyclerHasDescendant
import net.dublin.bus.ui.view.utilities.TestUtils.clickOnList
import net.dublin.bus.ui.view.utilities.TestUtils.sleep
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters.NAME_ASCENDING

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(NAME_ASCENDING)
@LargeTest
class SearchScreenTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

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
    fun test_1_searchNoRecent_DisplayedInUi() {
        launchActivity()

        //Check no search result and no recent
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_recent_view)).check(matches(not(isDisplayed())))
    }

    @Test
    fun test_2_searchText_DisplayedInUi() {
        launchActivity()

        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard())

        //Check search result
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()))
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_routes_view, 1, "120")
        checkRecyclerHasDescendant(R.id.search_routes_view, 3, "123")
        checkRecyclerHasDescendant(R.id.search_stops_view, 1, "12")
        checkRecyclerHasDescendant(R.id.search_stops_view, 1, "Dorset St, St. Joseph's Parade")
    }


    @Test
    fun test_3_searchText_cleanText_DisplayedInUi() {
        launchActivity()

        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard())
        onView(withId(R.id.search_text_view)).perform(clearText(), closeSoftKeyboard())

        //Check no search result and no recent
        onView(withId(R.id.search_recent_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
    }

    @Test
    fun test_4_searchText_clickRoute_DisplayedInUi() {
        setDispatcherRouteInbound200()
        launchActivity()
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard())

        //click result - route
        clickOnList(R.id.search_routes_view, 1)

        //Check route screen
        sleep()
        onView(withId(R.id.route_detail_progress_bar_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith("Parnell St"))))
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("57 stops"))))
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "3544")
        checkRecyclerHasDescendant(R.id.route_detail_recycler_view, 0, "Beechfield Mnr, Shanganagh Road")
        onView(withId(R.id.route_change_direction_view)).perform(click())
        onView(withText("Parnell St")).check(matches(isDisplayed()))
        onView(withText("Ashtown Stn")).check(matches(isDisplayed()))
    }

    @Test
    fun test_5_RouteRecent_DisplayedInUi() {
        launchActivity()

        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_recent_view, 1, "Route 120")
    }

    @Test
    fun test_6_searchText_cleanText_RouteRecent_DisplayedInUi() {
        launchActivity()

        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard())

        //Check search result
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()))
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_routes_view, 1, "120")

        //Clean search
        onView(withId(R.id.search_text_view)).perform(clearText(), closeSoftKeyboard())

        //Check recent
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_recent_view, 1, "Route 120")
    }

    @Test
    fun test_7_searchText_clickStop_DisplayedInUi() {
        setDispatcherRealTimeResponse200()
        launchActivity()
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("13"), closeSoftKeyboard())

        //click result - stop
        clickOnList(R.id.search_stops_view, 1)

        //Check stop screen
        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith("130"))))
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith("Glasnevin Avenue, Glasnevin Drive"))))
    }

    @Test
    fun test_8_RouteAndStopRecent_DisplayedInUi() {
        launchActivity()
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_recent_view, 1, "Stop 130")
        checkRecyclerHasDescendant(R.id.search_recent_view, 2, "Route 120")
    }

    @Test
    fun test_9_searchText_cleanText_showRecent_DisplayedInUi() {
        launchActivity()
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard())

        //Check search result
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()))
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_routes_view, 1, "120")

        //Clean search
        onView(withId(R.id.search_text_view)).perform(clearText(), closeSoftKeyboard())
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()))

        //Check recent
        checkRecyclerHasDescendant(R.id.search_recent_view, 1, "Stop 130")
        checkRecyclerHasDescendant(R.id.search_recent_view, 2, "Route 120")
    }

    @Test
    fun test_9_1_clickResetSearch_DisplayedInUi() {
        launchActivity()
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard())

        //Click reset search
        onView(withId(R.id.search_reset_view)).perform(click())

        //Check recent
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()))
        checkRecyclerHasDescendant(R.id.search_recent_view, 1, "Stop 130")
        checkRecyclerHasDescendant(R.id.search_recent_view, 2, "Route 120")

        //Check search input
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.search_hint)
        onView(withId(R.id.search_text_view)).check(matches(withHint(text)))
    }


    private fun launchActivity() {
        val intent = Intent()
        activityTestRule.launchActivity(intent)
        TestUtils.sleepLong()
        onView(withId(R.id.main_search_view)).perform(click())
        TestUtils.sleepLong()
    }
}
