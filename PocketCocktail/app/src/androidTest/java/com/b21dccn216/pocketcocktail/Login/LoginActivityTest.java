package com.b21dccn216.pocketcocktail.Login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;


import android.app.Activity;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.view.Login.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import kotlin.jvm.JvmField;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityTestRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSuccessfulLogin() {
        onView(withId(R.id.edtEmail))
                .perform(typeText("dat@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.edtPassword))
                .perform(typeText("123456"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());

        onView(withText("Login Successful"))
                .inRoot(withDecorView(not(is(getCurrentActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    // Helper to get the current activity context
    private Activity getCurrentActivity() {
        final Activity[] currentActivity = new Activity[1];
        getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED);
            if (!activities.isEmpty()) {
                currentActivity[0] = activities.iterator().next();
            }
        });
        return currentActivity[0];
    }


}