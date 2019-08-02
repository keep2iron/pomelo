package io.github.keep2iron.pomelo.convert

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @author keep2iron
 * @version 1.0
 * @see retrofit2.converter.gson.GsonConverterFactory
 *
 * @since 2018/03/16 16:58
 */
class CustomGsonConvertFactory private constructor(private val gson: Gson) : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, Any> {
    val adapter = gson.getAdapter(TypeToken.get(type))
    return CustomGsonResponseConverter(gson, type, adapter)
  }

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<Annotation>,
    methodAnnotations: Array<Annotation>,
    retrofit: Retrofit?
  ): Converter<*, RequestBody> {
    val adapter = gson.getAdapter(TypeToken.get(type))

    return CustomGsonRequestConverter(gson, adapter)
  }

  companion object {

    @JvmStatic
    fun create(): CustomGsonConvertFactory {
      return CustomGsonConvertFactory(
          GsonBuilder()
              .registerTypeAdapterFactory(NullStringToEmptyAdapterFactory())
              .create()
      )
    }
  }
}