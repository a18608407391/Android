package org.cs.tec.library.http

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException

import android.net.ParseException

import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException

import java.net.ConnectException

import retrofit2.HttpException


/**
 * Created by goldze on 2017/5/11.
 */
class ExceptionHandle {


    /**
     * 约定异常 这个具体规则需要与服务端或者领导商讨定义
     */
    internal object ERROR {
        /**
         * 未知错误
         */
        val UNKNOWN = 1000
        /**
         * 解析错误
         */
        val PARSE_ERROR = 1001
        /**
         * 网络错误
         */
        val NETWORD_ERROR = 1002
        /**
         * 协议出错
         */
        val HTTP_ERROR = 1003

        /**
         * 证书出错
         */
        val SSL_ERROR = 1005

        /**
         * 连接超时
         */
        val TIMEOUT_ERROR = 1006
    }

    companion object {

        private val UNAUTHORIZED = 401
        private val FORBIDDEN = 403
        private val NOT_FOUND = 404
        private val REQUEST_TIMEOUT = 408
        private val INTERNAL_SERVER_ERROR = 500
        private val SERVICE_UNAVAILABLE = 503

        fun handleException(e: Throwable): ResponseThrowable {
            val ex: ResponseThrowable
            if (e is HttpException) {
                ex = ResponseThrowable(e, ERROR.HTTP_ERROR)
                when (e.code()) {
                    UNAUTHORIZED -> ex.message = "操作未授权"
                    FORBIDDEN -> ex.message = "请求被拒绝"
                    NOT_FOUND -> ex.message = "资源不存在"
                    REQUEST_TIMEOUT -> ex.message = "服务器执行超时"
                    INTERNAL_SERVER_ERROR -> ex.message = "服务器内部错误"
                    SERVICE_UNAVAILABLE -> ex.message = "服务器不可用"
                    else -> ex.message = "网络错误"
                }
                return ex
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException || e is MalformedJsonException) {
                ex = ResponseThrowable(e, ERROR.PARSE_ERROR)
                ex.message = "解析错误"
                return ex
            } else if (e is ConnectException) {
                ex = ResponseThrowable(e, ERROR.NETWORD_ERROR)
                ex.message = "连接失败"
                return ex
            } else if (e is javax.net.ssl.SSLException) {
                ex = ResponseThrowable(e, ERROR.SSL_ERROR)
                ex.message = "证书验证失败"
                return ex
            } else if (e is ConnectTimeoutException) {
                ex = ResponseThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = "连接超时"
                return ex
            } else if (e is java.net.SocketTimeoutException) {
                ex = ResponseThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = "连接超时"
                return ex
            } else if (e is java.net.UnknownHostException) {
                ex = ResponseThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = "主机地址未知"
                return ex
            } else {
                ex = ResponseThrowable(e, ERROR.UNKNOWN)
                ex.message = "未知错误"
                return ex
            }
        }
    }

}

