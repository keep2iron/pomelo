package io.github.keep2iron.pomelo.databinding

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.0
 * @since 2017/11/17 15:25
 */
open class WeakRecyclerViewChangedAdapter<T>(mAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) :
    ObservableList.OnListChangedCallback<ObservableList<T>>() {

  private val weakRef = WeakReference(mAdapter)

  override fun onChanged(sender: ObservableList<T>) {
    val adapter = weakRef.get()
    adapter?.notifyDataSetChanged()
  }

  override fun onItemRangeChanged(
    sender: ObservableList<T>,
    positionStart: Int,
    itemCount: Int
  ) {
    val adapter = weakRef.get()
    adapter?.notifyItemRangeChanged(positionStart, itemCount)
  }

  override fun onItemRangeInserted(
    sender: ObservableList<T>,
    positionStart: Int,
    itemCount: Int
  ) {
    val adapter = weakRef.get()
    adapter?.notifyItemRangeInserted(positionStart, itemCount)
  }

  override fun onItemRangeMoved(
    sender: ObservableList<T>,
    fromPosition: Int,
    toPosition: Int,
    itemCount: Int
  ) {
    val adapter = weakRef.get()
    adapter?.notifyItemMoved(fromPosition, toPosition)
  }

  override fun onItemRangeRemoved(
    sender: ObservableList<T>,
    positionStart: Int,
    itemCount: Int
  ) {
    val adapter = weakRef.get()
    adapter?.notifyItemRangeRemoved(positionStart, itemCount)
  }
}
