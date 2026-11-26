package com.example.icslauncher.apps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.Random;

/**
 * Simple fortune teller app.
 */
public class ICSFortuneActivity extends AppCompatActivity {

    private TextView fortuneView;
    private Button fortuneButton;

    private final String[] fortunes = {
        "A pleasant surprise is waiting for you.",
        "Your hard work will soon pay off.",
        "Good things are coming your way.",
        "Now is the perfect time to try something new.",
        "You will meet someone interesting today.",
        "A friend will ask for your help soon.",
        "Your creativity will shine through.",
        "Good news is on its way.",
        "You will find what you are looking for.",
        "A journey awaits you in the near future.",
        "Your dedication will be recognized.",
        "Love is in the air for you.",
        "A small act of kindness will make a big difference.",
        "You will receive good news soon.",
        "Your patience will be rewarded.",
        "A new opportunity is coming your way.",
        "Trust your instincts.",
        "Good health and happiness are yours.",
        "You will discover something unexpected.",
        "Your efforts will be appreciated."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Fortune Teller");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        fortuneView = new TextView(this);
        fortuneView.setTextSize(18);
        fortuneView.setPadding(0, 20, 0, 20);
        root.addView(fortuneView);

        fortuneButton = new Button(this);
        fortuneButton.setText("Get Fortune");
        fortuneButton.setTextSize(18);
        fortuneButton.setOnClickListener(v -> getFortune());
        root.addView(fortuneButton);

        setContentView(root);

        // Show initial fortune
        getFortune();
    }

    private void getFortune() {
        Random random = new Random();
        String fortune = fortunes[random.nextInt(fortunes.length)];
        fortuneView.setText(fortune);
    }
}
