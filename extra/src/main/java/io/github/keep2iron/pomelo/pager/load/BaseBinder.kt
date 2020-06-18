package io.github.keep2iron.pomelo.pager.load

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import io.github.keep2iron.pomelo.helper.LayoutHelper
import io.github.keep2iron.pomelo.helper.LinearLayoutHelper
import io.github.keep2iron.pomelo.pager.LoadMore
import io.github.keep2iron.pomelo.pager.Refreshable
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.DelegateAdapter
import io.github.keep2iron.pomelo.pager.adapter.VLayoutLoadMoreAbleAdapter

abstract class BaseBinder(
    private val recyclerView: RecyclerView
) {

    private var viewPool: RecycledViewPool? = null

    private lateinit var loadListener: LoadListener

    private var loadMore: LoadMore? = null

    private var refreshable: Refreshable? = null

    lateinit var loadController: LoadController

    internal var spanCount = 1

    fun setLoadListener(loadListener: LoadListener): BaseBinder {
        this.loadListener = loadListener
        return this
    }

    fun setRefreshable(refreshable: Refreshable?): BaseBinder {
        this.refreshable = refreshable
        return this
    }

    fun setLoadMore(loadMore: LoadMore?): BaseBinder {
        this.loadMore = loadMore
        return this
    }

    fun setViewPool(viewPool: RecycledViewPool): BaseBinder {
        this.viewPool = viewPool
        return this
    }

    protected abstract fun onBindDelegateAdapter(context: Context,recyclerView:RecyclerView, delegateAdapter: DelegateAdapter):RecyclerView.LayoutManager

    protected abstract fun onBindViewPool(viewPool: RecycledViewPool)

    fun bind(): BaseBinder {
        val viewPool = viewPool ?: RecycledViewPool()

        onBindViewPool(viewPool)
        recyclerView.setRecycledViewPool(viewPool)

        val adapter = DelegateAdapter()
        val loadMoreAdapter = if (loadMore != null) {
            VLayoutLoadMoreAbleAdapter(loadMore!!)
        } else {
            null
        }

        if (refreshable != null || (loadMoreAdapter != null)) {
            loadController = LoadController(
                loadMoreAdapter,
                refreshable,
                loadListener
            )
        }

        //if enable refresh
        if (refreshable != null) {
            loadController.setupRefresh()
        }

        val context = recyclerView.context.applicationContext
        val layoutManager = onBindDelegateAdapter(context,recyclerView,adapter)

        //if enable load more
        if (loadMoreAdapter != null) {
            loadController.setupLoadMore()
            adapter.addAdapter(loadMoreAdapter)
            viewPool.setMaxRecycledViews(LoadMore.ITEM_TYPE, 1)
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        return this
    }

    /**
     * 调用加载
     */
    fun load() {
        loadListener.onLoad(loadController, loadController.pagerValue, false)
    }

    /**
     * 重置分页数 重新进行默认加载
     */
    fun reload() {
        loadController.reset()
        loadListener.onLoad(loadController, loadController.pagerValue, false)
    }
}