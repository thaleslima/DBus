package net.dublin.bus.ui.view.realtime;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import net.dublin.bus.R;
import net.dublin.bus.ui.view.main.MainActivity;
import net.dublin.bus.ui.view.utilities.StringUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RealTimeFavouriteScreenTest {
    private static final String TAG = RealTimeFavouriteScreenTest.class.getName();

    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    private MockWebServer server;

    @Before
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start(2543);
    }

    @After
    public void after() throws Exception {
        try {
            server.shutdown();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    //@Test
    public void save_remove_favourite_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200());
        launchActivity();

        SystemClock.sleep(1500);
        onView(withId(R.id.navigation_stop)).perform(click());
        SystemClock.sleep(500);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        SystemClock.sleep(500);
        onView(withId(R.id.menu_favorite)).perform(click());
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.title_add_favourite);
        onView(withText(text)).check(matches(isDisplayed()));
        mDevice.pressBack();
        SystemClock.sleep(500);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        SystemClock.sleep(500);
        onView(withId(R.id.menu_favorite)).perform(click());
        text = InstrumentationRegistry.getTargetContext().getString(R.string.title_remove_favourite);
        onView(withText(text)).check(matches(isDisplayed()));
        SystemClock.sleep(1500);
    }

    @Test
    public void save_remove_favourite_check_favourite_screen_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200());
        launchActivity();
        SystemClock.sleep(1500);
        onView(withId(R.id.navigation_stop)).perform(click());
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        SystemClock.sleep(500);
        onView(withId(R.id.menu_favorite)).perform(click());
        mDevice.pressBack();
        SystemClock.sleep(500);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        SystemClock.sleep(500);
        onView(withId(R.id.menu_favorite)).perform(click());
        mDevice.pressBack();
        SystemClock.sleep(500);
        onView(withId(R.id.navigation_favorite)).perform(click());
        SystemClock.sleep(500);

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("2")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Parnell Square, Parnell Street")))));

        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("4")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("Parnell Square, Rotunda Hospital")))));

        SystemClock.sleep(500);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.menu_favorite)).perform(click());
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.title_remove_favourite);
        onView(withText(text)).check(matches(isDisplayed()));
        mDevice.pressBack();
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.menu_favorite)).perform(click());
        onView(withText(text)).check(matches(isDisplayed()));
    }

    private static final String FILE_NAME_REAL_TIME_RESPONSE = "real_time_response.xml";

    private class DispatcherResponse200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    private void launchActivity() {
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);
        SystemClock.sleep(500);
    }
}
