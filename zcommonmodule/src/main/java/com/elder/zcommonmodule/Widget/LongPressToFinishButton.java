package com.elder.zcommonmodule.Widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.elder.zcommonmodule.R;

import org.cs.tec.library.Utils.ConvertUtils;

public class LongPressToFinishButton extends RelativeLayout {
    private int mLastMotionX, mLastMotionY;
    private static final int TOUCH_SLOP = 20;
    private final int DURATION = 1200;
    int roundWidth;//圆环宽度
    private boolean isMoved = false;
    private int progress = 0;
    private Paint backgroundCirclePaint, progressCirclePaint;
    private ValueAnimator valueAnimator;
    private boolean isPress;
    RelativeLayout buttonLayout;

    public LongPressToFinishButton(Context context) {
        super(context);
        init();
    }

    public LongPressToFinishButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LongPressToFinishButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.long_press_to_finish_button, this, true);
        buttonLayout = (RelativeLayout) convertView.findViewById(R.id.button_layout);
        roundWidth = ConvertUtils.Companion.dp2px(2);
        backgroundCirclePaint = new Paint();
        backgroundCirclePaint.setStyle(Paint.Style.STROKE);
        backgroundCirclePaint.setColor(Color.parseColor("#10000000"));
        backgroundCirclePaint.setAntiAlias(true);
        backgroundCirclePaint.setStrokeWidth(roundWidth);
        progressCirclePaint = new Paint();
        progressCirclePaint.setStyle(Paint.Style.STROKE);
        progressCirclePaint.setColor(Color.parseColor("#FFFFFF"));
        progressCirclePaint.setAntiAlias(true);
        progressCirclePaint.setStrokeWidth(roundWidth);
        valueAnimator = ValueAnimator.ofInt(0, 100);

        valueAnimator.setDuration(DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
                if (progress == 100) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isPress = false;
                            postInvalidate();
                            if (onFinishListener != null) {
                                onFinishListener.onFinish();
                            }
                        }
                    }, 50);
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isPress = false;
                postInvalidate();
            }
        });
    }


    private OnFinishListener onFinishListener;

    public interface OnFinishListener {
        void onFinish();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.i("nieqi", "dispatchDraw , progress = " + progress);
        int center = getWidth() / 2 ;
        int radius = center - roundWidth / 2  - ConvertUtils.Companion.dp2px(10); //圆环的半径
        canvas.save();
        RectF oval = new RectF(center - radius, center - radius, center
                + radius, center + radius);
        if (isPress) {
            canvas.drawArc(oval, 0, 360, false, backgroundCirclePaint);
            canvas.drawArc(oval, -90, 360 * progress / 100, false, progressCirclePaint);
        }
        canvas.restore();
    }

    private void startAnim() {
        Log.i("nieqi", "startAnimation");
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    private void cancelAnimation() {
        isPress = false;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        progress = 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x >= buttonLayout.getLeft() && x <= buttonLayout.getRight()
                        && y >= buttonLayout.getTop() && y <= buttonLayout.getBottom()) {//
                    mLastMotionX = x;
                    mLastMotionY = y;
                    isMoved = false;
                    isPress = true;
                    startAnim();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoved) {
                    break;
                }
                if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                        || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                    isMoved = true;
                    cancelAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                cancelAnimation();
                break;
        }
        return true;
    }
}
