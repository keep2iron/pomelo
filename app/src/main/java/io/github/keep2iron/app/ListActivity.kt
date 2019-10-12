package io.github.keep2iron.app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.keep2iron.pomelo.utilities.FindService
import io.github.keep2iron.pomlo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomlo.pager.SwipeRefreshAble
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubListAdapter
import io.github.keep2iron.pomlo.pager.adapter.RecyclerViewHolder
import io.github.keep2iron.pomlo.pager.load.ListBinder
import io.github.keep2iron.pomlo.pager.load.LoadController
import io.github.keep2iron.pomlo.pager.load.LoadListener
import io.github.keep2iron.pomlo.pager.rx.LoadListSubscriber
import io.github.keep2iron.pomlo.state.PomeloPageStateLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ListActivity : AppCompatActivity(), LoadListener {

    private val apiService by FindService(ApiService::class.java)

    val data = AsyncDiffObservableList(object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_state)

        val pageStateLayout = findViewById<PomeloPageStateLayout>(R.id.pageStateLayout)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        ListBinder(recyclerView, SwipeRefreshAble(refreshLayout), true)
            .addSubAdapter(object : AbstractSubListAdapter<Movie>(data) {

                override fun render(holder: RecyclerViewHolder, item: Movie, position: Int) {
                    holder.itemView.findViewById<TextView>(R.id.tvText).text = item.movieName
                }

                override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int =
                    R.layout.item_list
            })
            .setLoadListener(this)
            .bind()
    }

    override fun onLoad(controller: LoadController, pagerValue: Any, isPullToRefresh: Boolean) {
        apiService.indexHome(pagerValue as Int)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.value
            }
            .subscribe(LoadListSubscriber<Movie>(controller, data, pagerValue) {
                onSuccess = {
                    if (controller.isLoadDefault(pagerValue)) {
                        data.update(it)
                    } else {
                        data.updateAppend(it)
                    }
                    controller.intInc()
                }
            })
    }

    override fun defaultValue(): Any = 1

}