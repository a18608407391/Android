package com.cstec.administrator.party_module;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;
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
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel;
import com.elder.zcommonmodule.ConfigKt;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.elder.zcommonmodule.Utils.URLImageParser;
import com.elder.zcommonmodule.Widget.ExpandableTextView;
import com.zk.library.Base.BaseApplication;
import com.zk.library.Utils.PreferenceUtils;
import com.zk.library.Utils.TextView.HtmlSpanner;


import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Utils.ConvertUtils;
import org.cs.tec.library.binding.command.BindingCommand;
import org.xml.sax.XMLReader;

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
        RoundedCorners corners = new RoundedCorners(ConvertUtils.Companion.dp2px(8));
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

    @BindingAdapter(value = "PartyTopBg")
    public static void PartyTopBg(ImageView img, String path) {
        String url = "";
        url = LocalUtilsKt.getImageUrl(path);
        int width = BaseApplication.Companion.getInstance().getGetWidthPixels();
        RequestOptions options = new RequestOptions().error(R.drawable.driver_home_top_bg).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(width, ConvertUtils.Companion.dp2px(375));
        Glide.with(img).asBitmap().load(url).apply(options).into(img);
    }

    @BindingAdapter("setViewHeight")
    public static void setViewHeight(LinearLayout view, PartyDetailEntity.PartyDetailRoadListItem detailRoadListItem) {
        View view1 = view.findViewById(R.id.mesure);
        view.measure(0, 0);
        if (detailRoadListItem.getItemtype() == 0) {
            view1.setMinimumHeight(view.getMeasuredHeight());
        } else if (detailRoadListItem.getItemtype() == 1) {
            view1.setMinimumHeight(view.getMeasuredHeight() - ConvertUtils.Companion.dp2px(15F));
        }
    }


    @BindingAdapter("initHtml")
    public static void initHtml(TextView tv, String str) {
        tv.setText(Html.fromHtml(str));
    }


    @BindingAdapter("initStrText")
    public static void initStrText(RelativeLayout layout, String value) {
//        final ExpandableTextView text = layout.findViewById(R.id.party_ex);
//        text.setText(value);
//        final TextView visible = layout.findViewById(R.id.party_visible);
//        visible.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (text.isCollapsed()) {
//                    text.setCollapsed(false);
//                    visible.setBackground(visible.getContext().getDrawable(R.color.trans));
//                    visible.setText("收起");
//                } else {
//                    visible.setBackground(visible.getContext().getDrawable(R.drawable.gradient_bg));
//                    text.setCollapsed(true);
//                    visible.setText("展开更多");
//                }
//                text.change();
//            }
//        });
//        textView.setText(value);
//        textView.mStateTv.setVisibility(View.GONE);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//        params.height = textView.getMeasuredHeight() + ConvertUtils.Companion.dp2px(50F);
//        textView.setLayoutParams(params);
    }


    @BindingAdapter({"initPartyTab"})
    public static void initPartyTab(TabLayout tab, final BindingCommand<Integer> command) {
        tab.addTab(tab.newTab().setText("介绍"));
        tab.addTab(tab.newTab().setText("相册"));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (command != null) {
                    command.execute(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @BindingAdapter({"initSubjectPartyHori", "initSubjectPartyHoriCommand"})
    public static void initSubjectPartyHori(LinearLayout layout, ArrayList<HoriTitleEntity> check, final BindingCommand command) {
        layout.removeAllViews();
        for (int i = 0; i < check.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.horizontal_subject_title_child, layout, false);
            binding.setVariable(BR.hori_data, check.get(i));
            final int finalI = i;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (command != null) {
                        command.execute(finalI);
                    }
                }
            });
            layout.addView(binding.getRoot());
        }
        layout.invalidate();
    }

    @BindingAdapter("setHoriType")
    public static void setHoriType(LinearLayout linearLayout, ArrayList<String> title) {
        linearLayout.removeAllViews();
        for (int i = 0; i < title.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) linearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.horizontal_type_title_child, linearLayout, false);
            binding.setVariable(BR.type_data, title.get(i));
            linearLayout.addView(binding.getRoot());
        }
        linearLayout.invalidate();
    }

    @BindingAdapter("setHoriLovelyData")
    public static void setHoriLovelyData(LinearLayout linearLayout, ArrayList<String> title) {
        linearLayout.removeAllViews();
        for (int i = 0; i < title.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) linearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.horizontal_type_title_child, linearLayout, false);
            binding.setVariable(BR.type_data, title.get(i));
            linearLayout.addView(binding.getRoot());
        }
        linearLayout.invalidate();
    }


    @BindingAdapter("PartyHtmlText")
    public static void setHtmlText(TextView tv, String html) {
        if (html == null) {
            return;
        }

        if (html.contains(".jpg")) {
            HtmlSpanner spanner = new HtmlSpanner();
//            spanner.registerHandler("img",new ImageHandler());
            Spannable spannable =  spanner.fromHtml(html);
            tv.setText(spannable);
//            tv.setMovementMethod(LinkMovementMethodExt.getInstance(handler, ImageSpan.class));

//            tv.setText(Html.fromHtml(html, new URLImageParser(tv), new Html.TagHandler() {
//                @Override
//                public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//
//                }
//            }));
        } else {
            tv.setText(Html.fromHtml(html));
        }
    }

    @BindingAdapter("PartyMembers")
    public static void PartyMembers(RelativeLayout layout, ArrayList<String> img) {
        layout.removeAllViews();
        for (int i = 0; i <= img.size(); i++) {
            ImageView imgs = new ImageView(layout.getContext());
            if (i == img.size()) {
                imgs.setImageResource(R.drawable.party_more_head);
            } else {
                CircleCrop crop = new CircleCrop();
                RequestOptions options = new RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(240F), ConvertUtils.Companion.dp2px(160F));
                Glide.with(imgs).asBitmap().load(LocalUtilsKt.getImageUrl(img.get(i))).apply(options).into(imgs);
            }
            layout.addView(imgs);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgs.getLayoutParams();
            params.setMargins(ConvertUtils.Companion.dp2px(i * 30), 0, 0, 0);
            params.height = ConvertUtils.Companion.dp2px(36);
            params.width = ConvertUtils.Companion.dp2px(36);
            imgs.setLayoutParams(params);
        }
        layout.invalidate();
    }


    @BindingAdapter("setNestScroller")
    public static void setNestScroller(RecyclerView recyclerView, Boolean enable) {
        recyclerView.setNestedScrollingEnabled(enable);
    }

    @BindingAdapter("LoadItemImge")
    public static void LoadItemImge(ImageView img, String url) {
        if (url == null || url.isEmpty()) {
            img.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions().error(R.drawable.nomal_img).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.Companion.dp2px(240F), ConvertUtils.Companion.dp2px(160F));
        Glide.with(img).asBitmap().load(url).apply(options).into(img);
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


    @BindingAdapter("setStates")
    public static void setStates(TextView tv, int status) {
        if (status == 1) {
            tv.setText("立即报名");
            tv.setTextColor(Color.WHITE);
            tv.setBackground(UtilsKt.getContext().getDrawable(R.drawable.login_btn_shape_nomal));
        } else if (status == 2) {
            tv.setTextColor(Color.BLACK);
            tv.setText("活动进行中");
            tv.setBackground(UtilsKt.getContext().getDrawable(R.drawable.login_btn_shape_bottom1));
        } else if (status == 3) {
            tv.setText("活动结束");
            tv.setTextColor(Color.BLACK);
            tv.setBackground(UtilsKt.getContext().getDrawable(R.drawable.login_btn_shape_bottom1));
        }
    }
}
