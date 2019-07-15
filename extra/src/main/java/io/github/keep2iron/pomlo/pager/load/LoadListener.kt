package io.github.keep2iron.pomlo.pager.load

interface LoadListener {

    fun onLoad(controller:LoadController, pagerValue: Any)

    fun defaultValue(): Any {
        return 0
    }
}