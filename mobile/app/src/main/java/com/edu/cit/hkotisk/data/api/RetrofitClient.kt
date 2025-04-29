package com.edu.cit.hkotisk.data.api

import android.content.Context
import android.util.Log
import com.edu.cit.hkotisk.data.api.AuthService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080"  // Android emulator special alias for host's localhost

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Add Authorization header interceptor
    fun getToken(context: Context): String? {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("token", null)
    }

    private fun createAuthInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val token = getToken(context)

            val requestBuilder = originalRequest.newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
    }

    private fun createAuthClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .followRedirects(true)  // Let OkHttp handle redirects automatically
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
    }

    private fun createAuthenticatedClientBuilder(context: Context): OkHttpClient.Builder {
        return createAuthClientBuilder()
            .addInterceptor(createAuthInterceptor(context))
    }

    fun createAuthService(context: Context): AuthService {
        val okHttpClient = createAuthClientBuilder().build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    fun createProductService(context: Context): ProductService {
        val okHttpClient = createAuthenticatedClientBuilder(context).build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductService::class.java)
    }
}
