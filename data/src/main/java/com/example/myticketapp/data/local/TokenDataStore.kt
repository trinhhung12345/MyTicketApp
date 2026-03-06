package com.example.myticketapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Extension property để tạo DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Singleton class quản lý việc lưu trữ và đọc dữ liệu User từ DataStore
 * Lưu trữ: Token, Email, Address
 */
@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
    }

    /**
     * Lưu access token và email vào DataStore
     */
    suspend fun saveToken(token: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
            preferences[USER_EMAIL_KEY] = email
        }
    }

    /**
     * Lưu địa chỉ user vào DataStore
     */
    suspend fun saveAddress(address: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ADDRESS_KEY] = address
        }
    }

    /**
     * Đọc địa chỉ user từ DataStore dưới dạng Flow
     */
    val userAddress: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_ADDRESS_KEY] ?: ""
        }

    /**
     * Đọc access token từ DataStore dưới dạng Flow
     */
    val accessToken: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }

    /**
     * Đọc email từ DataStore dưới dạng Flow
     */
    val userEmail: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_EMAIL_KEY]
        }

    /**
     * Xóa token và email khỏi DataStore (dùng khi logout)
     */
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
    }

    /**
     * Xóa toàn bộ dữ liệu khi đăng xuất
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Kiểm tra xem token đã tồn tại chưa (dùng để check auto login)
     */
    val hasToken: Flow<Boolean> = accessToken.map { token ->
        !token.isNullOrEmpty()
    }
}
