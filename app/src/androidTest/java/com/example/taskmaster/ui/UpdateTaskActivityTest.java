package com.example.taskmaster.ui;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.taskmaster.R;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
/*
 * @FixMethodOrder
 * https://stackoverflow.com/questions/3693626/how-to-run-test-methods-in-specific-order-in-junit4
 * This annotation allow me to run the the tests method in specific order
 * But I had to reName the method to do the right order because there is now way in this version of Junit test to do that
 * you can read about that in this answer https://stackoverflow.com/a/3693706
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UpdateTaskActivityTest {

    @Rule
    public ActivityScenarioRule<UpdateTaskActivity> mActivityRule = new ActivityScenarioRule<>(UpdateTaskActivity.class);

    @Test
    public void ATestEditTextVisibility() {

        onView(withId(R.id.update_task_title_box)).check(matches(withText("Bring Ingredients")));


        onView(withId(R.id.update_task_description_box)).check(matches(withText("Go to market and bring some milk and 5 eggs and some butter and don't forget the flour")));

        /*
         * https://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
         */
        onView(withId(R.id.update_task_states_spinner)).check(matches(withSpinnerText(containsString("In progress"))));
    }


    @Test
    public void BTestButtonClick() {

        // enter Title and description
        onView(withId(R.id.update_task_title_box)).perform(typeText(" V2"), closeSoftKeyboard());
        onView(withId(R.id.update_task_description_box)).perform(typeText(" Then go to street."), closeSoftKeyboard());
        onView(withId(R.id.update_task_description_box)).perform(closeSoftKeyboard());

        onView(withId(R.id.update_task_states_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Completed"))).perform(click());
        // clear text
        onView(withText("Save")).perform(click());


//                mActivityRule.getScenario().onActivity(activity ->
//                Espresso.onView(ViewMatchers.withText("Updated")).inRoot(RootMatchers.withDecorView
//                        (not(is(activity.getWindow().getDecorView())))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        );

    }

    @Test
    public void CTestTextAfterUpdate() {

        onView(withId(R.id.update_task_title_box)).check(matches(withText("Bring Ingredients V2")));


        onView(withId(R.id.update_task_title_box)).check(matches(withText("Go to market and bring some milk and 5 eggs and some butte Then go to street.r and don't forget the flour")));

        /*
         * https://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
         */
        onView(withId(R.id.update_task_states_spinner)).check(matches(withSpinnerText(containsString("Completed"))));
    }

}
