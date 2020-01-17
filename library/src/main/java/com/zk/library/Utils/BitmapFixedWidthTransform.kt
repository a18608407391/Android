package com.zk.library.Utils

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


class BitmapFixedWidthTransform : BitmapTransformation {

    constructor()
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        var targetWidth = outWidth

        var aspectRatio = toTransform.height / toTransform.width
        var targetHeight = targetWidth * aspectRatio
        var result = Bitmap.createScaledBitmap(toTransform, targetWidth, targetHeight, true)
        if (result != toTransform) {
//            toTransform.recycle()
        }
        return result
    }

}