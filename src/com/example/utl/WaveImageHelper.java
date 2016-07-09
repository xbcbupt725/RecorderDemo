package com.example.utl;

import java.nio.ShortBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

public class WaveImageHelper {

    // reset wave graph image
    public static void recylceWaveBitmap(ImageView waveImage) {
        if (waveImage != null) {
            BitmapDrawable d = (BitmapDrawable) waveImage.getDrawable();
            if (d != null) {
                d.setCallback(null);
                if (d.getBitmap() != null && !d.getBitmap().isRecycled()) {
                    d.getBitmap().recycle();
                }
                d = null;
            }
            waveImage.setImageDrawable(null);
            waveImage.setImageBitmap(null);
        }
    }

    // show wave form for the input audio
    public static void showWave(ShortBuffer waveBuffer, int startPosition,
            int endPosition, ImageView imageview) {
        View parent = (View) imageview.getParent();
        final int w = parent.getWidth();
        final int h = imageview.getHeight();
        if (w <= 0 || h <= 0) {
            // view is not visible this time. Skip drawing.
            return;
        }
        recylceWaveBitmap(imageview);
        final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(b);
        final Paint paint = new Paint();
        paint.setColor(Color.GRAY); // 0xAARRGGBB
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(0x90);

        final PathEffect effect = new CornerPathEffect(3);
        paint.setPathEffect(effect);

        final int numSamples = waveBuffer.remaining();
        int endIndex;
        if (endPosition == 0) {
            endIndex = numSamples;
        } else {
            endIndex = Math.min(endPosition, numSamples);
        }

        int startIndex = startPosition - 2000; // include 250ms before speech
        if (startIndex < 0) {
            startIndex = 0;
        }
        final int numSamplePerWave = 200; // 16KHz 12.5ms = 200 samples
        final float scale = 10.0f / 65536.0f;

        final int count = (endIndex - startIndex) / numSamplePerWave;
        final float deltaX = 1.0f * w / count;
        int yMax = h / 2 - 8;
        Path path = new Path();
        c.translate(0, yMax);
        float x = 0;
        path.moveTo(x, 0);
        for (int i = 0; i < count; i++) {
            final int avabs = getAverageAbs(waveBuffer, startIndex, i,
                    numSamplePerWave);
            int sign = ((i & 01) == 0) ? -1 : 1;
            final float y = Math.min(yMax, avabs * h * scale) * sign;
            path.lineTo(x, y);
            x += deltaX;
            path.lineTo(x, y);
        }
        if (deltaX > 4) {
            paint.setStrokeWidth(3);
        } else {
            paint.setStrokeWidth(Math.max(1, (int) (deltaX - .05)));
        }
        c.drawPath(path, paint);
        imageview.setBackgroundDrawable(null);
        imageview.setImageBitmap(b);
        imageview.setVisibility(View.VISIBLE);
    }

    // calculate the absolute average value of the specified buffer
    private static int getAverageAbs(ShortBuffer buffer, int start, int i,
            int npw) {
        int from = start + i * npw;
        int end = from + npw;
        int total = 0;
        for (int x = from; x < end; x++) {
            total += Math.abs(buffer.get(x));
        }
        return total / npw;
    }
}
