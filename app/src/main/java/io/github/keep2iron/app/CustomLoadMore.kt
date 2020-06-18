package io.github.keep2iron.app

import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.pomelo.pager.SampleLoadMore

class CustomLoadMore : SampleLoadMore() {

    override fun onCreateViewLayoutId(): Int {
        return R.layout.layout_load_more
    }
}