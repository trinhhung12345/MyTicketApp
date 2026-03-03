package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.BaseResponse
import com.example.myticketapp.data.remote.dto.CategoryDto
import com.example.myticketapp.data.remote.dto.EventDto
import retrofit2.http.GET

interface HomeApi {
    @GET("categories")
    suspend fun getCategories(): BaseResponse<List<CategoryDto>>

    @GET("events")
    suspend fun getEvents(): BaseResponse<List<EventDto>>
}