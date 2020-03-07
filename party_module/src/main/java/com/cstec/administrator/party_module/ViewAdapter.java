package com.cstec.administrator.party_module;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.cs.tec.library.Utils.ConvertUtils;
import org.cs.tec.library.binding.command.BindingCommand;

import java.util.ArrayList;

public class ViewAdapter {


    @BindingAdapter({"setPartyLinearLayout", "setPartyChildClick"})
    public static void setPartyLinearLayout(LinearLayout layout, final PartyHotRecommand datas, final BindingCommand<PartyHomeEntity.HotRecommend> command) {
        layout.removeAllViews();
        for (int i = 0; i < datas.getList().size(); i++) {
            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.horizontal_hot_recommand_child, layout, false);
            final int finalI = i;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (command != null) {
                        command.execute(datas.getList().get(finalI));
                    }
                }
            });
            binding.setVariable(BR.hot_recommand, datas.getList().get(i));
            layout.addView(binding.getRoot());
        }
        layout.invalidate();
    }


    @BindingAdapter("LoadRoadImg")
    public static void LoadRoadImg(ImageView img, String url) {
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(264), ConvertUtils.Companion.dp2px(168));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }

    @BindingAdapter("LoadClockRoadImg")
    public static void LoadClockRoadImg(ImageView img, String url) {
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(154), ConvertUtils.Companion.dp2px(98));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }

    @BindingAdapter("LoadMBRoadImg")
    public static void LoadMBRoadImg(ImageView img, String url) {
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(167), ConvertUtils.Companion.dp2px(143));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }


    @BindingAdapter(value = "setPartyAvatar")
    public static void setPartyAvatar(ImageView img, String path) {
        CircleCrop crop = new CircleCrop();
        if (path != null && !path.isEmpty()) {
            if (path.startsWith("/Activity")) {
                path = LocalUtilsKt.getImageUrl(path);
            } else if (path.startsWith("/home")) {
                path = LocalUtilsKt.getImageUrl(path);
            }
        }
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(240F), ConvertUtils.Companion.dp2px(160F));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }
}
