package io.github.keep2iron.app.moshi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Movie {
  @Json(name = "id")
  var id: Int = 0

  @Json(name = "movieName")
  var movieName: String = ""

  @Json(name = "movieImage")
  var movieImage: String = ""

  @Json(name = "description")
  var description: String = ""

  @Json(name = "year")
  var year: Int = 0

  @Json(name = "local")
  var local: String = ""

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Movie

    if (id != other.id) return false
    if (movieName != other.movieName) return false
    if (movieImage != other.movieImage) return false
    if (description != other.description) return false
    if (year != other.year) return false
    if (local != other.local) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id
    result = 31 * result + movieName.hashCode()
    result = 31 * result + movieImage.hashCode()
    result = 31 * result + description.hashCode()
    result = 31 * result + year
    result = 31 * result + local.hashCode()
    return result
  }

}