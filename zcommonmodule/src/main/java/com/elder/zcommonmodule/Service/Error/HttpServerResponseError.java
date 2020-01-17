package com.elder.zcommonmodule.Service.Error;

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse;
import com.google.gson.Gson;

import io.reactivex.functions.Function;

public class HttpServerResponseError implements Function<BaseResponse, String> {

    @Override
    public String apply(BaseResponse reponse) throws Exception {
        //对返回码进行判断，如果不是0，则证明服务器端返回错误信息了，便根据跟服务器约定好的错误码去解析异常
        //服务器请求数据成功，返回里面的数据实体
        String json = new Gson().toJson(reponse.getData());
        if (json.contains("\\")) {
            json.replace("\\", "");
        }
        return json;
    }
}