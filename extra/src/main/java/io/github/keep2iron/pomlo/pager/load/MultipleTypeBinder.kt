package io.github.keep2iron.pomlo.pager.load

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import io.github.keep2iron.pomlo.pager.Refreshable
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomlo.pager.adapter.MultiTypeListAdapter

open class MultipleTypeBinder(
  data: ObservableList<Any>,
  recyclerView: RecyclerView,
  refreshable: Refreshable,
  loadMoreEnabled: Boolean = false
) : BaseBinder(recyclerView, refreshable, loadMoreEnabled) {

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