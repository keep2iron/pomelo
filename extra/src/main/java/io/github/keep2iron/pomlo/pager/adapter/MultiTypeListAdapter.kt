package io.github.keep2iron.pomlo.pager.adapter

import android.databinding.ObservableList
import android.view.ViewGroup

open class MultiTypeListAdapter(data: ObservableList<Any>) : AbstractSubListAdapter<Any>(data) {

    val adapterMap: HashMap<Class<*>, AbstractSubAdapter> = HashMap(20)

    override fun render(holder: RecyclerViewHolder, item: Any, position: Int) {
        throw IllegalArgumentException("no need to call this method,because of onBindViewHolder is override.")
    }

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int {
        throw IllegalArgumentException("no need to call this method,because of onCreateViewHolder is override.")
    }

    /**
     * 通过实体类型来注册渲染的Adapter
     *
     * 如果onBindViewHolder上的实体类型与adapter有对应 那么会触发adapter的render方法
     */
    inline fun <reified T> registerAdapter(adapter: AbstractSubAdapter) {
        adapterMap[T::class.java] = adapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        for ((_, value) in adapterMap) {
            if (value.viewType == viewType) {
                return value.onCreateViewHolder(parent, viewType)
            }
        }
        throw IllegalArgumentException("itemType = $viewType miss match !please add it call registerAdapter()")
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val adapter = getCurrentBindAdapter(position)
        adapter.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int, payloads: MutableList<Any>) {
        val adapter = getCurrentBindAdapter(position)
        adapter.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        return getCurrentBindAdapter(position).getItemViewType(position)
    }

    private fun getCurrentBindAdapter(position: Int): AbstractSubAdapter {
        val item = data[position]

        return adapterMap[item.javaClass]
            ?: throw IllegalArgumentException("position on $position ,item type ${item.javaClass.name} is miss match !please add it call registerAdapter()")
    }
}