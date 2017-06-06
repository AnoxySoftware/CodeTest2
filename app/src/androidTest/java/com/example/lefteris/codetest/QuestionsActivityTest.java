/*
 * Copyright (c) 2017. Lefteris Haritou
 */

package com.example.lefteris.codetest;

import android.content.ComponentName;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.toddsyndromelib.toddsSyndrome;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Lefteris on 08/02/17.
 */

public class QuestionsActivityTest {

    @Rule
    public ActivityTestRule<QuestionsActivity> mActivityRule = new ActivityTestRule<>(QuestionsActivity.class);

    private QuestionsActivity mActivity;

    private toddsSyndrome libSyndrome;

    @Before
    public void setUp() {
        Intents.init();
        mActivity = mActivityRule.getActivity();
        //init our library
        libSyndrome = new toddsSyndrome(mActivity.getApplicationContext());
    }

    @After
    public void cleanUp() {
        Intents.release();
        libSyndrome.closeDatabase();
    }

    @Test
    public void testPagerQuestions() {
        //tests if the viewpager has the correct number of pages...
        int numberOfQuestions = libSyndrome.getQuestions().size();
        assertEquals(numberOfQuestions,mActivity.mPager.getAdapter().getCount());
    }

    @Test
    public void testShowResulsWithMissingAnswers() {
        //jump to the last page
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mActivity.mPager.setCurrentItem(mActivity.mPager.getAdapter().getCount()-1);
            }
        });

        //press the finish button....
        onView(withId(R.id.action_next)).perform(click());
        //check that our alert dialog was shown
        onView(withText(R.string.alert_missingAnswers_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testShowResulsWithCompleteAnswers() {
        //TODO: This test only works with our 2 only possible options dropDown and Numeric
        //should we expand to more options this should also be changed, else it will fail...

        //loop through all our pages....
        while (mActivity.mPager.getCurrentItem()<mActivity.mPager.getAdapter().getCount()-1) {
            //get if our page is dropDown or numeric...

            onView(withId(R.id.editText)).withFailureHandler(new FailureHandler() {
                @Override
                public void handle(Throwable error, Matcher<View> viewMatcher) {
                    //If this fails, this is a drop down field we just don't want to stop the test if it's a dropdown
                }
            }).check(matches(isDisplayed())).perform(typeText("21"));

            //press continue
            onView(withId(R.id.action_next)).perform(click());
        }

        //set our email and id to test values and confirm they are correctly passed to the ResultsActivity class
        mActivity.mUserEmail = "unitTestRandom@unitTest.com";
        mActivity.mUserId = 123;

        //we are at the last page, we will click finish and verify that the results activity has launched with the correct data
        onView(withId(R.id.action_next)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), ResultsActivity.class)));
        intended(hasExtra(MainActivity.EXTRA_USER_EMAIL,mActivity.mUserEmail));
        intended(hasExtra(MainActivity.EXTRA_USER_ID,mActivity.mUserId));
    }
}
