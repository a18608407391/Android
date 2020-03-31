package org.cs.tec.library.Base.Utils

import android.content.Context
import android.support.v4.content.ContextCompat.getSystemService
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.MainCoroutineDispatcher
import com.zk.library.Base.BaseApplication
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainCoroutineDispatcher


var context = BaseApplication.getInstance().applicationContext
var MainApplication = BaseApplication.getInstance()
var job = Job()
var uiContext: MainCoroutineDispatcher = Dispatchers.Main
var ioContext: CoroutineDispatcher = Dispatchers.IO

fun getString(id: Int): String {
    return context.resources.getString(id)
}

fun getScreenHeightPx(): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    if (windowManager != null) {
        //            windowManager.getDefaultDisplay().getMetrics(dm);
        windowManager.defaultDisplay.getRealMetrics(dm)
        return dm.heightPixels
    }
    return 0
}

fun getRealScreenRelatedInformation(context: Context) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    if (windowManager != null) {
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels
        val heightPixels = outMetrics.heightPixels
        val densityDpi = outMetrics.densityDpi
        val density = outMetrics.density
        val scaledDensity = outMetrics.scaledDensity
        //可用显示大小的绝对宽度（以像素为单位）。
        //可用显示大小的绝对高度（以像素为单位）。
        //屏幕密度表示为每英寸点数。
        //显示器的逻辑密度。
        //显示屏上显示的字体缩放系数。
        Log.e("display", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                ",densityDpi = " + densityDpi + "\n" +
                ",density = " + density + ",scaledDensity = " + scaledDensity)
    }
}
fun getStatusBarHeight(): Int {
    val resources = context.getResources()
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}
//public static int getStatusBarHeight(Activity activity) {
//    Class<?> c = null;
//    Object obj = null;
//    Field field = null;
//    int x = 0, sbar = 0;
//    try {
//        c = Class.forName("com.android.internal.R$dimen");
//        obj = c.newInstance();
//        field = c.getField("status_bar_height");
//        x = Integer.parseInt(field.get(obj).toString());
//        sbar = activity.getResources().getDimensionPixelSize(x);
//    } catch(Exception e1) {
//        Log.e("StatusBarHeight","get status bar height fail");
//        e1.printStackTrace();
//    }
//    Log.i("StatusBarHeight", "" + sbar);
//    return sbar;
//}
fun getInteger(id: Int): Int {
    return context.resources.getInteger(id)
}

fun getArray(id: Int): Array<String> {
    return context.resources.getStringArray(id)
}

fun getColor(id: Int): Int {
    return context.resources.getColor(id)
}

fun getIntArray(id: Int): IntArray {
    return context.resources.getIntArray(id)
}

fun getText(id: Int): CharSequence {
    return context.resources.getText(id)
}

fun getDisplayMetrics(): DisplayMetrics? {
    return context.resources.displayMetrics
}

fun getDimension(id: Int): Float {
    return context.resources.getDimension(id)
}

fun getWindowWidth(): Int? {
    return getDisplayMetrics()?.widthPixels
}

fun getWindowHight(): Int? {
    return getDisplayMetrics()?.heightPixels
}

fun getDpi(): Int? {
    return getDisplayMetrics()?.densityDpi
}

fun setPhone(phone: String): String {
    var t = phone.substring(0, 3)
    var k = phone.substring(3, 7)
    return t + " " + k + " " + phone.substring(7)
}