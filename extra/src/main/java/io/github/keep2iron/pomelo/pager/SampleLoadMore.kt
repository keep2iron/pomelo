package io.github.keep2iron.pomelo.pager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @date 2018/10/17
 *
 * 加载更多adapter的父类 后面一些样式上的问题 可以直接继承该adapter
 *
 * @param isFirstEnableLoadMore 是否允许第一次默认进行加载更多
 */
open class SampleLoadMore(
    isFirstEnableLoadMore: Boolean = true
) : LoadMore {

    private var recyclerViewRef: WeakReference<RecyclerView>? = null

    private lateinit var adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>

    var mOnLoadMoreListener: ((adapter: LoadMore) -> Unit)? = null

    override fun setOnLoadMoreListener(listener: (loadMore: LoadMore) -> Unit) {
        mOnLoadMoreListener = listener
    }

    var isEnableLoadMore: Boolean = isFirstEnableLoadMore
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    /**
     * load more state
     */
    private var showState = STATE_DEFAULT

    /**
     * 设置距离底部还有preLoadNumber个item就进行预加载
     */
    var preLoadNumber = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException(
                    "you set $value for preLoadNumber and it is illegal,you must set it >= 0"
                )
            }
            field = value
        }

    override fun scrollToPosition(position: Int) {
        val recyclerView = recyclerViewRef?.get()
        recyclerView?.post {
            recyclerView.scrollToPosition(position)
        }
    }

    override fun setLoadMoreEnable(isEnabled: Boolean) {
        isEnableLoadMore = isEnabled
        showState = STATE_DEFAULT
        notifyItemChanged(0)
    }

    override fun getItemCount(): Int {
        return 1
    }

    open fun onCreateViewLayoutId(): Int {
        return R.layout.pomelo_item_load_more
    }

    override fun onCreateView(
        viewParent: ViewGroup,
        viewType: Int
    ): View {
        if (viewType == LoadMore.ITEM_TYPE) {
            val itemView =
                LayoutInflater.from(viewParent.context)
                    .inflate(onCreateViewLayoutId(), viewParent, false)
            itemView.findViewById<View>(R.id.pomelo_load_more_load_fail_view)
                .setOnClickListener {
                    mOnLoadMoreListener?.invoke(this@SampleLoadMore)
                    showLoading()
                }
            return itemView
        }
        throw IllegalArgumentException("not support viewType = $viewType")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val itemView = holder.itemView
        if (isEnableLoadMore) {
            if (showState == STATE_DEFAULT) {
                showState = STATE_LOADING
                holder.itemView.post {
                    mOnLoadMoreListener?.invoke(this@SampleLoadMore)
                }
            }
            when (showState) {
                STATE_DEFAULT -> {
                    this.visibleLoading(itemView, false)
                    this.visibleLoadFail(itemView, false)
                    this.visibleLoadEnd(itemView, false)
                }
                STATE_LOADING -> {
                    this.visibleLoading(itemView, true)
                    this.visibleLoadFail(itemView, false)
                    this.visibleLoadEnd(itemView, false)
                }
                STATE_LOAD_MORE_FAILED -> {
                    this.visibleLoading(itemView, false)
                    this.visibleLoadFail(itemView, true)
                    this.visibleLoadEnd(itemView, false)
                }
                STATE_LOAD_MORE_END -> {
                    this.visibleLoading(itemView, false)
                    this.visibleLoadFail(itemView, false)
                    this.visibleLoadEnd(itemView, true)
                }
            }
        } else {
            when (showState) {
                STATE_LOAD_MORE_FAILED -> {
                    this.visibleLoading(itemView, false)
                    this.visibleLoadFail(itemView, true)
                    this.visibleLoadEnd(itemView, false)
                }
                STATE_LOAD_MORE_END -> {
                    this.visibleLoading(itemView, false)
                    this.visibleLoadFail(itemView, false)
                    this.visibleLoadEnd(itemView, true)
                }
                else -> {
                    this.visibleLoading(itemView, false)
                    this.visibleLoadFail(itemView, false)
                    this.visibleLoadEnd(itemView, false)
                }
            }
        }
    }

    private fun visibleLoading(
        itemView: View,
        visible: Boolean
    ) {
        itemView.findViewById<View>(R.id.pomelo_load_more_loading_view)
            .visibility =
            if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun visibleLoadFail(
        itemView: View,
        visible: Boolean
    ) {
        itemView.findViewById<View>(R.id.pomelo_load_more_load_fail_view)
            .visibility =
            if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun visibleLoadEnd(
        itemView: View,
        visible: Boolean
    ) {
        itemView.findViewById<View>(R.id.pomelo_load_more_load_end_view)
            .visibility =
            if (visible) View.VISIBLE else View.INVISIBLE
    }

    override fun showLoadMoreFailed() {
        showState = STATE_LOAD_MORE_FAILED
        notifyItemChanged(0)
    }

    override fun showLoadMoreEnd() {
        isEnableLoadMore = false
        showState = STATE_LOAD_MORE_END
        notifyItemChanged(0)
    }

    private fun showLoading() {
        showState = STATE_LOADING
        notifyItemChanged(0)
    }

    override fun showLoadMoreComplete() {
        showState = STATE_DEFAULT
        notifyItemChanged(0)
    }

    override fun notifyItemChanged(position: Int) {
        adapter.notifyItemChanged(position, null)
    }

    override fun attachAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
        this.adapter = adapter
    }

    override fun attachRecyclerView(recyclerView: RecyclerView) {
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)

                if (showState == STATE_LOADING || !isEnableLoadMore) {
                    return
                }

                if (newState == RecyclerView.SCROLL_STATE_SETTLING ||
                    newState == RecyclerView.SCROLL_STATE_DRAGGING
                ) {
                    val isBottom: Boolean
                    val layoutManager = recyclerView.layoutManager
                    isBottom = when (layoutManager) {
                        is androidx.recyclerview.widget.LinearLayoutManager -> {
                            layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1 - preLoadNumber
                        }
                        is androidx.recyclerview.widget.StaggeredGridLayoutManager -> {
                            val into = IntArray(layoutManager.spanCount)
                            layoutManager.findLastVisibleItemPositions(into)
                            last(into) >= layoutManager.getItemCount() - 1
                        }
                        else -> (layoutManager as androidx.recyclerview.widget.GridLayoutManager).findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1
                    }
                    if (isBottom && isEnableLoadMore && showState == STATE_DEFAULT) {
                        showLoading()
                        mOnLoadMoreListener?.invoke(this@SampleLoadMore)
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(onScrollListener)

        this.recyclerViewRef = WeakReference(recyclerView)
    }

    companion object {
        private const val STATE_DEFAULT = 1
        private const val STATE_LOADING = 2
        private const val STATE_LOAD_MORE_FAILED = 3
        private const val STATE_LOAD_MORE_END = 4

        /**
         * 取到最后的一个节点
         */
        private fun last(lastPositions: IntArray): Int {
            return lastPositions.max()
                ?: lastPositions[0]
        }
    }
}