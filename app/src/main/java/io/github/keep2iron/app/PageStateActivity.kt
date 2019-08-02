package io.github.keep2iron.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.orhanobut.logger.Logger
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.utilities.FindService
import io.github.keep2iron.pomlo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomlo.collections.DiffObservableList
import io.github.keep2iron.pomlo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomlo.pager.adapter.RecyclerViewHolder
import io.github.keep2iron.pomlo.pager.load.BaseBinder
import io.github.keep2iron.pomlo.pager.load.LoadController
import io.github.keep2iron.pomlo.pager.load.LoadListener
import io.github.keep2iron.pomlo.pager.load.MultipleTypeBinder
import io.github.keep2iron.pomlo.state.PageState
import io.github.keep2iron.pomlo.state.PageStateObservable
import io.github.keep2iron.pomlo.state.PomeloPageStateLayout
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

  private val apiService by FindService(ApiService::class.java)

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

//        val view = LayoutInflater.from(applicationContext).inflate(R.layout.item_page_error, pageStateLayout, false)
    pageStateLayout.setPageStateView(PageState.NETWORK_ERROR, R.layout.item_page_error)
    pageStateLayout.setPageStateReloadListener(PageState.NETWORK_ERROR) { state, view ->
      pageState.setPageState(PageState.LOADING)
      binder.load()
    }

    pageState.setupWithPageStateLayout(pageStateLayout)

    binder = MultipleTypeBinder(data, recyclerView, refreshLayout, true)
        .addSubAdapter<Movie>(object : AbstractSubAdapter(1, 10) {
          override fun render(
            holder: RecyclerViewHolder,
            position: Int
          ) {
            val item = data[position]

          }

          override fun onInflateLayoutId(
            parent: ViewGroup,
            viewType: Int
          ): Int = R.layout.item_list
        })
        .addSubAdapter<Recommend>(object : AbstractSubAdapter(2, 10) {
          override fun render(
            holder: RecyclerViewHolder,
            position: Int
          ) {
            val item = data[position]
          }

          override fun onInflateLayoutId(
            parent: ViewGroup,
            viewType: Int
          ): Int = R.layout.item_recommend
        })
        .setLoadMore(CustomLoadMore(recyclerView))
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
    pageState.setPageState(PageState.ORIGIN)
    if (isPullToRefresh && controller.isLoadDefault(pagerValue)) {
      isLoadHomeEnd = false
    }

    if (!isLoadHomeEnd) {
      apiService.indexHome(controller.pagerValue() as Int)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .map {
            it.value
          }
          .subscribe(AndroidSubscriber<List<Movie>> {
            onSuccess = { respList ->
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
      apiService.indexRecommend(controller.pagerValue() as Int)
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
