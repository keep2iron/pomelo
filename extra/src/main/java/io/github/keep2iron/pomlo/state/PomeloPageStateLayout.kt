package io.github.keep2iron.pomlo.state

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import io.github.keep2iron.pomlo.pager.R
import java.util.*

enum class PageState {
    ORIGIN,
    NO_DATA,
    NO_NETWORK,
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
    private var mOriginView: View? = null
        set(value) {
            field = value
            views[PageState.ORIGIN] = value
        }
    /**
     * 无数据view
     */
    private var mNoDataView: View? = null
        set(value) {
            field = value
            value?.visibility = View.GONE
            views[PageState.NO_DATA] = value
        }
    /**
     * 无网络
     */
    private var mNoNetwork: View? = null
        set(value) {
            field = value
            value?.visibility = View.GONE
            views[PageState.NO_NETWORK] = value
        }
    /**
     * 加载失败
     */
    private var mLoadError: View? = null
        set(value) {
            field = value
            value?.visibility = View.GONE
            views[PageState.LOAD_ERROR] = value
        }
    /**
     * 正在加载
     */
    private var mLoadingView: View? = null
        set(value) {
            field = value
            value?.visibility = View.GONE
            views[PageState.LOADING] = value
        }

    private var pageState = PageState.ORIGIN

    private var views: EnumMap<PageState, View?> = EnumMap(PageState::class.java)

    private var duration = 500

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        if (attrs != null) {
            val array = resources.obtainAttributes(attrs, R.styleable.PomeloPageStateLayout)

            for (i in 0 until array.indexCount) {
                val index = array.getIndex(i)
                when (index) {
                    R.styleable.PomeloPageStateLayout_psl_layout_loadError -> {
                        mLoadError =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
//                        mLoadError?.
                    }
                    R.styleable.PomeloPageStateLayout_psl_layout_noData -> {
                        mNoDataView =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        mNoDataView?.visibility = View.GONE
                    }
                    R.styleable.PomeloPageStateLayout_psl_layout_noNetwork -> {
                        mNoNetwork =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        mNoNetwork?.visibility = View.GONE
                    }
                    R.styleable.PomeloPageStateLayout_psl_layout_loading -> {
                        mLoadingView =
                            LayoutInflater.from(getContext()).inflate(array.getResourceId(index, -1), this, false)
                        mLoadingView?.visibility = View.GONE
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
        }

        mOriginView = getChildAt(0)

        mLoadError?.apply {
            addView(this, 0)
            visibility = View.GONE
        }

        mNoDataView?.apply {
            addView(this, 0)
            visibility = View.GONE
        }

        mNoNetwork?.apply {
            addView(this, 0)
            visibility = View.GONE
        }

        mLoadingView?.apply {
            addView(this, 0)
            visibility = View.GONE
        }

        views[PageState.ORIGIN] = mOriginView
        views[PageState.LOAD_ERROR] = mLoadError
        views[PageState.NO_DATA] = mNoDataView
        views[PageState.NO_NETWORK] = mNoNetwork
        views[PageState.LOADING] = mLoadingView

        initPageState(pageState)
    }

    fun setPageStateView(state: PageState, view: View) {
        views[state] = view
    }

    /**
     * loaderror -> loading
     */
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

                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                }
            }).setDuration(duration.toLong())
            .alpha(0f)
            .start()

        showView.animate().cancel()
        showView.alpha = 0f
        showView.visibility = View.VISIBLE
        showView.animate()
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                }
            })
            .setDuration(duration.toLong())
            .alpha(1f)
            .start()
    }

    fun isVisible(view: View): Boolean {
        return view.visibility == View.VISIBLE
    }

    fun displayOriginView() {
        if (pageState != PageState.ORIGIN) {
            val preState = pageState
            pageState = PageState.ORIGIN
            animStateView(preState, mOriginView!!)
        }
    }

    fun displayNoNetwork() {
        if (pageState != PageState.NO_NETWORK) {
            val preState = pageState
            pageState = PageState.NO_NETWORK
            if (mNoNetwork == null) {
                throw IllegalArgumentException("you should add no network layout")
            }
            animStateView(preState, mNoNetwork!!)
        }
    }

    fun displayNoData() {
        if (pageState != PageState.NO_DATA) {
            val preState = pageState
            pageState = PageState.NO_DATA
            if (mNoDataView == null) {
                throw IllegalArgumentException("you should add no data layout")
            }
            animStateView(preState, mNoDataView!!)
        }
    }

    fun displayLoading() {
        if (pageState != PageState.LOADING) {
            val preState = pageState
            pageState = PageState.LOADING
            if (mLoadingView == null) {
                throw IllegalArgumentException("you should add loading layout")
            }
            animStateView(preState, mLoadingView!!)
        }
    }

    fun displayLoadError() {
        if (pageState != PageState.LOAD_ERROR) {
            val preState = pageState
            pageState = PageState.LOAD_ERROR
            if (mLoadError == null) {
                throw IllegalArgumentException("you should add load error layout")
            }
            animStateView(preState, mLoadError!!)
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