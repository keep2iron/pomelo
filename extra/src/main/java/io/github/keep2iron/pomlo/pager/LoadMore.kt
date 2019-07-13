package io.github.keep2iron.pomlo.pager

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.github.keep2iron.pomlo.pager.LoadMoreAble

interface LoadMore : LoadMoreAble {

    companion object {
        const val ITEM_TYPE = 10001
    }

    fun getItemCount(): Int

    fun onCreateView(viewParent: ViewGroup, viewType: Int): View

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    fun notifyItemChanged(position: Int)

}