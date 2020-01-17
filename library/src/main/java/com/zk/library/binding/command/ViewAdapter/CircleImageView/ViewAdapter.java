package com.zk.library.binding.command.ViewAdapter.CircleImageView;


import android.databinding.BindingAdapter;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.zk.library.R;


import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by goldze on 2017/6/18.
 */
public final class ViewAdapter {
    @BindingAdapter("android:imgUrlCircle")
    public static void setImageUri(final CircleImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            RequestOptions options =new RequestOptions().placeholder(R.drawable.icon_default).error(R.drawable.icon_default).diskCacheStrategy(DiskCacheStrategy.ALL).override(50,50);
            Glide.with(imageView.getContext()).load(url).apply(options).into(imageView);
        }
    }
}

