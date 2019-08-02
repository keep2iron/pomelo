package io.github.keep2iron.pomelo.dialog

import android.app.Activity
import io.github.keep2iron.pomelo.AndroidSubscriber

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @date 2019/1/8
 */
interface ProgressDialogController {

  /**
   * 控制dialog 让其显示
   */
  fun <T> onDialogShow(
    activity: Activity,
    subscriber: AndroidSubscriber<T>
  )

  /**
   * 控制dialog 让其隐藏
   */
  fun onDialogDismiss()

}