package com.allcashless.registros.models

import kotlinx.serialization.Serializable

@Serializable
data class Administrador (

    val numeroDocumento: String,
    val nombre: String,
    val correo: String,

)

