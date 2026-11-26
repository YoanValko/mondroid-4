package com.yoanvalko.memdroid.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.yoanvalko.memdroid.R;

/**
 * About screen: version, credits, license, and trademark information.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
