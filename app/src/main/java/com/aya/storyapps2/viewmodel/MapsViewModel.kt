package com.aya.storyapps2.viewmodel

import androidx.lifecycle.ViewModel
import com.aya.storyapps2.paging.PagingRepository

class MapsViewModel(private val pagingRepository: PagingRepository) : ViewModel() {
    fun getMaps(auth: String) = pagingRepository.getMaps(auth)
}