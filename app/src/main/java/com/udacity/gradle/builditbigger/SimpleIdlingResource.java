package com.udacity.gradle.builditbigger;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Laci on 29/04/2018.
 */

public class SimpleIdlingResource implements IdlingResource {

    @Nullable private volatile  ResourceCallback mCallback;
    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);
    private String mAsyncTaskResultJoke;

    public String getAsyncTaskResultJoke() {
        return mAsyncTaskResultJoke;
    }

    public void setAsyncTaskResultJoke(String result) {
        this.mAsyncTaskResultJoke = result;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.mCallback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }
}
