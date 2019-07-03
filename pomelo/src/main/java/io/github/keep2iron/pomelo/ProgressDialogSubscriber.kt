package io.github.keep2iron.pomelo

import android.app.Activity
import android.support.annotation.CallSuper
import io.github.keep2iron.pomelo.dialog.ProgressDialogController
import org.reactivestreams.Subscription
import java.lang.ref.WeakReference
import io.reactivex.disposables.Disposable

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2017/12/27 17:46
 */
open class ProgressDialogSubscriber<T>(
    activity: Activity,
    private val controller: ProgressDialogController,
    block: AndroidSubscriber<T>.() -> Unit
) : AndroidSubscriber<T>(block) {
    private var activityRef = WeakReference(activity)

    @CallSuper
    override fun onSubscribe(disposable: Disposable) {
        super.onSubscribe(disposable)
        val act = activityRef.get()
        if (act != null) {
            controller.onDialogShow(act, this as AndroidSubscriber<Any>)
        }
    }

    @CallSuper
    override fun onSubscribe(subscription: Subscription) {
        super.onSubscribe(subscription)
        val act = activityRef.get()
        if (act != null) {
            controller.onDialogShow(act, this as AndroidSubscriber<Any>)
        }
    }

    @CallSuper
    override fun onNext(t: T) {
        super.onNext(t)
        controller.onDialogDismiss()
    }

    @CallSuper
    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        controller.onDialogDismiss()
    }

    @CallSuper
    override fun onComplete() {
        super.onComplete()
        controller.onDialogDismiss()
    }
}
