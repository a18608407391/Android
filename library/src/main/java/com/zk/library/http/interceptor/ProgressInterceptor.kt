package org.cs.tec.library.http.interceptor

import org.cs.tec.library.http.download.ProgressResponseBody

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by goldze on 2017/5/10.
 */

class ProgressInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
                .body(ProgressResponseBody(originalResponse.body()!!))
                .build()
    }
}
