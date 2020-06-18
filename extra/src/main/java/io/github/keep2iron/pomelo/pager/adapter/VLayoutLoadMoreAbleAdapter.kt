package io.github.keep2iron.pomelo.pager.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomelo.helper.LinearLayoutHelper
import io.github.keep2iron.pomelo.pager.LoadMore
import io.github.keep2iron.pomelo.pager.LoadMoreAble

class VLayoutLoadMoreAbleAdapter(private val loadMore: LoadMore) :
  AbstractSubAdapter(
    viewType = LoadMore.ITEM_TYPE,
    layoutHelper = LinearLayoutHelper()
  ),
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

  override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int = 0

  override fun render(holder: RecyclerViewHolder, position: Int) {
    loadMore.onBindViewHolder(holder, position)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerViewHolder {
    return RecyclerViewHolder(loadMore.onCreateView(parent, viewType))
  }

  override fun getItemCount(): Int = 1

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