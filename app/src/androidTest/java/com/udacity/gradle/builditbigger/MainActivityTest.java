package com.udacity.gradle.builditbigger;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.ads.InterstitialAd;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import udacity.com.javajokes.JokeProvider;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by Laci on 29/04/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> mMainActivityIntentsTestRule
            = new IntentsTestRule<>(MainActivity.class);

    private SimpleIdlingResource mIdlingResource;
    private InterstitialAd mInterstitialAd;
    private static final String INTERSTITIAL_AD_CLOSE_BUTTON = "Interstitial close button";

    @Before
    public void register() {
        mIdlingResource = mMainActivityIntentsTestRule.getActivity().getIdlingResource();
        mInterstitialAd = mMainActivityIntentsTestRule.getActivity().getInterstitialAd();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    /**
     * Testing with interstitial ad close button click source:
     * https://stackoverflow.com/questions/37843039/how-to-handleclose-interstitial-ad-during-espresso-tests
     */
    @Test
    public void testResultOfAsyncTask() {
        while (mInterstitialAd.isLoading()){}
        onView(withId(R.id.btn_tell_joke)).perform(click());
        ViewInteraction imageButton = onView(
                allOf(withContentDescription(INTERSTITIAL_AD_CLOSE_BUTTON), isDisplayed()));
        imageButton.perform(click());
        onView(withText( mIdlingResource.getAsyncTaskResultJoke() ))
                .check(
                        matches(
                                not(
                                        withText(isEmptyOrNullString())
                                )
                        )
                );
    }

    @After
    public void unregister() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
