package com.cstec.administrator.social.ViewAdapter;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cstec.administrator.social.Adapter.GridRecycleViewAdapter;
import com.cstec.administrator.social.Entity.GridClickEntity;
import com.cstec.administrator.social.Entity.PhotoEntitiy;
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity;
import com.elder.zcommonmodule.Entity.DynamicsSimple;
import com.elder.zcommonmodule.Entity.SocialPhotoEntity;
import com.cstec.administrator.social.R;
import com.elder.zcommonmodule.ConfigKt;
import com.elder.zcommonmodule.Inteface.OnItemPictureClickListener;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.elder.zcommonmodule.NineGridSimpleLayout;
import com.elder.zcommonmodule.Widget.ExpandableTextView;
import com.elder.zcommonmodule.Widget.RichEditText.RichTextBuilder;
import com.elder.zcommonmodule.Widget.RichEditText.RichTextView;
import com.elder.zcommonmodule.Widget.RichEditText.listener.SpanAtUserCallBack;
import com.elder.zcommonmodule.Widget.RichEditText.listener.SpanTopicCallBack;
import com.elder.zcommonmodule.Widget.RichEditText.listener.SpanUrlCallBack;
import com.elder.zcommonmodule.Widget.RichEditText.model.TopicModel;
import com.elder.zcommonmodule.Widget.RichEditText.model.UserModel;
import com.jakewharton.rxbinding2.view.RxView;
import com.zk.library.Base.BaseApplication;
import com.zk.library.Utils.PreferenceUtils;

import org.cs.tec.library.Utils.ConvertUtils;
import org.cs.tec.library.binding.command.BindingCommand;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;

public class ViewAdapter {
    @BindingAdapter(value = {"setNineImageUrl"})
    public static void setNineImageUrl(NineGridSimpleLayout layout, ArrayList<String> url) {
        if (layout != null) {
            layout.setUrlList(url);
        }

    }

    @BindingAdapter(value = "localEntityImageLoad")
    public static void setLocalEntityImage(ImageView img, DynamicsCategoryEntity.Dynamics path) {
        String url = "";
//        if (path.getReleaseDynamicParent() != null) {
//            if (!path.getReleaseDynamicParent().getDynamicImageList().isEmpty()) {
//                SocialPhotoEntity photo = path.getReleaseDynamicParent().getDynamicImageList().get(0);
//                url = ConfigKt.Base_URL + photo.getProjectUrl() + photo.getFilePathUrl() + photo.getFilePath();
//            } else {
//                img.setVisibility(View.GONE);
//                return;
//            }
//        } else {
//            return;
//        }


        if (!path.getDynamicImageList().isEmpty()) {
            SocialPhotoEntity photo = path.getDynamicImageList().get(0);
            url = ConfigKt.Base_URL + photo.getProjectUrl() + photo.getFilePathUrl() + photo.getFilePath();
        }
        RequestOptions options = new RequestOptions().error(R.drawable.road_book_ex_icon).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(200, 200);
        Glide.with(img).asBitmap().load(url).apply(options).into(img);
    }

    @BindingAdapter(value = "socialImageLoad")
    public static void socialImageLoad(ImageView img, String path) {
        RequestOptions options = new RequestOptions().error(R.drawable.release_icon).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(60), ConvertUtils.Companion.dp2px(60));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter(value = "socialImageselectLoad")
    public static void socialImageselectLoad(ImageView img, String path) {
        RequestOptions options = new RequestOptions().error(R.drawable.picture_logo).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(60), ConvertUtils.Companion.dp2px(60));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter(value = "socialBGLoad")
    public static void socialBGLoad(ImageView img, String path) {
        String url = "";
//        POST /SameCity/getBackgroundImages

        String token = PreferenceUtils.getString(img.getContext(), ConfigKt.USER_TOKEN);
        if (path == null || path.startsWith("/storage")) {
            url = path;
        } else {
            url = ConfigKt.Base_URL + "AmoskiActivity/SameCity/getBackgroundImages?fileNameUrl=" + path + "&appToken=" + token;
        }
        Log.e("result", "封面图片" + url);
        int width = BaseApplication.Companion.getInstance().getGetWidthPixels();
        RequestOptions options = new RequestOptions().error(R.drawable.driver_home_top_bg).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(width / 2, ConvertUtils.Companion.dp2px(220) / 2);
        Glide.with(img).asBitmap().load(url).apply(options).into(img);
    }

    @BindingAdapter("socialAvatar")
    public static void setAvatar(ImageView img, String path) {
        if (path != null && !path.isEmpty()) {
            if (path.startsWith("/Activity")) {
                path = LocalUtilsKt.getImageUrl(path);
            }
        }
        CircleCrop crop = new CircleCrop();
        RequestOptions options = new RequestOptions().error(R.drawable.default_avatar).transform(crop).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(60), ConvertUtils.Companion.dp2px(60));
        Glide.with(img).asBitmap().load(path).apply(options).into(img);
    }

    @BindingAdapter("LoadPicture")
    public static void LoadPicture(ImageView img, String url) {
        int width = BaseApplication.Companion.getInstance().getGetWidthPixels();
        int realWidth = (width - (ConvertUtils.Companion.dp2px(10) * 2 + ConvertUtils.Companion.dp2px(16) * 2)) / 3;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img.getLayoutParams();
        params.width = realWidth;
        params.height = realWidth;
//        params.setMargins(ConvertUtils.Companion.dp2px(10), 0, ConvertUtils.Companion.dp2px(10), 0);
        img.setLayoutParams(params);
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
        RequestOptions options = new RequestOptions().transform(corners).error(R.drawable.release_add_pic).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(100), ConvertUtils.Companion.dp2px(100));
        Glide.with(img.getContext()).asBitmap().load(url).apply(options).into(img);
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

    @BindingAdapter("ParseTime")
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


    @BindingAdapter("ParseToTime")
    public static void ParseToTime(TextView tv, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long lo = sdf.parse(time).getTime();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            String k = sd.format(new Date(lo));
            tv.setText(k);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter(value = {"initGridImage", "initGridListener"})
    public static void initGridImage(NineGridSimpleLayout grid, ArrayList<SocialPhotoEntity> url, final BindingCommand<GridClickEntity> listener) {
        if (url == null) {
            grid.setUrlList(new ArrayList());
            return;
        }
        ArrayList<String> loadUrl = new ArrayList();
        for (int i = 0; i < url.size(); i++) {
            String t = ConfigKt.Base_URL + url.get(i).getProjectUrl() + url.get(i).getFilePathUrl() + url.get(i).getFilePath();
            loadUrl.add(t);
        }
        grid.setUrlList(loadUrl);

        if (listener != null) {
            grid.setListener(new OnItemPictureClickListener() {
                @Override
                public void onItemPictureClick(int itemPostion, int i, @NotNull String url, @NotNull List<String> urlList, @NotNull ImageView imageView) {
                    GridClickEntity entity = new GridClickEntity();
                    entity.setItemPosition(itemPostion);
                    entity.setChildPosition(i);
                    entity.setUrl(url);
                    entity.setUrlList(urlList);
                    entity.setImg(imageView);
                    listener.execute(entity);
                }
            });
        }
    }


    @BindingAdapter(value = {"initExpand"})
    public static void initExpand(ExpandableTextView text, String simple) {
        Log.e("result", "当前布局的高度" + text.getHeight() + "当前的text" + simple + text.getMeasuredHeight());

        if (simple == null) {
            Log.e("result", "当前流程" + "simplen=ull");
            if (text.getTag() != null) {
                Log.e("result", "当前流程" + "simplen=ull" + "tag!=null");
                text.setTag(null);
            } else {
                Log.e("result", "当前流程" + "simplen=ull" + "tag==null");
            }
            return;
        } else {
            if (text.getTag() == null) {
                text.setTag(simple);
            } else if (text.getTag() != null) {
                if (text.getTag() != simple) {
                    text.mStateTv.setCompoundDrawablePadding(0);
                    if (!text.isCollapsed()) {
                        text.mTv.setText("");
                        Log.e("result", "当前流程" + "simplen!=null" + "tag!=null" + "展开了" + text.mStateTv.getHeight());
                        text.setCollapsed(true);
                        text.change();
                        ViewGroup.LayoutParams params = text.getLayoutParams();
                        ViewGroup.LayoutParams tparams =text.mTv.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        tparams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        text.setLayoutParams(params);
                        text.mTv.setLayoutParams(tparams);
                    }
                }
            }
        }
        text.mTv.setText("");
        text.setText(simple);
        Log.e("result", "设置完文字以后的高度" + text.mTv.getHeight());
        if (text.mCollapsedHeight != -1) {
            text.mCollapsedHeight = text.mTv.getHeight();
        }
    }

    @BindingAdapter(value = {"initGrid1Image", "initGrid1Listener"})
    public static void initGrid1Image(NineGridSimpleLayout grid, ArrayList<SocialPhotoEntity> url, final BindingCommand<GridClickEntity> listener) {
//        if (grid.getTag() == null) {
//            if (url == null || url.isEmpty()) {
//                grid.setVisibility(View.GONE);
//            } else {
//                grid.setVisibility(View.VISIBLE);
//            }
        if (url == null) {
            grid.setUrlList(new ArrayList());
            return;
        }
        ArrayList<String> loadUrl = new ArrayList();
        for (int i = 0; i < url.size(); i++) {
            String t = ConfigKt.Base_URL + url.get(i).getProjectUrl() + url.get(i).getFilePathUrl() + url.get(i).getFilePath();
            loadUrl.add(t);
        }
        grid.setUrlList(loadUrl);
        if (listener != null) {
            grid.setListener(new OnItemPictureClickListener() {
                @Override
                public void onItemPictureClick(int itemPostion, int i, @NotNull String url, @NotNull List<String> urlList, @NotNull ImageView imageView) {
                    GridClickEntity entity = new GridClickEntity();
                    entity.setItemPosition(itemPostion);
                    entity.setChildPosition(i);
                    entity.setUrl(url);
                    entity.setUrlList(urlList);
                    entity.setImg(imageView);
                    listener.execute(entity);
                }
            });
        }
    }


    @BindingAdapter("initRecyclerView")
    public static void initRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        if (adapter == null) {
            return;
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }


    @BindingAdapter({"initRichText", "spanClick"})
    public static void initRichText(TextView text, final DynamicsSimple simple, final BindingCommand command) {
        if (simple == null) {
            if (text.getTag() != null) {
                text.setTag(null);
            } else {
            }
            text.setVisibility(View.GONE);
            return;
        } else {
            text.setVisibility(View.VISIBLE);
            if (text.getTag() == null) {
                text.setTag(simple);
            } else if (text.getTag() != null) {
                if (text.getTag() != simple) {
                    text.setTag(simple);
                }
            }
        }
        text.setMovementMethod(LinkMovementMethod.getInstance());
        String s = "@" + simple.getMemberName();
        SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(s + " " + simple.getPublishContent());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3FC5C9"));
        spannableStringBuilder1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                command.execute(simple);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String chinese = "[\u4e00-\u9fa5]";
        spannableStringBuilder1.setSpan(foregroundColorSpan, 0, s.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        text.setText(spannableStringBuilder1);
    }
//
//     String_length(value: String): Int {
//        var valueLength = 0
//
//        for (i in 0 until value.length) {
//            val temp = value.substring(i, i + 1)
//            if (temp.matches(chinese.toRegex())) {
//                valueLength += 2
//            } else {
//                valueLength += 1
//            }
//        }
//        return valueLength
//    }


}
