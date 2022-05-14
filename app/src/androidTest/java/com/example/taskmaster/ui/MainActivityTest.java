package com.example.taskmaster.ui;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;

import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {


    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);


//    @Test
//    public void listTest() {
//        onData(allOf(is(instanceOf(Map.class)), hasEntry(equalTo("Title"), is("Bring Ingredients"))))
//                .perform(click());
//    }


}
