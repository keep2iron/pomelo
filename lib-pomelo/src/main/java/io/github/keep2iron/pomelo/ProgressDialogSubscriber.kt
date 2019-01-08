package io.github.keep2iron.pomelo

import android.app.Activity
import android.app.ProgressDialog
import io.github.keep2iron.pomelo.dialog.ProgressDialogController
import org.reactivestreams.Subscription
import java.lang.ref.WeakReference
import io.reactivex.disposables.Disposable

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2017/12/27 17:46
 */
abstract class ProgressDialogSubscriber<T>(
    activity: Activity,
    private val controller: ProgressDialogController
) : AndroidSubscriber<T>() {
    private var activityRef = WeakReference<Activity>(activity)

    override fun onSubscribe(disposable: Disposable) {
        super.onSubscribe(disposable)
        val act = activityRef.get()
        if (act != null) {
            controller.onDialogShow(act, this as AndroidSubscriber<Any>)
        }
    }

    override fun onSubscribe(subscription: Subscription) {
        super.onSubscribe(subscription)
        val act = activityRef.get()
        if (act != null) {
            controller.onDialogShow(act, this as AndroidSubscriber<Any>)
        }
    }

    override fun onNext(t: T) {
        super.onNext(t)
        controller.onDialogDismiss()
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        controller.onDialogDismiss()
    }

    override fun onComplete() {
        super.onComplete()
        controller.onDialogDismiss()
    }
}
