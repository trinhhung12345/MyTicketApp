package com.example.myticketapp.data.di

import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.data.remote.api.AuthApi
import com.example.myticketapp.data.remote.api.HomeApi
import com.example.myticketapp.data.repository.AuthRepositoryImpl
import com.example.myticketapp.data.repository.HomeRepositoryImpl
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, tokenDataStore: TokenDataStore): AuthRepository {
        return AuthRepositoryImpl(api, tokenDataStore)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(api: HomeApi): HomeRepository {
        return HomeRepositoryImpl(api)
    }
}
