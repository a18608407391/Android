package com.cstec.administrator.chart_module.Utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cstec.administrator.chart_module.View.ChatUtils.imageloader.ImageBase;
import com.cstec.administrator.chart_module.View.ChatUtils.imageloader.ImageLoader;

import java.io.IOException;



public class ImageLoadUtils extends ImageLoader {

    public ImageLoadUtils(Context context) {
        super(context);
    }

    @Override
    protected void displayImageFromFile(String imageUri, ImageView imageView) throws IOException {
        String filePath = ImageBase.Scheme.FILE.crop(imageUri);
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(filePath)
                .into(imageView);
    }

    @Override
    protected void displayImageFromAssets(String imageUri, ImageView imageView) throws IOException {
        String uri = ImageBase.Scheme.cropScheme(imageUri);
        ImageBase.Scheme.ofUri(imageUri).crop(imageUri);
        Glide.with(imageView.getContext())
                .load(Uri.parse("file:///android_asset/" + uri))
                .into(imageView);
    }
}
