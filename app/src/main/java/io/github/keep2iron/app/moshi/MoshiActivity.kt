package io.github.keep2iron.app.moshi

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi

class MoshiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(View(this))

        val moshi = Moshi.Builder()
            .build()


        val json = "{\n" +
                "  \"width\": \"asd\",\n" +
                "  \"height\": 768,\n" +
                "  \"color\": 16711680\n" +
                "}"

        val fromJson = moshi.adapter(MoshiBean::class.java)
            .fromJson(json)

        Log.d("moshi","bean : ${fromJson}")

    }

}