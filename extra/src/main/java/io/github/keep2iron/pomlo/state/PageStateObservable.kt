package io.github.keep2iron.pomlo.state

import android.databinding.ObservableField
import android.view.View

open class PageStateObservable(
        private var state: PageState = PageState.ORIGIN
) : ObservableField<PageState>(state) {

    private var pageStateLayout: PomeloPageStateLayout? = null


    private val listener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            pageStateLayout?.initPageState(state)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            clear()
        }
    }

    fun setupWithPageStateLayout(pageStateLayout: PomeloPageStateLayout) {
        this.pageStateLayout = pageStateLayout
        clear()
        pageStateLayout.addOnAttachStateChangeListener(listener)
    }

    fun setPageState(pageState: PageState) {
        when (pageState) {
            PageState.ORIGIN -> {
                pageStateLayout?.displayOriginView()
            }
            PageState.LOADING -> {
                pageStateLayout?.displayLoading()
            }
            PageState.LOAD_ERROR -> {
                pageStateLayout?.displayLoadError()
            }
            PageState.NO_DATA -> {
                pageStateLayout?.displayNoData()
            }
            PageState.NO_NETWORK -> {
                pageStateLayout?.displayNoNetwork()
            }
        }
        set(state)
    }

    fun clear() {
        pageStateLayout?.removeOnAttachStateChangeListener(listener)
    }
}
