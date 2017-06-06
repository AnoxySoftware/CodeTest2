/*
 * Copyright (c) 2017. Lefteris Haritou
 */

package com.example.lefteris.codetest;

import android.content.ComponentName;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.example.toddsyndromelib.Models.Question;
import com.example.toddsyndromelib.toddsSyndrome;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Lefteris on 08/02/17.
 */

public class ResultsActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity;

    //our library
    private toddsSyndrome libSyndrome;

    //our test email address
    private String unitTestEmail = "unitTestRandom@unitTest.com";

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
    public void testResultsWithAllPositive() {
        //add our test user
        long userId = libSyndrome.addUserWithEmail(unitTestEmail);

        //get the questions and add all answers as positive for the user...
        List<Question> mQuestionsList  = libSyndrome.getQuestions();

        for (Question question : mQuestionsList) {
            //add all positive values
            libSyndrome.addAnswerForUserId(userId,question.getId(),question.getAnswerGroup().getPositiveValue());
        }

        //launch the results activity by filling the email and pressing the sign in button
        onView(withId(R.id.email)).perform(typeText(unitTestEmail));
        onView(withId(R.id.email_sign_in_button)).perform(click());

        //verify that our ResultsActivity has launched and has the email address and userId correctly
        intended(hasComponent(new ComponentName(getTargetContext(), ResultsActivity.class)));
        intended(hasExtra(mActivity.EXTRA_USER_EMAIL,unitTestEmail));
        intended(hasExtra(mActivity.EXTRA_USER_ID,userId));

        //verify that we correctly show the user
        onView(withId(R.id.txtUser)).check(matches(withText(mActivity.getString(R.string.user_result_title, unitTestEmail))));

        //verify that we correctly show the score
        onView(withId(R.id.txtScore)).check(matches(withText(mActivity.getString(R.string.user_result_score, 100))));
    }

}
