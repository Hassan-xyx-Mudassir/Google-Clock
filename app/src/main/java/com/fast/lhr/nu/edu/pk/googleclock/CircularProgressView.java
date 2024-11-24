package com.fast.lhr.nu.edu.pk.googleclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.animation.ValueAnimator;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF circleBounds;
    private float progress = 0f; // Progress from 0 to 360 degrees
    private float strokeWidth = 30f; // Thickness of the circle
    private float radiusScaleFactor = 0.95f; // Factor to slightly shrink the radius
    private int backgroundColor = 0xFFCCCCCC; // Default gray color
    private int progressColor = 0xFF007BFF; // Default blue color

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Background circle paint
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(backgroundColor); // Default gray
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        // Progress circle paint
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(progressColor); // Default blue
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        circleBounds = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Center coordinates
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Calculate radius with scale factor
        float radius = (Math.min(centerX, centerY) - (strokeWidth / 2f)) * radiusScaleFactor;

        // Set bounds for the circle
        circleBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw background circle
        canvas.drawArc(circleBounds, 0, 360, false, backgroundPaint);

        // Draw progress arc
        canvas.drawArc(circleBounds, -90, progress, false, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = progress; // Set progress in degrees
        invalidate(); // Redraw the view
    }

    public void startProgressAnimation(int duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 360f); // Animate from 0 to full circle
        animator.setDuration(duration); // Set animation duration
        animator.addUpdateListener(animation -> {
            setProgress((float) animation.getAnimatedValue());
        });
        animator.start();
    }

    public void animateProgressToFull(int duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(progress, 360f); // Animate from current progress to full circle
        animator.setDuration(duration); // Set animation duration
        animator.addUpdateListener(animation -> {
            setProgress((float) animation.getAnimatedValue());
        });
        animator.start();
    }

    public void resetProgress() {
        progress = 0f; // Reset progress to 0
        progressPaint.setColor(backgroundColor); // Change progress circle color to gray
        invalidate(); // Redraw the view
    }

    public void startNewLap() {
        resetProgress(); // Reset progress
        progressPaint.setColor(progressColor); // Change color back to blue
        invalidate();
    }
}
