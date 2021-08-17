package zechs.mvvm.memeapi.example.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import zechs.mvvm.memeapi.example.models.MemeResponse


interface MemeAPI {

    @GET("gimme/{count}")
    suspend fun getMemes(
        @Path("count")
        count: Int,
    ): Response<MemeResponse>

}