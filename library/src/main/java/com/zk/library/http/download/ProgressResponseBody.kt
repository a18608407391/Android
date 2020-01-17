package org.cs.tec.library.http.download

import org.cs.tec.library.Bus.RxBus

import java.io.IOException

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source

/**
 * Created by goldze on 2017/5/11.
 */

class ProgressResponseBody : ResponseBody {
    private var responseBody: ResponseBody? = null

    private var bufferedSource: BufferedSource? = null
    private var tag: String = ""

    constructor(responseBody: ResponseBody) {
        this.responseBody = responseBody
    }

    constructor(responseBody: ResponseBody, tag: String) {
        this.responseBody = responseBody
        this.tag = tag
    }

    override fun contentType(): MediaType? {
        return responseBody!!.contentType()
    }

    override fun contentLength(): Long {
        return responseBody!!.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody!!.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            internal var bytesReaded: Long = 0
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                bytesReaded += if (bytesRead.toInt() == -1) 0 else bytesRead
                //使用RxBus的方式，实时发送当前已读取(上传/下载)的字节数据
                RxBus.default!!.post(DownLoadStateBean(contentLength(), bytesReaded, tag))
                return bytesRead
            }
        }
    }
}
