package com.example.taskmaster.ui;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.taskmaster.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/*
 * The whole tests done by follow this article
 * https://www.pluralsight.com/guides/testing-in-android-with-espresso-part-1
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddTaskActivityTest {

    @Rule
    public ActivityScenarioRule<AddTaskActivity> mActivityRule = new ActivityScenarioRule<>(AddTaskActivity.class);


    @Test
    public void testHintVisibility() {
        // check hint visibility
        onView(withId(R.id.taskTitleBox)).check(matches(withHint("Task Title")));
        // enter name
        onView(withId(R.id.taskTitleBox)).perform(typeText("First Task"), closeSoftKeyboard());
        onView(withId(R.id.taskTitleBox)).check(matches(withText("First Task")));

        // check another hint visibility
        onView(withId(R.id.taskDescriptionBox)).check(matches(withHint("Task Description")));
        // enter name
        onView(withId(R.id.taskDescriptionBox)).perform(typeText("Go to sleep"), closeSoftKeyboard());
        onView(withId(R.id.taskDescriptionBox)).check(matches(withText("Go to sleep")));

        /*
         * https://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
         */
        onView(withId(R.id.task_states_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("In progress"))).perform(click());
        onView(withId(R.id.task_states_spinner)).check(matches(withSpinnerText(containsString("In progress"))));
        onView(withId(R.id.taskDescriptionBox)).perform(closeSoftKeyboard());

    }

    @Test
    public void testButtonClick() {

        // enter Title and description
        onView(withId(R.id.taskTitleBox)).perform(typeText("First Task"), closeSoftKeyboard());
        onView(withId(R.id.taskDescriptionBox)).perform(typeText("Go to sleep"), closeSoftKeyboard());
        onView(withId(R.id.task_states_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("In progress"))).perform(click());

        onView(withText("ADD TASK")).perform(click());


//        mActivityRule.getScenario().onActivity(activity ->
//                Espresso.onView(ViewMatchers.withText("Submitted!")).inRoot(RootMatchers.withDecorView
//                        (not(is(activity.getWindow().getDecorView())))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        );
    }


}
