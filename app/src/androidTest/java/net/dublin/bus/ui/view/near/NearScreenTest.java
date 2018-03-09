package net.dublin.bus.ui.view.near;

import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
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
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(NAME_ASCENDING)
@LargeTest
public class NearScreenTest {
    private static final String TAG = NearScreenTest.class.getName();
    private UiDevice device = UiDevice.getInstance(getInstrumentation());

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
    public void loadData_map_change_marker_LoadIntoView() throws InterruptedException, UnsupportedEncodingException {
        launchActivity();
        SystemClock.sleep(500);
        onView(withId(R.id.navigation_near)).perform(click());

        SystemClock.sleep(1000);
        onView(withText("O'Connell St, O'Connell Bridge")).check(matches(isDisplayed()));
        onView(withText("58 m")).check(matches(isDisplayed()));

        UiObject marker = device.findObject(new UiSelector().descriptionContains("stopNumber274"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        SystemClock.sleep(1500);
        onView(withText("O'Connell St, Henry Street")).check(matches(isDisplayed()));
        onView(withText("334 m")).check(matches(isDisplayed()));
    }

    private void launchActivity() {
        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);
        SystemClock.sleep(500);
    }
}
