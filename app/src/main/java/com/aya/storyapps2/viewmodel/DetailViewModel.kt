package com.aya.storyapps2.viewmodel

import androidx.lifecycle.ViewModel
import com.aya.storyapps2.paging.PagingRepository

class DetailViewModel(private val pagingRepository: PagingRepository) : ViewModel() {
    fun getDetail(auth: String, id: String) = pagingRepository.getStoryDetail(auth, id)
}