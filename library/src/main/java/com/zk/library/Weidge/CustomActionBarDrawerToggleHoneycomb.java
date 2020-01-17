package com.zk.library.Weidge;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Method;

public class CustomActionBarDrawerToggleHoneycomb {

    private static final String TAG = "ActionBarDrawerToggleHC";
    private static final int[] THEME_ATTRS = new int[]{16843531};

    public static SetIndicatorInfo setActionBarUpIndicator(SetIndicatorInfo info, Activity activity, Drawable drawable, int contentDescRes) {
        info = new SetIndicatorInfo(activity);
        if (info.setHomeAsUpIndicator != null) {
            try {
                ActionBar actionBar = activity.getActionBar();
                info.setHomeAsUpIndicator.invoke(actionBar, drawable);
                info.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
            } catch (Exception var5) {
                Log.w("ActionBarDrawerToggleHC", "Couldn't set home-as-up indicator via JB-MR2 API", var5);
            }
        } else if (info.upIndicatorView != null) {
            info.upIndicatorView.setImageDrawable(drawable);
        } else {
            Log.w("ActionBarDrawerToggleHC", "Couldn't set home-as-up indicator");
        }

        return info;
    }

    public static SetIndicatorInfo setActionBarDescription(SetIndicatorInfo info, Activity activity, int contentDescRes) {
        if (info == null) {
            info = new SetIndicatorInfo(activity);
        }

        if (info.setHomeAsUpIndicator != null) {
            try {
                ActionBar actionBar = activity.getActionBar();
                info.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
                if (Build.VERSION.SDK_INT <= 19) {
                    actionBar.setSubtitle(actionBar.getSubtitle());
                }
            } catch (Exception var4) {
                Log.w("ActionBarDrawerToggleHC", "Couldn't set content description via JB-MR2 API", var4);
            }
        }

        return info;
    }

    public static Drawable getThemeUpIndicator(Activity activity) {
        TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
        Drawable result = a.getDrawable(0);
        a.recycle();
        return result;
    }

    private CustomActionBarDrawerToggleHoneycomb() {
    }

    static class SetIndicatorInfo {
        public Method setHomeAsUpIndicator;
        public Method setHomeActionContentDescription;
        public ImageView upIndicatorView;

        SetIndicatorInfo(Activity activity) {
            try {
                this.setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator", Drawable.class);
                this.setHomeActionContentDescription = ActionBar.class.getDeclaredMethod("setHomeActionContentDescription", Integer.TYPE);
            } catch (NoSuchMethodException var8) {
                @SuppressLint("ResourceType") View home = activity.findViewById(16908332);
                if (home != null) {
                    ViewGroup parent = (ViewGroup)home.getParent();
                    int childCount = parent.getChildCount();
                    if (childCount == 2) {
                        View first = parent.getChildAt(0);
                        View second = parent.getChildAt(1);
                        @SuppressLint("ResourceType") View up = first.getId() == 16908332 ? second : first;
                        if (up instanceof ImageView) {
                            this.upIndicatorView = (ImageView)up;
                        }

                    }
                }
            }
        }
    }
}
