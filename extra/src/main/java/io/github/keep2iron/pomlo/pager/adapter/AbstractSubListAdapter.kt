package io.github.keep2iron.pomlo.pager.adapter

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomlo.databinding.ObservableOnListChangeAdapter

abstract class AbstractSubListAdapter<T>(
  val data: ObservableList<T>,
  viewType: Int = 0,
  cacheMaxViewCount: Int = 1
) : AbstractSubAdapter(viewType, cacheMaxViewCount) {

//  init {
//    data.addOnListChangedCallback(ObservableOnListChangeAdapter<T>(this))
//  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    data.addOnListChangedCallback(ObservableOnListChangeAdapter<T>(this))
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