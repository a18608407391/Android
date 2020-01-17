package org.cs.tec.library.Utils.compression

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.text.TextUtils
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers


class Luban constructor(private val mCacheDir: File) {

    private var compressListener: OnCompressListener? = null
    private var mFile: String? = null
    private var mListFile: List<String> = ArrayList()
    private var gear = THIRD_GEAR
    private var filename: String? = null

    fun launch(): Luban {
        Preconditions.checkNotNull<Any>(mFile, "the image file cannot be null, please call .load() before this method!")

        if (compressListener != null) compressListener!!.onStart()
        if (gear == Luban.FIRST_GEAR)
            Observable.just(mFile!!)
                    .map { s ->
                        val file = File(s)
                        firstCompress(file)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { throwable -> if (compressListener != null) compressListener!!.onError(throwable) }
                    .onErrorResumeNext(Observable.empty())
                    .filter {
                        it != null
                    }
                    .subscribe { file -> if (compressListener != null) compressListener!!.onSuccess(file!!) }
        else if (gear == Luban.THIRD_GEAR)
            Observable.just(mFile!!)
                    .map { s ->
                        val file = File(s)
                        thirdCompress(file)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { throwable -> if (compressListener != null) compressListener!!.onError(throwable) }
                    .onErrorResumeNext(Observable.empty())
                    .filter {
                        it != null
                    }
                    .subscribe { file -> if (compressListener != null) compressListener!!.onSuccess(file!!) }
        return this
    }


    fun load(file: String): Luban {
        mFile = file
        return this
    }

    fun load(listFile: List<String>): Luban {
        mListFile = listFile
        return this
    }

    fun setCompressListener(listener: OnCompressListener): Luban {
        compressListener = listener
        return this
    }

    fun putGear(gear: Int): Luban {
        this.gear = gear
        return this
    }


    @Deprecated("")
    fun setFilename(filename: String): Luban {
        this.filename = filename
        return this
    }

    fun asObservable(): Observable<File?>? {
        return if (gear == FIRST_GEAR)
            Observable.just(mFile!!).map { s ->
                if (TextUtils.isEmpty(s) || s.contains("http")) {
                    null
                } else {
                    val file = File(s)
                    if (file.exists()) {
                        firstCompress(file)
                    } else {
                        null
                    }
                }
            }
        else if (gear == THIRD_GEAR)
            Observable.just(mFile!!).map { s ->
                if (TextUtils.isEmpty(s) || s.contains("http")) {
                    null
                } else {
                    val file = File(s)
                    if (file.exists()) {
                        thirdCompress(file)
                    } else {
                        null
                    }
                }
            }
        else
            Observable.empty()
    }

    fun asListObservable(): Observable<File?>? {
        return if (gear == FIRST_GEAR)
            Observable.fromIterable(mListFile).map { s ->
                if (TextUtils.isEmpty(s)) {
                    null
                } else {
                    val file = File(s)
                    if (file.exists()) {
                        firstCompress(file)
                    } else {
                        null
                    }
                }
            }
        else if (gear == THIRD_GEAR)
            Observable.fromIterable(mListFile).map { s ->
                if (TextUtils.isEmpty(s)) {
                    null
                } else {
                    val file = File(s)
                    if (file.exists()) {
                        thirdCompress(file)
                    } else {
                        null
                    }
                }
            }
        else
            Observable.empty()
    }

    private fun thirdCompress(file: File): File? {
        val thumb = mCacheDir.absolutePath + File.separator +
                (if (TextUtils.isEmpty(filename)) System.currentTimeMillis() else filename) + ".jpg"

        var size: Double
        val filePath = file.absolutePath

        val angle = getImageSpinAngle(filePath)
        var width = getImageSize(filePath)[0]
        var height = getImageSize(filePath)[1]
        var thumbW = if (width % 2 == 1) width + 1 else width
        var thumbH = if (height % 2 == 1) height + 1 else height

        width = if (thumbW > thumbH) thumbH else thumbW
        height = if (thumbW > thumbH) thumbW else thumbH

        val scale = width.toDouble() / height

        if (scale <= 1 && scale > 0.5625) {
            if (height < 1664) {
                if (file.length() / 1024 < 150) return file

                size = width * height / Math.pow(1664.0, 2.0) * 150
                size = if (size < 60) 60.0 else size
            } else if (height in 1664..4989) {
                thumbW = width / 2
                thumbH = height / 2
                size = thumbW * thumbH / Math.pow(2495.0, 2.0) * 300
                size = if (size < 60) 60.0 else size
            } else if (height in 4990..10239) {
                thumbW = width / 4
                thumbH = height / 4
                size = thumbW * thumbH / Math.pow(2560.0, 2.0) * 300
                size = if (size < 100) 100.0 else size
            } else {
                val multiple = if (height / 1280 == 0) 1 else height / 1280
                thumbW = width / multiple
                thumbH = height / multiple
                size = thumbW * thumbH / Math.pow(2560.0, 2.0) * 300
                size = if (size < 100) 100.0 else size
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (height < 1280 && file.length() / 1024 < 200) return file

            val multiple = if (height / 1280 == 0) 1 else height / 1280
            thumbW = width / multiple
            thumbH = height / multiple
            size = thumbW * thumbH / (1440.0 * 2560.0) * 400
            size = if (size < 100) 100.0 else size
        } else {
            val multiple = Math.ceil(height / (1280.0 / scale)).toInt()
            thumbW = width / multiple
            thumbH = height / multiple
            size = thumbW * thumbH / (1280.0 * (1280 / scale)) * 500
            size = if (size < 100) 100.0 else size
        }

        return compress(filePath, thumb, thumbW, thumbH, angle, size.toLong())
    }

    private fun firstCompress(file: File): File? {
        val minSize = 60
        val longSide = 720
        val shortSide = 1280

        val filePath = file.absolutePath
        val thumbFilePath = mCacheDir.absolutePath + File.separator +
                (if (TextUtils.isEmpty(filename)) System.currentTimeMillis() else filename) + ".jpg"

        var size: Long = 0
        val maxSize = file.length() / 5

        val angle = getImageSpinAngle(filePath)
        val imgSize = getImageSize(filePath)
        var width = 0
        var height = 0
        if (imgSize[0] <= imgSize[1]) {
            val scale = imgSize[0].toDouble() / imgSize[1].toDouble()
            if (scale <= 1.0 && scale > 0.5625) {
                width = if (imgSize[0] > shortSide) shortSide else imgSize[0]
                height = width * imgSize[1] / imgSize[0]
                size = minSize.toLong()
            } else if (scale <= 0.5625) {
                height = if (imgSize[1] > longSide) longSide else imgSize[1]
                width = height * imgSize[0] / imgSize[1]
                size = maxSize
            }
        } else {
            val scale = imgSize[1].toDouble() / imgSize[0].toDouble()
            if (scale <= 1.0 && scale > 0.5625) {
                height = if (imgSize[1] > shortSide) shortSide else imgSize[1]
                width = height * imgSize[0] / imgSize[1]
                size = minSize.toLong()
            } else if (scale <= 0.5625) {
                width = if (imgSize[0] > longSide) longSide else imgSize[0]
                height = width * imgSize[1] / imgSize[0]
                size = maxSize
            }
        }

        return compress(filePath, thumbFilePath, width, height, angle, size)
    }

    /**
     * obtain the image's width and height
     *
     * @param imagePath the path of image
     */
    fun getImageSize(imagePath: String): IntArray {
        val res = IntArray(2)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 1
        BitmapFactory.decodeFile(imagePath, options)

        res[0] = options.outWidth
        res[1] = options.outHeight

        return res
    }

    /**
     * obtain the thumbnail that specify the size
     *
     * @param imagePath the target image path
     * @param width     the width of thumbnail
     * @param height    the height of thumbnail
     * @return [Bitmap]
     */
    private fun compress(imagePath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        val outH = options.outHeight
        val outW = options.outWidth
        var inSampleSize = 1

        if (outH > height || outW > width) {
            val halfH = outH / 2
            val halfW = outW / 2

            while (halfH / inSampleSize > height && halfW / inSampleSize > width) {
                inSampleSize *= 2
            }
        }

        options.inSampleSize = inSampleSize

        options.inJustDecodeBounds = false

        val heightRatio = Math.ceil((options.outHeight / height.toFloat()).toDouble()).toInt()
        val widthRatio = Math.ceil((options.outWidth / width.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio
            } else {
                options.inSampleSize = widthRatio
            }
        }
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(imagePath, options)
    }

    /**
     * obtain the image rotation angle
     *
     * @param path path of target image
     */
    private fun getImageSpinAngle(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /**
     * 指定参数压缩图片
     * create the thumbnail with the true rotate angle
     *
     * @param largeImagePath the big image path
     * @param thumbFilePath  the thumbnail path
     * @param width          width of thumbnail
     * @param height         height of thumbnail
     * @param angle          rotation angle of thumbnail
     * @param size           the file size of image
     */
    private fun compress(largeImagePath: String, thumbFilePath: String, width: Int, height: Int, angle: Int, size: Long): File? {
        var thbBitmap = compress(largeImagePath, width, height)

        thbBitmap = rotatingImage(angle, thbBitmap)

        return saveImage(thumbFilePath, thbBitmap, size)
    }

    /**
     * 保存图片到指定路径
     * Save image with specified size
     *
     * @param filePath the image file save path 储存路径
     * @param bitmap   the image what be save   目标图片
     * @param size     the file size of image   期望大小
     */
    private fun saveImage(filePath: String, bitmap: Bitmap, size: Long): File? {
        Preconditions.checkNotNull<Any>(bitmap, TAG + "bitmap cannot be null")

        val result = File(filePath.substring(0, filePath.lastIndexOf("/")))

        if (!result.exists() && !result.mkdirs()) return null

        val stream = ByteArrayOutputStream()
        var options = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream)

        while (stream.toByteArray().size / 1024 > size && options > 6) {
            stream.reset()
            options -= 6
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream)
        }
        try {
            val fos = FileOutputStream(filePath)
            fos.write(stream.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return File(filePath)
    }

    companion object {

        const val FIRST_GEAR = 1
        const val THIRD_GEAR = 3

        val TAG = "smartcity"
        const val DEFAULT_DISK_CACHE_DIR = "smartcity_disk_cache"

        @Volatile
        private var INSTANCE: Luban? = null

        /**
         * Returns a directory with a default name in the private cache directory of the application to
         * use to store
         * retrieved media and thumbnails.
         *
         * @param context A context.
         * @see .getPhotoCacheDir
         */
        @Synchronized
        private fun getPhotoCacheDir(context: Context): File? {
            return getPhotoCacheDir(context, Luban.DEFAULT_DISK_CACHE_DIR)
        }

        /**
         * Returns a directory with the given name in the private cache directory of the application to
         * use to store
         * retrieved media and thumbnails.
         *
         * @param context   A context.
         * @param cacheName The name of the subdirectory in which to store the cache.
         * @see .getPhotoCacheDir
         */
        private fun getPhotoCacheDir(context: Context, cacheName: String): File? {
            val cacheDir = context.cacheDir
            //File cacheDir = ImageUtils.checkTargetCacheDir(FileConstant.IMAGE_COMPRESS);
            if (cacheDir != null) {
                val result = File(cacheDir, cacheName)
                if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) {
                    // File wasn't able to create a directory, or the result exists but not a directory
                    return null
                }

                val noMedia = File(cacheDir.toString() + "/.nomedia")
                return if (!noMedia.mkdirs() && (!noMedia.exists() || !noMedia.isDirectory)) {
                    null
                } else result

            }
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "default disk cache dir is null")
            }
            return null
        }

        operator fun get(context: Context): Luban? {
            if (INSTANCE == null) INSTANCE = Luban(Luban.getPhotoCacheDir(context)!!)
            return INSTANCE
        }

        /**
         * 旋转图片
         * rotate the image with specified angle
         *
         * @param angle  the angle will be rotating 旋转的角度
         * @param bitmap target image               目标图片
         */
        private fun rotatingImage(angle: Int, bitmap: Bitmap): Bitmap {
            //rotate image
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())

            //create a new image
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }
}