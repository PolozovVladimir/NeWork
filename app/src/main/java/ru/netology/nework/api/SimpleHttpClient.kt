package ru.netology.nework.api

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import ru.netology.nework.dto.AuthDto

class SimpleHttpClient(
    private val baseUrl: String,
    private val apiKey: String,
    private val gson: Gson
) {
    private val client = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .retryOnConnectionFailure(true)
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .removeHeader("Transfer-Encoding")
                .addHeader("Connection", "close")
                .build()
            chain.proceed(request)
        }
        .build()

    suspend fun login(login: String, password: String): Result<AuthDto> = withContext(Dispatchers.IO) {
        try {
            Log.d("SimpleHttpClient", "Попытка авторизации: login=$login")

            Log.d("SimpleHttpClient", "Form данные для авторизации: login=$login, password=***")

            val formBody = FormBody.Builder()
                .add("login", login)
                .add("password", password)
                .build()

            val requestBody = formBody
            val url = "${baseUrl}api/users/authentication".toHttpUrl()
                       
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Api-Key", apiKey)
                .build()

            val response = client.newCall(request).execute()
            Log.d("SimpleHttpClient", "Ответ сервера: code=${response.code}, isSuccessful=${response.isSuccessful}")

            if (response.isSuccessful) {
                val responseBody = try {
                    response.body?.string() ?: ""
                } catch (e: Exception) {
                    Log.e("SimpleHttpClient", "Ошибка чтения тела ответа", e)
                    ""
                }
                Log.d("SimpleHttpClient", "Тело ответа: $responseBody")
                
                if (responseBody.isBlank()) {
                    Log.e("SimpleHttpClient", "Empty response body for successful login - treating as failure")
                    Result.failure(Exception("Empty response from server"))
                } else {
                    val authDto = parseAuthResponse(responseBody)
                    Log.d("SimpleHttpClient", "Успешная авторизация: id=${authDto.id}")
                    Result.success(authDto)
                }
            } else {
                Log.e("SimpleHttpClient", "Ошибка авторизации: code=${response.code}")

                val headers = response.headers
                Log.d("SimpleHttpClient", "Заголовки ответа: ${headers.toMultimap()}")
                Log.d("SimpleHttpClient", "URL запроса: ${request.url}")
                Log.d("SimpleHttpClient", "Метод запроса: ${request.method}")
                Log.d("SimpleHttpClient", "Заголовки запроса: ${request.headers.toMultimap()}")
                
                       val errorBody = try {
                           response.body?.string() ?: "Empty response body"
                       } catch (e: java.io.EOFException) {
                           "EOFException when reading error body"
                       } catch (e: Exception) {
                           "Exception when reading error body: ${e.message}"
                       }
                Log.d("SimpleHttpClient", "Тело ответа при ошибке: $errorBody")
                
                val errorMessage = when (response.code) {
                    404 -> "Сервер не найден. Проверьте подключение к интернету."
                    415 -> "Сервер ожидает данные в формате form-data, а не JSON."
                    400 -> "Некорректные данные для авторизации. Ответ сервера: $errorBody"
                    401 -> "Неправильный логин или пароль."
                    else -> "Ошибка авторизации: ${response.code}. Ответ сервера: $errorBody"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("SimpleHttpClient", "Исключение при авторизации", e)
            Result.failure(e)
        }
    }

    suspend fun register(login: String, password: String, name: String): Result<AuthDto> = withContext(Dispatchers.IO) {
        try {
            Log.d("SimpleHttpClient", "Попытка регистрации: login=$login, name=$name")

            Log.d("SimpleHttpClient", "Form данные для регистрации: login=$login, name=$name, password=***")

            val formBody = FormBody.Builder()
                .add("login", login)
                .add("password", password)
                .add("name", name)
                .build()

            // Логируем содержимое FormBody для отладки
            val formBodyString = buildString {
                for (i in 0 until formBody.size) {
                    append("${formBody.name(i)}=${formBody.value(i)}&")
                }
            }.removeSuffix("&")
            Log.d("SimpleHttpClient", "FormBody содержимое: $formBodyString")

            val requestBody = formBody
            val url = "${baseUrl}api/users/registration".toHttpUrl()
                       
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Api-Key", apiKey)
                .build()

            val response = client.newCall(request).execute()
            Log.d("SimpleHttpClient", "Ответ сервера: code=${response.code}, isSuccessful=${response.isSuccessful}")

            if (response.isSuccessful) {
                val responseBody = try {
                    response.body?.string() ?: ""
                } catch (e: Exception) {
                    Log.e("SimpleHttpClient", "Ошибка чтения тела ответа", e)
                    ""
                }
                Log.d("SimpleHttpClient", "Тело ответа: $responseBody")
                
                if (responseBody.isBlank()) {
                    Log.e("SimpleHttpClient", "Empty response body for successful registration - treating as failure")
                    Result.failure(Exception("Empty response from server"))
                } else {
                    val authDto = parseAuthResponse(responseBody)
                    Log.d("SimpleHttpClient", "Успешная регистрация: id=${authDto.id}")
                    Result.success(authDto)
                }
            } else {
                Log.e("SimpleHttpClient", "Ошибка регистрации: code=${response.code}")

                val headers = response.headers
                Log.d("SimpleHttpClient", "Заголовки ответа: ${headers.toMultimap()}")

                Log.d("SimpleHttpClient", "URL запроса: ${request.url}")
                Log.d("SimpleHttpClient", "Метод запроса: ${request.method}")
                Log.d("SimpleHttpClient", "Заголовки запроса: ${request.headers.toMultimap()}")
                
                val errorBody = try {
                    val body = response.body
                    if (body != null) {
                        val contentLength = body.contentLength()
                        Log.d("SimpleHttpClient", "Content-Length: $contentLength")
                        // Попробуем прочитать ответ по частям
                        val source = body.source()
                        source.request(Long.MAX_VALUE)
                        val buffer = source.buffer
                        val responseBody = buffer.clone().readUtf8()
                        Log.d("SimpleHttpClient", "Raw response body: $responseBody")
                        responseBody
                    } else {
                        "Response body is null"
                    }
                } catch (e: java.io.EOFException) {
                    Log.w("SimpleHttpClient", "EOFException при чтении тела ответа", e)
                    "Сервер закрыл соединение преждевременно (EOFException)"
                } catch (e: Exception) {
                    Log.w("SimpleHttpClient", "Ошибка при чтении тела ответа", e)
                    "Ошибка чтения ответа: ${e.message}"
                }
                Log.d("SimpleHttpClient", "Тело ответа при ошибке: $errorBody")
                
                val errorMessage = when (response.code) {
                    404 -> "Сервер не найден. Проверьте подключение к интернету."
                    415 -> "Сервер ожидает данные в формате form-data, а не JSON."
                    400 -> "Некорректные данные для регистрации. Код ошибки: 400. Детали: $errorBody"
                    409 -> "Пользователь с таким логином уже зарегистрирован."
                    else -> "Ошибка регистрации: ${response.code}. Детали: $errorBody"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("SimpleHttpClient", "Исключение при регистрации", e)
            Result.failure(e)
        }
    }

    private fun parseAuthResponse(json: String): AuthDto {
        return try {
            Log.d("SimpleHttpClient", "Parsing JSON: $json")
            val result = gson.fromJson(json, AuthDto::class.java)
            Log.d("SimpleHttpClient", "Parsed AuthDto: id=${result.id}, token=${result.token.take(10)}...")
            result
        } catch (e: Exception) {
            Log.e("SimpleHttpClient", "Ошибка парсинга JSON: $json", e)
            throw Exception("Failed to parse authentication response: ${e.message}")
        }
    }
}
