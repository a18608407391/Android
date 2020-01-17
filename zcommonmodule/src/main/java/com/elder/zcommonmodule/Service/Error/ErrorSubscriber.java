package com.elder.zcommonmodule.Service.Error;

import org.reactivestreams.Subscriber;

public abstract class ErrorSubscriber<T> implements Subscriber<T> {
    @Override
    public void onError(Throwable e) {
        if(e instanceof ApiException){
            onError((ApiException)e);
        }else{
            onError(new ApiException(e,123));
        }
    }

    /**
     * 错误回调
     */
    protected abstract void onError(ApiException ex);
}