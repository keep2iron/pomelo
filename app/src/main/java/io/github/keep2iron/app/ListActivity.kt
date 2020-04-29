package io.github.keep2iron.app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.keep2iron.app.moshi.Movie
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.helper.LinearLayoutHelper
import io.github.keep2iron.pomelo.pager.SampleLoadMore
import io.github.keep2iron.pomelo.pager.SwipeRefreshAble
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubListAdapter
import io.github.keep2iron.pomelo.pager.adapter.RecyclerViewHolder
import io.github.keep2iron.pomelo.pager.load.ListBinder
import io.github.keep2iron.pomelo.pager.load.LoadController
import io.github.keep2iron.pomelo.pager.load.LoadListener
import io.github.keep2iron.pomelo.pager.rx.LoadListSubscriber
import io.github.keep2iron.pomelo.state.PomeloPageStateLayout
import io.github.keep2iron.pomelo.utilities.FindService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ListActivity : AppCompatActivity(), LoadListener {

  private val apiService: ApiService by FindService()

  val items = AsyncDiffObservableList(object : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
      return oldItem == newItem
    }
  })
//    val data = ObservableArrayList<Movie>()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_page_state)

    val pageStateLayout = findViewById<PomeloPageStateLayout>(R.id.pageStateLayout)
    val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

    val sampleLoadMore = SampleLoadMore(true)
    val baseBinder = ListBinder(recyclerView, SwipeRefreshAble(refreshLayout), sampleLoadMore)
      .addSubAdapter(object :
        AbstractSubListAdapter<Movie>(items, layoutHelper = LinearLayoutHelper()) {

        init {
          setOnItemClickListener { position, view, itemView ->
            val newList = items.toList()
            newList.removeAt(position)
            items.update(newList)
          }
        }

        override fun render(holder: RecyclerViewHolder, item: Movie, position: Int) {
          holder.itemView.findViewById<TextView>(R.id.tvText).text = item.movieName
          ImageLoaderManager.getInstance()
            .showImageView(holder.findViewById(R.id.imageView), item.movieImage)
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
      .subscribe(LoadListSubscriber<Movie>(controller, items, pagerValue) {
        onSuccess = {
          if (controller.isLoadDefault(pagerValue)) {
            items.update(it)
//                        data.clear()
//                        data.addAll(it)
          } else {
//                        data.addAll(it)
            items.updateAppend(it)
          }
          controller.intInc()
        }
      })
  }

  override fun defaultValue(): Any = 1

}