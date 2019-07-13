package io.github.keep2iron.pomlo.pager.load

import android.databinding.ObservableList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.vlayout.VirtualLayoutManager
import io.github.keep2iron.pomlo.pager.LoadMore
import io.github.keep2iron.pomlo.pager.LoadMoreImpl
import io.github.keep2iron.pomlo.pager.SwipeRefreshAble
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomlo.pager.adapter.LoadMoreAbleAdapter
import io.github.keep2iron.pomlo.pager.adapter.MultiTypeListAdapter
import io.github.keep2iron.pomlo.pager.adapter.VLayoutLoadMoreAbleAdapter
import io.github.keep2iron.pomlo.pager.manager.WrapperVirtualLayoutManager

class MultipleTypeBinder(
    private val recyclerView: RecyclerView,
    private val refreshLayout: View,
    data: ObservableList<Any>
) {

    val adapter = MultiTypeListAdapter(data)

    private var viewPool: RecyclerView.RecycledViewPool? = null

    var vLayoutEnabled = false

    var useWrapperLayoutManager = false

    var loadMore: LoadMore? = null

    lateinit var loadListener: LoadListener

    inline fun <reified T> registerAdapter(subAdapter: AbstractSubAdapter) {
        adapter.registerAdapter<T>(subAdapter)
    }

    fun bind() {
        if (viewPool == null) {
            viewPool = RecyclerView.RecycledViewPool()
        }

        if (loadMore == null) {
            loadMore = LoadMoreImpl(recyclerView)
        }

        adapter.adapterMap.values.forEach {
            viewPool!!.setMaxRecycledViews(it.viewType, it.cacheMaxViewCount)
        }

        val layoutManager = if (!vLayoutEnabled) {
            val controller = LoadController(
                LoadMoreAbleAdapter(loadMore!!),
                SwipeRefreshAble(refreshLayout), loadListener
            )
            controller.setup()

            LinearLayoutManager(recyclerView.context.applicationContext)
        } else if (!useWrapperLayoutManager) {
            val controller = LoadController(
                VLayoutLoadMoreAbleAdapter(loadMore!!),
                SwipeRefreshAble(refreshLayout), loadListener
            )
            controller.setup()
            VirtualLayoutManager(recyclerView.context.applicationContext)
        } else {
            val controller = LoadController(
                VLayoutLoadMoreAbleAdapter(loadMore!!),
                SwipeRefreshAble(refreshLayout), loadListener
            )
            controller.setup()
            WrapperVirtualLayoutManager(recyclerView.context.applicationContext)
        }

        recyclerView.setRecycledViewPool(viewPool)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

}