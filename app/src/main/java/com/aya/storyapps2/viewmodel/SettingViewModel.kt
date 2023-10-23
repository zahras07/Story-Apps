package com.aya.storyapps2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aya.storyapps2.dataclass.User
import com.aya.storyapps2.paging.PagingRepository

class SettingViewModel(private val pagingRepository: PagingRepository) : ViewModel() {
    fun getUser(): LiveData<User> = pagingRepository.getUser()
    fun logout() = pagingRepository.logout()
}