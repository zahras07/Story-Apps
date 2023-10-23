package com.aya.storyapps2.paging

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.aya.storyapps2.api.RetrofitService
import com.aya.storyapps2.dataclass.UserPreferences

object Injection {
    fun provideRepository(dataStore: DataStore<Preferences>): PagingRepository {
        val apiService = RetrofitService.getApiService()
        val userPreferences = UserPreferences.getInstance(dataStore)
        return PagingRepository(apiService, userPreferences)
    }
}