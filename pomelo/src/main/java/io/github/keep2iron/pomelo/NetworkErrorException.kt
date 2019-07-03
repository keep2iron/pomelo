package io.github.keep2iron.pomelo

class NetworkErrorException(val url: String, val exception: Exception) {
    override fun toString(): String {
        return "$url $exception"
    }
}