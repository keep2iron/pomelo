package io.github.keep2iron.app

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.keep2iron.pomelo.utilities.FindService
import io.github.keep2iron.pomlo.collections.DiffObservableList
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubListAdapter
import io.github.keep2iron.pomlo.pager.adapter.RecyclerViewHolder
import io.github.keep2iron.pomlo.pager.load.BaseBinder
import io.github.keep2iron.pomlo.pager.load.ListBinder
import io.github.keep2iron.pomlo.pager.load.LoadController
import io.github.keep2iron.pomlo.pager.load.LoadListener
import io.github.keep2iron.pomlo.pager.rx.LoadListSubscriber
import io.github.keep2iron.pomlo.state.PageState
import io.github.keep2iron.pomlo.state.PageStateObservable
import io.github.keep2iron.pomlo.state.PomeloPageStateLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PageStateActivity : AppCompatActivity(), LoadListener {

    val data = DiffObservableList(object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(p0: Movie, p1: Movie): Boolean = p0.id == p1.id

        override fun areContentsTheSame(p0: Movie, p1: Movie): Boolean = p0 == p1
    })

    private val apiService by FindService(ApiService::class.java)

    private val pageState = PageStateObservable(PageState.LOADING)

    private lateinit var binder: BaseBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_state)
        val pageStateLayout = findViewById<PomeloPageStateLayout>(R.id.pageStateLayout)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val view = LayoutInflater.from(applicationContext).inflate(R.layout.item_page_error, pageStateLayout, false)
        pageStateLayout.setPageStateView(PageState.NETWORK_ERROR, view)
        pageStateLayout.setPageStateReloadListener(PageState.NETWORK_ERROR) { state, view ->
            pageState.setPageState(PageState.LOADING)
            binder.load()
        }

        pageState.setupWithPageStateLayout(pageStateLayout)

        binder = ListBinder(recyclerView, refreshLayout, true)
            .addSubAdapter(object : AbstractSubListAdapter<Movie>(data, 1, 10) {
                override fun render(holder: RecyclerViewHolder, item: Movie, position: Int) {
                    holder.setText(R.id.tvText, item.movieName)
                }

                override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int = R.layout.item_list
            })
            .setLoadMore(CustomLoadMore(recyclerView))
            .setLoadListener(this)
            .bind()

        binder.load()
    }

    override fun onLoad(controller: LoadController, pagerValue: Any) {
        apiService.indexHome(controller.pagerValue() as Int)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.value
            }
            .subscribe(LoadListSubscriber<Movie>(controller, data, pagerValue, pageState) {
                onSuccess = {
                    controller.intInc()
                }
            })
    }

    override fun defaultValue(): Any = 1

}
