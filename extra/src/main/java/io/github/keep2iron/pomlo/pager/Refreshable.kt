package io.github.keep2iron.pomlo.pager

import android.view.View

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/05/02 17:03
 */
interface Refreshable {
  fun setRefreshEnable(isEnabled: Boolean)

  fun refreshing()

  fun showRefreshComplete()

  fun setOnRefreshListener(listener: (refreshLayout: View) -> Unit)
}