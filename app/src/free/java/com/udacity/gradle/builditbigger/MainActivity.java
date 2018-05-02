package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

import udacity.com.androidjokes.JokeActivity;


public class MainActivity extends AppCompatActivity {

    private SimpleIdlingResource mIdlingResource;
    private InterstitialAd mInterstitialAd;
    private static final String INTERSTITIAL_AD_WAIT_REQUEST
            = "Please, try it some moments later again!";
    private Toast mToast;
    private static ProgressBar mProgressBar;

    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @VisibleForTesting
    @NonNull
    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.pb_loading);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdOpened() {
                if (mToast != null) mToast.cancel();
            }

            @Override
            public void onAdClosed() {
                new EndpointsAsyncTask(MainActivity.this, mIdlingResource).execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This function is to display a joke in the created Android library
     * @param view
     */
    public void tellJoke(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            if (mToast != null) mToast.cancel();
            mToast = Toast.makeText(this, INTERSTITIAL_AD_WAIT_REQUEST, Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    /**
     * The basic idea of this class if from here:
     * https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/77e9910911d5412e5efede5fa681ec105a0f02ad/HelloEndpoints
     */
    static class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
        private MyApi myApiService = null;
        private Context mContext;
        private SimpleIdlingResource mIdlingResource;

        public EndpointsAsyncTask(Context context, SimpleIdlingResource idlingResource) {
            this.mContext = context;
            this.mIdlingResource = idlingResource;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            if (mIdlingResource != null) mIdlingResource.setIdleState(false);
        }

        @Override
        protected String doInBackground(Void... contexts) {
            if(myApiService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl(mContext.getResources().getString(R.string.root_url))
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
            mProgressBar.setVisibility(View.INVISIBLE);
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
}
