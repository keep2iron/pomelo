package io.github.keep2iron.pomelo.interceptor

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class PostParamsInterceptor(val listener: (url: String, headerParams: HashMap<String, String>) -> Unit) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method() == "POST") {
            if (request.body() is FormBody) {
                val bodyBuilder = FormBody.Builder()
                val formBody = request.body() as FormBody
                //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                for (i in 0 until formBody.size()) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i))
                }

                val headerParams = HashMap<String, String>()
                for ((key, value) in headerParams) {
                    bodyBuilder.addEncoded(key, value)
                }

                return chain.proceed(request.newBuilder().post(formBody).build())
            }
        }
        return chain.proceed(request)
    }
}