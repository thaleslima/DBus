package net.dublin.bus.ui.view.favourite

import android.content.Intent
import android.os.SystemClock
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.v7.widget.RecyclerView
import net.dublin.bus.R
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.utilities.MockServer
import net.dublin.bus.ui.view.utilities.TestUtils
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FavouriteScreenTest {
    private lateinit var stopRepository: StopRepository

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)
    private val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    @Throws(Exception::class)
    fun setup() {
        MockServer.start()
    }

    @After
    @Throws(Exception::class)
    fun after() {
        MockServer.shutdown()
        stopRepository.removeAllFavourites()
    }

    private fun removeAllFavourites() {
        stopRepository = StopRepository(activityTestRule.activity.application)
        stopRepository.removeAllFavourites()

        onView(withId(R.id.navigation_stop)).perform(click())
        onView(withId(R.id.navigation_favorite)).perform(click())
    }

    @Test
    fun saveAndRemoveFavourite_scroll_checkFavouriteScreen_DisplayedInUi() {
        MockServer.setDispatcherRealTime200()
        launchActivity()

        onView(withId(R.id.favorite_message_empty)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_stop)).perform(click())

        // add favorites
        addFavorite(0)
        addFavorite(1)
        addFavorite(2)
        addFavorite(3)
        addFavorite(10)
        addFavorite(12)
        addFavorite(13)
        addFavorite(14)
        addFavorite(20)

        TestUtils.sleep()
        onView(withId(R.id.navigation_favorite)).perform(click())
        TestUtils.sleep()
        onView(withId(R.id.favorite_message_empty)).check(matches(not(isDisplayed())))

        // Verify only one favourite 2 was inserted
        checkTestOnList(0, "38")
        checkTestOnList(0, "Damastown via Corduff")
        checkTestOnList(0, "3 min")
        checkTestOnList(0, "46a")
        checkTestOnList(0, "Phoenix Pk via Donnybrook")
        checkTestOnList(0, "9 min")
        checkTestOnList(0, "38a")
        checkTestOnList(0, "Damastown via Navan Road")
        checkTestOnList(0, "23 min")
        TestUtils.sleep()
        checkTestOnList(8, "27")
        TestUtils.sleep()
        checkTestOnList(0, "Damastown via Corduff")
        checkTestOnList(0, "Phoenix Pk via Donnybrook")
        checkTestOnList(0, "Damastown via Navan Road")
    }

    @Test
    fun errorConnection_ShowsErrorUi() {
        MockServer.setDispatcherRealTime200And500()
        launchActivity()

        onView(withId(R.id.navigation_stop)).perform(click())

        // add favorites
        addFavorite(0)
        addFavorite(6)

        TestUtils.sleep()
        onView(withId(R.id.navigation_favorite)).perform(click())
        TestUtils.sleep()

        // check favorites
        checkTestOnList(0, "38")
        checkTestOnList(0, "Damastown via Corduff")
        checkTestOnList(0, "3 min")

        val messageErrorRequired = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_error_message)
        checkTestOnList(1, messageErrorRequired)
    }

    @Test
    fun saveAndRemoveFavourite_DisplayedInUi() {
        MockServer.setDispatcherRealTimeResponse200()
        launchActivity()

        //Add Favourite
        onView(withId(R.id.navigation_stop)).perform(click())
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        TestUtils.sleep()
        var text = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_add_favourite)
        onView(withText(text)).check(matches(isDisplayed()))
        mDevice.pressBack()

        //Remove Favourite
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        TestUtils.sleep()
        TestUtils.sleep()
        onView(withId(R.id.menu_favorite)).perform(click())
        text = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_remove_favourite)
        onView(withText(text)).check(matches(isDisplayed()))
    }

    @Test
    fun saveAndRemoveFavourite_checkFavouriteScreen_DisplayedInUi() {
        MockServer.setDispatcherRealTimeResponse200()
        launchActivity()

        onView(withId(R.id.navigation_stop)).perform(click())

        //Add Favourite 1
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        mDevice.pressBack()
        TestUtils.sleep()

        //Add Favourite 2
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        mDevice.pressBack()


        TestUtils.sleep()
        onView(withId(R.id.navigation_favorite)).perform(click())

        //Check 1 favourite
        TestUtils.checkRecyclerHasDescendant(R.id.favourite_recycler_view, 0, "2")
        TestUtils.checkRecyclerHasDescendant(R.id.favourite_recycler_view, 0, "Parnell Square, Parnell Street")

        //Check 2 favourite
        TestUtils.checkRecyclerHasDescendant(R.id.favourite_recycler_view, 1, "4")
        TestUtils.checkRecyclerHasDescendant(R.id.favourite_recycler_view, 1, "Parnell Square, Rotunda Hospital")

        //Remove Favourite 1
        onView(withId(R.id.favourite_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        val text = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_remove_favourite)
        onView(withText(text)).check(matches(isDisplayed()))
        mDevice.pressBack()

        // Verify only one favourite 1 was deleted
        onView(withText("Parnell Square, Parnell Street")).check(ViewAssertions.doesNotExist())

        //Remove Favourite 2
        onView(withId(R.id.favourite_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        onView(withText(text)).check(matches(isDisplayed()))
        mDevice.pressBack()

        TestUtils.sleep()
        // Verify only one favourite 2 was deleted
        onView(withId(R.id.favorite_message_empty)).check(matches(isDisplayed()))
    }

    private fun launchActivity() {
        val intent = Intent()
        activityTestRule.launchActivity(intent)
        removeAllFavourites()
        TestUtils.sleep()
    }

    private fun addFavorite(position: Int) {
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
        TestUtils.sleep()
        TestUtils.sleep()
        onView(withId(R.id.menu_favorite)).perform(click())
        mDevice.pressBack()
    }

    private fun checkTestOnList(position: Int, text: String) {
        TestUtils.checkRecyclerHasDescendant(R.id.favourite_recycler_view, position, text)
    }
}
