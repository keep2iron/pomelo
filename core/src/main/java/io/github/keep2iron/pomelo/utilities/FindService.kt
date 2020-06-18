package io.github.keep2iron.pomelo.utilities

import android.view.View
import io.github.keep2iron.pomelo.NetworkManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FindService constructor(
    val host: String? = null
) {

    inline operator fun <reified T> getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T {
        return if (host == null) {
            NetworkManager.getInstance()
                .getService(T::class.java)
        } else {
            NetworkManager.getInstance()
                .getService(host, T::class.java)
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

fun findService(host: String? = null): FindService {
    return FindService(host)
}