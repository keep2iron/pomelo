package io.github.keep2iron.app.moshi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoshiBean(@Json(name = "color")
                     val color: Int = 0,
                     @Json(name = "width")
                     val width: Int = 0,
                     @Json(name = "height")
                     val height: Int = 0){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoshiBean

        if (color != other.color) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}