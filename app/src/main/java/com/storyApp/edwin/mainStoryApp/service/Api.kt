package com.storyApp.edwin.mainStoryApp.service

import com.storyApp.edwin.mainStoryApp.model.*
import com.storyApp.edwin.mainStoryApp.model.request.LoginRequest
import com.storyApp.edwin.mainStoryApp.model.request.RegisterRequest
import com.storyApp.edwin.mainStoryApp.model.response.AddandRegisterResponse
import com.storyApp.edwin.mainStoryApp.network.StoryResponseItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("stories")
    fun getAllStories(): Call<Story>

    @GET("stories")
    suspend fun getAllStoriesPaging(
        @Query("page") page:Int,
        @Query("size") size:Int
    ): List<StoryResponseItem>

    @GET("stories")
    fun getFindLocation(@Query("location") query: Int):Call<Story>

    @Multipart
    @POST("stories")
    fun postNewStory(@Part file: MultipartBody.Part, @Part("description") description: RequestBody): Call<AddandRegisterResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest) : Call<AddandRegisterResponse>

    @POST("login")
    fun login(@Body userRequest: LoginRequest): Call<LoginResponse>
}