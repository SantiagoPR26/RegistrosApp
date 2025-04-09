package com.allcashless.registros.network

import com.allcashless.registros.models.Evento
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventoATApiService {

    @GET("eventos/{pId}")
    suspend fun findById(@Path("pId") pId: Long): Response<Evento>

}