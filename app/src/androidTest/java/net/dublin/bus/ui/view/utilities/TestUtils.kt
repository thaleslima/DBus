package net.dublin.bus.ui.view.utilities

import android.os.SystemClock
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View

import org.hamcrest.Description
import org.hamcrest.Matcher

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.swipeDown
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.internal.util.Checks.checkNotNull

object TestUtils {
    fun checkRecyclerHasDescendant(id: Int, position: Int, text: String) {
        onView(withId(id))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(matches(atPosition(position, hasDescendant(withText(text)))))
    }

    fun swipeRefresh(id: Int) {
        sleep()
        onView(withId(id)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)))
    }

    fun sleep() {
        SystemClock.sleep(500)
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        checkNotNull(itemMatcher)

        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(item: RecyclerView): Boolean {
                val viewHolder = item.findViewHolderForAdapterPosition(position)
                        ?: // has no item on such position
                        return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun withCustomConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return constraints
            }

            override fun getDescription(): String {
                return action.description
            }

            override fun perform(uiController: UiController, view: View) {
                action.perform(uiController, view)
            }
        }
    }
}
