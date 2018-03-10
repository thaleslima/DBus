package net.dublin.bus.ui.view.favourite;

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
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.runners.MethodSorters.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(NAME_ASCENDING)
@LargeTest
public class FavouriteScreenTest {
    private static final String TAG = FavouriteScreenTest.class.getName();

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

    @Test
    public void favourite_1_save_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200());
        launchActivity();
        SystemClock.sleep(500);

        onView(withId(R.id.favorite_message_empty)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_stop)).perform(click());

        addFavorite(0);
        addFavorite(1);
        addFavorite(2);
        addFavorite(3);
        addFavorite(10);
        addFavorite(12);
        addFavorite(13);
        addFavorite(14);
        addFavorite(20);

        onView(withId(R.id.navigation_favorite)).perform(click());
        onView(withId(R.id.favorite_message_empty)).check(matches(not(isDisplayed())));
    }

    private void addFavorite(int position) {
        SystemClock.sleep(500);
        onView(withId(R.id.stop_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
        SystemClock.sleep(1000);
        onView(withId(R.id.menu_favorite)).perform(click());
        mDevice.pressBack();
        SystemClock.sleep(500);
    }

    @Test
    public void favourite_2_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200());
        launchActivity();
        SystemClock.sleep(500);
        checkTestOnList(0, "38");
        checkTestOnList(0, "Damastown via Corduff");
        checkTestOnList(0, "3 min");
        checkTestOnList(0, "46a");
        checkTestOnList(0, "Phoenix Pk via Donnybrook");
        checkTestOnList(0, "9 min");
        checkTestOnList(0, "38a");
        checkTestOnList(0, "Damastown via Navan Road");
        checkTestOnList(0, "23 min");
        SystemClock.sleep(500);
        checkTestOnList(8, "Teste 08c");
        checkTestOnList(8, "Teste 08b");
        checkTestOnList(8, "Teste 08a");
        SystemClock.sleep(500);
        checkTestOnList(0, "Damastown via Corduff");
        checkTestOnList(0, "Phoenix Pk via Donnybrook");
        checkTestOnList(0, "Damastown via Navan Road");
        SystemClock.sleep(500);
    }

    private void checkTestOnList(int position, String text) {
        onView(withId(R.id.favourite_recycler_view))
                .perform(scrollToPosition(position))
                .check(matches(atPosition(position, hasDescendant(withText(text)))));
    }

    @Test
    public void favourite_3_save_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200_1());
        launchActivity();
        SystemClock.sleep(1500);

        onView(withId(R.id.favourite_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("38")))));
        onView(withId(R.id.favourite_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("Damastown via Corduff")))));
        onView(withId(R.id.favourite_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("3 min")))));


        String messageErrorRequired = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_error_message);
        onView(withId(R.id.favourite_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText(messageErrorRequired)))));
    }

    @Test
    public void favourite_4_remove_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse200());
        launchActivity();

        removeFavorite();
        removeFavorite();
        removeFavorite();
        removeFavorite();
        removeFavorite();
        removeFavorite();
        removeFavorite();
        removeFavorite();
        removeFavorite();

        onView(withId(R.id.favorite_message_empty)).check(matches(isDisplayed()));
    }

    private void removeFavorite() {
        SystemClock.sleep(500);
        onView(withId(R.id.favourite_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.menu_favorite)).perform(click());
        mDevice.pressBack();
    }

    private static final String FILE_NAME_REAL_TIME_RESPONSE = "real_time_response.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_1 = "real_time_response_1.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_2 = "real_time_response_2.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_3 = "real_time_response_3.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_4 = "real_time_response_4.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_5 = "real_time_response_5.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_6 = "real_time_response_6.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_7 = "real_time_response_7.xml";
    private static final String FILE_NAME_REAL_TIME_RESPONSE_8 = "real_time_response_8.xml";

    private class DispatcherResponse200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=3")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_1));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=4")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_2));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=6")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_3));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=15")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_4));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=17")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_5));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=18")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_6));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=19")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_7));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=27")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_8));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=2")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private class DispatcherResponse200_1 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=2")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE));
            }

            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit&n=7")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_REAL_TIME_RESPONSE_2));
            }

            return new MockResponse()
                    .setResponseCode(500)
                    .setBody("");
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
