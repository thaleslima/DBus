package net.dublin.bus.ui.view.realtime

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice

import net.dublin.bus.R
import net.dublin.bus.ui.view.main.MainActivity
import net.dublin.bus.ui.view.utilities.MockServer
import net.dublin.bus.ui.view.utilities.TestUtils

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.io.UnsupportedEncodingException

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView

@RunWith(AndroidJUnit4::class)
@LargeTest
class RealTimeFavouriteScreenTest {

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
    }

    @Test
    @Throws(InterruptedException::class, UnsupportedEncodingException::class)
    fun saveAndRemoveFavourite_DisplayedInUi() {
        MockServer.setDispatcherRealTimeResponse200()
        launchActivity()

        //Add Favourite
        onView(withId(R.id.navigation_stop)).perform(click())
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        var text = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_add_favourite)
        onView(withText(text)).check(matches(isDisplayed()))
        mDevice.pressBack()

        //Remove Favourite
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        text = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_remove_favourite)
        onView(withText(text)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(InterruptedException::class, UnsupportedEncodingException::class)
    fun saveAndRemoveFavourite_checkFavouriteScreen_DisplayedInUi() {
        MockServer.setDispatcherRealTimeResponse200()
        launchActivity()

        onView(withId(R.id.navigation_stop)).perform(click())

        //Add Favourite 1
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        mDevice.pressBack()

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
        onView(withText("Parnell Square, Parnell Street")).check(doesNotExist())

        //Remove Favourite 2
        onView(withId(R.id.favourite_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.menu_favorite)).perform(click())
        onView(withText(text)).check(matches(isDisplayed()))
        mDevice.pressBack()

        // Verify only one favourite 2 was deleted
        onView(withText("Parnell Square, Rotunda Hospital")).check(doesNotExist())
    }

    private fun launchActivity() {
        val intent = Intent()
        activityTestRule.launchActivity(intent)
        TestUtils.sleep()
    }
}
