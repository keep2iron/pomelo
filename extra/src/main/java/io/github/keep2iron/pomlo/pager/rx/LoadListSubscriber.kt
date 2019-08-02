package io.github.keep2iron.pomlo.pager.rx

import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomlo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomlo.collections.DiffObservableList
import io.github.keep2iron.pomlo.pager.load.LoadController
import io.github.keep2iron.pomlo.state.PageStateObservable

class LoadListSubscriber<T>(
  controller: LoadController,
  private val list: List<T>,
  val pagerValue: Any,
  pageState: PageStateObservable? = null,
  block: AndroidSubscriber<List<T>>.() -> Unit
) : LoadSubscriber<List<T>>(controller, {
  it.isEmpty()
}, pageState, block) {

  override fun onNext(resp: List<T>) {
    super.onNext(resp)

    if (list is DiffObservableList) {
      if (pagerValue == controller.pager.defaultValue) {
        list.update(resp)
      } else {
        list.apply {
          val newList = toMutableList()
          newList.addAll(resp)
          update(newList)
        }
      }
    } else if (list is AsyncDiffObservableList) {
      if (pagerValue == controller.pager.defaultValue) {
        list.update(resp)
      } else {
        list.apply {
          val newList = toMutableList()
          newList.addAll(resp)
          update(newList)
        }
      }
    } else if (list is MutableList) {
      if (pagerValue == controller.pager.defaultValue) {
        list.clear()
        list.addAll(resp)
      } else {
        list.addAll(resp)
      }
    }
  }

}