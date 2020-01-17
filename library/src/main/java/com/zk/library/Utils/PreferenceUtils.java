package com.zk.library.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zk.library.Base.BaseActivity;

public class PreferenceUtils {

    private static SharedPreferences mPreferences;

    private static SharedPreferences getSp(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences("ChangShengInfo", Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    /**
     * 获得boolean类型的�?�，没有时默认�?�为false
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * 获得boolean类型的数�?
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 设置boolean类型的�??
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获得String类型的�?�，没有时默认�?�为null
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * 获得String类型的数�?
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getString(key, defValue);
    }

    /**
     * 设置String类型的�??
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putStringSet(Context context, String key, Set<String> value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defValue) {
        SharedPreferences sp = getSp(context);

        return sp.getStringSet(key, defValue);
    }

    /**
     * 获得int类型的�?�，没有时默认�?�为-1
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 获得String类型的数�?
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getInt(key, defValue);
    }

    /**
     * 设置int类型的�??
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获得long类型的�?�，没有时默认�?�为-1
     *
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * 获得String类型的数�?
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getLong(key, defValue);
    }

    /**
     * 设置int类型的�??
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putLong(Context context, String key, long value) {
        SharedPreferences sp = getSp(context);
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static int getStringResId(Context context, String name, String language) {
        return context.getResources().getIdentifier(name + "_" + language, "string", context.getPackageName());
    }

    public static String getLanguage(Context context) {
        SharedPreferences sp = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        String language = sp.getString("language", "");
        if (language == null || language.equals("")) {
            language = Locale.getDefault().getLanguage();
        }
        Log.v("message", "language new :" + language);
        return language;
    }

    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isTranslucentOrFloating(BaseActivity activity) {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = activity.obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

//    public static boolean isTranslucentOrFloating(TypedArray attributes) {
//        final boolean isTranslucent =
//                attributes.getBoolean(com.android.internal.R.styleable.Window_windowIsTranslucent,
//                        false);
//        final boolean isSwipeToDismiss = !attributes.hasValue(
//                com.android.internal.R.styleable.Window_windowIsTranslucent)
//                && attributes.getBoolean(
//                com.android.internal.R.styleable.Window_windowSwipeToDismiss, false);
//        final boolean isFloating =
//                attributes.getBoolean(com.android.internal.R.styleable.Window_windowIsFloating,
//                        false);
//
//        return isFloating || isTranslucent || isSwipeToDismiss;
//    }
}
