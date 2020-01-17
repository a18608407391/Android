package com.elder.zcommonmodule.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.DecimalFormat;

public class CircleBar extends View {

    private RectF mColorWheelRectangle = new RectF();
    private Paint mDefaultWheelPaint;
    private Paint mColorWheelPaint;
    private Paint mColorWheelPaintCentre;
    private float circleStrokeWidth;
    private float mSweepAnglePer;
    private float mPercent;
    private int stepnumber, stepnumbernow;
    private float pressExtraStrokeWidth;
    private BarAnimation anim;
    private int stepnumbermax = 12;
    private DecimalFormat fnum = new DecimalFormat("#.0");

    private int mColors[];

    public CircleBar(Context context) {
        this(context, null);
    }

    public CircleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        mColors = new int[]{0xFF6BB7ED, 0xFF47D9CE, 0xFF56CADC};
        Shader s = new SweepGradient(0, 0, mColors, null);
        mColorWheelPaint = new Paint();
        mColorWheelPaint.setShader(s);
        mColorWheelPaint.setStyle(Paint.Style.STROKE);
        mColorWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mColorWheelPaint.setAntiAlias(true);

        mColorWheelPaintCentre = new Paint();
        mColorWheelPaintCentre.setColor(Color.rgb(214, 246, 254));
        mColorWheelPaintCentre.setStyle(Paint.Style.STROKE);
        mColorWheelPaintCentre.setStrokeCap(Paint.Cap.ROUND);
        mColorWheelPaintCentre.setAntiAlias(true);

        mDefaultWheelPaint = new Paint();
        mDefaultWheelPaint.setColor(Color.rgb(127, 127, 127));
        mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
        mDefaultWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mDefaultWheelPaint.setAntiAlias(true);

        anim = new BarAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);

        circleStrokeWidth = Textscale(35, min);
        pressExtraStrokeWidth = Textscale(2, min);

        mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth,
                circleStrokeWidth + pressExtraStrokeWidth, min
                        - circleStrokeWidth - pressExtraStrokeWidth, min
                        - circleStrokeWidth - pressExtraStrokeWidth);
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth - 5);
        mColorWheelPaintCentre.setStrokeWidth(circleStrokeWidth + 5);
        mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth - Textscale(2, min));
        mDefaultWheelPaint.setShadowLayer(Textscale(10, min), 0, 0, Color.rgb(127, 127, 127));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mColorWheelRectangle, 0, 359, false, mDefaultWheelPaint);
        canvas.drawArc(mColorWheelRectangle, 0, 359, false, mColorWheelPaintCentre);
        canvas.drawArc(mColorWheelRectangle, 270, mSweepAnglePer, false, mColorWheelPaint);
    }

    public class BarAnimation extends Animation {
        public BarAnimation() {

        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mPercent = Float.parseFloat(fnum.format(interpolatedTime
                        * stepnumber * 100f / stepnumbermax));
                mSweepAnglePer = interpolatedTime * stepnumber * 360
                        / stepnumbermax;
                stepnumbernow = (int) (interpolatedTime * stepnumber);
            } else {
                mPercent = Float.parseFloat(fnum.format(stepnumber * 100f
                        / stepnumbermax));
                mSweepAnglePer = stepnumber * 360 / stepnumbermax;
                stepnumbernow = stepnumber;
            }
            postInvalidate();
        }
    }

    public float Textscale(float n, float m) {
        return n / 500 * m;
    }

    public void update(int stepnumber, int time) {
        this.stepnumber = stepnumber;
        setAnimationTime(time);
        this.startAnimation(anim);
    }

    public void setMaxstepnumber(int Maxstepnumber) {
        stepnumbermax = Maxstepnumber;
    }

    public void setColor(int color) {
        mColorWheelPaint.setShader(null);
        mColorWheelPaint.setColor(color);
    }

    public void setShaderColor(int[] shaderColor) {
        this.mColors = shaderColor;
        Shader newShader = new SweepGradient(0, 0, mColors, null);
        mColorWheelPaint.setShader(newShader);
    }

    public void setAnimationTime(int time) {
        anim.setDuration(time * stepnumber / stepnumbermax);
    }

}