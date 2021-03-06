/*
 * Create bt Keep2iron on 17-6-22 下午4:01
 * Copyright (c) 2017. All rights reserved.
 */

package io.github.keep2iron.pomelo

import android.util.Log
import androidx.annotation.CallSuper
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

open class AndroidSubscriber<T>(
    block: AndroidSubscriber<T>.() -> Unit
) : Observer<T>, Subscriber<T>, SingleObserver<T> {

    init {
        apply(block)
    }

    private var disposable: Disposable? = null
    private var subscription: Subscription? = null

    var onSuccess: ((resp: T) -> Unit)? = null
    var onError: ((throwable: Throwable) -> Unit)? = null

    override fun onComplete() {
    }

    @CallSuper
    override fun onSubscribe(disposable: Disposable) {
        this.disposable = disposable
    }

    @CallSuper
    override fun onError(throwable: Throwable) {
        Log.e(AndroidSubscriber::class.java.name,Log.getStackTraceString(throwable))
        onError?.invoke(throwable)
    }

    @CallSuper
    override fun onNext(t: T) {
        onSuccess?.invoke(t)
    }

    @CallSuper
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

    @CallSuper
    override fun onSuccess(t: T) {
        onNext(t)
    }
}