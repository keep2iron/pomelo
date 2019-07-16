package io.github.keep2iron.pomlo.pager.load

import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.vlayout.DelegateAdapter
import io.github.keep2iron.pomlo.pager.LoadMore
import io.github.keep2iron.pomlo.pager.SampleLoadMore
import io.github.keep2iron.pomlo.pager.SwipeRefreshAble
import io.github.keep2iron.pomlo.pager.adapter.VLayoutLoadMoreAbleAdapter
import io.github.keep2iron.pomlo.pager.manager.WrapperVirtualLayoutManager

abstract class BaseBinder(
        private val recyclerView: RecyclerView,
        private val refreshLayout: View,
        private val loadMoreEnabled: Boolean
) {

    private var viewPool: RecyclerView.RecycledViewPool? = null

    private var loadMore: LoadMore? = null

    private lateinit var loadListener: LoadListener

    lateinit var loadController: LoadController

    fun setLoadListener(loadListener: LoadListener): BaseBinder {
        this.loadListener = loadListener
        return this
    }

    fun setLoadMore(loadMore: LoadMore): BaseBinder {
        this.loadMore = loadMore
        return this
    }

    protected abstract fun onBindDelegateAdapter(delegateAdapter: DelegateAdapter)

    protected abstract fun onBindViewPool(viewPool: RecyclerView.RecycledViewPool)

    fun bind(): BaseBinder {
        if (viewPool == null) {
            viewPool = RecyclerView.RecycledViewPool()
        }

        val layoutManager = WrapperVirtualLayoutManager(recyclerView.context.applicationContext)

        onBindViewPool(viewPool!!)
        recyclerView.setRecycledViewPool(viewPool)
        recyclerView.layoutManager = layoutManager

        val adapter = DelegateAdapter(layoutManager)
        onBindDelegateAdapter(adapter)

        if (loadMore == null) {
            loadMore = SampleLoadMore(recyclerView)
        }
        loadController = LoadController(
                VLayoutLoadMoreAbleAdapter(loadMore!!),
                SwipeRefreshAble(refreshLayout), loadListener
        )

        loadController.setupRefresh()

        if (loadMoreEnabled) {
            loadController.setupLoadMore()

            adapter.addAdapter(VLayoutLoadMoreAbleAdapter(loadMore!!))
            viewPool!!.setMaxRecycledViews(LoadMore.ITEM_TYPE, 1)
        }

        recyclerView.adapter = adapter

        return this
    }
}