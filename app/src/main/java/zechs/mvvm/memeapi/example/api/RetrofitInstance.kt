package zechs.mvvm.memeapi.example.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zechs.mvvm.memeapi.example.utils.Constants.Companion.MEME_API

class RetrofitInstance {

    companion object {

        private val meme_api by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            Retrofit.Builder()
                .baseUrl(MEME_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build()
                )
                .build()
        }

        val api: MemeAPI by lazy {
            meme_api.create(MemeAPI::class.java)
        }

    }
}