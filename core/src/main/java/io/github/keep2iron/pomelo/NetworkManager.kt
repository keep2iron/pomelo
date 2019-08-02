/*
 * Create bt Keep2iron on 17-5-25 下午2:32
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author keep2iron
 */
class NetworkManager private constructor() {

  companion object {
    private var INSTANCE: NetworkManager? = null

    internal var TAG_NAME = "pomelo"

    @JvmStatic
    fun init(
      baseHost: String,
      block: Builder.() -> Unit
    ) {
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: Builder(baseHost)
            .apply(block)
            .build().also {
              INSTANCE = it
            }
      }
    }

    fun getInstance(): NetworkManager {
      return INSTANCE!!
    }
  }

  /**
   * 因为存在android客户端连接多个地址，因此通过Map进行配置
   */
  private val mRetrofitMap = HashMap<String, Retrofit>()
  /**
   * 默认的url地址
   */
  private lateinit var defaultUrl: String

  fun <T> getService(clazz: Class<T>): T {
    if (mRetrofitMap[defaultUrl] == null) {
      throw IllegalArgumentException("default Url is not null")
    }
    return mRetrofitMap[defaultUrl]!!.create(clazz)
  }

  fun <T> getService(
    url: String?,
    clazz: Class<T>
  ): T {
    if (url == null) {
      throw IllegalArgumentException("default Url is not null")
    }
    val retrofit = mRetrofitMap[url] ?: throw IllegalArgumentException("do you sure add the url ?")
    return retrofit.create(clazz)
  }

  /**
   * 使用建造者模式进行网络请求的构建
   */
  class Builder constructor(
    private val defaultUrl: String
  ) {
    private val urlList = ArrayList<String>()

    private val okhttpBuilderMap = HashMap<String, OkHttpClient.Builder>()

    private val retrofitBuilderMap = HashMap<String, Retrofit.Builder>()

    init {
      addBaseUrl(defaultUrl)
    }

    /**
     * 添加的请求的基础地址,默认最先添加的url地址是请求的默认地址
     *
     * @param url 请求地址的基础地址
     */
    fun addBaseUrl(url: String): Builder {
      check(url.isNotBlank()) { "url is not null." }

      urlList.add(url)
      okhttpBuilderMap[url] = OkHttpClient.Builder()
      retrofitBuilderMap[url] = Retrofit.Builder()

      return this
    }

    fun initOkHttp(
      url: String = defaultUrl,
      block: OkHttpClient.Builder.() -> Unit
    ) {
      check(urlList.indexOf(url) != -1) { "do you forget call addBaseUrl at first." }

      okhttpBuilderMap[url]!!.apply(block)
    }

    fun initRetrofit(
      url: String = defaultUrl,
      block: Retrofit.Builder.() -> Unit
    ) {
      check(urlList.indexOf(url) != -1) { "do you forget call addBaseUrl at first." }

      retrofitBuilderMap[url]!!.apply(block)
    }

    fun setNetworkTagName(networkTagName: String) {
      TAG_NAME = networkTagName
    }

    fun build(): NetworkManager {
      val mNetworkClient = NetworkManager()

      mNetworkClient.defaultUrl = defaultUrl

      urlList.forEach { url ->
        val okHttpClient = okhttpBuilderMap[url]!!.build()
        val retrofit = retrofitBuilderMap[url]!!.client(okHttpClient)
            .baseUrl(url)
            .build()

        mNetworkClient.mRetrofitMap[url] = retrofit
      }

      return mNetworkClient
    }
  }
}