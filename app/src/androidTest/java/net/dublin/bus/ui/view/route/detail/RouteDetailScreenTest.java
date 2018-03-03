package net.dublin.bus.ui.view.route.detail;

import android.content.Intent;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
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
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RouteDetailScreenTest {
    private static final String TAG = RouteDetailScreenTest.class.getName();

    private static final String STOP_NUMBER = "2";
    private static final String STOP_DESCRIPTION = "TEST 2";

    private static final String ROUTE_NUMBER = "7b";
    private static final String ROUTE_IN_TOWARDS = "route_inbound_towards";
    private static final String ROUTE_OUT_TOWARDS = "route_outbound_towards";

    @Rule
    public final ActivityTestRule<RouteDetailActivity> activityTestRule = new ActivityTestRule<>(RouteDetailActivity.class, false, false);

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
    public void loadData_inbound_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        loadData_inbound_LoadIntoView(true);
    }

    @Test
    public void loadData_inbound_rotate_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.setOrientationLeft();

        SystemClock.sleep(500);
        onView(withId(R.id.route_detail_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_IN_TOWARDS))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("57 stops"))));

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("3544")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Beechfield Mnr, Shanganagh Road")))));

        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("3552")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("Shanganagh Road, Rathsallagh")))));

        onView(withId(R.id.route_change_direction_view)).perform(click());
        SystemClock.sleep(500);
        onView(withText(ROUTE_IN_TOWARDS)).check(matches(isDisplayed()));
        onView(withText(ROUTE_OUT_TOWARDS)).check(matches(isDisplayed()));

        device.setOrientationNatural();
        SystemClock.sleep(500);

    }

    public void loadData_inbound_LoadIntoView(boolean launchActivity) throws InterruptedException, UnsupportedEncodingException {
        if (launchActivity) {
            server.setDispatcher(new DispatcherInboundResponse200());
            launchActivity();
        }

        SystemClock.sleep(500);
        onView(withId(R.id.route_detail_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_IN_TOWARDS))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("57 stops"))));

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("3544")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Beechfield Mnr, Shanganagh Road")))));

        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("3552")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("Shanganagh Road, Rathsallagh")))));

        onView(withId(R.id.route_change_direction_view)).perform(click());
        SystemClock.sleep(500);
        onView(withText(ROUTE_IN_TOWARDS)).check(matches(isDisplayed()));
        onView(withText(ROUTE_OUT_TOWARDS)).check(matches(isDisplayed()));
    }

    @Test
    public void loadData_outbound_rotate_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        server.setDispatcher(new DispatcherOutboundResponse200());
        onView(withId(R.id.route_change_direction_view)).perform(click());
        onView(withText(ROUTE_OUT_TOWARDS)).perform(click());

        SystemClock.sleep(1500);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.setOrientationLeft();

        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))));

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("4962")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Mountjoy Square, Mountjoy Sq Nth")))));


        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("6059")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("O'Connell St, North Earl Street")))));

        device.setOrientationNatural();
        SystemClock.sleep(500);
    }


    @Test
    public void loadData_outbound_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        server.setDispatcher(new DispatcherOutboundResponse200());
        onView(withId(R.id.route_change_direction_view)).perform(click());
        onView(withText(ROUTE_OUT_TOWARDS)).perform(click());
        SystemClock.sleep(500);

        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))));

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("4962")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Mountjoy Square, Mountjoy Sq Nth")))));


        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("6059")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("O'Connell St, North Earl Street")))));
    }

    @Test
    public void loadData_inbound_no_items_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherInboundResponseNoItems200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.route_detail_progress_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void loadData_outbound_without_inbound_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherOutboundResponse200());
        launchActivity2();

        SystemClock.sleep(500);
        onView(withId(R.id.route_detail_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))));

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("4962")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Mountjoy Square, Mountjoy Sq Nth")))));

        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("6059")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(1, hasDescendant(withText("O'Connell St, North Earl Street")))));

        onView(withId(R.id.route_change_direction_view)).perform(click());
        SystemClock.sleep(500);
        onView(withText(ROUTE_OUT_TOWARDS)).check(matches(isDisplayed()));
        onView(withText(ROUTE_IN_TOWARDS)).check(doesNotExist());
    }


    @Test
    public void loadData_inbound_ErrorConnection_ShowsErrorUi() {
        server.setDispatcher(new DispatcherResponse500());
        launchActivity();

        SystemClock.sleep(1000);
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.error_message);
        onView(withText(text)).check(matches(isDisplayed()));
    }

    @Test
    public void loadData_inbound_ErrorConnection_ShowsErrorUi_Refresh_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        server.setDispatcher(new DispatcherResponse500());
        launchActivity();

        SystemClock.sleep(1000);
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.title_retry);
        server.setDispatcher(new DispatcherInboundResponse200());
        onView(withText(text)).perform(click());
        SystemClock.sleep(1000);
        loadData_inbound_LoadIntoView(false);
    }

    @Test
    public void loadData_outbound_ErrorConnection_ShowsErrorUi() {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        server.setDispatcher(new DispatcherResponse500());
        onView(withId(R.id.route_change_direction_view)).perform(click());
        onView(withText(ROUTE_OUT_TOWARDS)).perform(click());
        SystemClock.sleep(500);

        String text = InstrumentationRegistry.getTargetContext().getString(R.string.title_retry);
        server.setDispatcher(new DispatcherOutboundResponse200());
        onView(withText(text)).perform(click());

        onView(withId(R.id.route_name_towards_view)).check(matches(withText(startsWith(ROUTE_OUT_TOWARDS))));
        onView(withId(R.id.route_count_view)).check(matches(withText(startsWith("53 stops"))));

        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("4962")))));
        onView(withId(R.id.list))
                .check(matches(atPosition(0, hasDescendant(withText("Mountjoy Square, Mountjoy Sq Nth")))));
    }


    private static final String FILE_ROUTE_INBOUND_RESPONSE = "stops_by_route_inbound_response.xml";
    private static final String FILE_ROUTE_OUTBOUND_RESPONSE = "stops_by_route_outbound_response.xml";
    private static final String FILE_ROUTE_INBOUND_NO_ITEMS_RESPONSE = "stops_by_route_inbound_no_items_response.xml";

    private class DispatcherResponse500 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            return new MockResponse()
                    .setResponseCode(500)
                    .setBody("");
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

    private class DispatcherOutboundResponse200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetStopDataByRouteAndDirection")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_ROUTE_OUTBOUND_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private class DispatcherInboundResponseNoItems200 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetStopDataByRouteAndDirection")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_ROUTE_INBOUND_NO_ITEMS_RESPONSE));
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
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
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
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_NUMBER, ROUTE_NUMBER);
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_OUT_TOWARDS, ROUTE_OUT_TOWARDS);
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_IN_TOWARDS, ROUTE_IN_TOWARDS);

        activityTestRule.launchActivity(intent);
        SystemClock.sleep(500);
    }

    private void launchActivity2() {
        Intent intent = new Intent();
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_NUMBER, ROUTE_NUMBER);
        intent.putExtra(RouteDetailActivity.EXTRA_ROUTE_OUT_TOWARDS, ROUTE_OUT_TOWARDS);
        activityTestRule.launchActivity(intent);
        SystemClock.sleep(500);
    }
}
