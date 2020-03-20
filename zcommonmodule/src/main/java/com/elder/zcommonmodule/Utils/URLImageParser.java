package com.elder.zcommonmodule.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.utils.Consts;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class URLImageParser implements Html.ImageGetter {
    TextView mTextView;

    public URLImageParser(TextView textView) {
        this.mTextView = textView;
    }

    @Override
    public Drawable getDrawable(final String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        Observable.just(source).map(new Function<String, File>() {
            @Override
            public File apply(String s) throws Exception {
                Log.e("result", "URLImageParse图片路径" + source);

                FutureTarget<File> file = Glide.with(mTextView).load(source).downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                return file.get();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File files) throws Exception {
                Bitmap loadedImage = BitmapFactory.decodeFile(files.getPath());
                urlDrawable.bitmap = loadedImage;
                urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
                mTextView.invalidate();
                mTextView.setText(mTextView.getText()); // 解决图文重叠
            }
        });


//        Glide.with(mTextView.getContext()).asBitmap().listener(new RequestListener<Bitmap>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap loadedImage, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

//                return false;
//            }
//        }).load(source);
//        ImageLoader.getInstance().loadImage(Consts.BASE_URL + source, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//
//            }
//        });
        return urlDrawable;
    }
}
