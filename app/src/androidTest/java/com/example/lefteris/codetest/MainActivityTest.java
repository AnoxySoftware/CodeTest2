/*
 * Copyright (c) 2017. Lefteris Haritou
 */

package com.example.lefteris.codetest;

import android.content.ComponentName;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.toddsyndromelib.toddsSyndrome;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by Lefteris on 08/02/17.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity;

    //our test email address
    private String unitTestEmail = "unitTestRandom@unitTest.com";

    //our library
    private toddsSyndrome libSyndrome;

    @Before
    public void setUp() {
        Intents.init();
        mActivity = mActivityRule.getActivity();
        //delete our test email before we start the test (if it exists), else it will don't do anything
        libSyndrome = new toddsSyndrome(mActivity.getApplicationContext());
        libSyndrome.deleteUserWithEmail(unitTestEmail);
    }

    @After
    public void cleanUp() {
        Intents.release();
        libSyndrome.closeDatabase();
    }

    @Test
    public void testLoginWithoutEmail() {
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(matches(hasErrorText(mActivity.getString(R.string.error_field_required))));
    }

    @Test
    public void testLoginWithWrongEmail() {
        onView(withId(R.id.email)).perform(typeText("wrongemail@.com"));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(matches(hasErrorText(mActivity.getString(R.string.error_invalid_email))));
    }

    @Test
    public void testCorrectLogin() {
        onView(withId(R.id.email)).perform(typeText(unitTestEmail));
        onView(withId(R.id.email_sign_in_button)).perform(click());

        //verify that our QuestionsActivity has launched and has the email address and userId correctly
        intended(hasComponent(new ComponentName(getTargetContext(), QuestionsActivity.class)));
        intended(hasExtra(MainActivity.EXTRA_USER_EMAIL,unitTestEmail));
        intended(hasExtra(MainActivity.EXTRA_USER_ID,mActivity.existingUserId));
    }

    @Test
    public void testExistingEmailLogin() {
        //create the user account first
        libSyndrome.addUserWithEmail(unitTestEmail);

        //try using the previous account again and perform login
        onView(withId(R.id.email)).perform(typeText(unitTestEmail));
        onView(withId(R.id.email_sign_in_button)).perform(click());

        //verify that our ResultsActivity has launched and has the email address and userId correctly
        intended(hasComponent(new ComponentName(getTargetContext(), ResultsActivity.class)));
        intended(hasExtra(mActivity.EXTRA_USER_EMAIL,unitTestEmail));
        intended(hasExtra(mActivity.EXTRA_USER_ID,mActivity.existingUserId));
    }

}