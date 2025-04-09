package com.allcashless.registros.models

import kotlinx.serialization.Serializable

@Serializable
data class Localidad (
    val id: Long,
    val nombre: String,
)
