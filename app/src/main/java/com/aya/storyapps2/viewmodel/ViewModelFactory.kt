package com.aya.storyapps2.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aya.storyapps2.paging.Injection
import com.aya.storyapps2.paging.PagingRepository

class ViewModelFactory(private val pagingRepository: PagingRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(pagingRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(pagingRepository) as T
        }else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(pagingRepository) as T
        }else if (modelClass.isAssignableFrom(SettingViewModel::class.java)){
            return SettingViewModel(pagingRepository) as T
        }else if (modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(pagingRepository) as T
        }else if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(pagingRepository) as T
        }else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(pagingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(dataStore: DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(dataStore))
            }.also { instance = it }
    }
}