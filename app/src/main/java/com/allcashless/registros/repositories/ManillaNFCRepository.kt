package com.allcashless.registros.repositories

import com.allcashless.registros.models.ManillaNFC
import com.allcashless.registros.network.RecargasApiService
import com.allcashless.registros.network.RetrofitClient


class ManillaNFCRepository(private val apiService: RecargasApiService = RetrofitClient.recargasApiService) {

    suspend fun fetchManillaInfo(codigoManilla: String, eventoId: Long): ManillaNFC {
        return try {
            val response = apiService.leerManillaNFC(codigoManilla, eventoId)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                throw Exception("Error al obtener la información de la manilla: Código ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor: ${e.message}")
        }
    }

    suspend fun findById(pId: Long): ManillaNFC {
        return try {
            val response = apiService.findById(pId)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                throw Exception("Error al obtener la manilla por ID: Código ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Error al conectar con el servidor: ${e.message}")
        }
    }

}





