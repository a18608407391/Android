package com.zk.library.binding.command.ViewAdapter.Banner;

import android.databinding.BindingAdapter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import org.cs.tec.library.binding.command.ViewAdapter.Banner.BannerData;
import org.cs.tec.library.binding.command.ViewAdapter.Banner.OnBannerItemClickListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewAdapter {

    @BindingAdapter(value = {"android:setBannerUrl", "onBannerClickCommand"})
    public static void setBannerToPlayer(final Banner banner, final String url, final OnBannerItemClickListener bindingCommand) {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
                Request request = new Request.Builder().addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)").url(url).build();
                Call call = client.newCall(request);
                Response response = call.execute();
                emitter.onNext(response);
            }
        }).subscribeOn(Schedulers.io()).map(new Function<Response, ArrayList<BannerData>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public ArrayList<BannerData> apply(Response response) throws Exception {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.e("result", "轮播图数据" + json);
                    org.cs.tec.library.binding.command.ViewAdapter.Banner.Banner banner1 = new Gson().fromJson(json, org.cs.tec.library.binding.command.ViewAdapter.Banner.Banner.class);
                    final ArrayList<BannerData> data = new ArrayList<>();
                    if (banner1.getPic() != null) {
                        for (int i = 0; i < banner1.getPic().size(); i++) {
                            data.add(new BannerData(banner1.getPic().get(i).getType(), banner1.getPic().get(i).getMo_type(), banner1.getPic().get(i).getCode(), banner1.getPic().get(i).getRandpic()));
                        }
                        return data;
                    }
                }
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<BannerData>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(final ArrayList<BannerData> bannerData) {
//                player(banner, null, bannerData);
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        if (bindingCommand != null) {
                            bindingCommand.onItemClick(bannerData.get(position));
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

//    public static void player(Banner banner, List<String> titles, List<BannerData> bannerImages) {
//
//        if (null != titles) {
//            banner.setBannerStyle(com.youth.banner.BannerConfig.CIRCLE_INDICATOR_TITLE);
//            banner.setBannerTitles(titles);
//        } else {
//            banner.setBannerStyle(com.youth.banner.BannerConfig.CIRCLE_INDICATOR);
//        }
//        banner.setImageLoader(new ImageLoader() {
//            @Override
//            public void displayImage(Context context, Object path, ImageView imageView) {
//                RequestOptions options = new RequestOptions().placeholder(R.drawable.icon_default).error(R.drawable.icon_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(750, 300);
//                Glide.with(context).asDrawable().apply(options).load(path).into(imageView);
//            }
//        });
//        ArrayList<String> list = new ArrayList();
//        for (int i = 0; i < bannerImages.size(); i++) {
//            list.add(bannerImages.get(i).getRandpic());
//        }
//        banner.setImages(list);
//        banner.setDelayTime(3000);
//        banner.isAutoPlay(true);
//        banner.setIndicatorGravity(com.youth.banner.BannerConfig.CENTER);
//        banner.setBannerAnimation(com.youth.banner.Transformer.Default);
//        banner.start();
//    }
//
//    @BindingAdapter(value = {"android:setHomeBannerUrl", "onBinnerClickCommand"})
//    public static void setHomeBanner(final Banner banner, final VideoListData url, final OnBinnerItemClickListener bindingCommand) {
//         final ArrayList<BinnerData>  data = url.getBannerUrls();
//        if (bindingCommand != null) {
//            banner.setOnBannerListener(new OnBannerListener() {
//                @Override
//                public void OnBannerClick(int position) {
//                    bindingCommand.onItemClick(data.get(position));
//                }
//            });
//        }
//        ArrayList<String> imags = new ArrayList();
//        ArrayList<String> name = new ArrayList<>();
//        for (int i = 0; i < data.size(); i++) {
//            imags.add(data.get(i).getImgUrl());
//            name.add(data.get(i).getName());
//        }
//        banner.setBannerStyle(com.youth.banner.BannerConfig.CIRCLE_INDICATOR_TITLE);
//        banner.setBannerTitles(name);
//        banner.setImageLoader(new ImageLoader() {
//            @Override
//            public void displayImage(Context context, Object path, ImageView imageView) {
//                RequestOptions options = new RequestOptions().placeholder(R.drawable.icon_default).error(R.drawable.icon_default).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(750, 300);
//                Glide.with(context).asDrawable().apply(options).load(path).into(imageView);
//            }
//        });
//        banner.setImages(imags);
//        banner.setDelayTime(3000);
//        banner.isAutoPlay(true);
//        banner.setIndicatorGravity(com.youth.banner.BannerConfig.CENTER);
//        banner.setBannerAnimation(com.youth.banner.Transformer.Default);
//        banner.start();
//
//    }
}
