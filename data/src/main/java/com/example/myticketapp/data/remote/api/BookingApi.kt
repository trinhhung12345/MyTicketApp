package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.BaseResponse
import com.example.myticketapp.data.remote.dto.SeatMapResponse
import com.example.myticketapp.data.remote.dto.TicketTypeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BookingApi {
    @GET("seat-maps/showings/{showingId}")
    suspend fun getSeatMap(@Path("showingId") showingId: Int): BaseResponse<List<SeatMapResponse>>

    // API Lấy giá vé theo showingId
    @GET("ticket-types/showing/{showingId}")
    suspend fun getTicketTypes(@Path("showingId") showingId: Int): BaseResponse<List<TicketTypeDto>>
}