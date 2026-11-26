package com.yoanvalko.memdroid.wallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;

/**
 * Reimplementation of the Ice Cream Sandwich default wallpaper look: a stylized android figure on
 * an ice cream sandwich, drawn entirely with Canvas primitives (no proprietary assets).
 * <p>
 * The engine responds to wallpaper offsets so the image drifts as the user swipes between home
 * screen pages, and stops drawing when the wallpaper is not visible to save battery.
 */
public class ICSWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new ICSEngine();
    }

    private class ICSEngine extends Engine implements Callback {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private SurfaceHolder holder;
        private boolean visible = false;
        private float offsetX = 0f;   // -1 .. 1 parallax offset
        private int width, height;
        private final RectF rectF = new RectF();

        ICSEngine() {
            holder = getSurfaceHolder();
            holder.addCallback(this);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            visible = false;
            holder.removeCallback(this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) drawFrame();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawFrame();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            this.width = w;
            this.height = h;
            drawFrame();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            visible = false;
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep,
                                     int xPixels, int yPixels) {
            this.offsetX = xOffset;
            drawFrame();
        }

        private void drawFrame() {
            if (!visible || holder == null) return;
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c == null) return;
                c.drawColor(Color.parseColor("#0077AA")); // ICS default wallpaper blue

                paint.setStyle(Paint.Style.FILL);

                float cx = width * 0.5f + offsetX * width * 0.15f;
                float cy = height * 0.55f;
                float scale = Math.min(width, height) / 480f;

                drawBread(c, cx - 140f * scale, cy + 40f * scale, 280f * scale, scale);
                drawCream(c, cx - 130f * scale, cy + 30f * scale, 260f * scale, scale);
                drawBread(c, cx - 140f * scale, cy + 20f * scale, 280f * scale, scale);
                drawAndroid(c, cx, cy - 90f * scale, scale);
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
        }

        private void drawBread(Canvas c, float left, float top, float w, float scale) {
            paint.setColor(Color.parseColor("#F5DEB3"));
            rectF.set(left, top, left + w, top + 14f * scale);
            c.drawRect(rectF, paint);
            paint.setColor(Color.parseColor("#D2A679"));
            c.drawArc(left, top, left + w, top + 14f * scale, 180, 180, true, paint);
        }

        private void drawCream(Canvas c, float left, float top, float w, float scale) {
            paint.setColor(Color.parseColor("#FFF8DC"));
            rectF.set(left, top, left + w, top + 12f * scale);
            c.drawRect(rectF, paint);
        }

        private void drawAndroid(Canvas c, float cx, float cy, float scale) {
            // head
            paint.setColor(Color.parseColor("#A4C639"));
            float hw = 52f * scale, hh = 34f * scale;
            rectF.set(cx - hw, cy - hh, cx + hw, cy);
            c.drawRoundRect(rectF, 8f * scale, 8f * scale, paint);
            // eyes
            paint.setColor(Color.WHITE);
            float ex = 10f * scale, ey = 10f * scale;
            c.drawRect(cx - 18f * scale, cy - 20f * scale, cx - 8f * scale, cy - 10f * scale, paint);
            c.drawRect(cx + 8f * scale, cy - 20f * scale, cx + 18f * scale, cy - 10f * scale, paint);
            // body
            float bw = 60f * scale, bh = 36f * scale;
            rectF.set(cx - bw / 2, cy, cx + bw / 2, cy + bh);
            c.drawRoundRect(rectF, 8f * scale, 8f * scale, paint);
            // arms
            float aw = 30f * scale, ah = 14f * scale;
            rectF.set(cx - bw / 2 - aw, cy + 6f * scale, cx - bw / 2, cy + 6f * scale + ah);
            c.drawRoundRect(rectF, 4f * scale, 4f * scale, paint);
            rectF.set(cx + bw / 2, cy + 6f * scale, cx + bw / 2 + aw, cy + 6f * scale + ah);
            c.drawRoundRect(rectF, 4f * scale, 4f * scale, paint);
            // legs
            float lw = 16f * scale, lh = 26f * scale;
            rectF.set(cx - 14f * scale, cy + bh, cx - 14f * scale + lw, cy + bh + lh);
            c.drawRoundRect(rectF, 4f * scale, 4f * scale, paint);
            rectF.set(cx - 2f * scale, cy + bh, cx - 2f * scale + lw, cy + bh + lh);
            c.drawRoundRect(rectF, 4f * scale, 4f * scale, paint);
            // antenna
            paint.setColor(Color.parseColor("#7E942E"));
            c.drawRect(cx - 2f * scale, cy - 30f * scale, cx + 2f * scale, cy - 18f * scale, paint);
            paint.setColor(Color.parseColor("#A4C639"));
            c.drawCircle(cx, cy - 32f * scale, 3f * scale, paint);
        }
    }
}
