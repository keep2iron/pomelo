
package io.github.keep2iron.pomelo.convert;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import kotlin.jvm.functions.Function1;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author keep2iron [Contract me.](http://keep2iron.github.io)
 * @author keep2iron
 * @version 1.0
 * @see retrofit2.converter.gson.GsonConverterFactory
 * @since 2018/03/16 16:58
 */
public class CustomConvertFactory extends Converter.Factory {
    private Gson gson;
    private Function1<? super String, String> responseProcessListener;

    /**
     * 在这里要传入状态检测的一个测试类
     *
     * @param responseProcessListener
     * @return
     */
    public static CustomConvertFactory create(Function1<? super String, String> responseProcessListener) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        CustomConvertFactory customConvertFactory = new CustomConvertFactory(gson);
        customConvertFactory.responseProcessListener = responseProcessListener;

        return customConvertFactory;
    }

    /**
     * 是否需要包含状态检测
     *
     * @param gson gson实体对象
     */
    private CustomConvertFactory(@NonNull Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new CustomResponseConverter(gson, type, responseProcessListener);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new CustomRequestConverter<>(gson, adapter);
    }
}