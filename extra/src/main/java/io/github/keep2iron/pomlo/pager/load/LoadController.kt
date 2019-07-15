package io.github.keep2iron.pomlo.pager.load

import android.view.View
import io.github.keep2iron.pomlo.pager.LoadMore
import io.github.keep2iron.pomlo.pager.LoadMoreAble
import io.github.keep2iron.pomlo.pager.Refreshable
import java.lang.IllegalArgumentException

class LoadController(
        private val loadMoreAble: LoadMoreAble,
        private val refreshAble: Refreshable,
        private val loadListener: LoadListener
) : LoadMoreAble, Refreshable {

    internal val pager: Pager = Pager(loadListener.defaultValue())

    internal fun setup() {
        loadMoreAble.setOnLoadMoreListener {
            refreshAble.setRefreshEnable(false)
            loadListener.onLoad(this, pager.value)
        }
        loadMoreAble.setLoadMoreEnable(true)

        refreshAble.setOnRefreshListener {
            pager.reset()
            loadMoreAble.setLoadMoreEnable(false)
            loadListener.onLoad(this, pager)
        }
    }

    fun intInc() {
        (pager.value as Int).inc()
    }

    override fun showLoadMoreComplete() {
        loadMoreAble.showLoadMoreComplete()
    }

    override fun showLoadMoreEnd() {
        loadMoreAble.showLoadMoreEnd()
    }

    override fun showLoadMoreFailed() {
        loadMoreAble.showLoadMoreFailed()
    }

    override fun setLoadMoreEnable(isEnabled: Boolean) {
        loadMoreAble.setLoadMoreEnable(isEnabled)
    }

    override fun setRefreshEnable(isEnabled: Boolean) {
        refreshAble.setRefreshEnable(isEnabled)
    }

    override fun showRefreshComplete() {
        refreshAble.showRefreshComplete()
    }

    override fun refreshing() {
        refreshAble.refreshing()
    }

    override fun scrollToPosition(position: Int) {
        loadMoreAble.scrollToPosition(position)
    }

    override fun setOnLoadMoreListener(listener: (loadMore: LoadMore) -> Unit) {
        throw IllegalArgumentException("setOnLoadMoreListener is call in internal")
    }

    override fun setOnRefreshListener(listener: (refreshLayout: View) -> Unit) {
        throw IllegalArgumentException("setOnRefreshListener is call in internal")
    }
}