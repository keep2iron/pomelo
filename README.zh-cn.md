![Image](image/banner.png)

# 为什么要使用该库？
都2020年Android开发圈中已经产生了很多重量级的开源库，如OkHttp、Retrofit、BaseQuickAdapter、Vlayout等等。

因此本库汲取了上述的几个个人认为编写不错的开源库的思路。

针对以下几个问题进行入手：

- 网络库的封装组织
- 分页代码以及adapter的代码复用
- 多类型recyclerView的代码处理

# Pomelo
Pomelo 是基于 [retrofit](https://github.com/square/retrofit) 、 [okhttp](https://github.com/square/okhttp) 、[rxjava](https://github.com/ReactiveX/RxJava) 的封装库

一共分为三个部分：
- core：基于OkHttp、Retrofit、RxJava的封装
- extra：处理分页问题、RecyclerView多类型问题
- log：网络日志打印

|     名称     |                           版本                            |       描述       |
| :----------: | :----------------------------------------------------------: | :---------------------: |
|     pomelo-core     | ![Release](https://api.bintray.com/packages/keep2iron/maven/pomelo-core/images/download.svg) | 网络库封装 |
| pomelo-extra | ![Release](https://api.bintray.com/packages/keep2iron/maven/pomelo-extra/images/download.svg) |    分页加载、RecyclerView多类型    |
| pomelo-log  | ![Release](https://api.bintray.com/packages/keep2iron/maven/pomelo-log/images/download.svg) |    网络日志处理    |

## Download
gradle:
```groovy
dependencies {
    implementation 'io.github.keep2iron:pomelo-core:$latest_version'
    implementation 'io.github.keep2iron:pomelo-extra:$latest_version'
    implementation 'com.github.keep2iron.pomelo:log:$latest_version'
}
```

## 下载

##### 初始化 NetworkManager

```kotlin
NetworkManager.init(host) {
    //初始化OkHttp
    initOkHttp {
        connectTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)

		//可选：全局网络错误拦截器
        addInterceptor(NetworkErrorHandleInterceptor { exception ->
            Log.d("keep2iron","${exception}")
        })

		//可选：全局header参数添加拦截器
        addInterceptor(HeaderParamsInterceptor { _, headerParams ->
            headerParams["test-header-params"] = ""
        })

		//可选：全局Get参数添加拦截器
        addInterceptor(GetParamsInterceptor { url, getParams ->
            getParams["test-get-params"] = ""
        })

		//可选：全局Post参数添加拦截器
        addInterceptor(PostParamsInterceptor { url, postParams ->
            postParams["test-post-params"] = ""
        })

		//可选：网络日志打印拦截器
        addNetworkInterceptor(NetworkLoggingInterceptor(object: NetworkLoggingInterceptor.Logger {
            override fun d(message: String) {
                Logger.d(message)
            }
        }))
    }

    //初始化Retrofit
    initRetrofit {
		//custom gson convert Factory
        addConverterFactory(CustomGsonConvertFactory.create())
    }
}
```

##### 请求 rest api

通过kotlin代理进行创建网络请求service
```kotlin
private val apiService by FindService(ApiService::class.java)
```

通过NetworkManager创建网络请求service
```kotlin
val apiService = NetworkManager.getInstance().getService(ApiService::class.java)
```

网络请求通过rxjava2
```kotlin
apiService.indexHome(0)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(AndroidSubscriber {
        onSuccess = { resp ->
            Log.d("keep2iron", "onSuccessful..........${resp.value}")
        }
        onError = {
        }
    })
```

### 请求分页

1.添加**io.github.keep2iron.pomlo.pager.load.LoadListener**，可以在activity或者viewModel中实现该接口。 

实现 **onLoad** 方法, 该方法将会在刷新时和加载更多时调用。

在该方法中使用 **LoadListSubscriber** 进行网络回调处理。

> LoadController.isLoadDefault(pagerValue)进行判断是否是加载第一次分页。
如果分页值是Int类型，那么可以用controller.intInc()进行分页值的自增。

````kotlin
override fun onLoad(controller: LoadController, pagerValue: Any) {
    apiService.indexHome(controller.pagerValue() as Int)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map {
        	it.value
        }
        .subscribe(LoadListSubscriber<Movie>(controller, data, pagerValue, pageState) {
            .......
    	})
}
````

2.使用ListBinder进行绑定RecyclerView与刷新布局

ListBinder(recyclerView, SwipeRefreshAble(refreshLayout), true)

3.使用ListBinder添加header或者list adapter

> 可选, 在 **io.github.keep2iron.pomlo.pager.load.LoadListener** 可以进行重写 **defaultValue():Any** 方法去返回一个默认的分页值(在 pomelo 中默认值是0),在 demo 中我们用1作为默认值.



**LoadListSubscriber** already packed up for the List page loading logic.

if you want to custom,you can use **io.github.keep2iron.pomlo.pager.rx.LoadSubscriber** or **io.github.keep2iron.pomelo.AndroidSubscriber** 