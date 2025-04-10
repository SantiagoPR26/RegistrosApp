package com.allcashless.registros.network

import com.allcashless.registros.models.Acceso
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface AccesosApiService {

    @POST("acreditaciones/acreditaciones/crear/{codigoNFC}")
    suspend fun crearAcceso(@Path("codigoNFC") codigoNFC: String,
                                  @Query("pId") pId: Long,
                            @Query("localidadId") localidadId: Long): Response<Acceso>


}