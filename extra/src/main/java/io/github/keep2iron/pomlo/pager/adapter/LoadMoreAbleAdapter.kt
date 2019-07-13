package io.github.keep2iron.pomlo.pager.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.github.keep2iron.pomlo.pager.LoadMore
import io.github.keep2iron.pomlo.pager.LoadMoreAble

class LoadMoreAbleAdapter(private val loadMore: LoadMore) : RecyclerView.Adapter<RecyclerViewHolder>() , LoadMoreAble {

    override fun setOnLoadMoreListener(listener: (loadMore: LoadMore) -> Unit) {
        loadMore.setOnLoadMoreListener(listener)
    }

    override fun onCreateViewHolder(viewParent: ViewGroup, viewType: Int): RecyclerViewHolder =
        RecyclerViewHolder(loadMore.onCreateView(viewParent, viewType))

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
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
}