package com.elder.logrecodemodule;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.elder.logrecodemodule.Inteface.RankingClickListener;
import com.elder.zcommonmodule.Entity.HotData;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.zk.library.Base.BaseApplication;
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Utils.ConvertUtils;
import org.cs.tec.library.binding.command.BindingCommand;

public class ViewAdapter {

    @BindingAdapter(value = "log_addcar_local")
    public static void setCar(ImageView img, String path) {
        CircleCrop crop = new CircleCrop();
        if (path != null && !path.isEmpty()) {
            if (path.startsWith("/Activity")) {
                path = LocalUtilsKt.getImageUrl(path);
            }
        }
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(240F), ConvertUtils.Companion.dp2px(160F));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }


    @BindingAdapter(value = "localAvatar")
    public static void setlocalAvatar(ImageView img, String path) {
        CircleCrop crop = new CircleCrop();
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }


    @BindingAdapter("LoadLogRoadImg")
    public static void LoadLogRoadImg(ImageView img, String path) {
        String s = LocalUtilsKt.getRoadImgUrl(path);
        RoundedCorners crop = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(230), ConvertUtils.Companion.dp2px(120));
        Glide.with(img).asBitmap().load(s).apply(options).into(img);
    }


    @BindingAdapter("LinearBgLoad")
    public static void LoadLinearBg(final ImageView relativeLayout, HotData url) {
        int width = url.getWidth();
        int height = url.getHeight();
        int realWidth = (BaseApplication.Companion.getInstance().getGetWidthPixels() - ConvertUtils.Companion.dp2px(10)) / 2;
        float scale = realWidth * 1F / width;
        String s = LocalUtilsKt.getRoadImgUrl(url.getBill());
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).timeout(3000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        Glide.with(relativeLayout.getContext()).asBitmap().load(s).apply(options).into(relativeLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        params.height = (int) (height * scale);
        relativeLayout.setLayoutParams(params);
    }


    @BindingAdapter("LoadLogImgUrl")
    public static void LoadLogImgUrl(ImageView img, String path) {
        String s = LocalUtilsKt.getRoadImgUrl(path);
        RoundedCorners crop = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.log_hor_ex_icon).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(264), ConvertUtils.Companion.dp2px(120));
        Glide.with(img).asBitmap().load(s).apply(options).into(img);
    }


    @BindingAdapter(value = "LoadTextType")
    public static void setType(TextView tv, boolean flag) {

    }


    @BindingAdapter("setBackgroundByType")
    public static void setBackgroundByType(LinearLayout tv, int flag) {
        if (flag == 0) {
            tv.setBackgroundResource(R.drawable.corner_dialog);
        } else if (flag == 1) {
            tv.setBackgroundResource(R.drawable.log_add_team);
        } else if (flag == 2) {
            tv.setBackgroundResource(R.drawable.log_addroad);
        } else if (flag == 3) {
            tv.setBackgroundResource(R.drawable.log_add_ticket);
        } else if (flag == 4) {
            tv.setBackgroundResource(R.drawable.log_add_location);
        }
//        tv.setElevation(ConvertUtils.Companion.dp2px(5));
    }


    @BindingAdapter("setNumberImage")
    public static void setNumberImage(TextView tv, int position) {
        if (position == 0) {
            tv.setCompoundDrawablesWithIntrinsicBounds(tv.getContext().getResources().getDrawable(R.drawable.number_one),
                    null, null, null);
            tv.setText("");
        } else if (position == 1) {
            tv.setCompoundDrawablesWithIntrinsicBounds(tv.getContext().getResources().getDrawable(R.drawable.number_two),
                    null, null, null);
            tv.setText("");
        } else if (position == 2) {
            tv.setCompoundDrawablesWithIntrinsicBounds(tv.getContext().getResources().getDrawable(R.drawable.number_three),
                    null, null, null);
            tv.setText("");
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(null,
                    null, null, null);
            tv.setText("" + (position + 1));
        }
    }

    @BindingAdapter("DistanceInit")
    public static void DistanceInit(TextView view, String listener) {
        if (listener == null || listener.isEmpty()) {
            view.setText("0");
            return;
        }
        Double d = Double.valueOf(listener);
        view.setText(((d.intValue() / 1000)) + "");
    }
}
