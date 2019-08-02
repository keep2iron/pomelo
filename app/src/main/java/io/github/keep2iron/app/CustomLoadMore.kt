package io.github.keep2iron.app

import io.github.keep2iron.pomlo.pager.SampleLoadMore

class CustomLoadMore(recyclerView: androidx.recyclerview.widget.RecyclerView) : SampleLoadMore(
    recyclerView
) {

  override fun onCreateViewLayoutId(): Int {
    return R.layout.layout_load_more
  }
}