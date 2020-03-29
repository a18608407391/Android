package com.elder.zcommonmodule;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.zk.library.Utils.PreferenceUtils;

public class ViewAdapter {


    @BindingAdapter("android:imgUrlForPhoto")
    public static void setImageUri(final ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            String token = PreferenceUtils.getString(imageView.getContext(), ConfigKt.USER_TOKEN);
            String path = ConfigKt.Base_URL + "/AmoskiActivity/userCenterManager/getImg?imgUrl=" + url + "&appToken=" + token;
            RequestOptions options = new RequestOptions().error(com.zk.library.R.drawable.icon_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(300, 300);
            Glide.with(imageView.getContext()).asDrawable().load(path).apply(options).into(imageView);
        }
    }


    @BindingAdapter("setColorFilter")
    public static void setColorFilter(ImageView move, boolean flag) {
        if (flag) {
            move.setColorFilter(Color.WHITE);
        } else {
            move.setColorFilter(Color.BLACK);
        }
    }



}
