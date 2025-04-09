package com.allcashless.registros.network

import com.allcashless.registros.models.Cliente
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UsuariosATApiService {

    @GET("/admin/{pId}")
    suspend fun getUsuario(@Path("pId") pId: String): Response<Cliente>

}