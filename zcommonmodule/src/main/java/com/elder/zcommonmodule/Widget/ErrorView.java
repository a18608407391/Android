package com.elder.zcommonmodule.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ErrorView extends View {
    //总体大小
    private int mHeight;
    private int mWidth;
    private int mProgress = 0;
    private int mLineOneX = 0;
    private int mLineOneY = 0;
    private int mLineTwoX = 0;
    private int mLineTwoY = 0;
    private boolean isLineDrawDone = false;
    private Context mContext = null;

    public ErrorView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public ErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        RectF rectF = new RectF(0 + 10, 0 + 10, mWidth - 10, mHeight - 10);
        canvas.drawArc(rectF, 180, 360 * mProgress / 100, false, p);
        mProgress += 1;
        if (mProgress > 80) {
            if (mLineOneX < mWidth * 0.5) {
                mLineOneX += 4;
                mLineOneY += 4;
            }
            canvas.drawLine(mWidth / 4 - 10, mHeight / 4 - 10, mWidth / 4 + mLineOneX + 10, mHeight / 4 + mLineOneY + 10, p);
            if (mLineOneX == mWidth * 0.5) {
                if (mLineTwoX < mWidth * 0.5) {
                    mLineTwoX += 1;
                    mLineTwoY += 1;
                } else {
                    //判断全部绘制完成
                    isLineDrawDone = true;
                }
                canvas.drawLine(mWidth / 4 - 10, (float) (mHeight * 0.75) + 10, mWidth / 4 + mLineTwoX + 10, (float) (mHeight * 0.75) - mLineTwoY - 10, p);
            }
        }
        if (isLineDrawDone) {
            Log.e("wing", "draw done");
        } else {
            postInvalidateDelayed(10);
        }
    }


    public void reset() {
        mProgress = 0;
        mLineOneX = 0;
        mLineOneY = 0;
        mLineTwoX = 0;
        mLineTwoY = 0;
        isLineDrawDone = false;
        invalidate();
    }
}