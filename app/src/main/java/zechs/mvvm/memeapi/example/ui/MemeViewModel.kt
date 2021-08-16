package zechs.mvvm.memeapi.example.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import zechs.mvvm.memeapi.example.ThisApp
import zechs.mvvm.memeapi.example.models.MemeResponse
import zechs.mvvm.memeapi.example.repository.MemeRepository
import zechs.mvvm.memeapi.example.utils.Resource
import java.io.IOException


class MemeViewModel(
    app: Application,
    private val memeRepository: MemeRepository
) : AndroidViewModel(app) {

    val memeList: MutableLiveData<Resource<MemeResponse>> = MutableLiveData()
    private var memeListResponse: MemeResponse? = null
    private var pageCount = 20

    init {
        getMemes()
    }

    fun getMemes() =
        viewModelScope.launch {
            memeList.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = memeRepository.getMemes(pageCount)
                    memeList.postValue(handleLogsListResponse(response))
                } else {
                    memeList.postValue(Resource.Error("No internet connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> memeList.postValue(Resource.Error("Network Failure"))
                    else -> memeList.postValue(Resource.Error("Conversion Error"))
                }
            }
        }

    private fun handleLogsListResponse(response: Response<MemeResponse>): Resource<MemeResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageCount += 20
                if (memeListResponse == null) {
                    memeListResponse = resultResponse
                } else {
                    val oldMemes = memeListResponse?.memes
                    val newMemes = resultResponse.memes
                    oldMemes?.addAll(newMemes)
                }
                return Resource.Success(memeListResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<ThisApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}