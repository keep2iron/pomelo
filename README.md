![Image](/image/banner.png)

# Pomelo
[![Release](https://jitpack.io/v/keep2iron/pomelo.svg)](https://jitpack.io/v/#keep2iron/pejoy) ![BuildStatus](https://travis-ci.org/keep2iron/pomelo.svg?branch=master)

Pomelo is base on [retrofit](https://github.com/square/retrofit) and [okhttp](https://github.com/square/okhttp)

- Simple use
- Beautiful log

## Download

gradle:
```groovy
repositories {
	maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.keep2iron.pomelo:pomelo:$latest_version'
	//optional provide network log
    implementation 'com.github.keep2iron.pomelo:log:$latest_version'
}
```

## Simple usage snippet

##### Init NetworkManager

```kotlin
//use Logger,please add "implementation 'com.orhanobut:logger:${logger_version}' " in gradle

val formatStrategy = PrettyFormatStrategy.newBuilder()
    .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
    .methodOffset(2)        // (Optional) Hides internal method calls up to offset. Default 5
    .tag("pomelo")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
    .build()
Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

NetworkManager.init(host) {
    initOkHttp {
        protocols(Collections.singletonList(Protocol.HTTP_1_1))         //解决 https://www.cnblogs.com/myhalo/p/6811472.html
        connectTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)

		//optional global network error. exp: http code 500
        addInterceptor(NetworkErrorHandleInterceptor { exception ->
            Log.d("keep2iron","${exception}")
        })

		//optional global header params.
        addInterceptor(HeaderParamsInterceptor { _, headerParams ->
            headerParams["test-header-params"] = ""
        })

		//optional global get params.
        addInterceptor(GetParamsInterceptor { url, getParams ->
            getParams["test-get-params"] = ""
        })

		//optional global post params.
        addInterceptor(PostParamsInterceptor { url, postParams ->
            postParams["test-post-params"] = ""
        })

		//optional network log.
        addNetworkInterceptor(NetworkLoggingInterceptor(object : NetworkLoggingInterceptor.Logger {
            override fun d(message: String) {
                Logger.d(message)
            }
        }))
    }

    initRetrofit {
		//custom gson convert Factory
        addConverterFactory(CustomGsonConvertFactory.create())
    }
}
```

##### Request rest api

create api service by kotlin delegate
```kotlin
private val apiService by FindService(ApiService::class.java)
```

create api service by instance
```kotlin
val apiService = NetworkManager.getInstance().getService(ApiService::class.java)
```

request use rxjava2
```kotlin
apiService.indexHome("test")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(AndroidSubscriber<BaseResponse<String>>{
        onSuccess = {
        }
		onError = {
		}
	})
```