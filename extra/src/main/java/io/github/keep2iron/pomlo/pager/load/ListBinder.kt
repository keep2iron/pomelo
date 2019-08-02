package io.github.keep2iron.pomlo.pager.load

import android.view.View
import com.alibaba.android.vlayout.DelegateAdapter
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomlo.pager.adapter.MultiTypeListAdapter

class ListBinder(
  recyclerView: androidx.recyclerview.widget.RecyclerView,
  refreshLayout: View,
  loadMoreEnabled: Boolean = false
) : BaseBinder(recyclerView, refreshLayout, loadMoreEnabled) {

  private var adapters = arrayListOf<AbstractSubAdapter>()

  override fun onBindDelegateAdapter(delegateAdapter: DelegateAdapter) {
    delegateAdapter.addAdapters(
        adapters as List<DelegateAdapter.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>>
    )
  }

  override fun onBindViewPool(viewPool: androidx.recyclerview.widget.RecyclerView.RecycledViewPool) {
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