package com.allcashless.registros.repositories

import com.allcashless.registros.models.Acceso
import com.allcashless.registros.network.AccesosApiService
import com.allcashless.registros.network.RetrofitClient
import retrofit2.Response


class AccesosRepository(private val service: AccesosApiService = RetrofitClient.accesosApiService) {

    suspend fun crearAcceso(codigoNFC: String,
                            eventoId: Long,
                            localidad: Long): Response<Acceso> {
        return service.crearAcceso(codigoNFC, eventoId, localidad)
    }


}
