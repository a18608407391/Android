package com.elder.zcommonmodule.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.elder.zcommonmodule.R;

import org.cs.tec.library.Utils.ConvertUtils;

@SuppressLint("AppCompatCustomView")
public class MoveLayoutByScreen extends View {

    /**
     * 浮动工具类
     */
    private int lastX = 0;
    private int lastY = 0;

    private int dx;
    private int dy;
    private float movex = 0;
    private float movey = 0;

    private int screenWidth;
    private int screenHeight;


    private int TvColor = Color.BLACK;

    private int rsid = R.drawable.maker_location;

    public MoveLayoutByScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = ConvertUtils.Companion.dp2px(272);
        screenHeight = ConvertUtils.Companion.dp2px(402);
    }


    public void setTvColor(int color) {
        this.TvColor = color;
        invalidate();
    }

    public void setResid(int rsid) {
        this.rsid = rsid;
    }

    String distance = "200KM";
    String date = "2019-03-09 14:45";
    String weather = "晴";

    public void setDatas(String distance, String date, String weather) {
        this.distance = distance;
        this.date = date;
        this.weather = weather;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint labelsPaint = new Paint();
        labelsPaint.setColor(TvColor);
        labelsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        labelsPaint.setAntiAlias(true);
        labelsPaint.setTextSize(ConvertUtils.Companion.sp2px(30));
        canvas.drawText(distance, 0, ConvertUtils.Companion.dp2px(20), labelsPaint);

        Paint labelsPaint1 = new Paint();
        labelsPaint1.setColor(TvColor);
        labelsPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
        labelsPaint1.setAntiAlias(true);
        labelsPaint1.setTextSize(ConvertUtils.Companion.sp2px(15F));

        Paint mStartPaint = new Paint();
        if (rsid != 0) {
            Bitmap bit = BitmapFactory.decodeResource(getContext().getResources(), rsid);
            RectF rect = new RectF();
            rect.left = 0;
            rect.top = ConvertUtils.Companion.dp2px(25);
            rect.right = bit.getWidth();
            rect.bottom = bit.getHeight();
            canvas.drawBitmap(bit, null, rect, mStartPaint);
            canvas.drawText(date + " " + weather, bit.getWidth() + ConvertUtils.Companion.dp2px(5), ConvertUtils.Companion.dp2px(40), labelsPaint1);
        } else {
            canvas.drawText(date + " " + weather, 0, ConvertUtils.Companion.dp2px(40), labelsPaint1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                movex = lastX;
                movey = lastY;
                break;
            case MotionEvent.ACTION_MOVE:
                dx = (int) event.getRawX() - lastX;
                dy = (int) event.getRawY() - lastY;

                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                layout(left, top, right, bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //避免滑出触发点击事件
                if ((int) (event.getRawX() - movex) != 0
                        || (int) (event.getRawY() - movey) != 0) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}