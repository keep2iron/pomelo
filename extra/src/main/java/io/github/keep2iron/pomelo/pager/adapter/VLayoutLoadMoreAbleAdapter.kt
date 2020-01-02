package io.github.keep2iron.pomelo.pager.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import io.github.keep2iron.pomelo.pager.LoadMore
import io.github.keep2iron.pomelo.pager.LoadMoreAble

class VLayoutLoadMoreAbleAdapter(private val loadMore: LoadMore) :
    DelegateAdapter.Adapter<RecyclerViewHolder>(),
    LoadMoreAble {

  init {
    loadMore.attachAdapter(this)
  }

  override fun scrollToPosition(position: Int) {
    loadMore.scrollToPosition(position)
  }

  override fun setOnLoadMoreListener(listener: (loadMore: LoadMore) -> Unit) {
    loadMore.setOnLoadMoreListener(listener)
  }

  override fun onCreateViewHolder(
    viewParent: ViewGroup,
    viewType: Int
  ): RecyclerViewHolder {
    return RecyclerViewHolder(loadMore.onCreateView(viewParent, viewType))
  }

  override fun getItemCount(): Int = 1

  override fun onCreateLayoutHelper(): LayoutHelper = SingleLayoutHelper()

  override fun onBindViewHolder(
    holder: RecyclerViewHolder,
    position: Int
  ) {
    loadMore.onBindViewHolder(holder, position)
  }

  override fun setLoadMoreEnable(isEnabled: Boolean) {
    loadMore.setLoadMoreEnable(isEnabled)
  }

  override fun showLoadMoreComplete() {
    loadMore.showLoadMoreComplete()
  }

  override fun showLoadMoreFailed() {
    loadMore.showLoadMoreFailed()
  }

  override fun showLoadMoreEnd() {
    loadMore.showLoadMoreEnd()
  }

  override fun getItemViewType(position: Int): Int = LoadMore.ITEM_TYPE

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    loadMore.attachRecyclerView(recyclerView)
  }
}