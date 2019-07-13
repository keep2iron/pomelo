package io.github.keep2iron.pomlo.pager.load

import io.github.keep2iron.pomlo.pager.LoadMoreAble
import io.github.keep2iron.pomlo.pager.Refreshable

interface LoadListener {

    fun onLoad(controller:LoadController, pagerValue: Any)

    fun defaultValue(): Any {
        return 0
    }
}