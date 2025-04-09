package com.allcashless.registros.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class Cliente(
    @SerializedName("numeroDocumento")
    val numeroDocumento: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("celular")
    val celular: String,

    @SerializedName("dineroTotal")
    val dineroTotal: Double
)
