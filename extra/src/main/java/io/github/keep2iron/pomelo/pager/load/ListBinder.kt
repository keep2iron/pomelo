package io.github.keep2iron.pomelo.pager.load

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomelo.PomeloLinearLayoutManager
import io.github.keep2iron.pomelo.helper.GridLayoutHelper
import io.github.keep2iron.pomelo.pager.LoadMore
import io.github.keep2iron.pomelo.pager.Refreshable
import io.github.keep2iron.pomelo.pager.SampleLoadMore
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.DelegateAdapter
import io.github.keep2iron.pomelo.pager.adapter.MultiTypeListAdapter
import java.lang.IllegalArgumentException

class ListBinder(
  recyclerView: RecyclerView,
  refreshable: Refreshable? = null,
  loadMoreAble: LoadMore? = SampleLoadMore()
) : BaseBinder(recyclerView) {

  init {
    setRefreshable(refreshable)
    setLoadMore(loadMoreAble)
  }

  private var adapters = arrayListOf<AbstractSubAdapter>()

  private var layoutManager: RecyclerView.LayoutManager? = null

  override fun onBindDelegateAdapter(
    context: Context,
    recyclerView: RecyclerView,
    delegateAdapter: DelegateAdapter
  ): RecyclerView.LayoutManager {
    delegateAdapter.addAdapters(adapters)

    return if (spanCount > 1) {
      return when (val finalLayoutManager = layoutManager) {
        null -> {
          GridLayoutManager(context, spanCount)
        }
        is GridLayoutManager -> {
          finalLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
              val (_, adapter) = delegateAdapter.findAdapterByPosition(position)

              return if (adapter.layoutHelper is GridLayoutHelper) {
                adapter.layoutHelper.spanCount
              } else {
                spanCount
              }
            }
          }
          finalLayoutManager
        }
        else -> {
          throw IllegalArgumentException("spanCount is > 1,layoutManager must be set a GridLayoutManager.")
        }
      }
    } else {
      layoutManager ?: PomeloLinearLayoutManager(context)
    }
  }

  override fun onBindViewPool(viewPool: RecyclerView.RecycledViewPool) {
    adapters.forEach { adapter ->
      if (adapter is MultiTypeListAdapter) {
        adapter.adapterMap.values.forEach { itemAdapter ->
          viewPool.setMaxRecycledViews(
            itemAdapter.viewType,
            itemAdapter.cacheMaxViewCount
          )
        }
      } else {
        viewPool.setMaxRecycledViews(adapter.viewType, adapter.cacheMaxViewCount)
      }
    }
  }

  fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): ListBinder {
    this.layoutManager = layoutManager
    return this
  }

  fun setSpanCount(spanCount: Int): ListBinder {
    check(spanCount > 1) { "spanCount value must > 1." }
    this.spanCount = spanCount
    return this
  }

  fun addSubAdapter(
    multipleTypeAdapter: MultiTypeListAdapter
  ): ListBinder {
    adapters.add(multipleTypeAdapter)
    return this
  }

  fun addSubAdapter(
    subAdapter: AbstractSubAdapter
  ): ListBinder {
    adapters.add(subAdapter)
    return this
  }
}