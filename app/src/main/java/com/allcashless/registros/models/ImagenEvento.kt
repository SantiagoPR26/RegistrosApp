package com.allcashless.registros.models

import kotlinx.serialization.Serializable

@Serializable
data class ImagenEvento(

val id:Long,

val name:String,

val url:String,

val tipo:Int,

)
