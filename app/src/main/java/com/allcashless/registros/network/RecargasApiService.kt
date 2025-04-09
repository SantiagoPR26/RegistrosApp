package com.allcashless.registros.network

import com.allcashless.registros.models.ManillaNFC
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecargasApiService {

    //MANILLAS
    @GET("recargas/manillas/{pId}")
    suspend fun findById(
        @Path("pId") pId: Long
    ): Response<ManillaNFC>

    @GET("recargas/manillas/escanear")
    suspend fun leerManillaNFC(@Query("pCodigoManilla") codigoManilla:String,
                               @Query("pEventoId") eventoId:Long
    ): Response<ManillaNFC>




}