/*
 * Create bt Keep2iron on 17-5-25 下午2:33
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2018/03/16 15:54
 *
 * 统一的header添加管理器
 */
class HeaderParamsInterceptor(val listener: (url: String, headerParams: HashMap<String, String>) -> Unit) :
    Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val headerParams = HashMap<String, String>()
    val url = request.url()
        .uri()
        .toString()
    listener(url, headerParams)

    val builder = request.newBuilder()
    for ((key, value) in headerParams) {
      builder.addHeader(key, value)
    }

    return chain.proceed(builder.build())
  }
}