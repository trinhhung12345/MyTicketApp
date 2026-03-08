package com.example.myticketapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.data.local.room.AppDatabase
import com.example.myticketapp.data.local.room.NotificationDao
import com.example.myticketapp.data.remote.api.AuthApi
import com.example.myticketapp.data.remote.api.BookingApi
import com.example.myticketapp.data.remote.api.ChatApi
import com.example.myticketapp.data.remote.api.HomeApi
import com.example.myticketapp.data.remote.api.OrderApi
import com.example.myticketapp.data.remote.api.UserApi
import com.example.myticketapp.data.repository.AuthRepositoryImpl
import com.example.myticketapp.data.repository.BookingRepositoryImpl
import com.example.myticketapp.data.repository.ChatRepositoryImpl
import com.example.myticketapp.data.repository.HomeRepositoryImpl
import com.example.myticketapp.data.repository.NotificationRepositoryImpl
import com.example.myticketapp.data.repository.OrderRepositoryImpl
import com.example.myticketapp.data.repository.UserRepositoryImpl
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.repository.BookingRepository
import com.example.myticketapp.domain.repository.ChatRepository
import com.example.myticketapp.domain.repository.HomeRepository
import com.example.myticketapp.domain.repository.NotificationRepository
import com.example.myticketapp.domain.repository.OrderRepository
import com.example.myticketapp.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideBookingApi(retrofit: Retrofit): BookingApi {
        return retrofit.create(BookingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
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

    @Provides
    @Singleton
    fun provideBookingRepository(api: BookingApi): BookingRepository {
        return BookingRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: UserApi): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(api: OrderApi): OrderRepository {
        return OrderRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideChatRepository(api: ChatApi): ChatRepository {
        return ChatRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tixcon_db"
        ).build()
    }

    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao

    @Provides
    @Singleton
    fun provideNotificationRepository(dao: NotificationDao): NotificationRepository {
        return NotificationRepositoryImpl(dao)
    }
}
