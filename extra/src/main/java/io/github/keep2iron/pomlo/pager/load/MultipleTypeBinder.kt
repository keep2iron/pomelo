package io.github.keep2iron.pomlo.pager.load

import android.view.View
import androidx.databinding.ObservableList
import com.alibaba.android.vlayout.DelegateAdapter
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomlo.pager.adapter.MultiTypeListAdapter

class MultipleTypeBinder(
  data: ObservableList<Any>,
  recyclerView: androidx.recyclerview.widget.RecyclerView,
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

  override fun onBindViewPool(viewPool: androidx.recyclerview.widget.RecyclerView.RecycledViewPool) {
    adapter.adapterMap.values.forEach { adapter ->
      viewPool.setMaxRecycledViews(adapter.viewType, adapter.cacheMaxViewCount)
    }
  }
}