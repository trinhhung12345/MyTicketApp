package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.BookingApi
import com.example.myticketapp.data.remote.dto.toDomain
import com.example.myticketapp.domain.model.BookingInitData
import com.example.myticketapp.domain.repository.BookingRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApi
) : BookingRepository {

    override fun getBookingData(showingId: Int): Flow<Resource<BookingInitData>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                // Gọi 2 API song song
                val seatMapDeferred = async { api.getSeatMap(showingId) }
                val ticketTypeDeferred = async { api.getTicketTypes(showingId) }

                val seatMapResponse = seatMapDeferred.await()
                val ticketTypeResponse = ticketTypeDeferred.await()

                if (ticketTypeResponse.code == 200) {
                    val ticketTypesDto = ticketTypeResponse.data ?: emptyList()
                    val domainTicketTypes = ticketTypesDto.map { it.toDomain() }

                    // Kiểm tra xem SeatMap có dữ liệu không
                    val seatMapDto = seatMapResponse.data?.firstOrNull()
                    val domainSeatMap = if (seatMapDto != null && seatMapResponse.code == 200) {
                        seatMapDto.toDomain(ticketTypesDto) // Gọi hàm mapper cũ
                    } else {
                        null // Không có sơ đồ ghế
                    }

                    emit(Resource.Success(BookingInitData(domainSeatMap, domainTicketTypes)))
                } else {
                    emit(Resource.Error("Lỗi tải giá vé"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Lỗi kết nối mạng"))
        }
    }
}
