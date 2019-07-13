package io.github.keep2iron.pomlo.pager

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View


/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/05/02 17:02
 */
class SwipeRefreshAble(layout: View) : Refreshable {

    private lateinit var listener: (refreshLayout: View) -> Unit

    override fun setOnRefreshListener(listener: (refreshLayout: View) -> Unit) {
        this.listener = listener
    }

    private val layoutView = layout as SwipeRefreshLayout

    init {
        layoutView.setOnRefreshListener {
            listener(layout)
        }
    }

    override fun setRefreshEnable(isEnabled: Boolean) {
        layoutView.isEnabled = isEnabled
    }

    override fun refreshing() {
        layoutView.isRefreshing = true
    }

    override fun showRefreshComplete() {
        layoutView.isRefreshing = false
    }
}