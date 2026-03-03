package com.example.myticketapp.domain.repository
import com.example.myticketapp.domain.model.Category
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getCategories(): Flow<Resource<List<Category>>>
    fun getEvents(): Flow<Resource<List<Event>>>
}