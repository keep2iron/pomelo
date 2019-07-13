package io.github.keep2iron.pomlo.state

import android.view.View

open class PageStateObservable(
    private val pageStateLayout: PomeloPageStateLayout,
    private var state: PageState = PageState.ORIGIN
) {

    private val listener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            pageStateLayout.initPageState(state)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            clear()
        }
    }

    private var stateChangeListener: ((state: PageState) -> Unit)? = null

    init {
        pageStateLayout.addOnAttachStateChangeListener(listener)
    }

    fun setPageState(pageState: PageState) {
        when (pageState) {
            PageState.ORIGIN -> {
                pageStateLayout.displayOriginView()
            }
            PageState.LOADING -> {
                pageStateLayout.displayLoading()
            }
            PageState.LOAD_ERROR -> {
                pageStateLayout.displayLoadError()
            }
            PageState.NO_DATA -> {
                pageStateLayout.displayNoData()
            }
            PageState.NO_NETWORK -> {
                pageStateLayout.displayNoNetwork()
            }
        }
        if (this.state != pageState) {
            this.state = pageState
            stateChangeListener?.invoke(this.state)
        }
    }

    fun setOnStateChangeListener(stateChangeListener: (state: PageState) -> Unit) {
        this.stateChangeListener = stateChangeListener
    }

    fun clear() {
        pageStateLayout.removeOnAttachStateChangeListener(listener)
    }
}
