package com.demo.tvlauncher.shared.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ReflectionImageView extends View {

    // @REMOVE-LATER: 顶部保持卡片原色，间距可在这里调整。
    private static final float TOP_GAP_DP = 2f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint fadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PorterDuffXfermode fadeMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private final RectF destination = new RectF();
    private final OnLayoutChangeListener sourceLayoutListener = (view, left, top, right, bottom,
                                                                  oldLeft, oldTop, oldRight,
                                                                  oldBottom) -> {
        recycleReflection();
        invalidate();
    };
    private View backgroundSource;
    private View contentSource;
    private Bitmap reflectionBitmap;

    public ReflectionImageView(Context context) {
        super(context);
    }

    public ReflectionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReflectionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSource(View backgroundSource, View contentSource) {
        removeSourceListener(this.backgroundSource);
        removeSourceListener(this.contentSource);
        this.backgroundSource = backgroundSource;
        this.contentSource = contentSource;
        recycleReflection();
        addSourceListener(backgroundSource);
        addSourceListener(contentSource);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap reflection = getReflectionBitmap();
        int width = getWidth();
        int height = getHeight();
        if (reflection == null || width <= 0 || height <= 0) {
            return;
        }

        int saveCount = canvas.save();
        canvas.clipRect(0f, 0f, width, height);
        float gap = Math.min(
                TOP_GAP_DP * getResources().getDisplayMetrics().density,
                height * 0.2f);
        float imageWidth = Math.min(reflection.getWidth(), width);
        float imageHeight = reflection.getHeight() * imageWidth / reflection.getWidth();
        float left = (width - imageWidth) / 2f;
        destination.set(left, gap, left + imageWidth, gap + imageHeight);

        // 独立图层内只改变透明度，不叠加黑灰色，也不擦除父布局。
        int layer = canvas.saveLayer(0f, 0f, width, height, null);
        canvas.drawBitmap(reflection, null, destination, paint);
        fadePaint.setXfermode(fadeMode);
        canvas.drawRect(0f, 0f, width, height, fadePaint);
        fadePaint.setXfermode(null);
        canvas.restoreToCount(layer);

        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        float gap = Math.min(TOP_GAP_DP * getResources().getDisplayMetrics().density,
                height * 0.2f);
        if (height > 0) {
            fadePaint.setShader(new LinearGradient(0f, gap, 0f, height,
                    Color.argb(90, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        }
    }

    private Bitmap getReflectionBitmap() {
        if (backgroundSource == null || contentSource == null
                || backgroundSource.getWidth() <= 0 || backgroundSource.getHeight() <= 0) {
            return null;
        }
        if (reflectionBitmap != null
                && reflectionBitmap.getWidth() == backgroundSource.getWidth()
                && reflectionBitmap.getHeight() == backgroundSource.getHeight()) {
            return reflectionBitmap;
        }

        recycleReflection();
        reflectionBitmap = Bitmap.createBitmap(
                backgroundSource.getWidth(), backgroundSource.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mirrorCanvas = new Canvas(reflectionBitmap);
        mirrorCanvas.scale(1f, -1f, 0f, backgroundSource.getHeight() / 2f);
        // 在倒置画布上合成卡片背景和内容，避免创建中间 Bitmap。
        drawBackground(mirrorCanvas);
        drawContent(mirrorCanvas);
        return reflectionBitmap;
    }

    private void drawBackground(Canvas canvas) {
        Drawable background = backgroundSource.getBackground();
        if (background == null) {
            return;
        }

        Rect oldBounds = new Rect(background.getBounds());
        background.setBounds(0, 0, backgroundSource.getWidth(), backgroundSource.getHeight());
        background.draw(canvas);
        background.setBounds(oldBounds);
    }

    private void drawContent(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.translate(
                contentSource.getLeft() - backgroundSource.getScrollX(),
                contentSource.getTop() - backgroundSource.getScrollY());
        contentSource.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void addSourceListener(View view) {
        if (view != null) {
            view.addOnLayoutChangeListener(sourceLayoutListener);
        }
    }

    private void removeSourceListener(View view) {
        if (view != null) {
            view.removeOnLayoutChangeListener(sourceLayoutListener);
        }
    }

    private void recycleReflection() {
        if (reflectionBitmap != null && !reflectionBitmap.isRecycled()) {
            reflectionBitmap.recycle();
        }
        reflectionBitmap = null;
    }
}
