package io.github.keep2iron.pomelo.pager.adapter

import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomelo.pager.LoadMore
import io.github.keep2iron.pomelo.pager.SampleLoadMore

/**
 * for recyclerView
 */
open class RecyclerDiffListAdapter<T>(
  val data: ObservableList<T>,
  private val loadMoreClass: Class<out SampleLoadMore> = SampleLoadMore::class.java
) : RecyclerView.Adapter<RecyclerViewHolder>() {

  private lateinit var loadMore: SampleLoadMore

  fun addHeaderAdapter() {

  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    val constructor =
      loadMoreClass.getConstructor(RecyclerView::class.java)
    loadMore = constructor.newInstance(recyclerView)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerViewHolder {
    return if (viewType == LoadMore.ITEM_TYPE) {
      RecyclerViewHolder(loadMore.onCreateView(parent, viewType))
    } else {
      RecyclerViewHolder(loadMore.onCreateView(parent, viewType))
    }
  }

  override fun getItemCount(): Int = data.size + 1

  override fun onBindViewHolder(
    viewHolder: RecyclerViewHolder,
    position: Int
  ) {
  }

  override fun getItemViewType(position: Int): Int {
    return if (position >= data.size) {
      LoadMore.ITEM_TYPE
    } else {
      0
    }
  }
}