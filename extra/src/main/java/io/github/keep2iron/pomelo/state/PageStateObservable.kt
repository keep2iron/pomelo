package io.github.keep2iron.pomelo.state

import androidx.databinding.ObservableField

open class PageStateObservable(
  private var initState: PageState = PageState.ORIGIN
) : ObservableField<PageState>(initState) {

  private var pageStateLayout: PomeloPageStateLayout? = null

  fun setupWithPageStateLayout(pageStateLayout: PomeloPageStateLayout) {
    this.pageStateLayout = pageStateLayout
    set(initState)
    pageStateLayout.initPageState(initState)
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
      PageState.EMPTY_DATA -> {
        pageStateLayout?.displayEmptyData()
      }
      PageState.NETWORK_ERROR -> {
        pageStateLayout?.displayNetworkError()
      }
    }
    set(pageState)
  }
}
