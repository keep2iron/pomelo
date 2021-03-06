package io.github.keep2iron.pomelo.pager.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * @author keep2iron
 */
open class RecyclerViewHolder : RecyclerView.ViewHolder {
  companion object {
    const val DEFAULT_TAG = 0
  }

  private var cacheView: SparseArray<View> = SparseArray()

  constructor(view: View) : super(view)

  constructor(
    context: Context, @LayoutRes layoutRes: Int,
    parent: ViewGroup?
  ) : this(LayoutInflater.from(context).inflate(layoutRes, parent, false))

  fun <T : View?> findViewById(viewId: Int): T {
    var view: T = cacheView.get(viewId) as T
    if (view == null) {
      view = itemView.findViewById<T>(viewId)
      cacheView.put(viewId, view)
    }
    return view
  }

  fun getTextView(viewId: Int): TextView = findViewById(viewId)

  fun getButton(viewId: Int): Button = findViewById(viewId)

  fun getImageView(viewId: Int): ImageView = findViewById(viewId)

  fun getImageButton(viewId: Int): ImageButton = findViewById(viewId)

  fun getEditText(viewId: Int): EditText = findViewById(viewId)

  fun setText(
    viewId: Int,
    value: CharSequence
  ): RecyclerViewHolder {
    val view = findViewById<TextView>(viewId)
    view.text = value
    return this
  }

  fun setBackground(
    viewId: Int,
    resId: Int
  ): RecyclerViewHolder {
    val view = findViewById<View>(viewId)
    view.setBackgroundResource(resId)
    return this
  }

  fun setClickListener(
    viewId: Int,
    listener: View.OnClickListener
  ): RecyclerViewHolder {
    val view = findViewById<View>(viewId)
    view.setOnClickListener(listener)
    return this
  }

  fun setVisibility(
    viewId: Int,
    isVisibility: Boolean
  ) {
    findViewById<View>(viewId).visibility = if (isVisibility) {
      View.VISIBLE
    } else {
      View.GONE
    }
  }

  fun setTag(
    tag: Int,
    obj: Any
  ) {
    itemView.setTag(tag, obj)
  }

  fun setTag(obj: Any) {
    setTag(DEFAULT_TAG, obj)
  }

  fun <T> getTag(): T {
    return getTag(DEFAULT_TAG) as T
  }

  fun <T> getTag(key: Int): T {
    return itemView.getTag(key) as T
  }

//    fun getRealPosition(): Int {
//        return getTag(R.id.comp_position)
//    }
}