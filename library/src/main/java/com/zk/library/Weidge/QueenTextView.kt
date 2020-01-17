package org.cs.tec.library.Weidge

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView


class QueenTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr),Runnable {
    override fun run() {
        object : Runnable {
            override fun run() {
                currentScrollX += speed;  // 滚动速度每次加几个点
                scrollTo(currentScrollX, 0); // 滚动到指定位置
                if (isStop) {
                    return
                }
                if (currentScrollX >= endX) {   // 如果滚动的位置大于最大限度则滚动到初始位置
                    scrollTo(firstScrollX, 0)
                    currentScrollX = firstScrollX; // 初始化滚动速度
                    postDelayed(this, SCROLL_DELAYED.toLong())  // SCROLL_DELAYED毫秒之后重新滚动
                } else {
                    postDelayed(this, delayed.toLong())  // delayed毫秒之后再滚动到指定位置
                }
            }
        }
    }

    override fun isFocused(): Boolean {
        return true
    }

    private var currentScrollX = 0 // 当前滚动位置  X轴
    private var firstScrollX = 0  //  初始位置
    private var isStop = false  // 开始停止的标记
    private var textWidth: Int = 0  // 文本宽度
    private var mWidth = 0 // 控件宽度
    private var speed = 5  // 默认是两个点
    private var delayed = 1000 // 默认是1秒
    private var endX: Int = 0 // 滚动到哪个位置
    private var isFirstDraw = true // 当首次或文本改变时重置
    private var SCROLL_DELAYED = 4 * 1000


    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        isStop = true // 停止滚动
        this.removeCallbacks(this)   // 清空队列
        currentScrollX = firstScrollX  // 滚动到初始位置
        this.scrollTo(currentScrollX, 0)
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        // 需要重新设置参数
        isFirstDraw = true
        isStop = false
        postDelayed(this, SCROLL_DELAYED.toLong())
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isFirstDraw) {
//            getTextWidth()
            firstScrollX = scrollX // 获取第一次滑动的X轴距离
            System.out.println("firstScrollX======"+firstScrollX)
            currentScrollX = firstScrollX
            mWidth = this.width;  // 获取文本宽度，如果文本宽度大于屏幕宽度，则为屏幕宽度，否则为文本宽度
            endX = firstScrollX + textWidth - mWidth/2  // 滚动的最大距离，可根据需要来定
            isFirstDraw = false
        }
    }

    fun setSpeed(speed: Int) {
        this.speed = speed
    }


    /**
     * @param 滚动时间间隔
     */
    fun setDelayed(delayed: Int) {
        this.delayed = delayed
    }

    /**
     * 开始滚动
     */
    fun startScroll() {
        isStop = false
        this.removeCallbacks(this)  // 清空队列
        postDelayed(this, SCROLL_DELAYED.toLong())  // 4秒之后滚动到指定位置
    }

    /**
     * 停止滑动
     */
    fun stopScroll() {
        isStop = true
    }

    /**
     * 从头开始滑动
     */
    fun startFor() {
        currentScrollX = 0  // 将当前位置置为0
        startScroll()
    }
}