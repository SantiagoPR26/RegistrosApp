package com.allcashless.registros.network

import com.allcashless.registros.util.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.apply
import kotlin.jvm.java

object RetrofitClient{
    internal const val BASE_URL = "http://back.allcashlesscol.com/api/acreditaciones/"
    internal const val EVENTO_AT_BASE_URL = "https://back2.allticketscol.com/api/eventos/"
    internal const val USUARIOS_AT_BASE_URL = "https://back.allticketscol.com/api/usuarios/"
    internal const val AUTH_AT_BASE_URL = "https://back.allticketscol.com/api/security/"

    private val authInterceptor = Interceptor { chain ->
        val token = SessionManager.getToken()
        val request: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitEventoAT = Retrofit.Builder()
        .baseUrl(EVENTO_AT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitUsuarioAT = Retrofit.Builder()
        .baseUrl(USUARIOS_AT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitAuthAT = Retrofit.Builder()
        .baseUrl(AUTH_AT_BASE_URL)
        .client(authOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()




    val accesosApiService: AccesosApiService = retrofit.create(AccesosApiService::class.java)
    val eventoATApiService: EventoATApiService = retrofitEventoAT.create(EventoATApiService::class.java)
    val usuarioATApiService: UsuariosATApiService = retrofitUsuarioAT.create(UsuariosATApiService::class.java)
    val oauthApiService: OauthApiService = retrofitAuthAT.create(OauthApiService::class.java)

}