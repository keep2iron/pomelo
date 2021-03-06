package io.github.keep2iron.pomelo.pager.adapter

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomelo.databinding.WeakRecyclerViewChangedAdapter
import io.github.keep2iron.pomelo.helper.LayoutHelper
import io.github.keep2iron.pomelo.helper.LinearLayoutHelper

abstract class AbstractSubListAdapter<T>(
  val data: ObservableList<T>,
  viewType: Int = INVALID_VIEW_TYPE,
  cacheMaxViewCount: Int = 10,
  layoutHelper: LayoutHelper = LinearLayoutHelper()
) : AbstractSubAdapter(viewType, cacheMaxViewCount,layoutHelper) {

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    data.addOnListChangedCallback(WeakRecyclerViewChangedAdapter(this))
  }

  override fun render(
    holder: RecyclerViewHolder,
    position: Int
  ) {
  }

  override fun onBindViewHolder(
    holder: RecyclerViewHolder,
    position: Int
  ) {
    super.onBindViewHolder(holder, position)
    render(holder, data[position], position)
  }

  abstract fun render(
    holder: RecyclerViewHolder,
    item: T,
    position: Int
  )

  override fun getItemCount(): Int = data.size

}