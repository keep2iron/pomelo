/*
 * Create bt Keep2iron on 17-5-25 下午2:32
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo

import java.util.HashMap
import java.util.LinkedHashMap
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
     * 因为存在android客户端连接多个地址，因此通过
     */
    private val mRetrofitMap = LinkedHashMap<String, Retrofit>()
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

        private var mNetworkClient: NetworkManager = NetworkManager()
        private var mRetrofitBuilderMap = HashMap<String, Retrofit.Builder>()

        init {
            val retrofitBuilder = Retrofit.Builder().baseUrl(defaultUrl)
            mRetrofitBuilderMap[defaultUrl] = retrofitBuilder
        }

        /**
         * 状态检测的字节码文件
         */
        private var mClazz: Class<out IResponseStatus>? = null

        /**
         * 设置这个类主要是为了进行服务器基础数据的状态监测
         *
         *
         * 形如这样的json
         * {
         * "MessageCode":1000,
         * "Message":"成功",
         * {
         * 数据......
         * }
         * }
         * 传入字节码对象，因为内部会进行gson解析然后生成这个状态对象。
         *
         *
         * 这个检测对象必须实现了**CustomConvertFactory.IResponseStatus****这个类
         *
         * @param clazz 状态监测的字节码
         ** */
        fun setBaseServerResponse(
            clazz: Class<out IResponseStatus>
        ): Builder {
            this.mClazz = clazz

            return this
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
            val builder = mRetrofitBuilderMap[url] ?: throw IllegalArgumentException("do you forget call addBaseUrl at first.")
            builder.addConverterFactory(factory)
            return this
        }

        fun addCallAdapterFactory(factory: CallAdapter.Factory, url: String = this.defaultUrl): Builder {
            val builder = mRetrofitBuilderMap[url] ?: throw IllegalArgumentException("do you forget call addBaseUrl at first.")
            builder.addCallAdapterFactory(factory)
            return this
        }

        fun build(client: OkHttpClient): NetworkManager {
            mNetworkClient.mDefaultUrl = defaultUrl

            //retrofit对象的builder对象集合
            mRetrofitBuilderMap.forEach { (key, retrofitBuilder) ->
                mClazz?.let {
                    retrofitBuilder.addConverterFactory(CustomConvertFactory.create(mClazz))
                }
                retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                val retrofit = retrofitBuilder.client(client).build()
                mNetworkClient.mRetrofitMap[key] = retrofit
            }

            return mNetworkClient
        }
    }
}