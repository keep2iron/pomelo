package io.github.keep2iron.pomlo.pager.load

interface LoadListener {

    /**
     * when refresh or loadMore ,this method will call.
     *
     * @param controller Everything that controls the refresh and load of the object.
     * @param pagerValue PageIndex default value is 0(int),you can override method [defaultValue] to set custom defaultValue
     * @param isPullToRefresh Pull down refresh flag, true is pull down refresh
     */
    fun onLoad(controller: LoadController, pagerValue: Any, isPullToRefresh: Boolean)

    fun defaultValue(): Any {
        return 0
    }
}