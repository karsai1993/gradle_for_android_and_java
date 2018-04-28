package udacity.com.androidjokes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    private TextView mJokeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        mJokeTextView = findViewById(R.id.tv_joke);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mJokeTextView.setText( bundle.getString(Intent.EXTRA_TEXT) );
        }
    }

}
