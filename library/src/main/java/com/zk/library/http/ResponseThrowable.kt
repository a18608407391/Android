package org.cs.tec.library.http

/**
 * Created by goldze on 2017/5/11.
 */

class ResponseThrowable(throwable: Throwable, var code: Int) : Exception(throwable) {
    override var message: String? = null
}
