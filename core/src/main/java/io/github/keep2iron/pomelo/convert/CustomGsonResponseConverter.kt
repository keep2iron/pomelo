package io.github.keep2iron.pomelo.convert

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by 薛世君
 * Date : 2016/11/9
 * Email : 497881309@qq.com
 */
internal class CustomGsonResponseConverter<T>(
  private val gson: Gson,
  private val type: Type,
  private val adapter: TypeAdapter<out T>
) : Converter<ResponseBody, T> {

  companion object {
    const val CLASS_STRING = "java.lang.String"
    const val CLASS_RESPONSE_BODY = "okhttp3.ResponseBody"
  }

  /**
   * 这里就是错误预处理的函数逻辑
   *
   * @param responseBody 返回的响应实体
   * @return 返回转换的对象
   * @throws IOException
   */
  @Throws(IOException::class)
  override fun convert(responseBody: ResponseBody): T? {
    //如果返回类型是String那么就直接返回String对象,
    when (type) {
      is Class<*> -> {
        val body = responseBody.string()
        val clazz = type
        if (CLASS_STRING == clazz.name) {
          return body as T
        }

        //如果直接要求返回responseBody
        return if (CLASS_RESPONSE_BODY == clazz.name) {
          body as T
        } else gson.fromJson<T>(body, type)

      }
      is ParameterizedType -> {
        val jsonReader = gson.newJsonReader(responseBody.charStream())
        responseBody.use {
          val result = adapter.read(jsonReader)
          if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw JsonIOException("JSON document was not fully consumed.")
          }
          return result
        }
      }
      else -> throw RuntimeException("convert make a error!!")
    }
  }
}
