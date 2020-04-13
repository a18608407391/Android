package com.elder.logrecodemodule;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.elder.logrecodemodule.Entity.ActivityPartyEntity;
import com.elder.logrecodemodule.Inteface.RankingClickListener;
import com.elder.zcommonmodule.Entity.HotData;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.google.gson.Gson;
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
        if (tv.getTag() == null) {
            tv.setTag(position);
        } else {
            if ((int) tv.getTag() != position) {
                tv.setTag(position);
            }
        }
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

    @BindingAdapter("LoadActivityPartyMBRoadImg")
    public static void LoadActivityPartyMBRoadImg(ImageView img, String url) {
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(5));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(167), ConvertUtils.Companion.dp2px(143));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }

    @BindingAdapter("LoadActivityPartyClockRoadImg")
    public static void LoadActivityPartyClockRoadImg(ImageView img, String url) {
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(154), ConvertUtils.Companion.dp2px(98));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }

    @BindingAdapter({"setActivityPartyLinearLayout", "setActivityPartyChildClick"})
    public static void setActivityPartyLinearLayout(LinearLayout layout,
                                                    final ActivityPartyRecommand datas,
                                                    final BindingCommand<ActivityPartyEntity.HotRecommend> command) {
        layout.removeAllViews();
        for (int i = 0; i < datas.getList().size(); i++) {
            Log.e("partyLayout", new Gson().toJson(datas.getList().get(i)));
            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_party_horizontal_recommand_child, layout, false);
            final int finalI = i;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (command != null) {
                        command.execute(datas.getList().get(finalI));
                    }
                }
            });
            binding.setVariable(BR.activityPartyHotRecommend, datas.getList().get(i));
            layout.addView(binding.getRoot());
        }
        layout.invalidate();
    }

    @BindingAdapter(value = "setActivityPartyAvatar")
    public static void setActivityPartyAvatar(ImageView img, String path) {
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

    @BindingAdapter("LoadActivityPartyRoadImg")
    public static void LoadActivityPartyRoadImg(ImageView img, String url) {
        Log.e("partyLayout", "url:"+url);
        String s = LocalUtilsKt.getRoadImgUrl(url);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(264), ConvertUtils.Companion.dp2px(168));
        Glide.with(img.getContext()).asBitmap().load(s).apply(options).into(img);
    }

}
