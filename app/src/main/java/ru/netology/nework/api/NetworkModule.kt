package ru.netology.nework.api

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Protocol
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.netology.nework.R
import javax.inject.Named
import javax.inject.Singleton
import ru.netology.nework.auth.AuthTokenStorage

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("api_key")
    fun provideApiKey(@ApplicationContext context: Context): String {
        val apiKey = context.getString(R.string.api_key)
        android.util.Log.d("NetworkModule", "API Key: ${apiKey.take(10)}...")
        return apiKey
    }

    @Provides
    @Singleton
    @Named("base_url")
    fun provideBaseUrl(@ApplicationContext context: Context): String {
        val baseUrl = context.getString(R.string.base_url)
        android.util.Log.d("NetworkModule", "Base URL: $baseUrl")
        return baseUrl
    }

           @Provides
           @Singleton
           @Named("auth")
           fun provideAuthInterceptor(@Named("api_key") apiKey: String): Interceptor {
               return Interceptor { chain ->
                   val originalRequest = chain.request()
                   val url = originalRequest.url.toString()
                   val method = originalRequest.method
                   
                   android.util.Log.d("AuthInterceptor", "Request: $method $url")
                   android.util.Log.d("AuthInterceptor", "API Key preview: ${apiKey.take(10)}...")
                   
                   val request = originalRequest.newBuilder()
                       .addHeader("Accept", "application/json")
                       .addHeader("User-Agent", "NeWork-Android-App")
                       .addHeader("Connection", "keep-alive")
                       .addHeader("Api-Key", apiKey)
                       .build()
                       
                   android.util.Log.d("AuthInterceptor", "Request headers:")
                   request.headers.forEach { header ->
                       if (header.first == "Api-Key") {
                           android.util.Log.d("AuthInterceptor", "  ${header.first}: ${header.second.take(10)}...")
                       } else {
                           android.util.Log.d("AuthInterceptor", "  ${header.first}: ${header.second}")
                       }
                   }
                   
                   val response = chain.proceed(request)
                   android.util.Log.d("AuthInterceptor", "Response: ${response.code} ${response.message}")
                   
                   if (response.code == 403) {
                       android.util.Log.e("AuthInterceptor", "403 Forbidden - check API key and Bearer token")
                   }
                   
                   response
               }
           }

           @Provides
           @Singleton
           @Named("bearer")
           fun provideBearerInterceptor(tokenStorage: AuthTokenStorage): Interceptor {
               return Interceptor { chain ->
                   val originalRequest = chain.request()
                   val url = originalRequest.url.toString()
                   val method = originalRequest.method
                   val token = tokenStorage.token
                   
                   android.util.Log.d("BearerInterceptor", "Request: $method $url")
                   android.util.Log.d("BearerInterceptor", "Token available: ${!token.isNullOrBlank()}")
                   if (!token.isNullOrBlank()) {
                       android.util.Log.d("BearerInterceptor", "Token preview: ${token.take(20)}...")
                   }
                   
                   val requiresAuth = (url.contains("/api/posts") && method == "POST") ||
                           (url.contains("/api/events") && method == "POST") ||
                           url.contains("/api/my/") ||
                           url.contains("/likes") ||
                           (url.contains("/comments") && method == "POST") ||
                           url.contains("/api/media") ||
                           url.contains("/participants")

                   android.util.Log.d("BearerInterceptor", "Requires auth: $requiresAuth")
                   
                   val builder = originalRequest.newBuilder()
                   
                   if (requiresAuth) {
                       if (!token.isNullOrBlank()) {
                           builder.addHeader("Authorization", "Bearer $token")
                           android.util.Log.d("BearerInterceptor", "Added Authorization: Bearer header")
                       } else {
                           android.util.Log.e("BearerInterceptor", "ERROR: Auth required but token is null or blank!")
                       }
                   }
                   
                   val request = builder.build()
                   android.util.Log.d("BearerInterceptor", "Final request headers:")
                   request.headers.forEach { header ->
                       if (header.first == "Authorization") {
                           android.util.Log.d("BearerInterceptor", "  ${header.first}: ${header.second.take(27)}...")
                       } else {
                           android.util.Log.d("BearerInterceptor", "  ${header.first}: ${header.second}")
                       }
                   }
                   
                   try {
                       val response = chain.proceed(request)
                       android.util.Log.d("BearerInterceptor", "Response: ${response.code} ${response.message}")
                       
                       if (!response.isSuccessful) {
                           android.util.Log.e("BearerInterceptor", "Request failed: ${response.code}")
                           if (response.code == 403) {
                               android.util.Log.e("BearerInterceptor", "403 Forbidden - check token and API key")
                           }
                       }
                       
                       response
                   } catch (e: Exception) {
                       android.util.Log.e("BearerInterceptor", "Exception during request", e)
                       throw e
                   }
               }
           }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("chunked_disable")
    fun provideChunkedDisableInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .removeHeader("Transfer-Encoding")
                .addHeader("Connection", "close")
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("auth") authInterceptor: Interceptor,
        @Named("bearer") bearerInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(bearerInterceptor)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .followRedirects(false)
            .followSslRedirects(false)
            .protocols(listOf(Protocol.HTTP_1_1))
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("base_url") baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSimpleHttpClient(
        @Named("base_url") baseUrl: String,
        @Named("api_key") apiKey: String,
        gson: Gson
    ): SimpleHttpClient {
        return SimpleHttpClient(baseUrl, apiKey, gson)
    }

    @Provides
    @Singleton
    fun provideMediaApiService(retrofit: Retrofit): MediaApiService {
        return retrofit.create(MediaApiService::class.java)
    }
}
