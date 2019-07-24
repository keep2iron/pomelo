package io.github.keep2iron.pomlo.state

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import io.github.keep2iron.pomelo.pager.R
import java.util.*

enum class PageState {
    ORIGIN,
    EMPTY_DATA,
    NETWORK_ERROR,
    LOAD_ERROR,
    LOADING,
}

typealias ReloadListener = (PageState, View) -> Unit

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @version 1.1
 * @since 2018/03/08 17:26
 *
 * 状态管理layout，
 */
class PomeloPageStateLayout : FrameLayout {
    /**
     * 被状态管理的View
     */
    private lateinit var mOriginView: View

    private var pageState = PageState.ORIGIN

    private var views: EnumMap<PageState, View?> = EnumMap(PageState::class.java)

    private var duration = 500

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        if (attrs != null) {
            val array = resources.obtainAttributes(attrs, R.styleable.PomeloPageStateLayout)

            for (i in 0 until array.indexCount) {
                when (val index = array.getIndex(i)) {
                    R.styleable.PomeloPageStateLayout_pomelo_layout_load_error -> {
                        val loadError =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        views[PageState.LOAD_ERROR] = loadError
                    }
                    R.styleable.PomeloPageStateLayout_pomelo_layout_empty_data -> {
                        val emptyDataView =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        views[PageState.EMPTY_DATA] = emptyDataView
                    }
                    R.styleable.PomeloPageStateLayout_pomelo_layout_network_error -> {
                        val networkErrorView =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        views[PageState.NETWORK_ERROR] = networkErrorView
                    }
                    R.styleable.PomeloPageStateLayout_pomelo_layout_loading -> {
                        val loadingView =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        views[PageState.LOADING] = loadingView
                    }
                }
            }
            array.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount <= 0) {
            throw IllegalArgumentException(javaClass.simpleName + "'child count must > 0")
        } else if (childCount == 1) {
            mOriginView = getChildAt(0)
            mOriginView.visibility = View.GONE
            views[PageState.ORIGIN] = mOriginView
        }

        for ((state, view) in views) {
            if (state != PageState.ORIGIN && view != null && view.parent != this@PomeloPageStateLayout) {
                view.visibility = View.GONE
                addView(view, 0)
            }
        }

        initPageState(pageState)
    }

    fun setPageStateView(state: PageState, view: View) {
        views[state] = view
        onFinishInflate()
    }

    fun setPageStateView(state: PageState, @LayoutRes layoutId: Int) {
        val stateView = LayoutInflater.from(context.applicationContext).inflate(layoutId, this, false)
        views[state] = stateView
        onFinishInflate()
    }

    private fun animStateView(preState: PageState, showView: View) {
        val preSateView = views[preState]!!

        preSateView.animate().cancel()
        preSateView.visibility = View.VISIBLE
        preSateView.alpha = 1f
        preSateView.animate()
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    preSateView.visibility = View.GONE
                }

            }).setDuration(duration.toLong())
            .alpha(0f)
            .start()

        showView.animate().cancel()
        showView.alpha = 0f
        showView.visibility = View.VISIBLE
        showView.animate()
            .setListener(object : AnimatorListenerAdapter() {
            })
            .setDuration(duration.toLong())
            .alpha(1f)
            .start()
    }

    fun displayOriginView() {
        if (pageState != PageState.ORIGIN) {
            val preState = pageState
            pageState = PageState.ORIGIN
            animStateView(preState, mOriginView)
        }
    }

    fun displayNetworkError() {
        if (pageState != PageState.NETWORK_ERROR) {
            val preState = pageState
            pageState = PageState.NETWORK_ERROR
            val view = views[pageState] ?: throw IllegalArgumentException("you should add network error layout")
            animStateView(preState, view)
        }
    }

    fun displayEmptyData() {
        if (pageState != PageState.EMPTY_DATA) {
            val preState = pageState
            pageState = PageState.EMPTY_DATA
            val view = views[pageState] ?: throw IllegalArgumentException("you should add empty data layout")
            animStateView(preState, view)
        }
    }

    fun displayLoading() {
        if (pageState != PageState.LOADING) {
            val preState = pageState
            pageState = PageState.LOADING
            val view = views[pageState] ?: throw IllegalArgumentException("you should add loading layout")
            animStateView(preState, view)
        }
    }

    fun displayLoadError() {
        if (pageState != PageState.LOAD_ERROR) {
            val preState = pageState
            pageState = PageState.LOAD_ERROR
            val view = views[pageState] ?: throw IllegalArgumentException("you should add load error layout")
            animStateView(preState, view)
        }
    }

    /**
     * 设置初始化页面状态
     */
    fun initPageState(pageState: PageState) {
        this.pageState = pageState

        for ((key, view) in views) {
            if (key == pageState) {
                val stateView = view
                    ?: throw IllegalArgumentException("$pageState view not add you should add state layout id in your xml.")
                stateView.alpha = 1f
                stateView.visibility = View.VISIBLE
            } else {
                view?.let {
                    view.alpha = 0f
                    view.visibility = View.GONE
                }
            }
        }
    }

    fun setPageStateReloadListener(pageState: PageState, reloadListener: ReloadListener) {
        views[pageState]?.setOnClickListener {
            reloadListener(pageState, it)
        }
    }
}