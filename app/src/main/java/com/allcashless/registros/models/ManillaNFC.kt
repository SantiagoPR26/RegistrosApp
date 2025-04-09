package com.allcashless.registros.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class ManillaNFC(
    @SerializedName("id")
    val id: Long,

    @SerializedName("codigo")
    val codigo: String,

    @SerializedName("dinero")
    val dinero: Double,

    @SerializedName("estado")
    val estado: Int,

    @SerializedName("cliente")
    val cliente: Cliente? = null,  // Puede ser null

    @SerializedName("eventoId")
    val eventoId: Long
)
