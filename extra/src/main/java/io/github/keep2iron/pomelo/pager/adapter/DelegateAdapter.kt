package io.github.keep2iron.pomelo.pager.adapter

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class DelegateAdapter : RecyclerView.Adapter<RecyclerViewHolder>() {
  internal val innerAdapters = mutableListOf<AbstractSubAdapter>()

  private val innerDataObserverMap =
    HashMap<AbstractSubAdapter, DataObserver>()

  private val innerItemViewTypeMap =
    HashMap<Int, AbstractSubAdapter>()

  class DataObserver(
    private val curAdapterRef: WeakReference<RecyclerView.Adapter<out RecyclerView.ViewHolder>>,
    private val innerAdapters: List<RecyclerView.Adapter<out RecyclerView.ViewHolder>>,
    private val outerAdapter: DelegateAdapter
  ) : RecyclerView.AdapterDataObserver() {
    private fun findAdapterStartIndex(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>): Int {
      val startAdapterIndex = innerAdapters.indexOf(adapter)
      var startIndex = 0
      for (i in 0 until startAdapterIndex) {
        startIndex += innerAdapters[i].itemCount
      }
      return startIndex
    }

    override fun onChanged() {
      // Do nothing
      outerAdapter.notifyDataSetChanged()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
      val adapter = curAdapterRef.get() ?: return
      val startIndex = findAdapterStartIndex(adapter)
      outerAdapter.notifyItemRangeChanged(startIndex + positionStart, itemCount)
    }

    override fun onItemRangeInserted(
      positionStart: Int,
      itemCount: Int
    ) {
      val adapter = curAdapterRef.get() ?: return
      val startIndex = findAdapterStartIndex(adapter)
      outerAdapter.notifyItemRangeInserted(startIndex + positionStart, itemCount)
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) { // do nothing
      val adapter = curAdapterRef.get() ?: return
      val startIndex = findAdapterStartIndex(adapter)
      outerAdapter.notifyItemRangeRemoved(startIndex + positionStart, itemCount)
    }

    override fun onItemRangeMoved(
      fromPosition: Int,
      toPosition: Int,
      itemCount: Int
    ) { // do nothing
      val adapter = curAdapterRef.get() ?: return
      val startIndex = findAdapterStartIndex(adapter)
      outerAdapter.notifyItemMoved(startIndex + fromPosition, startIndex + toPosition)
    }
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    innerAdapters.forEach {
      val dataObserver = DataObserver(WeakReference(it), innerAdapters, this)
      innerDataObserverMap[it] = dataObserver
      it.onAttachedToRecyclerView(recyclerView)
      it.registerAdapterDataObserver(dataObserver)
    }
  }

  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    innerAdapters.forEach {
      val dataObserver = innerDataObserverMap[it]
      it.onDetachedFromRecyclerView(recyclerView)
      if (dataObserver != null) {
        it.unregisterAdapterDataObserver(dataObserver)
      }
    }
  }

  private fun findRecyclerView(view: View): RecyclerView {
    var curView: ViewParent? = view.parent
    do {
      if (curView is RecyclerView) {
        break
      } else if (curView != null) {
        curView = curView.parent
      }
    } while (curView != null)
    check(curView is RecyclerView)

    return curView
  }

  private fun findViewHolder(recyclerView: RecyclerView, view: View): RecyclerView.ViewHolder {
    val layoutParams = view.layoutParams

    return if (layoutParams is RecyclerView.LayoutParams) {
      recyclerView.getChildViewHolder(view)
    } else {
      findViewHolder(recyclerView, view.parent as View)
    }
  }

  private val internalClickListener = View.OnClickListener {
    val recyclerView = findRecyclerView(it)
    val viewHolder = findViewHolder(recyclerView, it)
    val position = recyclerView.getChildAdapterPosition(
      viewHolder.itemView
    )
    var (startIndex, adapter) = findAdapterByPosition(position)

    //rootItem click
    val listener = if (viewHolder.itemView == it) {
      adapter.listenerMap[-1]
    } else {
      adapter.listenerMap[it.id]
    }

    listener?.invoke(
      position - startIndex,
      it,
      viewHolder.itemView
    )
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    var adapter = innerItemViewTypeMap[viewType]
      ?: throw IllegalArgumentException("viewType: $viewType adapter not find!")
    val viewHolder = adapter.onCreateViewHolder(parent, viewType)
    adapter = if (adapter is MultiTypeListAdapter) {
      adapter.getBindAdapterByViewType(viewType)
    } else {
      adapter
    }

    for (i in 0 until adapter.listenerMap.size()) {
      val key = adapter.listenerMap.keyAt(i)
      val listener = adapter.listenerMap.get(key)
      if (key == -1 && listener != null) {
        viewHolder.itemView.setOnClickListener(internalClickListener)

      } else if (key > 0 && listener != null) {
        val childView = viewHolder.itemView.findViewById<View>(key)
        childView.setOnClickListener(internalClickListener)
      }
    }
    return viewHolder
  }

  override fun getItemCount(): Int {
    return innerAdapters.asSequence()
      .map {
        it.itemCount
      }.reduce { acc, i ->
        acc + i
      }
  }

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    val (startIndex, adapter) = findAdapterByPosition(position)
    adapter.onBindViewHolder(holder, position - startIndex)
  }

  override fun getItemViewType(position: Int): Int {
    val (startIndex, adapter) = findAdapterByPosition(position)
    val itemViewType = adapter.getItemViewType(position - startIndex)
    innerItemViewTypeMap[itemViewType] = adapter
    return itemViewType
  }

  internal fun findAdapterByPosition(position: Int): Pair<Int, AbstractSubAdapter> {
    var startIndex = 0
    innerAdapters.forEach { adapter ->
      if (position >= startIndex && position < (startIndex + adapter.itemCount)) {
        return if (adapter is MultiTypeListAdapter) {
          (startIndex to adapter.getBindAdapterByPosition(position - startIndex))
        } else {
          (startIndex to adapter)
        }
      }
      startIndex += adapter.itemCount
    }
    throw IllegalArgumentException("itemView type is illegal!")
  }

  fun addAdapter(adapter: AbstractSubAdapter) {
    innerAdapters.add(adapter)
  }

  fun addAdapters(list: ArrayList<AbstractSubAdapter>) {
    innerAdapters.addAll(list)
  }
}
