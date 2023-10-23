package com.aya.storyapps2.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.aya.storyapps2.api.ApiService
import com.aya.storyapps2.dataclass.User
import com.aya.storyapps2.dataclass.UserPreferences
import com.aya.storyapps2.dataclass.Result
import com.aya.storyapps2.responses.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PagingRepository (private val apiService: ApiService, private val userPref: UserPreferences) {

    fun getUser(): LiveData<User> = userPref.getUser().asLiveData()

    fun logout(){
        CoroutineScope(Dispatchers.Main).launch {
            userPref.logout()
        }
    }

    fun registerUser(username: String, email: String, password: String): LiveData<Result<RegisterResponses>> = liveData{
        emit(Result.Loading)
        try {
            val service = apiService.register(username, email, password)
            emit(Result.Success(service))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
        try {
            val service = apiService.login(email, password)

            userPref.saveUser(service.loginResult?.token!!, true)

            emit(Result.Success(service))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

    }

    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPaging(apiService, token)
            }
        ).liveData
    }

    fun getStoryDetail(auth: String, id: String): LiveData<Result<Story>> = liveData {
        emit(Result.Loading)
        try{
            val service = apiService.getStoryDetail("Bearer $auth", id)
            val response = service.story!!
            val story = Story(
                response.photoUrl,
                response.createdAt,
                response.name,
                response.description,
                response.id
            )
            emit(Result.Success(story))
        } catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadStory(auth: String, desc: RequestBody, image: MultipartBody.Part, lat: Float, lon: Float): LiveData<Result<StoryAddResponses>> = liveData {
        emit(Result.Loading)
        try{
            val service = apiService.imageUpload("Bearer $auth",image, desc, lat, lon)
            emit(Result.Success(service))
        } catch(e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getMaps(auth: String): LiveData<Result<List<MapsList>>> = liveData {
        emit(Result.Loading)
        try{
            val service = apiService.getLocation("Bearer $auth")
            val response = service.listStory
            val map = response.map {
                MapsList(
                    it.name,
                    it.description,
                    it.lon,
                    it.lat
                )
            }
            emit(Result.Success(map))
        } catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

}