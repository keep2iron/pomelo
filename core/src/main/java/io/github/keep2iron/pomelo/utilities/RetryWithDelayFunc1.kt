package io.github.keep2iron.pomelo.utilities

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * 网络请求重试
 */
class RetryWithDelayFunc1 @JvmOverloads constructor(
  private val mRetryTime: Int = 3,
  private val mDelayMillions: Long = 1000
) : Function<Observable<out Throwable>, Observable<*>> {
  private var time: Int = 0

  @Throws(Exception::class)
  override fun apply(@NonNull observable: Observable<out Throwable>): Observable<*> {
    return observable.flatMap(Function<Throwable, ObservableSource<*>> { throwable ->
      for (cls in mIgnoreThrowable) {
        if (throwable.javaClass == cls) {
          return@Function Observable.error<Any>(throwable)
        }
      }

      if (++time < mRetryTime) {
        Observable.timer(mDelayMillions, TimeUnit.MILLISECONDS)
      } else {
        Observable.error<Any>(throwable)
      }
    })
  }

  companion object {

    private val mIgnoreThrowable = ArrayList<Class<out Throwable>>()

    fun init(vararg clazz: Class<out Throwable>) {
      if (clazz != null) {
        for (cls in clazz) {
          mIgnoreThrowable.add(cls)
        }
      }
    }
  }
}