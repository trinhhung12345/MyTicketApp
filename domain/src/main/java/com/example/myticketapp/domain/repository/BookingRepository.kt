package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.SeatMap
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getSeatMap(showingId: Int): Flow<Resource<SeatMap>>
}