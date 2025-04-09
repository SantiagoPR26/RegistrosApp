package com.allcashless.registros.viewmodel

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allcashless.registros.models.Usuario
import com.allcashless.registros.models.auth.Auth
import com.allcashless.registros.models.auth.JwtPayload
import com.allcashless.registros.repositories.OauthRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.let
import kotlin.text.split

class LoginViewModel() : ViewModel() {

    private val authRepository = OauthRepository()


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _auth = MutableLiveData<Auth?>()
    val auth: LiveData<Auth?> = _auth

    private val _jwtPayload = MutableLiveData<JwtPayload>()
    val jwtPayload: LiveData<JwtPayload> = _jwtPayload

    fun login(usuario: Usuario) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null

        try {
            val response = authRepository.login(usuario)

            if (response.isSuccessful) {
                response.body()?.let { auth ->
                    val jwtPayload = decodeJwtPayload(auth.access_token)
                    _jwtPayload.value = jwtPayload

                    if (jwtPayload.authorities.contains("ROLE_ADMIN")) {
                        _auth.value = auth
                    } else {
                        _error.value = "Usuario no autorizado."
                    }
                }
            } else {
                _error.value = "Error de autenticación"
            }
        } catch (_: Exception) {
            _error.value = "Error de conexión"
        } finally {
            _isLoading.value = false
        }
    }

    //Obtiene el payload del token
    fun decodeJwtPayload(jwt: String): JwtPayload {
        val parts = jwt.split(".")
        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
        val payloadData = Gson().fromJson(payload, JwtPayload::class.java)
        return payloadData
    }

}