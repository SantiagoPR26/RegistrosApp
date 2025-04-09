package com.allcashless.registros.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val usuario: String,
    val contrasena:String,
    val numeroDocumento: String,
    val nombre :String
)