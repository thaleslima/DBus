package net.dublin.bus.ui.view.realtime;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
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
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RealTimeScreenTest {
    private static final String TAG = RealTimeScreenTest.class.getName();

    private static final String STOP_NUMBER = "2";
    private static final String STOP_DESCRIPTION = "TEST 2";

    @Rule
    public final ActivityTestRule<RealTimeActivity> activityTestRule = new ActivityTestRule<>(RealTimeActivity.class, false, false);

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


    public void loadData_LoadIntoView(boolean launchActivity) throws InterruptedException, UnsupportedEncodingException {
        if (launchActivity) {
            server.setDispatcher(new DispatcherResponse200());
            launchActivity();
        }

        SystemClock.sleep(500);
        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))));
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))));
        onView(withId(R.id.real_message_empty_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_line_note_view)).check(matches(not(isDisplayed())));

        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("38")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("Damastown via Corduff")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("3 min")))));

        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("46a")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("Phoenix Pk via Donnybrook")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("9 min")))));
    }

    @Test
    public void loadData_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        loadData_LoadIntoView(true);
    }

    @Test
    public void loadData_LoadIntoView_Update_Swipe() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse500());
        launchActivity();
        SystemClock.sleep(500);

        server.setDispatcher(new DispatcherResponse200());

        onView(withId(R.id.real_swipe_refresh_layout)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
        loadData_LoadIntoView(false);
    }

    @Test
    public void loadData_LoadIntoView_Update_Swipe_ShowsErrorUi() throws InterruptedException, UnsupportedEncodingException {
        loadData_LoadIntoView(true);
        SystemClock.sleep(500);
        server.setDispatcher(new DispatcherResponse500());

        onView(withId(R.id.real_swipe_refresh_layout)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.error_message);
        onView(withText(text)).check(matches(isDisplayed()));
        loadData_LoadIntoView(false);
    }

    @Test
    public void loadData_warning_no_item_update_swipe_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponseWarningNoItems200());
        launchActivity();

        SystemClock.sleep(500);
        server.setDispatcher(new DispatcherResponse500());

        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()));
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()));

        server.setDispatcher(new DispatcherResponse200());
        onView(withId(R.id.real_swipe_refresh_layout)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
        loadData_LoadIntoView(false);
    }


    @Test
    public void loadData_warning_with_items_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponseWarning200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))));
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))));
        onView(withId(R.id.real_message_empty_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()));

        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("1")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("Shanard Road via O'Connell Street")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(0, hasDescendant(withText("2 min")))));

        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("11")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("St Pappin's Rd via Drumcondra")))));
        onView(withId(R.id.real_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("16 min")))));

        SystemClock.sleep(500);
    }

    @Test
    public void loadData_warning_with_items_update_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponseWarning200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))));
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))));
        onView(withId(R.id.real_message_empty_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()));

        server.setDispatcher(new DispatcherResponse200());
        onView(withId(R.id.real_swipe_refresh_layout)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
        loadData_LoadIntoView(false);
    }

    @Test
    public void loadData_warning_no_item_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponseWarningNoItems200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()));
        onView(withId(R.id.real_line_note_view)).check(matches(isDisplayed()));
        SystemClock.sleep(500);
    }

    @Test
    public void loadData_NoItems_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponseNoItems200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.real_progress_bar_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.real_number_stop_view)).check(matches(withText(startsWith(STOP_NUMBER))));
        onView(withId(R.id.real_description_stop_view)).check(matches(withText(startsWith(STOP_DESCRIPTION))));
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()));

        String messageErrorRequired = InstrumentationRegistry.getTargetContext().getString(R.string.real_time_error_message);
        onView(withText(messageErrorRequired)).check(matches(isDisplayed()));
    }

    @Test
    public void loadData_ErrorConnection_ShowsErrorUi() {
        server.setDispatcher(new DispatcherResponse500());
        launchActivity();

        SystemClock.sleep(1000);
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.error_message);
        onView(withText(text)).check(matches(isDisplayed()));
        onView(withId(R.id.real_message_empty_view)).check(matches(isDisplayed()));
    }

    @Test
    public void loadData_ErrorConnection_ShowsErrorUi_Refresh_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse500());
        launchActivity();

        SystemClock.sleep(1000);
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.title_retry);
        server.setDispatcher(new DispatcherResponse200());
        onView(withText(text)).perform(click());
        SystemClock.sleep(1000);
        loadData_LoadIntoView(false);
    }

    private static final String FILE_NAME_REAL_TIME_RESPONSE = "real_time_response.xml";
    private static final String FILE_NAME_NO_ITEMS_REAL_TIME_RESPONSE = "real_time_no_items_response.xml";
    private static final String FILE_NAME_WARNING_NO_ITEMS_REAL_TIME_RESPONSE = "real_time_warning_response.xml";
    private static final String FILE_NAME_WARNING_REAL_TIME_RESPONSE = "real_time_line_note_response.xml";

    private class DispatcherResponse500 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            return new MockResponse()
                    .setResponseCode(500)
                    .setBody("");
        }
    }

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

    private class DispatcherResponseNoItems200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_NO_ITEMS_REAL_TIME_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private class DispatcherResponseWarningNoItems200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_WARNING_NO_ITEMS_REAL_TIME_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private class DispatcherResponseWarning200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetRealTimeStopData_ForceLineNoteVisit")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_NAME_WARNING_REAL_TIME_RESPONSE));
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

    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }

    private void launchActivity() {
        Intent intent = new Intent();
        intent.putExtra(RealTimeActivity.EXTRA_DESCRIPTION, STOP_DESCRIPTION);
        intent.putExtra(RealTimeActivity.EXTRA_STOP_NUMBER, STOP_NUMBER);
        activityTestRule.launchActivity(intent);
        SystemClock.sleep(500);
    }
}
