package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.BookingInitData
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getBookingData(showingId: Int): Flow<Resource<BookingInitData>>
}
