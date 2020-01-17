package com.elder.zcommonmodule.Service.Error;


import io.reactivex.Observable;
import io.reactivex.functions.Function;

class HttpResponseError<T> implements Function<Throwable, Observable<T>> {

    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ExceptionEngine.handleException(throwable));
    }
}