package com.allcashless.registros.models.auth

data class JwtPayload(
    val authorities: List<String>,
    val user_name: String,
    val scope: List<String>,
    val nombre: String,
    val exp: Long,
    val jti: String,
    val client_id: String,
    val cc: String
)
