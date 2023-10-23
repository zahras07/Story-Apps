package com.aya.storyapps2.viewmodel

import androidx.lifecycle.ViewModel
import com.aya.storyapps2.paging.PagingRepository

class LoginViewModel (private val pagingRepository: PagingRepository): ViewModel() {
    fun login(email: String, password: String) = pagingRepository.login(email, password)
}