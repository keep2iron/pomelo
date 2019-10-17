package io.github.keep2iron.pomelo.pager.load

import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import io.github.keep2iron.pomelo.pager.Refreshable
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.MultiTypeListAdapter

class ListBinder(
    recyclerView: RecyclerView,
    refreshable: Refreshable,
    loadMoreEnabled: Boolean = false
) : BaseBinder(recyclerView, refreshable, loadMoreEnabled) {

    private var adapters = arrayListOf<AbstractSubAdapter>()

    override fun onBindDelegateAdapter(delegateAdapter: DelegateAdapter) {
        val list = ArrayList<DelegateAdapter.Adapter<out RecyclerView.ViewHolder>>(adapters)
        delegateAdapter.addAdapters(list)
    }

    override fun onBindViewPool(viewPool: RecyclerView.RecycledViewPool) {
        adapters.forEach { adapter ->
            viewPool.setMaxRecycledViews(adapter.viewType, adapter.cacheMaxViewCount)
        }
    }

    fun addSubAdapter(multipleTypeAdapter: MultiTypeListAdapter): ListBinder {
        adapters.add(multipleTypeAdapter)
        return this
    }

    fun addSubAdapter(subAdapter: AbstractSubAdapter): ListBinder {
        adapters.add(subAdapter)
        return this
    }
}