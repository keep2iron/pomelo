package io.github.keep2iron.pomlo.pager.adapter

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.keep2iron.pomlo.pager.R

typealias OnItemClickListener = (position: Int, view: View) -> Unit

abstract class AbstractSubAdapter(val viewType: Int,val  cacheMaxViewCount: Int) :
    RecyclerView.Adapter<RecyclerViewHolder>() {

    private var listenerMap = SparseArrayCompat<OnItemClickListener>()

//    private var viewType: Int = 0
//
//    private var cacheMaxViewCount = 5
//
//    constructor() : this() {
//        this.viewType = viewType
//        this.cacheMaxViewCount = cacheMaxViewCount
//    }

    /**
     * 获取布局的layout id
     *
     * @return layout id
     */
    @LayoutRes
    abstract fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int

    /**
     *
     * @param holder
     * @param position
     */
    abstract fun render(holder: RecyclerViewHolder, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context.applicationContext)
            .inflate(onInflateLayoutId(parent, viewType), parent, false)

        val holder = RecyclerViewHolder(view)

        for (i in 0 until listenerMap.size()) {
            val key = listenerMap.keyAt(i)
            val listener = listenerMap.get(key)
            if (key == -1 && listener != null) {
                holder.itemView.setOnClickListener {
                    listener.invoke(holder.getTag(R.id.pomelo_adapter_position), it)
                }
            } else if (key > 0 && listener != null) {
                holder.itemView.setOnClickListener {
                    listener.invoke(holder.getTag(R.id.pomelo_adapter_position), it)
                }
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

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.setTag(R.id.pomelo_adapter_position, position)
        render(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
}