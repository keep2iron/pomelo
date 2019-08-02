package io.github.keep2iron.app

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

  @FormUrlEncoded
  @POST("/api/index/movie")
  fun indexHome(@Field("page") page: Int): Observable<BaseResponse<List<Movie>>>

  @FormUrlEncoded
  @POST("/api/index/recommend")
  fun indexRecommend(@Field("page") page: Int): Observable<BaseResponse<List<Recommend>>>

}