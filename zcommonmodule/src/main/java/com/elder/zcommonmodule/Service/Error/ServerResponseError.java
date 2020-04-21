package com.elder.zcommonmodule.Service.Error;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.elder.zcommonmodule.ConfigKt;
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse;
import com.elder.zcommonmodule.Even.RequestErrorEven;
import com.elder.zcommonmodule.Http.NetWorkManager;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.elder.zcommonmodule.Service.HttpRequest;
import com.elder.zcommonmodule.Service.Login.LoginService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zk.library.Base.AppManager;
import com.zk.library.Bus.event.RxBusEven;
import com.zk.library.Utils.PreferenceUtils;
import com.zk.library.Utils.RouterUtils;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Bus.RxBus;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ServerResponseError implements Function<BaseResponse, String> {

    @Override
    public String apply(BaseResponse reponse) throws Exception {
        //对返回码进行判断，如果不是0，则证明服务器端返回错误信息了，便根据跟服务器约定好的错误码去解析异常

        if (reponse.getCode() != 0) {
            Log.e(this.getClass().getName(), getIntGson().toJson(reponse).replace("\\", ""));
            //如果服务器端有错误信息返回，那么抛出异常，让下面的方法去捕获异常做统一处理

            if (reponse.getCode() == 10009) {
                HashMap<String, String> map = new HashMap();
                map.put("phoneNumber", new String(Base64.encodeToString(PreferenceUtils.getString(UtilsKt.getContext(), ConfigKt.USER_PHONE).getBytes(Charset.forName("UTF-8")), Base64.DEFAULT)));
                HttpRequest.Companion.getInstance().relogin(map);
            } else {
                RxBus.Companion.getDefault().post(new RequestErrorEven(reponse.getCode()));
            }

            return "";
        } else {
            //服务器请求数据成功，返回里面的数据实体
            if (reponse.getData() != null) {
                String json = getIntGson().toJson(reponse.getData());
                if (json.contains("\\")) {
                    json.replace("\\", "");
                }
                return json;
            } else {
                return "";
            }
        }
    }

    public static Gson getIntGson() {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

                    @Override
                    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                        if (src == src.longValue())
                            return new JsonPrimitive(src.longValue());
                        return new JsonPrimitive(src);
                    }
                }).create();
        return gson;
    }


}