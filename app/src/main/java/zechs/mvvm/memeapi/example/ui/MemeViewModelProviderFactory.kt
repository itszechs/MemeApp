package zechs.mvvm.memeapi.example.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import zechs.mvvm.memeapi.example.repository.MemeRepository


@Suppress("UNCHECKED_CAST")
class MemeViewModelProviderFactory(
    private val app: Application,
    private val memeRepository: MemeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MemeViewModel(app, memeRepository) as T
    }
}