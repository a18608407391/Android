package org.cs.tec.library.Base.Utils

import android.util.DisplayMetrics
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