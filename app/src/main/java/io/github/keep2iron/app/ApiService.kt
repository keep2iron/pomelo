package io.github.keep2iron.app

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("/api/index/movie")
    fun indexHome(@Field("params1") params1: String): Observable<BaseResponse<String>>

}