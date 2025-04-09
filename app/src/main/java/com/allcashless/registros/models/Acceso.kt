package com.allcashless.registros.models

import kotlinx.serialization.Serializable

@Serializable
data class Acceso (
    val id: Long,
    val codigoNFC: String,
    val localidad: Localidad,
    val eventoId: Long,
)