package io.github.keep2iron.pomelo.pager

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface LoadMore : LoadMoreAble {

  companion object {
    const val ITEM_TYPE = 10001
  }

  fun getItemCount(): Int

  fun onCreateView(
    viewParent: ViewGroup,
    viewType: Int
  ): View

  fun onBindViewHolder(
    holder: RecyclerView.ViewHolder,
    position: Int
  )

  fun notifyItemChanged(position: Int)

  fun attachAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>)

  fun attachRecyclerView(recyclerView:RecyclerView)

}