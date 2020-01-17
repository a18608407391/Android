package com.zk.library.binding.command.ViewAdapter.image;


import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.zk.library.R;

import java.lang.reflect.Field;


/**
 * Created by goldze on 2017/6/18.
 */
public final class ViewAdapter {
    @BindingAdapter("android:imgUrl")
    public static void setImageUri(final ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_album_default).error(R.drawable.ic_album_default).timeout(10000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(300, 300);
            Glide.with(imageView.getContext()).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    @BindingAdapter("android:imgUrlBig")
    public static void setImageUriBig(final ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            RequestOptions options = new RequestOptions().error(R.drawable.icon_default).timeout(10000).override(1080, 1920);
            Glide.with(imageView.getContext()).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    @BindingAdapter("android:imgUrlResource")
    public static void setImageUri(final ImageView imageView, int rsud) {
        if (imageView != null) {
            imageView.setImageResource(rsud);
        }
    }

    @BindingAdapter("android:musicimgUrl")
    public static void setMusicImageUri(final ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            RequestOptions options = new RequestOptions().placeholder(R.drawable.icon_default).error(R.drawable.img_minibar_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
            Glide.with(imageView.getContext()).asDrawable().load(url).apply(options).into(imageView);
        }
    }

    @BindingAdapter("android:setColorFilter")
    public static void setFilter(final ImageView imageView, boolean flag) {
        if (flag) {
            imageView.setColorFilter(Color.WHITE);
        } else {
            imageView.setColorFilter(Color.BLACK);
        }
    }

    @BindingAdapter("android:playGif")
    public static void playGif(final ImageView imageView, int i) {

        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//        Glide.with(imageView.getContext()).asGif().load(i).apply(options).into(imageView);

        Glide.with(imageView.getContext()).asGif().load(i).apply(options).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable gifDrawable, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                int duration = 0;
                Drawable.ConstantState state = gifDrawable.getConstantState();
                if (state != null) {
                    //不能混淆GifFrameLoader和GifState类
                    Object gifFrameLoader = null;
                    try {
                        gifFrameLoader = getValue(state, "frameLoader");
                        if (gifFrameLoader != null) {
                            Object decoder = getValue(gifFrameLoader, "gifDecoder");
                            if (decoder != null && decoder instanceof GifDecoder) {
                                for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
                                    duration += ((GifDecoder) decoder).getDelay(i);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("Glide4.0", "gif播放动画时长duration:" + duration);
                }
                return false;
            }
        }).into(imageView);
    }

    public static Object getValue(Object object, String fieldName) throws Exception {
        if (object == null) {
            return null;
        }
        if (TextUtils.isEmpty(fieldName)) {
            return null;
        }
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }
}

