package com.cstec.administrator.chart_module;


import android.databinding.BindingAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elder.zcommonmodule.LocalUtilsKt;

import org.cs.tec.library.Utils.ConvertUtils;

public class ViewAdapter {


    @BindingAdapter(value = "setChatPicLoad")
    public static void setChatPicLoad(ImageView img, String path) {
        if (path.isEmpty() || path == null) {
            img.setVisibility(View.GONE);
            return;
        }

        img.setVisibility(View.VISIBLE);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
        RequestOptions options = new RequestOptions().error(R.drawable.ic_album_default).transform(corners).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(260), ConvertUtils.Companion.dp2px(139));
        Glide.with(img).asBitmap().load(LocalUtilsKt.getImageUrl(path)).apply(options).into(img);
    }
}
