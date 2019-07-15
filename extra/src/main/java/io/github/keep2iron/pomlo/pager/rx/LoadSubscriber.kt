package io.github.keep2iron.pomlo.pager.rx

import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomlo.pager.exception.NoDataException
import io.github.keep2iron.pomlo.pager.load.LoadController
import io.github.keep2iron.pomlo.state.PageState
import io.github.keep2iron.pomlo.state.PageStateObservable

open class LoadSubscriber<T>(
        private val controller: LoadController,
        val testRespEmpty: (resp: T) -> Boolean,
        private val pageState: PageStateObservable? = null,
        block: AndroidSubscriber<T>.() -> Unit
) : AndroidSubscriber<T>(block) {

    override fun onNext(resp: T) {
        super.onNext(resp)
        controller.setRefreshEnable(true)
        controller.setLoadMoreEnable(true)

        controller.showRefreshComplete()
        controller.showLoadMoreComplete()

        val pager = controller.pager

        try {
            if (testRespEmpty(resp)) {
                if (pager.value == pager.defaultValue) {
                    controller.scrollToPosition(0)
                    pageState?.setPageState(PageState.NO_DATA)
                }
                super.onNext(resp)
                throw NoDataException()
            } else {
                if (pager.value == pager.defaultValue) {
                    controller.scrollToPosition(0)
                    pageState?.setPageState(PageState.ORIGIN)
                }
                super.onNext(resp)
            }
        } catch (exp: NoDataException) {
            controller.setRefreshEnable(true)
            controller.showLoadMoreEnd()
        }
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        val pager = controller.pager
        if (pager.value == pager.defaultValue) {
            pageState?.setPageState(PageState.LOAD_ERROR)
        }

        controller.setLoadMoreEnable(true)
        controller.setRefreshEnable(true)
        controller.showRefreshComplete()
        controller.showLoadMoreFailed()
    }

}