package ru.netology.nework.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var token: String?
        get() {
            val token = preferences.getString(KEY_TOKEN, null)
            android.util.Log.d("AuthTokenStorage", "Getting token: ${token?.take(10)}...")
            return token
        }
        set(value) {
            android.util.Log.d("AuthTokenStorage", "Setting token: ${value?.take(10)}...")
            preferences.edit().putString(KEY_TOKEN, value).apply()
        }

    var apiKey: String?
        get() = preferences.getString(KEY_API_KEY, null)
        set(value) {
            preferences.edit().putString(KEY_API_KEY, value).apply()
        }

    var userId: Long
        get() = preferences.getLong(KEY_USER_ID, 0L)
        set(value) {
            preferences.edit().putLong(KEY_USER_ID, value).apply()
        }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_API_KEY = "api_key"
    }
}



