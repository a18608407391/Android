package com.example.private_module;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.elder.zcommonmodule.ConfigKt;
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity;
import com.elder.zcommonmodule.Entity.DynamicsSimple;
import com.elder.zcommonmodule.Entity.LikesEntity;
import com.elder.zcommonmodule.Entity.SocialPhotoEntity;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.elder.zcommonmodule.PictureInfo;
import com.example.private_module.Bean.PhotoEntitiy;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.zk.bfind.LinearLayout.OnHorizontalScrollerViewClickListener;
import com.zk.library.Base.BaseApplication;
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Utils.ConvertUtils;
import org.cs.tec.library.binding.command.BindingCommand;

import java.text.SimpleDateFormat;

import io.reactivex.functions.Consumer;

public class ViewAdapter {


    @BindingAdapter(value = "localImageLoad")
    public static void setLocalImage(ImageView img, String path) {
        Log.e("result", "当前URL" + path);
        RequestOptions options = new RequestOptions().error(R.drawable.picture_logo).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }


    @BindingAdapter(value = "localsocialImageLoad")
    public static void setLocalSocialImage(ImageView img, String path) {
        Log.e("result", "当前URL" + path);
        RequestOptions options = new RequestOptions().error(R.drawable.ic_album_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }


    @BindingAdapter(value = "setRstoreImage")
    public static void setRstoreImage(ImageView img, SocialPhotoEntity path) {
        if (path != null) {
            String url = ConfigKt.Base_URL + path.getProjectUrl() + path.getFilePathUrl() + path.getFilePath();
            RequestOptions options = new RequestOptions().error(R.drawable.ic_album_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
            Glide.with(img).asBitmap().load(url).apply(options).into(img);
        } else {
            RequestOptions options = new RequestOptions().error(R.drawable.ic_album_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
            Glide.with(img).asBitmap().load("").apply(options).into(img);
        }
    }

    @BindingAdapter(value = "localEntityImageLoad")
    public static void setLocalEntityImage(ImageView img, DynamicsCategoryEntity.Dynamics path) {
        String url = "";
        Log.e("result", "当前收藏数据" + new Gson().toJson(path));
        if (path != null && path.getDynamicImageList() != null && path.getDynamicImageList().size() != 0) {
            img.setVisibility(View.VISIBLE);
            SocialPhotoEntity photo = path.getDynamicImageList().get(0);
            url = ConfigKt.Base_URL + photo.getProjectUrl() + photo.getFilePathUrl() + photo.getFilePath();
        } else if (path != null && path.getParentDynamin() != null && path.getParentDynamin().getDynamicImageList().size() != 0) {
            img.setVisibility(View.VISIBLE);
            DynamicsSimple photo = path.getParentDynamin();
            SocialPhotoEntity eb = photo.getDynamicImageList().get(0);
            url = ConfigKt.Base_URL + eb.getProjectUrl() + eb.getFilePathUrl() + eb.getFilePath();
        } else {
            img.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions().error(R.drawable.road_book_ex_icon).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
        Glide.with(img).asBitmap().load(url).apply(options).into(img);
    }

    @BindingAdapter(value = "HttpImageLoad")
    public static void setHttpImage(ImageView img, String path) {
        RequestOptions options = new RequestOptions().error(R.drawable.add_watermark_ex).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true);
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter(value = "HttpImageLoadLayout")
    public static void setHttpImageLayout(final RelativeLayout img, String path) {
        RequestOptions options = new RequestOptions().error(R.drawable.default_avatar).timeout(3000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        Glide.with(img.getContext()).asBitmap().load(path).apply(options).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
                int w = UtilsKt.getDisplayMetrics().widthPixels;
                double s = 1.01;
                if (Double.parseDouble(w + "") / resource.getWidth() > 1) {
                    s = Double.parseDouble(w + "") / resource.getWidth() * 0.8;
                } else if (Double.parseDouble(w + "") == resource.getWidth()) {
                    s = 0.70;
                } else if (Double.parseDouble(w + "") / resource.getWidth() < 1) {
                    if (Double.parseDouble(w + "") / resource.getWidth() > 0.75 && Double.parseDouble(w + "") / resource.getWidth() < 1) {
                        s = 0.70;
                    } else {
                        s = Double.parseDouble(w + "") / resource.getWidth() * 0.8;
                    }
                }
                params.width = (int) (resource.getWidth() * s);
                params.height = (int) (resource.getHeight() * s);
                img.setLayoutParams(params);
                img.setBackground(new BitmapDrawable(resource));
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                super.onLoadCleared(placeholder);
            }
        });
    }

    @BindingAdapter(value = "makerLoad")
    public static void setmakerLoad(ImageView img, String path) {
        if (path.isEmpty() || path == null) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            Glide.with(img).asBitmap().load(path).apply(options).into(img);
        }
    }


    @BindingAdapter(value = "addcar")
    public static void setCarLocal(ImageView img, String path) {

        if (path != null && !path.isEmpty()) {
            if (path.startsWith("/Activity")) {
                path = LocalUtilsKt.getImageUrl(path);
            } else if (path.startsWith("/home")) {
                path = LocalUtilsKt.getImageUrl(path);
            }
        }
        RequestOptions options = new RequestOptions().error(R.drawable.bond_car_camera).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(240F), ConvertUtils.Companion.dp2px(160F));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter(value = "addcar_local")
    public static void setCar(ImageView img, String path) {
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

    @BindingAdapter(value = "localAvatar")
    public static void setlocalAvatar(ImageView img, String path) {
        CircleCrop crop = new CircleCrop();
        RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter(value = "BiglocalImageLoad")
    public static void setBigLocalImage(ImageView img, String path) {
        if (path == null) {
        } else {
        }
        RequestOptions options = new RequestOptions().error(R.drawable.picture_logo).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(BaseApplication.Companion.getInstance().getScreenWidths(), BaseApplication.Companion.getInstance().getScreenHights());
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }


    public static void addView(LinearLayout layout, final String[] entity, final OnHorizontalScrollerViewClickListener listener) {
//        for (int i = 0; i < entity.length; i++) {
//            final int position = i;
//            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View view = inflater.inflate(R.layout.warter_mark_type_layout, layout, false);
//            ImageView img = view.findViewById(R.id.sdv_img_course);
//            RequestOptions options = new RequestOptions().error(R.drawable.picture_logo).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
//            Glide.with(img.getContext()).asDrawable().apply(options).load(entity[i]).into(img);
//            final int finalI = i;
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    HoriontalScrollerViewBean bean = new HoriontalScrollerViewBean();
//                    bean.position = finalI;
//                    bean.path = entity[finalI];
//                    listener.onItemClick(bean);
//                }
//            });
//            layout.addView(view);
//            layout.invalidate();
//        }
    }


    @BindingAdapter(value = {"onImageLongClick", "onImageDatas"})
    public static void setOnLongClick(View view, final BindingCommand command, final PhotoEntitiy entitiy) {
        RxView.longClicks(view).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (command != null) {
                    command.execute(entitiy);
                }
            }
        });
    }


    @BindingAdapter(value = {"onCheckedPhotoCommand", "checkitem"}, requireAll = false)
    public static void setCheckedChange(final CheckBox checkBox, final BindingCommand<PictureInfo> bindingCommand, final PictureInfo info) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                info.isCheced().set(isChecked);
                bindingCommand.execute(info);
            }
        });
    }

    @BindingAdapter("PrivateParseTime")
    public static void ParseTime(TextView tv, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long lo = sdf.parse(time).getTime();
            Long k = (System.currentTimeMillis() - lo) / 1000;
            if (k < 60) {
                tv.setText("刚刚");
            } else if (k < 3600) {
                tv.setText((k / 60) + "分钟前");
            } else if (k < 3600 * 24) {
                tv.setText((k / 3600) + "小时前");
            } else if (k > 3600 * 24) {
                tv.setText((k / 3600 / 24) + "天前");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"Logrender", "LogwebCommand", "LoadingFinish"})
    public static void loadHtml(WebView webView, final String html, final BindingCommand<String> command, final BindingCommand<String> finish) {
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);//允许js调用
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSetting.setAllowFileAccess(true);//在File域下，能够执行任意的JavaScript代码，同源策略跨域访问能够对私有目录文件进行访问等
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//控制页面的布局(使所有列的宽度不超过屏幕宽度)
        webSetting.setSupportZoom(true);//支持页面缩放
        webSetting.setBuiltInZoomControls(true);//进行控制缩放
        webSetting.setAllowContentAccess(true);//是否允许在WebView中访问内容URL（Content Url），默认允许
        webSetting.setUseWideViewPort(true);//设置缩放密度
        webSetting.setSupportMultipleWindows(false);//设置WebView是否支持多窗口,如果为true需要实现onCreateWindow(WebView, boolean, boolean, Message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //两者都可以
            webSetting.setMixedContentMode(webSetting.getMixedContentMode());//设置安全的来源
        }
        webSetting.setAppCacheEnabled(true);//设置应用缓存
        webSetting.setDomStorageEnabled(true);//DOM存储API是否可用
        webSetting.setGeolocationEnabled(true);//定位是否可用
        webSetting.setLoadWithOverviewMode(true);//是否允许WebView度超出以概览的方式载入页面，
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);//设置应用缓存内容的最大值
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);//设置是否支持插件
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);//重写使用缓存的方式

        webSetting.setAllowUniversalAccessFromFileURLs(true);//是否允许运行在一个file schema URL环境下的JavaScript访问来自其他任何来源的内容
        webSetting.setAllowFileAccessFromFileURLs(true);//是否允许运行在一个URL环境
        webView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                if (command != null) {
                    command.execute(s);
                }
                return true;
            }

            @Override
            public void onReceivedClientCertRequest(WebView webView, ClientCertRequest clientCertRequest) {
                super.onReceivedClientCertRequest(webView, clientCertRequest);
            }

            @Override
            public void onReceivedLoginRequest(WebView webView, String s, String s1, String s2) {
                super.onReceivedLoginRequest(webView, s, s1, s2);
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView webView, HttpAuthHandler httpAuthHandler, String s, String s1) {
                super.onReceivedHttpAuthRequest(webView, httpAuthHandler, s, s1);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (finish != null
                        ) {
                    finish.execute(s);
                }

            }
        });

        webView.loadUrl(html);

//步骤3. 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
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
}
