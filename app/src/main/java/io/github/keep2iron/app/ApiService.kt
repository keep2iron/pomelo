package io.github.keep2iron.app

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.POST

interface ApiService {

    @POST("/api/index/movie")
    fun indexHome(): Observable<String>

}