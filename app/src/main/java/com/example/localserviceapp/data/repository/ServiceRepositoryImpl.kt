package com.example.localserviceapp.data.repository

import com.example.localserviceapp.data.model.Service
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ServiceRepository {

    override fun addService(service: Service): Flow<Result<Unit>> = flow {
        firestore.collection("services").document(service.id).set(service).await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun getServices(): Flow<Result<List<Service>>> = flow {
        val snapshot = firestore.collection("services").get().await()
        val services = snapshot.toObjects(Service::class.java)
        emit(Result.success(services))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun getService(serviceId: String): Flow<Result<Service?>> = flow {
        val document = firestore.collection("services").document(serviceId).get().await()
        val service = document.toObject(Service::class.java)
        emit(Result.success(service))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun updateService(service: Service): Flow<Result<Unit>> = flow {
        firestore.collection("services").document(service.id).set(service).await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun deleteService(serviceId: String): Flow<Result<Unit>> = flow {
        firestore.collection("services").document(serviceId).delete().await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }
}
