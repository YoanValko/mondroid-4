package com.yoanvalko.memdroid.easteregg;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.yoanvalko.memdroid.R;

/**
 * Self-contained activity hosting the Ice Cream Sandwich Easter egg. Launched from Settings
 * (About) or directly; requires no system access.
 */
public class EasterEggActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);
    }
}
