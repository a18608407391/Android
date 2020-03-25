package com.elder.zcommonmodule.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.elder.zcommonmodule.ConfigKt;
import com.elder.zcommonmodule.R;
import com.zk.library.Base.BaseApplication;


import org.cs.tec.library.Base.Utils.UtilsKt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

    File files = new File(UtilsKt.getContext().getFilesDir().getAbsolutePath() + "/Amoski/" + "ex.jpg");

    @Override
    public Drawable getDrawable(final String source) {
//        Html.ImageGetter imgGetter = new Html.ImageGetter() {
//            @Override
//            public Drawable getDrawable(String source) {
//                URL url;
//                Drawable drawable = null;
//                try {
//                    url = new URL(source);
//                    drawable = Drawable.createFromStream(url.openStream(), "");
//
//                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                            drawable.getIntrinsicHeight());
//                    mTextView.invalidate();
//                    mTextView.setText(Html.fromHtml(mTextView.getText().toString())); // 解决图文重叠
//                    return drawable;
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        };
        final URLDrawable urlDrawable = new URLDrawable();
        String url = source;
        if (source.startsWith("/")) {
            url = ConfigKt.Base_URL.substring(0, ConfigKt.Base_URL.length() - 1) + source;
        }

        final String finalUrl = url;
        Observable.just(source).map(new Function<String, File>() {
            @Override
            public File apply(String s) throws Exception {
                FutureTarget<File> file = Glide.with(mTextView).load(finalUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                try {
                    return file.get();
                } catch (Exception exception) {
                    if (files.exists()) {
                        Log.e("result", "文件存在");
                        return files;
                    } else {
                        return drawableToFile(mTextView.getContext(), R.drawable.nomal_img, "ex.jpg");
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File files) throws Exception {
                Bitmap loadedImage = BitmapFactory.decodeFile(files.getPath());
                if (loadedImage != null) {
                    urlDrawable.bitmap = loadedImage;
                    int width = BaseApplication.Companion.getInstance().getGetWidthPixels();
                    float scale = ((float) width / loadedImage.getWidth());
                    int w = width;
                    int h = (int) (scale *loadedImage.getHeight());
                    urlDrawable.setBounds(0, 0, w, h);
                    mTextView.invalidate();
                    mTextView.setText(mTextView.getText()); // 解决图文重叠
                }
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

    public File drawableToFile(Context mContext, int drawableId, String fileName) {
//        InputStream is = view.getContext().getResources().openRawResource(R.drawable.logo);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
//        Bitmap bitmap = BitmapFactory.decodeStream(is);

        String defaultPath = mContext.getFilesDir()
                .getAbsolutePath() + "/Amoski/";
        File file = new File(defaultPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String defaultImgPath = defaultPath + "/" + fileName;
        file = new File(defaultImgPath);
        try {
            file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 20, fOut);
//            is.close();
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("result", "文件路径" + file.getPath());
        return file;
    }
}
