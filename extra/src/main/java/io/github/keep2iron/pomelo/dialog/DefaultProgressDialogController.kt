package io.github.keep2iron.pomelo.dialog

import android.app.Activity
import android.app.ProgressDialog
import io.github.keep2iron.pomelo.AndroidSubscriber

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @date 2019/1/8
 */
class DefaultProgressDialogController(
  private val title: String,
  private val message: String,
  private val canCancelable: Boolean = true
) : ProgressDialogController {

  override fun <T> onDialogShow(activity: Activity, subscriber: AndroidSubscriber<T>) {
    this.dialog = ProgressDialog.show(activity, title, message)
    this.dialog?.apply {
      this.setCancelable(canCancelable)
      this.setCanceledOnTouchOutside(canCancelable)
      this.setOnCancelListener {
        subscriber.cancel()
      }
    }
  }

  private var dialog: ProgressDialog? = null

  override fun onDialogDismiss() {
    dialog?.dismiss()
  }

}