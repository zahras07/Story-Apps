package com.aya.storyapps2.viewmodel
import androidx.lifecycle.ViewModel
import com.aya.storyapps2.paging.PagingRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val pagingRepository: PagingRepository): ViewModel(){
    fun uploadToServer(auth: String, desc: RequestBody, image: MultipartBody.Part,
                       lat: Float, lon: Float) = pagingRepository.uploadStory(auth, desc, image, lat, lon)
}