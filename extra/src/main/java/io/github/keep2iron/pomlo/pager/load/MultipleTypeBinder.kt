package io.github.keep2iron.pomlo.pager.load

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.vlayout.DelegateAdapter
import io.github.keep2iron.pomlo.pager.LoadMore
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomlo.pager.adapter.MultiTypeListAdapter

class MultipleTypeBinder(
    data: ObservableList<Any>,
    recyclerView: RecyclerView,
    refreshLayout: View,
    loadMoreEnabled: Boolean = false
) : BaseBinder(recyclerView, refreshLayout, loadMoreEnabled) {

    val adapter = MultiTypeListAdapter(data)

    inline fun <reified T> addSubAdapter(subAdapter: AbstractSubAdapter): MultipleTypeBinder {
        adapter.registerAdapter<T>(subAdapter)
        return this
    }

    override fun onBindDelegateAdapter(delegateAdapter: DelegateAdapter) {
        delegateAdapter.addAdapter(adapter)
    }

    override fun onBindViewPool(viewPool: RecyclerView.RecycledViewPool) {
        adapter.adapterMap.values.forEach { adapter ->
            viewPool.setMaxRecycledViews(adapter.viewType, adapter.cacheMaxViewCount)
        }
    }
}