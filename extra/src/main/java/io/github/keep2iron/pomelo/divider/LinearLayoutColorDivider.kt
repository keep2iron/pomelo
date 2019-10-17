package io.github.keep2iron.pomelo.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutColorDivider(
    context: Context,
    @ColorRes color: Int = -1,
    private val size: Int = 0,
    private val mOrientation: Int = LinearLayoutManager.VERTICAL
) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable? = null

    init {
        if (color != -1) {
            mDivider = ColorDrawable(ContextCompat.getColor(context, color))
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if(mDivider == null){
            return
        }

        var left: Int
        var right: Int
        var top: Int
        var bottom: Int
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams

                if (i == 0) {
                    left = child.left - params.leftMargin - size
                    right = left + size
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                } else {
                    left = child.right + params.rightMargin
                    right = left + size
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                }
            }
        } else {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                top = child.bottom + params.bottomMargin
                bottom = top + size
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c)
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            if (position == 0) {
                outRect.set(size, 0, size, 0)
            } else {
                outRect.set(0, 0, size, 0)
            }
        } else {
            outRect.set(0, 0, 0, size)
        }
    }
}