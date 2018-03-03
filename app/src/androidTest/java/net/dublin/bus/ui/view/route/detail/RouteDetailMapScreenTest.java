package net.dublin.bus.ui.view.route.detail;

import android.Manifest;
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
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RouteDetailMapScreenTest {
    private static final String TAG = RouteDetailMapScreenTest.class.getName();

    private static final String STOP_NUMBER = "2";
    private static final String STOP_DESCRIPTION = "TEST 2";

    private static final String ROUTE_NUMBER = "7b";
    private static final String ROUTE_IN_TOWARDS = "route_inbound_towards";
    private static final String ROUTE_OUT_TOWARDS = "route_outbound_towards";
    private UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public final ActivityTestRule<RouteDetailActivity> activityTestRule = new ActivityTestRule<>(RouteDetailActivity.class, false, false);

    @Rule
    public GrantPermissionRule locationPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

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
    public void loadData_map_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException, UiObjectNotFoundException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        SystemClock.sleep(500);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("stopNumber3544"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        SystemClock.sleep(500);
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()));
        onView(withId(R.id.detail_map_description_view)).check(matches(withText(startsWith("Beechfield Mnr, Shanganagh Road"))));
        onView(withId(R.id.route_detail_serving_aux_view)).check(matches(withText(startsWith("40, 40B, 40D"))));
        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
    }

    @Test
    public void loadData_map_change_marker_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException, UiObjectNotFoundException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        SystemClock.sleep(500);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("stopNumber3544"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        server.setDispatcher(new DispatcherInboundResponse200_2());
        SystemClock.sleep(1500);
        marker = device.findObject(new UiSelector().descriptionContains("stopNumber3552"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        SystemClock.sleep(1500);
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()));
        onView(withId(R.id.detail_map_description_view)).check(matches(withText(startsWith("Shanganagh Road, Hazelwood"))));
        onView(withId(R.id.route_detail_serving_aux_view)).check(matches(withText(startsWith("20D, 27, 40"))));
        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
    }

    @Test
    public void loadData_map_rotate_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException, UiObjectNotFoundException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        SystemClock.sleep(500);
        UiObject marker = device.findObject(new UiSelector().descriptionContains("stopNumber3544"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        SystemClock.sleep(1500);
        device.setOrientationLeft();
        SystemClock.sleep(2500);
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()));
        onView(withId(R.id.detail_map_description_view)).check(matches(withText(startsWith("Beechfield Mnr, Shanganagh Road"))));
        onView(withId(R.id.route_detail_serving_aux_view)).check(matches(withText(startsWith("40, 40B, 40D"))));
        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        device.setOrientationNatural();
    }

    @Test
    public void loadData_map_error_serving_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException, UiObjectNotFoundException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        SystemClock.sleep(500);
        server.setDispatcher(new DispatcherResponse500());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("stopNumber3544"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        SystemClock.sleep(500);
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()));
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.route_detail_error);
        onView(withId(R.id.route_detail_serving_aux_view)).check(matches(withText(startsWith(text))));
        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
    }

    @Test
    public void loadData_map_error_serving_rotate_LoadIntoView() throws InterruptedException, UnsupportedEncodingException, RemoteException, UiObjectNotFoundException {
        server.setDispatcher(new DispatcherInboundResponse200());
        launchActivity();

        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        SystemClock.sleep(500);
        server.setDispatcher(new DispatcherResponse500());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("stopNumber3544"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        SystemClock.sleep(1500);
        device.setOrientationLeft();
        SystemClock.sleep(2500);
        onView(withId(R.id.detail_map_stop_view)).check(matches(isDisplayed()));
        String text = InstrumentationRegistry.getTargetContext().getString(R.string.route_detail_error);
        onView(withId(R.id.route_detail_serving_aux_view)).check(matches(withText(startsWith(text))));
        SystemClock.sleep(500);
        onView(withId(R.id.route_change_map_list_view)).perform(click());
        device.setOrientationNatural();
    }


    private static final String FILE_ROUTE_INBOUND_RESPONSE = "stops_by_route_inbound_response.xml";
    private static final String FILE_ROUTE_OUTBOUND_RESPONSE = "stops_by_route_outbound_response.xml";
    private static final String FILE_ROUTE_INBOUND_NO_ITEMS_RESPONSE = "stops_by_route_inbound_no_items_response.xml";
    private static final String FILE_ROUTES_STOP_RESPONSE = "routes_by_stop_response.xml";
    private static final String FILE_ROUTES_STOP_2_RESPONSE = "routes_by_stop_2_response.xml";

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

            if (request.getPath().contains("GetRoutesServicedByStopNumber")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_ROUTES_STOP_RESPONSE));
            }

            throw new InterruptedException();
        }
    }

    private class DispatcherInboundResponse200_2 extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getPath().contains("GetStopDataByRouteAndDirection")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_ROUTE_INBOUND_RESPONSE));
            }

            if (request.getPath().contains("GetRoutesServicedByStopNumber")) {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(StringUtil.getStringFromFile(getInstrumentation().getContext(), FILE_ROUTES_STOP_2_RESPONSE));
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
