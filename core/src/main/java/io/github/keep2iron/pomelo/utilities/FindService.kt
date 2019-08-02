package io.github.keep2iron.pomelo.utilities

import android.view.View
import io.github.keep2iron.pomelo.NetworkManager
import kotlin.reflect.KProperty

class FindService<T> constructor(
  private val host: String? = null,
  private val clazz: Class<T>
) {

  constructor(clazz: Class<T>) : this(null, clazz)

  operator fun getValue(
    thisRef: Any,
    property: KProperty<*>
  ): T {
    return if (host == null) {
      NetworkManager.getInstance()
          .getService(clazz)
    } else {
      NetworkManager.getInstance()
          .getService(host, clazz)
    }
  }

  operator fun setValue(
    thisRef: Any,
    property: KProperty<*>,
    view: View
  ) {
    throw IllegalArgumentException("did't be allow to call setValue")
  }
}