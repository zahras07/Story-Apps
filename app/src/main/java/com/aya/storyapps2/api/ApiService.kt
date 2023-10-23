package com.aya.storyapps2.api

import com.aya.storyapps2.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("/v1/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("/v1/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponses

    @Multipart
    @POST("/v1/stories")
    suspend fun imageUpload(
        @Header("Authorization") token:String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : Float,
        @Part("lon") lon : Float,
    ): StoryAddResponses

    @GET("/v1/stories")
    suspend fun getStoryList(
        @Header("Authorization") token:String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesResponse

    @GET("/v1/stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token:String,
        @Path("id") id: String
    ): DetailStoriesResponse

    @GET("/v1/stories?location=1.")
    suspend fun getLocation(
        @Header("Authorization") token:String,
    ): MapsResponse


}