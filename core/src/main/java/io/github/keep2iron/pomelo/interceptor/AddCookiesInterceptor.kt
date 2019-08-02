/*
 * Create bt Keep2iron on 17-5-25 下午2:33
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This interceptor put all the Cookies in Pref in the Request.
 * Your implementation on how to get the Pref MAY VARY.
 *
 * 用于添加Cookie信息的拦截器
 *
 * Created by tsuharesu on 4/1/15.
 */
open class AddCookiesInterceptor : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val builder = request.newBuilder()

//        val preferences = SPUtils.instance.getStringSet("cookies", HashSet()) as HashSet<String>
//        for (cookie in preferences) {
//            builder.addHeader("Cookie", cookie)
//        }

    return chain.proceed(builder.build())
  }
}