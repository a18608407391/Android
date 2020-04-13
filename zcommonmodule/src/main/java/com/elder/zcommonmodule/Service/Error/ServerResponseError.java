package com.elder.zcommonmodule.Service.Error;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse;
import com.elder.zcommonmodule.Even.RequestErrorEven;
import com.elder.zcommonmodule.LocalUtilsKt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zk.library.Base.AppManager;
import com.zk.library.Utils.RouterUtils;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Bus.RxBus;

import java.lang.reflect.Type;

import io.reactivex.functions.Function;

public class ServerResponseError implements Function<BaseResponse, String> {

    @Override
    public String apply(BaseResponse reponse) throws Exception {
        //对返回码进行判断，如果不是0，则证明服务器端返回错误信息了，便根据跟服务器约定好的错误码去解析异常

        if (reponse.getCode() != 0) {
            Log.e(this.getClass().getName(), getIntGson().toJson(reponse).replace("\\", ""));
            //如果服务器端有错误信息返回，那么抛出异常，让下面的方法去捕获异常做统一处理
            RxBus.Companion.getDefault().post(new RequestErrorEven(reponse.getCode()));
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