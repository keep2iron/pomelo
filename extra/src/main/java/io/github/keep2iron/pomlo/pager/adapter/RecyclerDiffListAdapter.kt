package io.github.keep2iron.pomlo.pager.adapter

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.github.keep2iron.pomlo.pager.LoadMore
import io.github.keep2iron.pomlo.pager.LoadMoreImpl

/**
 * for recyclerView
 */
open class RecyclerDiffListAdapter<T>(
    val data: ObservableList<T>,
    private val loadMoreClass: Class<out LoadMoreImpl> = LoadMoreImpl::class.java
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    private lateinit var loadMore: LoadMoreImpl

    fun addHeaderAdapter() {

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val constructor = loadMoreClass.getConstructor(RecyclerView::class.java)
        loadMore = constructor.newInstance(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return if (viewType == LoadMore.ITEM_TYPE) {
            RecyclerViewHolder(loadMore.onCreateView(parent, viewType))
        } else {
            RecyclerViewHolder(loadMore.onCreateView(parent, viewType))
        }
    }

    override fun getItemCount(): Int = data.size + 1

    override fun onBindViewHolder(viewHolder: RecyclerViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= data.size) {
            LoadMore.ITEM_TYPE
        } else {
            0
        }
    }
}