package com.elder.zcommonmodule.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class LongClickView extends View {
    private int mHeight;
    private int mWidth;
    private int mProgress = 0;
    private int mLineOneX = 0;
    private int mLineOneY = 0;
    private int mLineTwoX = 0;
    private int mLineTwoY = 0;
    private boolean isLineDrawDone = false;
    private long startTime = 0;
    private Context mContext = null;
    private boolean isDown = false;

    public LongClickView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public LongClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = 160;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = 160;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setStrokeWidth(10);
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        if(isDown&&isLineDrawDone==false){
            RectF rectF = new RectF(0 + 10, 0 + 10, mWidth - 10, mHeight - 10);
            canvas.drawArc(rectF, 180, 360 * mProgress / 100, false, p);
            mProgress += 1;
            if (mProgress == 100) {
                isLineDrawDone = true;
            }
            if (isLineDrawDone) {
                Log.e("wing", "draw done");
            } else {
                postInvalidateDelayed(10);
            }
        }else{
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
