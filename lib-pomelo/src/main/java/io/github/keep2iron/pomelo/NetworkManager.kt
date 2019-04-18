/*
 * Create bt Keep2iron on 17-5-25 下午2:32
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo

import android.support.v4.util.ArrayMap
import java.util.HashMap
import okhttp3.OkHttpClient
import retrofit2.Retrofit

import android.text.TextUtils.isEmpty
import io.github.keep2iron.pomelo.convert.CustomConvertFactory
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @author keep2iron
 */
class NetworkManager private constructor() {
    /**
     * 因为存在android客户端连接多个地址，因此通过Map进行配置
     */
    private val mRetrofitMap = ArrayMap<String, Retrofit>()
    /**
     * 默认的url地址
     */
    private lateinit var mDefaultUrl: String

    fun <T> getService(clazz: Class<T>): T {
        if (mRetrofitMap[mDefaultUrl] == null) {
            throw IllegalArgumentException("default Url is not null")
        }
        return mRetrofitMap[mDefaultUrl]!!.create(clazz)
    }

    fun <T> getService(url: String?, clazz: Class<T>): T {
        if (url == null) {
            throw IllegalArgumentException("default Url is not null")
        }
        val retrofit = mRetrofitMap[url] ?: throw IllegalArgumentException("do you sure add the url ?")
        return retrofit.create(clazz)
    }

    /**
     * 使用建造者模式进行网络请求的构建
     */
    class Builder(private val defaultUrl: String) {

        private var mRetrofitBuilderMap = HashMap<String, Retrofit.Builder>()

        private var responseProcessListener: ((String?) -> String)? = null

        private var useDefaultConvertAdapter = true

        private var userRxJava2ConvertAdapter = true

        init {
            val retrofitBuilder = Retrofit.Builder().baseUrl(defaultUrl)
            mRetrofitBuilderMap[defaultUrl] = retrofitBuilder
        }

        fun useDefaultConvertAdapter(useDefaultConvertAdapter: Boolean) {
            this.useDefaultConvertAdapter = useDefaultConvertAdapter
        }

        fun userRxJava2ConvertAdapter(userRxJava2ConvertAdapter: Boolean) {
            this.userRxJava2ConvertAdapter = userRxJava2ConvertAdapter
        }

        /**
         * 添加的请求的基础地址,默认最先添加的url地址是请求的默认地址
         *
         * @param url 请求地址的基础地址
         */
        fun addBaseUrl(url: String): Builder {
            if (isEmpty(url)) {
                throw IllegalArgumentException("url is not null.")
            }

            val retrofitBuilder = Retrofit.Builder().baseUrl(url)
            mRetrofitBuilderMap[url] = retrofitBuilder

            return this
        }

        fun addConverterFactory(factory: Converter.Factory, url: String = this.defaultUrl): Builder {
            val builder =
                mRetrofitBuilderMap[url] ?: throw IllegalArgumentException("do you forget call addBaseUrl at first.")
            builder.addConverterFactory(factory)
            return this
        }

        fun addCallAdapterFactory(factory: CallAdapter.Factory, url: String = this.defaultUrl): Builder {
            val builder =
                mRetrofitBuilderMap[url] ?: throw IllegalArgumentException("do you forget call addBaseUrl at first.")
            builder.addCallAdapterFactory(factory)
            return this
        }

        fun setResponseListener(listener: (String?) -> String): Builder {
            this.responseProcessListener = listener
            return this
        }

        fun build(client: OkHttpClient): NetworkManager {
            val mNetworkClient = NetworkManager()

            mNetworkClient.mDefaultUrl = defaultUrl

            //retrofit对象的builder对象集合
            mRetrofitBuilderMap.forEach { (key, retrofitBuilder) ->
                if (useDefaultConvertAdapter) {
                    retrofitBuilder.addConverterFactory(CustomConvertFactory.create(this.responseProcessListener))
                }
                if (userRxJava2ConvertAdapter) {
                    retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                }
                val retrofit = retrofitBuilder.client(client).build()
                mNetworkClient.mRetrofitMap[key] = retrofit
            }

            return mNetworkClient
        }
    }
}