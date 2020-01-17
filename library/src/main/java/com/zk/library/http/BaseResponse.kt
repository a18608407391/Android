package org.cs.tec.library.http

/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
class BaseResponse<T> {
    var code: Int = 0
    var message: String? = null
    var result: T? = null

    val isOk: Boolean
        get() = code == 0
}
