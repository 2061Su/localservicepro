package com.example.localserviceapp.di

import com.example.localserviceapp.data.repository.AuthRepository
import com.example.localserviceapp.data.repository.AuthRepositoryImpl
import com.example.localserviceapp.data.repository.BookingRepository
import com.example.localserviceapp.data.repository.BookingRepositoryImpl
import com.example.localserviceapp.data.repository.ServiceRepository
import com.example.localserviceapp.data.repository.ServiceRepositoryImpl
import com.example.localserviceapp.repository.ImageRepo
import com.example.localserviceapp.repository.ImageRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthRepository {
        return AuthRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideServiceRepository(firestore: FirebaseFirestore): ServiceRepository {
        return ServiceRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(firestore: FirebaseFirestore): BookingRepository {
        return BookingRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideImageRepo(): ImageRepo {
        return ImageRepoImpl()
    }
}
