package net.dublin.bus.ui.view.search;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(NAME_ASCENDING)
@LargeTest
public class SearchScreenTest {
    private static final String TAG = SearchScreenTest.class.getName();

    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

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

    @Test
    public void searchData_1_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        launchActivity();

        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_recent_view)).check(matches(not(isDisplayed())));

        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard());
        SystemClock.sleep(500);
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()));

        checkTextOnList(R.id.search_routes_view, 1, "120");
        checkTextOnList(R.id.search_routes_view, 3, "123");

        checkTextOnList(R.id.search_stops_view, 1, "12");
        checkTextOnList(R.id.search_stops_view, 1, "Dorset St, St. Joseph's Parade");

        onView(withId(R.id.search_text_view)).perform(clearText(), closeSoftKeyboard());
        SystemClock.sleep(500);
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
    }

    @Test
    public void searchData_2_search_route_recent_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherInboundResponse200());

        launchActivity();
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard());
        SystemClock.sleep(500);

        clickOnList(R.id.search_routes_view, 1);
        SystemClock.sleep(1000);

        onView(withId(R.id.route_detail_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith("Parnell St"))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("57 stops"))));

        checkTextOnList(R.id.route_detail_recycler_view, 0, "3544");
        checkTextOnList(R.id.route_detail_recycler_view, 0, "Beechfield Mnr, Shanganagh Road");

        onView(withId(R.id.route_change_direction_view)).perform(click());
        SystemClock.sleep(500);
        onView(withText("Parnell St")).check(matches(isDisplayed()));
        onView(withText("Ashtown Stn")).check(matches(isDisplayed()));
    }

    @Test
    public void searchData_3_check_recent_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        launchActivity();
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()));

        SystemClock.sleep(500);
        checkTextOnList(R.id.search_recent_view, 1, "Route 120");

        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard());
        SystemClock.sleep(500);
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()));

        checkTextOnList(R.id.search_routes_view, 1, "120");
        onView(withId(R.id.search_text_view)).perform(clearText(), closeSoftKeyboard());
        SystemClock.sleep(500);

        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()));
        checkTextOnList(R.id.search_recent_view, 1, "Route 120");
    }

    @Test
    public void searchData_4_search_stop_recent_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200());

        launchActivity();
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("13"), closeSoftKeyboard());
        SystemClock.sleep(500);

        clickOnList(R.id.search_stops_view, 1);
        SystemClock.sleep(1000);

        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith("130"))));
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith("Glasnevin Avenue, Glasnevin Drive"))));
    }

    @Test
    public void searchData_5_check_recent_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        launchActivity();
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()));

        SystemClock.sleep(500);
        checkTextOnList(R.id.search_recent_view, 1, "Stop 130");
        checkTextOnList(R.id.search_recent_view, 2, "Route 120");

        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard());
        SystemClock.sleep(500);
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()));

        checkTextOnList(R.id.search_routes_view, 1, "120");
        onView(withId(R.id.search_text_view)).perform(clearText(), closeSoftKeyboard());
        SystemClock.sleep(500);

        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()));
        checkTextOnList(R.id.search_recent_view, 1, "Stop 130");
        checkTextOnList(R.id.search_recent_view, 2, "Route 120");
    }

    @Test
    public void searchData_6_clean_search_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        launchActivity();
        onView(withId(R.id.search_text_view)).perform(clearText(), typeText("12"), closeSoftKeyboard());
        SystemClock.sleep(500);
        onView(withId(R.id.search_routes_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_stops_view)).check(matches(isDisplayed()));

        onView(withId(R.id.search_reset_view)).perform(click());
        onView(withId(R.id.search_routes_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_stops_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_recent_view)).check(matches(isDisplayed()));
        checkTextOnList(R.id.search_recent_view, 1, "Stop 130");
        checkTextOnList(R.id.search_recent_view, 2, "Route 120");

        String text = InstrumentationRegistry.getTargetContext().getString(R.string.search_hint);
        onView(withId(R.id.search_text_view)).check(matches(withHint(text)));
    }

    private void clickOnList(final int id, int position) {
        onView(withId(id)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
        SystemClock.sleep(500);
    }

    private void checkTextOnList(final int id, int position, String text) {
        onView(withId(id))
                .perform(scrollToPosition(position))
                .check(matches(atPosition(position, hasDescendant(withText(text)))));
    }

    private static final String FILE_ROUTE_INBOUND_RESPONSE = "stops_by_route_inbound_response.xml";
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

    private class DispatcherInboundResponse200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetStopDataByRouteAndDirection")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_ROUTE_INBOUND_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private void launchActivity() {
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);
        SystemClock.sleep(500);
        onView(withId(R.id.main_search_view)).perform(click());
        SystemClock.sleep(500);
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
}
