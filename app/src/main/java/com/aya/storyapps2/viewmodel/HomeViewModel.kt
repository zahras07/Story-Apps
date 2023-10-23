package com.aya.storyapps2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aya.storyapps2.paging.PagingRepository
import com.aya.storyapps2.responses.ListStoryItem

class HomeViewModel(private val pagingRepository: PagingRepository): ViewModel() {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        pagingRepository.getAllStory(token).cachedIn(viewModelScope)
}