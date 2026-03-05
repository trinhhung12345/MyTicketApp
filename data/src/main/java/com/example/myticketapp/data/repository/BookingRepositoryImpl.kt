package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.BookingApi
import com.example.myticketapp.data.remote.dto.toDomain
import com.example.myticketapp.domain.model.SeatMap
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

    override fun getSeatMap(showingId: Int): Flow<Resource<SeatMap>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                // Gọi 2 API song song
                val seatMapDeferred = async { api.getSeatMap(showingId) }
                val ticketTypeDeferred = async { api.getTicketTypes(showingId) }

                val seatMapResponse = seatMapDeferred.await()
                val ticketTypeResponse = ticketTypeDeferred.await()

                if (seatMapResponse.code == 200 && ticketTypeResponse.code == 200) {
                    val seatMapDto = seatMapResponse.data?.firstOrNull()
                    val ticketTypes = ticketTypeResponse.data ?: emptyList()

                    if (seatMapDto != null) {
                        // Truyền ticketTypes vào Mapper để gộp dữ liệu
                        val domainSeatMap = seatMapDto.toDomain(ticketTypes)
                        emit(Resource.Success(domainSeatMap))
                    } else {
                        emit(Resource.Error("Không tìm thấy sơ đồ ghế"))
                    }
                } else {
                    emit(Resource.Error("Lỗi tải dữ liệu sơ đồ hoặc giá vé"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Lỗi kết nối mạng"))
        }
    }
}