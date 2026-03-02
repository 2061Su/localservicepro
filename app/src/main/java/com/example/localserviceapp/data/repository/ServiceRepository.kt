package com.example.localserviceapp.data.repository

import com.example.localserviceapp.data.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun addService(service: Service): Flow<Result<Unit>>
    fun getServices(): Flow<Result<List<Service>>>
    fun getService(serviceId: String): Flow<Result<Service?>>
    fun updateService(service: Service): Flow<Result<Unit>>
    fun deleteService(serviceId: String): Flow<Result<Unit>>
}
