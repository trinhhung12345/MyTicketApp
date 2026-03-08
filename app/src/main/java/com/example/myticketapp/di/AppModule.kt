package com.example.myticketapp.di

import android.content.Context
import com.example.myticketapp.BuildConfig
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.data.local.room.NotificationDao
import com.example.myticketapp.data.remote.AuthInterceptor
import com.example.myticketapp.data.remote.socket.StompSocketManager
import com.example.myticketapp.domain.repository.SocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSocketService(
        okHttpClient: OkHttpClient,
        notificationDao: NotificationDao,
        tokenDataStore: TokenDataStore
    ): SocketService {
        val wsUrl = BuildConfig.API_BASE_URL
            .replace("https://", "wss://")
            .replace("http://", "ws://")
            .trimEnd('/')
            .plus("/ws")
        return StompSocketManager(okHttpClient, notificationDao, tokenDataStore, wsUrl)
    }
}
