package com.allcashless.registros.network


import com.allcashless.registros.models.auth.Auth
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OauthApiService {

    //AUTENTICACION
    @POST("security/oauth/token")
    suspend fun login(
        @Header("Authorization") authorization: String,
        @Body params: RequestBody
    ): Response<Auth>
}