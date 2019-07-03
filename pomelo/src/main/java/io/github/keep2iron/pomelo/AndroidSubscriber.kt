/*
 * Create bt Keep2iron on 17-6-22 下午4:01
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo

import android.support.annotation.CallSuper
import android.util.Log
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

open class AndroidSubscriber<T>(
    private val useGlobalOnError: Boolean = true,
    private inline val onSuccess: ((resp: T) -> Unit)? = null,
    private inline val onError: ((throwable: Throwable) -> Unit)? = null
) : Observer<T>, Subscriber<T> {
    private var disposable: Disposable? = null
    private var subscription: Subscription? = null

    override fun onComplete() {
    }

    @CallSuper
    override fun onSubscribe(disposable: Disposable) {
        this.disposable = disposable
    }

    @CallSuper
    override fun onError(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(NetworkManager.TAG_NAME, Log.getStackTraceString(throwable))
        }
        onError?.invoke(throwable)
    }

    @CallSuper
    override fun onNext(t: T) {
        onSuccess?.invoke(t)
    }

    override fun onSubscribe(subscription: Subscription) {
        this.subscription = subscription
    }

    @CallSuper
    fun cancel() {
        disposable?.apply {
            if (!this.isDisposed) {
                this.dispose()
            }
        }

        subscription?.cancel()
    }
}