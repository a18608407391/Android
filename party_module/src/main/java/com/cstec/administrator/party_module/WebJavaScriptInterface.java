package com.cstec.administrator.party_module;

import android.util.Log;
import android.widget.Toast;

import com.elder.zcommonmodule.ConfigKt;

public class WebJavaScriptInterface {

    @android.webkit.JavascriptInterface
    public void hiddenBottomBar(Boolean flag) {
        Log.e("result", "WebJavaScriptInterface" + flag);
    }


}
