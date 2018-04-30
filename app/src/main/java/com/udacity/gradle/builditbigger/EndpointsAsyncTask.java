package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

import udacity.com.androidjokes.JokeActivity;

/**
 * Created by Laci on 28/04/2018.
 * The basic idea of this class if from here:
 * https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/77e9910911d5412e5efede5fa681ec105a0f02ad/HelloEndpoints
 */

public class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private static MyApi myApiService = null;
    private Context mContext;
    private SimpleIdlingResource mIdlingResource;

    public EndpointsAsyncTask(Context context, SimpleIdlingResource idlingResource) {
        this.mContext = context;
        this.mIdlingResource = idlingResource;
    }

    @Override
    protected void onPreExecute() {
        if (mIdlingResource != null) mIdlingResource.setIdleState(false);
    }

    @Override
    protected String doInBackground(Void... contexts) {
        if(myApiService == null) {
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(
                                AbstractGoogleClientRequest<?> abstractGoogleClientRequest
                        ) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            myApiService = builder.build();
        }

        try {
            return myApiService.pullJoke().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (mIdlingResource != null) {
            mIdlingResource.setAsyncTaskResultJoke(result);
            mIdlingResource.setIdleState(true);
        }
        Intent startJokeActivityIntent = new Intent(
                mContext,
                JokeActivity.class
        );
        startJokeActivityIntent.putExtra(Intent.EXTRA_TEXT, result);
        mContext.startActivity(startJokeActivityIntent);
    }
}
