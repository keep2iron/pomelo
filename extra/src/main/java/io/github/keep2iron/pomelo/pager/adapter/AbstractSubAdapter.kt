package io.github.keep2iron.pomelo.pager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper

/**
 * Item click will invoke
 */
typealias OnItemClickListener = (position: Int, view: View, itemView: View) -> Unit

abstract class AbstractSubAdapter(
    val viewType: Int,
    val cacheMaxViewCount: Int
) : DelegateAdapter.Adapter<RecyclerViewHolder>() {
    override fun onCreateLayoutHelper(): LayoutHelper = LinearLayoutHelper()
    private var listenerMap = SparseArrayCompat<OnItemClickListener>()

    /**
     * 获取布局的layout id
     *
     * @return layout id
     */
    @LayoutRes
    abstract fun onInflateLayoutId(
        parent: ViewGroup,
        viewType: Int
    ): Int

    /**
     *
     * @param holder
     * @param position
     */
    abstract fun render(
        holder: RecyclerViewHolder,
        position: Int
    )

    protected lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    private fun findViewHolder(view: View): RecyclerView.ViewHolder {
        requireNotNull(view.parent) { "View $view can't found RecyclerView.ViewHolder" }
        val layoutParams = view.layoutParams

        return if (layoutParams is RecyclerView.LayoutParams) {
            recyclerView.getChildViewHolder(view)
        } else {
            findViewHolder(view.parent as View)
        }
    }

    private val internalClickListener = View.OnClickListener {
        val listener = listenerMap[it.id]
//        val viewHolder = it.getTag(R.id.pomelo_view_holder) as RecyclerViewHolder

        val viewHolder = findViewHolder(it)

        listener?.invoke(
            recyclerView.getChildAdapterPosition(viewHolder.itemView),
            it,
            viewHolder.itemView
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(onInflateLayoutId(parent, viewType), parent, false)
        val holder = RecyclerViewHolder(view)

        for (i in 0 until listenerMap.size()) {
            val key = listenerMap.keyAt(i)
            val listener = listenerMap.get(key)
            if (key == -1 && listener != null) {
//                holder.itemView.setTag(R.id.pomelo_view_holder, holder)
                holder.itemView.setOnClickListener(internalClickListener)

            } else if (key > 0 && listener != null) {
                val childView = holder.itemView.findViewById<View>(key)
//                childView.setTag(R.id.pomelo_view_holder, holder)
                childView.setOnClickListener(internalClickListener)
            }
        }
        return holder
    }

    /**
     * [id]不传默认为-1则该listener添加到rootItem下面
     */
    fun setOnItemClickListener(@IdRes id: Int = -1, listener: OnItemClickListener) {
        listenerMap.put(id, listener)
    }

    override fun onBindViewHolder(
        holder: RecyclerViewHolder,
        position: Int
    ) {
        render(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getItemCount(): Int = 1
}