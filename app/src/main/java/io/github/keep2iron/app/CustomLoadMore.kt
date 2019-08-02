package io.github.keep2iron.app

import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomlo.pager.SampleLoadMore

class CustomLoadMore(recyclerView: RecyclerView) : SampleLoadMore(
    recyclerView
) {

  override fun onCreateViewLayoutId(): Int {
    return R.layout.layout_load_more
  }
}