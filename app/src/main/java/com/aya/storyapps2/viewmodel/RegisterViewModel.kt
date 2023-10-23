package com.aya.storyapps2.viewmodel

import androidx.lifecycle.ViewModel
import com.aya.storyapps2.paging.PagingRepository

class RegisterViewModel(private val pagingRepository: PagingRepository) : ViewModel() {
    fun createUserRegister(username: String, email: String, password: String) = pagingRepository.registerUser(username, email, password)
}