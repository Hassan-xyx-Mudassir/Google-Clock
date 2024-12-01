package com.fast.lhr.nu.edu.pk.googleclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.animation.ValueAnimator;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF circleBounds;
    private float progress = 0f; // Progress from 0 to 360 degrees
    private float strokeWidth = 30f; // Thickness of the circle
    private float radiusScaleFactor = 0.95f; // Factor to slightly shrink the radius
    private final int backgroundColor = 0xFF454746; // Default gray color
    private int progressColor = 0xFF007BFF; // Default blue color
    private ValueAnimator currentAnimator; // To hold the current animator for pause/resume
    private float currentProgress = 0f; // To store the current progress
    private long remainingDuration = 0; // Store the remaining duration when paused

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

    // Set the progress directly in degrees
    public void setProgress(float progress) {
        this.progress = progress; // Set progress in degrees
        invalidate(); // Redraw the view
    }

    // Start animation from full circle (360) to zero
    public void startProgressAnimationFromFull(long duration) {
        currentProgress = 360f; // Set the current progress to full circle
        animateProgressToZero(duration);
    }

    // Start animation from zero to full circle
    public void startProgressAnimation(long duration) {
        currentProgress = 0f; // Set the current progress to zero
        animateProgressToFull(duration);
    }

    // Animate progress to full circle from current progress
    public void animateProgressToFull(long duration) {
        Log.d("CircularProgressView", "Animating to full");
        // Stop any ongoing animation
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
        }

        currentAnimator = ValueAnimator.ofFloat(currentProgress, 360f); // Animate from current progress to full circle
        currentAnimator.setDuration(duration);
        currentAnimator.addUpdateListener(animation -> {
            setProgress((float) animation.getAnimatedValue()); // Update progress value
        });
        currentAnimator.start();
    }

    // Animate progress to zero from current progress
    public void animateProgressToZero(long duration) {
        Log.d("CircularProgressView", "Animating to zero");
        // Stop any ongoing animation
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
        }

        currentAnimator = ValueAnimator.ofFloat(currentProgress, 0f); // Animate from current progress to zero
        currentAnimator.setDuration(duration);
        currentAnimator.addUpdateListener(animation -> {
            setProgress((float) animation.getAnimatedValue()); // Update progress value
        });
        currentAnimator.start();
    }

    // Reset the progress to 0 and set the progress circle color to gray
    public void resetProgress() {
        currentProgress = 0f; // Reset progress to 0
        progressPaint.setColor(backgroundColor); // Change progress circle color to gray
        invalidate(); // Redraw the view
    }
    public void resetProgressToFull() {
        currentProgress = 360f; // Reset progress to 360
        progressPaint.setColor(progressColor); // Change progress circle color to gray
        invalidate(); // Redraw the view
    }

    // Start a new lap: reset progress and change the color back to the original
    public void startNewLap() {
        resetProgress(); // Reset progress
        progressPaint.setColor(progressColor); // Change color back to blue
        invalidate();
    }

    // Pause the animation
    public void pauseAnimation() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentProgress = (float) currentAnimator.getAnimatedValue(); // Store current progress
            remainingDuration = currentAnimator.getDuration() - currentAnimator.getCurrentPlayTime(); // Store remaining duration
            currentAnimator.cancel(); // Stop the animation
        }
    }

    // Resume the animation
    public void resumeAnimation() {
        if (remainingDuration > 0) {
            // Resume from the current progress
            currentAnimator = ValueAnimator.ofFloat(currentProgress, 0f); // Resume animation from current progress to full
            currentAnimator.setDuration(remainingDuration);
            currentAnimator.addUpdateListener(animation -> {
                setProgress((float) animation.getAnimatedValue()); // Update progress value
            });
            currentAnimator.start();
        }
    }

    // Pause the animation and reset the progress
    public void resetAndPauseAnimation() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
        }
        resetProgress(); // Reset progress
    }
}
