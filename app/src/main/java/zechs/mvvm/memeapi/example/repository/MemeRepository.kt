package zechs.mvvm.memeapi.example.repository

import zechs.mvvm.memeapi.example.api.RetrofitInstance

class MemeRepository {
    suspend fun getMemes(
        pageSize: Int,
    ) = RetrofitInstance.api.getMemes(
        count = pageSize
    )
}