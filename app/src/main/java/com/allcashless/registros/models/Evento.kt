package com.allcashless.registros.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Evento(

    val id:Long,

    val pulep:String,

    val nombre:String,

    val descripcion:String,

    val fecha: String,

    val fechaFin: String,

    val horaApertura:String,

    val horaFin:String,

    val horaInicio:String,

    val lugar:String,

    val tipo:String,

    val estado:String,

    val imagenes: List<ImagenEvento>,

    val ciudad:Ciudad?,

    val costoActivacion: Double
)
