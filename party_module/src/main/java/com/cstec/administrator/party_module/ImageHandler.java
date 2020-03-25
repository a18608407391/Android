package com.cstec.administrator.party_module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.zk.library.Utils.TextView.SpanStack;
import com.zk.library.Utils.TextView.TagNodeHandler;


import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;

public class ImageHandler extends TagNodeHandler {

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
        String src = node.getAttributeByName("src");

        builder.append("\uFFFC");

        Bitmap bitmap = loadBitmap(src);

        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth() - 1,
                    bitmap.getHeight() - 1);
            spanStack.pushSpan(new ImageSpan(drawable), start, builder.length());
        }
    }


    protected Bitmap loadBitmap(String url) {
        try {
            return BitmapFactory.decodeStream(new URL(url).openStream());
        } catch (IOException io) {
            return null;
        }
    }
}
