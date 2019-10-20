package io.github.keep2iron.pomelo.pager.load

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import io.github.keep2iron.pomelo.pager.LoadMore
import io.github.keep2iron.pomelo.pager.Refreshable
import io.github.keep2iron.pomelo.pager.SampleLoadMore
import io.github.keep2iron.pomelo.pager.adapter.VLayoutLoadMoreAbleAdapter
import io.github.keep2iron.pomelo.pager.manager.WrapperVirtualLayoutManager

abstract class BaseBinder(
    private val recyclerView: RecyclerView,
    private val refreshable: Refreshable,
    private val loadMoreEnabled: Boolean
) {

    private var viewPool: RecycledViewPool? = null

    private var loadMore: LoadMore? = null

    private lateinit var loadListener: LoadListener

    lateinit var loadController: LoadController

    lateinit var layoutManager:VirtualLayoutManager

    fun setLoadListener(loadListener: LoadListener): BaseBinder {
        this.loadListener = loadListener
        return this
    }

    fun setLoadMore(loadMore: LoadMore): BaseBinder {
        this.loadMore = loadMore
        return this
    }

    fun setViewPool(viewPool: RecycledViewPool): BaseBinder {
        this.viewPool = viewPool
        return this
    }

    protected abstract fun onBindDelegateAdapter(delegateAdapter: DelegateAdapter)

    protected abstract fun onBindViewPool(viewPool: RecycledViewPool)

    fun bind(): BaseBinder {
        if (viewPool == null) {
            viewPool = RecycledViewPool()
        }

        layoutManager = WrapperVirtualLayoutManager(recyclerView.context.applicationContext)

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
            refreshable, loadListener
        )

        loadController.setupRefresh()

        if (loadMoreEnabled) {
            loadController.setupLoadMore()

            adapter.addAdapter(VLayoutLoadMoreAbleAdapter(loadMore!!))
            viewPool!!.setMaxRecycledViews(LoadMore.ITEM_TYPE, 1)
        }
        recyclerView.adapter = adapter
        adapter.onAttachedToRecyclerView(recyclerView)
        for (i in 0 until adapter.adaptersCount) {
            adapter.findAdapterByIndex(i).onAttachedToRecyclerView(recyclerView)
        }

        return this
    }

    fun load() {
        loadListener.onLoad(loadController, loadController.pagerValue(), false)
    }
}