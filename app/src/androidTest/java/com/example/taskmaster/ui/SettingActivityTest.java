package com.example.taskmaster.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.taskmaster.R;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/*
 * The whole tests done by follow this article
 * https://www.pluralsight.com/guides/testing-in-android-with-espresso-part-1
 */
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
public class SettingActivityTest {

//    @Rule
//    public ActivityScenarioRule<SettingActivity> settingActivityTestRule =
//            new ActivityScenarioRule<>(SettingActivity.class);
//
//
//    @Test
//    public void ATestHintVisibility() {
//        // check hint visibility
//        onView(withId(R.id.edit_text_username)).check(matches(withHint("Username")));
//        // enter title
//        onView(withId(R.id.edit_text_username)).perform(typeText("Hashem"), closeSoftKeyboard());
//        onView(withId(R.id.edit_text_username)).check(matches(withText("Hashem")));
//
//        // check another hint visibility
//        onView(withId(R.id.edit_text_no_of_task)).check(matches(withHint("No of Task to show")));
//    }
//
//    @Test
//    public void BSetUsernameTest() {
//
//
//        onView(withId(R.id.edit_text_username)).perform(typeText("Ahmad"), closeSoftKeyboard());
//        onView(withText("SAVE")).perform(click());
//
//        onView(withId(R.id.username_welcoming)).check(matches(withText("Ahmad")));
//    }


}
