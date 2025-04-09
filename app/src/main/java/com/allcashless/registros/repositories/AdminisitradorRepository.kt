package com.allcashless.registros.repositories

import com.allcashless.registros.models.Cliente
import com.allcashless.registros.models.Evento
import com.allcashless.registros.network.EventoATApiService
import com.allcashless.registros.network.RetrofitClient
import com.allcashless.registros.network.UsuariosATApiService
import retrofit2.Response

class AdminisitradorRepository (private val serviceEvento: EventoATApiService = RetrofitClient.eventoATApiService,
    private val serviceUsuarios: UsuariosATApiService = RetrofitClient.usuarioATApiService
) {

    suspend fun getCliente(pId: String): Response<Cliente> {
      return serviceUsuarios.getUsuario(pId)
    }

    suspend fun getEventoAT(pId: Long): Response<Evento> {
        return serviceEvento.findById(pId)
    }



}