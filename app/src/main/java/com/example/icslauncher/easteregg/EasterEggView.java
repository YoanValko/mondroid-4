package com.example.icslauncher.easteregg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.icslauncher.R;

/**
 * Reimplementation of the Android 4.0.4 "Ice Cream Sandwich" Easter egg.
 * <p>
 * Behavior: tap repeatedly to reveal the ice cream sandwich with an Android figure, then tap the
 * sandwich to zoom into the scrolling Android-team credits. Entirely drawn with Canvas primitives;
 * no proprietary assets are bundled.
 */
public class EasterEggView extends View {

    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SANDWICH = 1;
    private static final int STATE_ZOOMED = 2;

    // Number of taps required to reveal the egg (mirrors the "tap Android version" interaction).
    private static final int TAPS_TO_REVEAL = 5;

    // Representative Android-team credits (reimplemented, not extracted from a system image).
    private static final String[] TEAM_NAMES = {
            "Android Engineering", "Platform Frameworks", "UI Toolkit", "Graphics",
            "Media", "Connectivity", "Bluetooth", "NFC", "Location", "Sensors",
            "Telephony", "Wi-Fi", "Audio", "Camera", "Display", "Input",
            "Power Management", "Security", "Dalvik / ART", "Package Manager",
            "Content Providers", "Notifications", "Widgets", "Accessibility",
            "Internationalization", "The Android Team"
    };

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private int state = STATE_HIDDEN;
    private int tapCount = 0;
    private float scrollY = 0f;
    private float lastY = 0f;
    private boolean dragging = false;

    public EasterEggView(Context context) {
        super(context);
        init();
    }

    public EasterEggView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EasterEggView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        paint.setColor(Color.parseColor("#A4C639"));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(getResources().getDimension(R.dimen.easter_egg_name_text_size));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // Background.
        canvas.drawColor(Color.parseColor("#1A1A1A"));

        if (state == STATE_HIDDEN) {
            // Nothing drawn here; the hint TextView in the layout invites the user to tap.
            return;
        }

        float scale = Math.min(w, h) / 480f;
        float cx = w / 2f;
        float cy;

        if (state == STATE_SANDWICH) {
            cy = h / 2f;
            drawSandwich(canvas, cx, cy, 0.45f * scale);
        } else {
            // Zoomed: sandwich near top, credits scrolling below.
            cy = h * 0.22f;
            drawSandwich(canvas, cx, cy, 0.55f * scale);
            drawCredits(canvas, h * 0.42f, h);
        }
    }

    private void drawSandwich(Canvas canvas, float cx, float cy, float scale) {
        float bw = 130f * scale;   // sandwich width
        float bh = 46f * scale;    // sandwich height

        // Top bread
        paint.setColor(Color.parseColor("#F5DEB3"));
        rectF.set(cx - bw / 2, cy - bh / 2, cx + bw / 2, cy - bh / 2 + 10f * scale);
        canvas.drawRect(rectF, paint);
        paint.setColor(Color.parseColor("#D2A679"));
        canvas.drawArc(cx - bw / 2, cy - bh / 2, cx + bw / 2, cy - bh / 2 + 10f * scale, 180, 180, true, paint);

        // Cream
        paint.setColor(Color.parseColor("#FFF8DC"));
        rectF.set(cx - bw / 2 + 6f * scale, cy - bh / 2 + 10f * scale,
                cx + bw / 2 - 6f * scale, cy - bh / 2 + 22f * scale);
        canvas.drawRect(rectF, paint);

        // Bottom bread
        paint.setColor(Color.parseColor("#F5DEB3"));
        rectF.set(cx - bw / 2, cy - bh / 2 + 22f * scale, cx + bw / 2, cy - bh / 2 + 34f * scale);
        canvas.drawRect(rectF, paint);
        paint.setColor(Color.parseColor("#D2A679"));
        canvas.drawArc(cx - bw / 2, cy - bh / 2 + 22f * scale, cx + bw / 2, cy - bh / 2 + 34f * scale, 180, 180, true, paint);

        // Android figure standing on the sandwich
        float ah = 40f * scale;
        float ax = cx;
        float ay = cy - bh / 2 - ah;
        // head
        paint.setColor(Color.parseColor("#A4C639"));
        float head = 18f * scale;
        rectF.set(ax - head, ay - head * 1.6f, ax + head, ay);
        canvas.drawRoundRect(rectF, 4f * scale, 4f * scale, paint);
        // eyes
        paint.setColor(Color.WHITE);
        float e = 4f * scale;
        canvas.drawRect(ax - 8f * scale, ay - 10f * scale, ax - 4f * scale, ay - 6f * scale, paint);
        canvas.drawRect(ax + 4f * scale, ay - 10f * scale, ax + 8f * scale, ay - 6f * scale, paint);
        // body
        paint.setColor(Color.parseColor("#A4C639"));
        float bodyW = 26f * scale, bodyH = 22f * scale;
        rectF.set(ax - bodyW / 2, ay, ax + bodyW / 2, ay + bodyH);
        canvas.drawRoundRect(rectF, 4f * scale, 4f * scale, paint);
        // legs
        float legW = 8f * scale, legH = 18f * scale;
        rectF.set(ax - 10f * scale, ay + bodyH, ax - 10f * scale + legW, ay + bodyH + legH);
        canvas.drawRoundRect(rectF, 3f * scale, 3f * scale, paint);
        rectF.set(ax + 2f * scale, ay + bodyH, ax + 2f * scale + legW, ay + bodyH + legH);
        canvas.drawRoundRect(rectF, 3f * scale, 3f * scale, paint);
    }

    private void drawCredits(Canvas canvas, float startY, float bottom) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(getResources().getDimension(R.dimen.easter_egg_name_text_size));
        Paint.FontMetrics fm = paint.getFontMetrics();
        float lineHeight = fm.descent - fm.ascent + 8f;
        float y = startY - scrollY;
        for (String name : TEAM_NAMES) {
            if (y + fm.descent > 0 && y < bottom) {
                float x = (getWidth() - paint.measureText(name)) / 2f;
                canvas.drawText(name, x, y, paint);
            }
            y += lineHeight;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                dragging = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = event.getY() - lastY;
                lastY = event.getY();
                if (state == STATE_ZOOMED && Math.abs(dy) > 4f) {
                    dragging = true;
                    scrollY += dy;
                    // Clamp scroll so credits stay roughly in range.
                    float maxScroll = Math.max(0f, TEAM_NAMES.length * 26f - getHeight() * 0.55f);
                    if (scrollY < 0) scrollY = 0;
                    if (scrollY > maxScroll) scrollY = maxScroll;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!dragging) {
                    handleTap();
                }
                dragging = false;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTap() {
        if (state == STATE_HIDDEN) {
            tapCount++;
            if (tapCount >= TAPS_TO_REVEAL) {
                state = STATE_SANDWICH;
                tapCount = 0;
            }
        } else if (state == STATE_SANDWICH) {
            state = STATE_ZOOMED;
            scrollY = 0f;
        } else {
            state = STATE_HIDDEN;
            tapCount = 0;
            scrollY = 0f;
        }
        invalidate();
    }
}
