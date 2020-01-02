package io.github.keep2iron.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.orhanobut.logger.Logger
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.utilities.FindService
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.pager.SwipeRefreshAble
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.MultiTypeListAdapter
import io.github.keep2iron.pomelo.pager.adapter.RecyclerViewHolder
import io.github.keep2iron.pomelo.pager.load.BaseBinder
import io.github.keep2iron.pomelo.pager.load.ListBinder
import io.github.keep2iron.pomelo.pager.load.LoadController
import io.github.keep2iron.pomelo.pager.load.LoadListener
import io.github.keep2iron.pomelo.state.PageState
import io.github.keep2iron.pomelo.state.PageStateObservable
import io.github.keep2iron.pomelo.state.PomeloPageStateLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class PageStateActivity : AppCompatActivity(), LoadListener {
    //    val data = DiffObservableList(object : DiffUtil.ItemCallback<Movie>() {
//        override fun areItemsTheSame(p0: Movie, p1: Movie): Boolean = p0.id == p1.id
//
//        override fun areContentsTheSame(p0: Movie, p1: Movie): Boolean = p0 == p1
//    })
    val data = AsyncDiffObservableList(object : ItemCallback<Any>() {
        override fun areItemsTheSame(
            p0: Any,
            p1: Any
        ): Boolean {
            if (p0 is Movie && p1 is Movie) {
                return p0.id == p1.id
            }
            if (p0 is Recommend && p1 is Recommend) {
                return p0.id == p1.id
            }
            return false
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            p0: Any,
            p1: Any
        ): Boolean {
            if (p0 is Movie && p1 is Movie) {
                return p1 == p0
            }
            if (p0 is Recommend && p1 is Recommend) {
                return p0 == p1
            }

            return false
        }
    })
    private val apiService: ApiService by FindService()
    private val pageState = PageStateObservable(PageState.LOADING)
    private lateinit var binder: BaseBinder
    private var isFirstLoad = true
    private var isLoadHomeEnd = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_state)

        val pageStateLayout = findViewById<PomeloPageStateLayout>(R.id.pageStateLayout)
        val refreshLayout =
            findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        pageStateLayout.setPageStateView(PageState.NETWORK_ERROR, R.layout.item_page_error)
        pageStateLayout.setPageStateReloadListener(PageState.NETWORK_ERROR) { state, view ->
            pageState.setPageState(PageState.LOADING)
            binder.load()
        }

        pageState.setupWithPageStateLayout(pageStateLayout)

        binder = ListBinder(recyclerView, SwipeRefreshAble(refreshLayout), CustomLoadMore())
            .addSubAdapter(MultiTypeListAdapter(data).apply {
                registerAdapter<Movie>(object : AbstractSubAdapter(1, 10) {

                    init {
                        setOnItemClickListener { position, view, itemView ->
                            Toast.makeText(
                                view.context,
                                "Movie position : $position",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        setOnItemClickListener(R.id.tvText) { position, view, itemView ->
                            Toast.makeText(
                                view.context,
                                "tvText Movie position : $position",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun render(
                        holder: RecyclerViewHolder,
                        position: Int
                    ) {
                        val item = data[position] as Movie
                        holder.itemView.findViewById<TextView>(R.id.tvText).text = item.movieName
                    }

                    override fun onInflateLayoutId(
                        parent: ViewGroup,
                        viewType: Int
                    ): Int = R.layout.item_list
                })
                registerAdapter<Recommend>(object : AbstractSubAdapter(2, 10) {
                    override fun render(
                        holder: RecyclerViewHolder,
                        position: Int
                    ) {
                        val item = data[position] as Recommend

                        setOnItemClickListener { position, view, _ ->
                            Toast.makeText(
                                view.context,
                                "Recommend position : $position",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        ImageLoaderManager.getInstance()
                            .showImageView(
                                holder.itemView.findViewById(R.id.imageView),
                                item.recommandImage
                            ) {
                                this.isLoadGif = true
                                this.resizeImageWidth = 100
                                this.resizeImageHeight = 100
                            }
                    }

                    override fun onInflateLayoutId(
                        parent: ViewGroup,
                        viewType: Int
                    ): Int = R.layout.item_recommend
                })
            })
            .setLoadListener(this)
            .bind()

        binder.load()
    }

    override fun onLoad(
        controller: LoadController,
        pagerValue: Any,
        isPullToRefresh: Boolean
    ) {
        Logger.d("onLoad........")
        if (isPullToRefresh && controller.isLoadDefault(pagerValue)) {
            isLoadHomeEnd = false
        }

        if (!isLoadHomeEnd) {
            apiService.indexHome(controller.pagerValue as Int)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.value
                }
                .subscribe(AndroidSubscriber<List<Movie>> {
                    onSuccess = { respList ->
                        pageState.setPageState(PageState.ORIGIN)
                        if (respList.isEmpty()) {
                            isLoadHomeEnd = true
                            controller.reset()
                        } else {
                            if (controller.isLoadDefault(pagerValue)) {
                                data.update(respList)
                            } else {
                                data.update(data.toMutableList().also {
                                    it.addAll(respList)
                                })
                            }
                            controller.intInc()
                        }
                        controller.loadComplete()
                        isFirstLoad = false
                    }
                    onError = {
                        controller.loadFailedComplete()

                        if (it is IOException && isFirstLoad) {
                            pageState.setPageState(PageState.NETWORK_ERROR)
                        } else {
                            pageState.setPageState(PageState.LOAD_ERROR)
                        }
                    }
                })
        } else {
            apiService.indexRecommend(controller.pagerValue as Int)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.value
                }
                .subscribe(AndroidSubscriber<List<Recommend>> {
                    onSuccess = { respList ->
                        controller.loadComplete()
                        if (respList.isEmpty()) {
                            controller.showLoadMoreEnd()
                        } else {
                            data.update(data.toMutableList().also {
                                it.addAll(respList)
                            })
                            controller.intInc()
                        }
                    }
                    onError = {
                        controller.loadFailedComplete()
                    }
                })
        }
    }

    override fun defaultValue(): Any = 1
}
