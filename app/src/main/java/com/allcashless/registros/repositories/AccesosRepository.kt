package com.allcashless.registros.repositories

import com.allcashless.registros.models.Acceso
import com.allcashless.registros.models.CountResponse
import com.allcashless.registros.network.AccesosApiService
import com.allcashless.registros.network.RetrofitClient
import retrofit2.Response


class AccesosRepository(private val service: AccesosApiService = RetrofitClient.accesosApiService) {

    suspend fun crearAcceso(codigoNFC: String,
                            eventoId: Long,
                            localidadId: Long): Response<Acceso> {
        return service.crearAcceso(codigoNFC, eventoId, localidadId)
    }

    suspend fun contarAcreditacionesPorLocalidad(
        localidadId: Long
    ): Response<CountResponse> {
        return service.contarAcreditacionesPorLocalidad(localidadId)
    }

}
