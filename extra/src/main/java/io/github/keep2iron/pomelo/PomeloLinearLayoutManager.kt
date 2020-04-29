package io.github.keep2iron.pomelo

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * 处理 https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in/40177879#40177879 问题
 */
class PomeloLinearLayoutManager : LinearLayoutManager {

  constructor(context: Context?) : super(context)

  constructor(
    context: Context?,
    orientation: Int,
    reverseLayout: Boolean
  ) : super(context, orientation, reverseLayout)

  constructor(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes)

  override fun supportsPredictiveItemAnimations(): Boolean {
    return false
  }

}