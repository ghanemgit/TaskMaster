package com.example.taskmaster.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.PerformException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.taskmaster.R;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/*
 * The whole tests done by follow this article
 * https://www.pluralsight.com/guides/testing-in-android-with-espresso-part-1
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecyclerViewTest {


    @Rule
    public ActivityScenarioRule<RecyclerViewActivity> activityScenarioRule =
            new ActivityScenarioRule<RecyclerViewActivity>(RecyclerViewActivity.class);

    @Test(expected = PerformException.class)
    public void itemWithText_doesNotExist() {
        // Attempt to scroll to an item that contains the special text.
        onView(ViewMatchers.withId(R.id.recycler_view))
                // scrollTo will fail the test if no item matches.
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText("not in the list"))
                ));
    }

    @Test
    public void scrollToItemBelowFold_checkItsText() {
        // First scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(2), click());
    }


}
