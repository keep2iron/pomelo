/*
 * Create bt Keep2iron on 17-5-25 下午2:32
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo

import android.annotation.SuppressLint
import android.os.Looper
import android.support.v4.util.ArrayMap
import android.support.v4.util.Preconditions
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import android.text.TextUtils.isEmpty
import io.github.keep2iron.pomelo.convert.CustomConvertFactory
import io.github.keep2iron.pomelo.interceptor.GetParamsInterceptor
import io.github.keep2iron.pomelo.interceptor.HeaderInterceptor
import io.github.keep2iron.pomelo.interceptor.HttpLogger
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @author keep2iron
 */
@SuppressLint("RestrictedApi")
class NetworkManager private constructor() {

    companion object {

        private var doOnGlobalError: ((throwable: Throwable) -> Unit)? = null

        var TAG = "pomelo"

        inline fun build(
            baseHost: String,
            block: Builder.() -> Unit
        ): NetworkManager {
            return Builder(baseHost).apply(block).build()
        }

        @JvmStatic
        fun doOnError(throwable: Throwable) {
            Looper.getMainLooper()
            doOnGlobalError?.invoke(throwable)
        }
    }

//    private var mRetrofitBuilderMap = HashMap<String, Retrofit.Builder>()

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
    class Builder constructor(
        private val defaultUrl: String
    ) {

        /**
         * 可用于返回的公共处理
         */
        private var responseProcessListener: ((String?) -> String?)? = null

        /**
         * Get方法统一参数拦截器
         *
         * @param listener 通过 getParams map添加参数
         */
        fun setGetParameterInterceptor(listener: (url: String, getParams: ArrayMap<String, String>) -> Unit) {
            okhttpBuilder.addInterceptor(GetParamsInterceptor(listener))
        }

        /**
         * header统一请求参数拦截器
         */
        fun setHeaderParameterInterceptor(listener: (url: String, getParams: ArrayMap<String, String>) -> Unit) {
            okhttpBuilder.addInterceptor(HeaderInterceptor(listener))
        }

        var userNetworkLogger = true

        var networkTagName = "pomelo"

        /**
         * 是否使用原有的GsonConvertAdapter
         */
        var useDefaultConvertAdapter = true

        /**
         * 是否使用RxJava2ConvertAdapter
         */
        var userRxJava2ConvertAdapter = true

        private var urlList = ArrayList<String>()

        private var callAdapterFactoryMap = ArrayMap<String, MutableList<CallAdapter.Factory>?>()

        private var convertAdapterFactoryMap = ArrayMap<String, MutableList<Converter.Factory>?>()

        /**
         * 统一错误处理
         */
        var doOnGlobalError: ((throwable: Throwable) -> Unit)? = null

        val okhttpBuilder = OkHttpClient.Builder()

        init {
            urlList.add(defaultUrl)
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

            urlList.add(url)

            return this
        }


        fun addConverterFactory(factory: Converter.Factory, url: String = this.defaultUrl) {
            Preconditions.checkArgument(urlList.indexOf(url) == -1, "do you forget call addBaseUrl at first.")

            var list = convertAdapterFactoryMap[url]
            if (list == null) {
                list = ArrayList()
                convertAdapterFactoryMap[url] = list
            }

            list.add(factory)
        }

        fun addCallAdapterFactory(factory: CallAdapter.Factory, url: String = this.defaultUrl) {
            Preconditions.checkArgument(urlList.indexOf(url) == -1, "do you forget call addBaseUrl at first.")

            var list = callAdapterFactoryMap[url]
            if (list == null) {
                list = ArrayList()
                callAdapterFactoryMap[url] = list
            }

            list.add(factory)
        }

        fun setResponseProcessListener(responseProcessListener:((String?) -> String?)? = null){
            this.responseProcessListener = responseProcessListener
        }

        inline fun initOkHttp(block: Builder.(OkHttpClient.Builder) -> Unit) {
            block(okhttpBuilder)
        }

        fun build(): NetworkManager {
            val mNetworkClient = NetworkManager()

            if (userNetworkLogger) {
                val loggingInterceptor = HttpLoggingInterceptor(HttpLogger(networkTagName))
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okhttpBuilder.addNetworkInterceptor(loggingInterceptor)
            }
            val okHttpClient = okhttpBuilder.build()

            mNetworkClient.mDefaultUrl = defaultUrl

            NetworkManager.doOnGlobalError = doOnGlobalError

            urlList.forEach { url ->
                val retrofitBuilder = Retrofit.Builder().client(okHttpClient)
                retrofitBuilder.baseUrl(url)

                val callAdapterList = callAdapterFactoryMap[url]
                callAdapterList?.forEach {
                    retrofitBuilder.addCallAdapterFactory(it)
                }

                val convertAdapterList = convertAdapterFactoryMap[url]
                convertAdapterList?.forEach {
                    retrofitBuilder.addConverterFactory(it)
                }

                if (useDefaultConvertAdapter) {
                    retrofitBuilder.addConverterFactory(CustomConvertFactory.create(this.responseProcessListener))
                }

                if (userRxJava2ConvertAdapter) {
                    retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                }

                mNetworkClient.mRetrofitMap[url] = retrofitBuilder.build()
            }

            return mNetworkClient
        }
    }
}