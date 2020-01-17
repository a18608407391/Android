package org.cs.tec.library.Utils

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getDisplayMetrics
import org.cs.tec.library.Utils.constant.MemoryConstants
import org.cs.tec.library.Utils.constant.TimeConstants
import java.io.*
import kotlin.experimental.and
import kotlin.experimental.or
import com.zk.library.Utils.OSUtil.toByteArray
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap




/**
 * Created by goldze on 2017/5/14.
 * 转换相关工具类
 */
class ConvertUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * outputStream转inputStream
     *
     * @param out 输出流
     * @return inputStream子类
     */
    fun output2InputStream(out: OutputStream?): ByteArrayInputStream? {
        return if (out == null) null else ByteArrayInputStream((out as ByteArrayOutputStream).toByteArray())
    }

    companion object {

        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        /**
         * byteArr转hexString
         *
         * 例如：
         * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
         *
         * @param bytes 字节数组
         * @return 16进制大写字符串
         */
        fun bytesToHexString(src: ByteArray?): String? {
            val stringBuilder = StringBuilder("")
            if (src == null || src.isEmpty()) {
                return null
            }
            for (i in 0 until src.size) {
                val v = src[i].toInt() and 0xFF
                val hv = Integer.toHexString(v)
                if (hv.length < 2) {
                    stringBuilder.append(0)
                }
                stringBuilder.append(hv)
            }
            return stringBuilder.toString()
        }

        public fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray? {
            var output = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, output)
            if (needRecycle) {
                bmp.recycle()
            }
            var result = output.toByteArray()
            output.close()
            return result;
        }

        /**
         * hexString转byteArr
         *
         * 例如：
         * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
         *
         * @param hexString 十六进制字符串
         * @return 字节数组
         */
        fun hexStringToBytes(hexString: String?): ByteArray? {
            var hexString = hexString
            if (hexString == null || hexString == "") {
                return null
            }
            hexString = hexString.toUpperCase()
            val length = hexString.length / 2
            val hexChars = hexString.toCharArray()
            val d = ByteArray(length)
            for (i in 0 until length) {
                val pos = i * 2
                d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
            }
            return d
        }

        private fun charToByte(c: Char): Byte {

            return "0123456789ABCDEF".indexOf(c).toByte()
        }

        /**
         * hexChar转int
         *
         * @param hexChar hex单个字节
         * @return 0..15
         */
        private fun hex2Dec(hexChar: Char): Int {
            return if (hexChar >= '0' && hexChar <= '9') {
                hexChar - '0'
            } else if (hexChar >= 'A' && hexChar <= 'F') {
                hexChar - 'A' + 10
            } else {
                throw IllegalArgumentException()
            }
        }

        /**
         * charArr转byteArr
         *
         * @param chars 字符数组
         * @return 字节数组
         */
        fun chars2Bytes(chars: CharArray?): ByteArray? {
            if (chars == null || chars.size <= 0) return null
            val len = chars.size
            val bytes = ByteArray(len)
            for (i in 0 until len) {
                bytes[i] = chars[i].toByte()
            }
            return bytes
        }

        /**
         * byteArr转charArr
         *
         * @param bytes 字节数组
         * @return 字符数组
         */
        fun bytes2Chars(bytes: ByteArray?): CharArray? {
            if (bytes == null) return null
            val len = bytes.size
            if (len <= 0) return null
            val chars = CharArray(len)
            for (i in 0 until len) {
                chars[i] = (bytes[i] and 0xff.toByte()).toChar()
            }
            return chars
        }

        /**
         * 以unit为单位的内存大小转字节数
         *
         * @param memorySize 大小
         * @param unit 单位类型
         *
         *  * [MemoryConstants.BYTE]: 字节
         *  * [MemoryConstants.KB]  : 千字节
         *  * [MemoryConstants.MB]  : 兆
         *  * [MemoryConstants.GB]  : GB
         *
         * @return 字节数
         */
        fun memorySize2Byte(memorySize: Long, @MemoryConstants.Unit unit: Int): Long {
            return if (memorySize < 0) -1 else memorySize * unit
        }

        /**
         * 字节数转以unit为单位的内存大小
         *
         * @param byteNum 字节数
         * @param unit 单位类型
         *
         *  * [MemoryConstants.BYTE]: 字节
         *  * [MemoryConstants.KB]  : 千字节
         *  * [MemoryConstants.MB]  : 兆
         *  * [MemoryConstants.GB]  : GB
         *
         * @return 以unit为单位的size
         */
        fun byte2MemorySize(byteNum: Long, @MemoryConstants.Unit unit: Int): Double {
            return if (byteNum < 0) -1.0 else byteNum.toDouble() / unit
        }

        /**
         * 字节数转合适内存大小
         *
         * 保留3位小数
         *
         * @param byteNum 字节数
         * @return 合适内存大小
         */
        @SuppressLint("DefaultLocale")
        fun byte2FitMemorySize(byteNum: Long): String {
            return if (byteNum < 0) {
                "shouldn't be less than zero!"
            } else if (byteNum < MemoryConstants.KB) {
                String.format("%.3fB", byteNum.toDouble() + 0.0005)
            } else if (byteNum < MemoryConstants.MB) {
                String.format("%.3fKB", byteNum.toDouble() / MemoryConstants.KB + 0.0005)
            } else if (byteNum < MemoryConstants.GB) {
                String.format("%.3fMB", byteNum.toDouble() / MemoryConstants.MB + 0.0005)
            } else {
                String.format("%.3fGB", byteNum.toDouble() / MemoryConstants.GB + 0.0005)
            }
        }

        fun duration2String(duration: Int): String {
            var result = ""
            val i = duration / 1000
            val min = i / 60
            val sec = i % 60
            if (min > 9) {
                if (sec > 9) {
                    result = "$min:$sec"
                } else {
                    result = "$min:0$sec"
                }
            } else {
                if (sec > 9) {
                    result = "0$min$sec"
                } else {
                    result = "0$min:0$sec"
                }
            }
            return result
        }

        fun duration2Min(duration: Int): String {
            var result = ""
            val i = duration
            val min = i / 60
            val sec = i % 60
            if (min > 9) {
                if (sec > 9) {
                    result = "$min 分钟"
                } else {
                    result = "$min 分钟"
                }
            } else {
                if (sec > 9) {
                    result = "$min 分钟"
                } else {
                    result = "$min 分钟"
                }
            }
            if (min > 60) {
                result = (min / 60).toString() + "小时" + (min % 60) + "分钟"
            }
            return result
        }

        fun duration2Stringtype(duration: Int): String {
            var result = ""
            val i = duration
            val min = i / 60
            val sec = i % 60
            if (min > 9) {
                if (sec > 9) {
                    result = "$min \" $sec\'"
                } else {
                    result = "$min \" 0$sec\'"
                }
            } else {
                if (sec > 9) {
                    result = "$min \" $sec\'"
                } else {
                    result = "$min \" 0$sec\'"
                }
            }
            return result
        }

        fun formatTimeS(seconds: Long): String {
            var temp = 0;
            var sb = StringBuffer()
            if (seconds > 3600) {
                temp = ((seconds / 3600).toInt());
                sb.append(if ((seconds / 3600) < 10) "0$temp:" else "$temp:")
                temp = ((seconds % 3600 / 60).toInt())
                changeSeconds(seconds, temp, sb);
            } else {
                temp = ((seconds % 3600 / 60).toInt())
                changeSeconds(seconds, temp, sb)
            }
            return sb.toString()
        }

        fun changeSeconds(seconds: Long, temp: Int, sb: StringBuffer) {
            sb.append(if ((temp < 10)) "0$temp:" else "$temp:")
            var temp = ((seconds % 3600 % 60).toInt())
            sb.append(if ((temp < 10)) "0$temp" else "" + temp)
        }

        fun compressByQuality(src: Bitmap,
                              maxByteSize: Long,
                              recycle: Boolean): ByteArray {
            val baos = ByteArrayOutputStream()
            src.compress(CompressFormat.JPEG, 100, baos)
            val bytes: ByteArray
            if (baos.size() <= maxByteSize) {
                bytes = baos.toByteArray()
            } else {
                baos.reset()
                src.compress(CompressFormat.JPEG, 0, baos)
                if (baos.size() >= maxByteSize) {
                    bytes = baos.toByteArray()
                } else {
                    // find the best quality using binary search
                    var st = 0
                    var end = 100
                    var mid = 0
                    while (st < end) {
                        mid = (st + end) / 2
                        baos.reset()
                        src.compress(CompressFormat.JPEG, mid, baos)
                        val len = baos.size()
                        if (len.toLong() == maxByteSize) {
                            break
                        } else if (len > maxByteSize) {
                            end = mid - 1
                        } else {
                            st = mid + 1
                        }
                    }
                    if (end == mid - 1) {
                        baos.reset()
                        src.compress(CompressFormat.JPEG, st, baos)
                    }
                    bytes = baos.toByteArray()
                }
            }
            if (recycle && !src.isRecycled) src.recycle()
            return bytes
        }
        /**
         * 以unit为单位的时间长度转毫秒时间戳
         *
         * @param timeSpan 毫秒时间戳
         * @param unit 单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return 毫秒时间戳
         */
        fun timeSpan2Millis(timeSpan: Long, @TimeConstants.Unit unit: Int): Long {
            return timeSpan * unit
        }

        /**
         * 毫秒时间戳转以unit为单位的时间长度
         *
         * @param millis 毫秒时间戳
         * @param unit 单位类型
         *
         *  * [TimeConstants.MSEC]: 毫秒
         *  * [TimeConstants.SEC]: 秒
         *  * [TimeConstants.MIN]: 分
         *  * [TimeConstants.HOUR]: 小时
         *  * [TimeConstants.DAY]: 天
         *
         * @return 以unit为单位的时间长度
         */
        fun millis2TimeSpan(millis: Long, @TimeConstants.Unit unit: Int): Long {
            return millis / unit
        }

        /**
         * 毫秒时间戳转合适时间长度
         *
         * @param millis 毫秒时间戳
         *
         * 小于等于0，返回null
         * @param precision 精度
         *
         *  * precision = 0，返回null
         *  * precision = 1，返回天
         *  * precision = 2，返回天和小时
         *  * precision = 3，返回天、小时和分钟
         *  * precision = 4，返回天、小时、分钟和秒
         *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
         *
         * @return 合适时间长度
         */
        @SuppressLint("DefaultLocale")
        fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
            var millis = millis
            var precision = precision
            if (millis <= 0 || precision <= 0) return null
            val sb = StringBuilder()
            val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
            val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
            precision = Math.min(precision, 5)
            for (i in 0 until precision) {
                if (millis >= unitLen[i]) {
                    val mode = millis / unitLen[i]
                    millis -= mode * unitLen[i]
                    sb.append(mode).append(units[i])
                }
            }
            return sb.toString()
        }

        /**
         * bytes转bits
         *
         * @param bytes 字节数组
         * @return bits
         */
        fun bytes2Bits(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (aByte in bytes) {
                for (j in 7 downTo 0) {
                    sb.append(if (aByte.toInt() shr j and 0x01 == 0) '0' else '1')
                }
            }
            return sb.toString()
        }

        /**
         * bits转bytes
         *
         * @param bits 二进制
         * @return bytes
         */
        fun bits2Bytes(bits: String): ByteArray {
            var bits = bits
            val lenMod = bits.length % 8
            var byteLen = bits.length / 8
            // 不是8的倍数前面补0
            if (lenMod != 0) {
                for (i in lenMod..7) {
                    bits = "0$bits"
                }
                byteLen++
            }
            val bytes = ByteArray(byteLen)
            for (i in 0 until byteLen) {
                for (j in 0..7) {
                    bytes[i] = (bytes[i].toInt() shl 1).toByte()
                    bytes[i] = bytes[i] or (bits[i * 8 + j] - '0').toByte()
                }
            }
            return bytes
        }

        /**
         * inputStream转outputStream
         *
         * @param is 输入流
         * @return outputStream子类
         */
        fun input2OutputStream(`is`: InputStream?): ByteArrayOutputStream? {
            if (`is` == null) return null
            try {
                val os = ByteArrayOutputStream()
                val b = ByteArray(MemoryConstants.KB)
                var len: Int = 0
                `is`.use {
                    while (`is`.read().also { it -> len = it } != -1) {
                        os.write(b, 0, len)
                    }
                }
                return os
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(`is`)
            }
        }

        /**
         * inputStream转byteArr
         *
         * @param is 输入流
         * @return 字节数组
         */
        fun inputStream2Bytes(`is`: InputStream?): ByteArray? {
            return if (`is` == null) null else input2OutputStream(`is`)!!.toByteArray()
        }

        /**
         * byteArr转inputStream
         *
         * @param bytes 字节数组
         * @return 输入流
         */
        fun bytes2InputStream(bytes: ByteArray?): InputStream? {
            return if (bytes == null || bytes.isEmpty()) null else ByteArrayInputStream(bytes)
        }

        /**
         * outputStream转byteArr
         *
         * @param out 输出流
         * @return 字节数组
         */
        fun outputStream2Bytes(out: OutputStream?): ByteArray? {
            return if (out == null) null else (out as ByteArrayOutputStream).toByteArray()
        }

        /**
         * outputStream转byteArr
         *
         * @param bytes 字节数组
         * @return 字节数组
         */
        fun bytes2OutputStream(bytes: ByteArray?): OutputStream? {
            if (bytes == null || bytes.isEmpty()) return null
            var os: ByteArrayOutputStream? = null
            try {
                os = ByteArrayOutputStream()
                os.write(bytes)
                return os
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(os!!)
            }
        }

        /**
         * inputStream转string按编码
         *
         * @param is 输入流
         * @param charsetName 编码格式
         * @return 字符串
         */
        fun inputStream2String(`is`: InputStream?, charsetName: String): String? {
            if (`is` == null || isSpace(charsetName.toString())) return null
            try {
                return String(inputStream2Bytes(`is`)!!, charset(charsetName))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            }
        }

        /**
         * string转inputStream按编码
         *
         * @param string 字符串
         * @param charsetName 编码格式
         * @return 输入流
         */
        fun string2InputStream(string: String?, charsetName: String): InputStream? {
            if (string == null || isSpace(charsetName)) return null
            try {
                return ByteArrayInputStream(string.toByteArray(charset(charsetName)))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * outputStream转string按编码
         *
         * @param out 输出流
         * @param charsetName 编码格式
         * @return 字符串
         */
        fun outputStream2String(out: OutputStream?, charsetName: String): String? {
            if (out == null || isSpace(charsetName)) return null
            try {
                return String(outputStream2Bytes(out)!!, charset(charsetName))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            }
        }

        /**
         * string转outputStream按编码
         *
         * @param string 字符串
         * @param charsetName 编码格式
         * @return 输入流
         */
        fun string2OutputStream(string: String?, charsetName: String): OutputStream? {
            if (string == null || isSpace(charsetName)) return null
            try {
                return bytes2OutputStream(string.toByteArray(charset(charsetName)))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            }
        }

        /**
         * bitmap转byteArr
         *
         * @param bitmap bitmap对象
         * @param format 格式
         * @return 字节数组
         */
        fun bitmap2Bytes(bitmap: Bitmap?, format: Bitmap.CompressFormat): ByteArray? {
            if (bitmap == null) return null
            val baos = ByteArrayOutputStream()
            bitmap.compress(format, 100, baos)
            return baos.toByteArray()
        }

        /**
         * byteArr转bitmap
         *
         * @param bytes 字节数组
         * @return bitmap
         */
        fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
            return if (bytes == null || bytes.size == 0) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * drawable转bitmap
         *
         * @param drawable drawable对象
         * @return bitmap
         */
        fun drawable2Bitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap
            if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                bitmap = Bitmap.createBitmap(1, 1,
                        if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight,
                        if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * bitmap转drawable
         *
         * @param bitmap bitmap对象
         * @return drawable
         */
        fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
            return if (bitmap == null) null else BitmapDrawable(context.resources, bitmap)
        }

        /**
         * drawable转byteArr
         *
         * @param drawable drawable对象
         * @param format 格式
         * @return 字节数组
         */
        fun drawable2Bytes(drawable: Drawable?, format: Bitmap.CompressFormat): ByteArray? {
            return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
        }

        /**
         * byteArr转drawable
         *
         * @param bytes 字节数组
         * @return drawable
         */
        fun bytes2Drawable(bytes: ByteArray?): Drawable? {
            return if (bytes == null) null else bitmap2Drawable(bytes2Bitmap(bytes))
        }

        /**
         * view转Bitmap
         *
         * @param view 视图
         * @return bitmap
         */
        fun view2Bitmap(view: View?): Bitmap? {
            if (view == null) return null
            val ret = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(ret)
            val bgDrawable = view.background
            if (bgDrawable != null) {
                bgDrawable.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            view.draw(canvas)
            return ret
        }

        /**
         * dp转px
         *
         * @param dpValue dp值
         * @return px值
         */
        fun dp2px(dpValue: Float): Int {
            val scale = getDisplayMetrics()!!.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * px转dp
         *
         * @param pxValue px值
         * @return dp值
         */
        fun px2dp(pxValue: Float): Int {
            val scale = getDisplayMetrics()!!.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * sp转px
         *
         * @param spValue sp值
         * @return px值
         */
        fun sp2px(spValue: Float): Int {
            val fontScale = getDisplayMetrics()!!.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * px转sp
         *
         * @param pxValue px值
         * @return sp值
         */
        fun px2sp(pxValue: Float): Int {
            val fontScale = getDisplayMetrics()!!.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * 判断字符串是否为null或全为空白字符
         *
         * @param s 待校验字符串
         * @return `true`: null或全空白字符<br></br> `false`: 不为null且不全空白字符
         */
        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}
