package io.github.keep2iron.pomelo.pager.rx

import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.collections.DiffObservableList
import io.github.keep2iron.pomelo.pager.load.LoadController
import io.github.keep2iron.pomelo.state.PageStateObservable

class LoadListSubscriber<T>(
    controller: LoadController,
    private val list: List<T>,
    private val pagerValue: Any,
    pageState: PageStateObservable? = null,
    testRespEmpty: (resp: List<T>) -> Boolean = {
        it.isEmpty()
    },
    block: AndroidSubscriber<List<T>>.() -> Unit
) : LoadSubscriber<List<T>>(controller, testRespEmpty, pageState, block) {

    override fun onNext(resp: List<T>) {
        if (list is DiffObservableList) {
            if (pagerValue == controller.pager.defaultValue) {
                list.update(resp)
            } else {
                list.updateAppend(resp)
            }
        } else if (list is AsyncDiffObservableList) {
            if (pagerValue == controller.pager.defaultValue) {
                list.update(resp)
            } else {
                list.updateAppend(resp)
            }
        } else if (list is MutableList) {
            if (pagerValue == controller.pager.defaultValue) {
                list.clear()
                list.addAll(resp)
            } else {
                list.addAll(resp)
            }
        }

        super.onNext(resp)
    }

}