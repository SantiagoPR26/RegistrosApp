package com.allcashless.registros.repositories

import android.util.Base64
import com.allcashless.registros.models.Usuario
import com.allcashless.registros.models.auth.Auth
import com.allcashless.registros.network.OauthApiService
import com.allcashless.registros.network.RetrofitClient
import okhttp3.FormBody
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.text.padStart
import kotlin.text.toByteArray

class OauthRepository(private val apiService: OauthApiService = RetrofitClient.oauthApiService) {

    suspend fun login(usuario: Usuario): Response<Auth> {

        val credentials = "alltickets.front:l!Uq!Ujhfzyjd%Mk*a6H"
        val basicAuth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        // Crear MD5 de la contraseña
        val md = MessageDigest.getInstance("MD5")
        val passwordMd5 = BigInteger(1, md.digest(usuario.contrasena.toByteArray()))
            .toString(16)
            .padStart(32, '0')

        // Crear parametros necesarios para el metodo de autenticacion
        val formBody = FormBody.Builder()
            .add("grant_type", "password")
            .add("username", usuario.usuario)
            .add("password", passwordMd5)
            .build()

        return apiService.login(basicAuth, formBody)
    }
}