package com.allcashless.registros.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allcashless.registros.models.Administrador
import com.allcashless.registros.models.Cliente
import com.allcashless.registros.models.Evento
import com.allcashless.registros.models.ManillaNFC

class GlobalDataViewModel() : ViewModel() {

    val manillaId = MutableLiveData<String?>()
    val evento = MutableLiveData<Evento>()
    val _manilla = MutableLiveData<ManillaNFC>()
    val administrador = MutableLiveData<Cliente>()
    val administradorId = MutableLiveData<String?>()

    fun setManillaId(id: String?) {
        manillaId.value = id
    }

    fun setManilla(manilla: ManillaNFC) {
        _manilla.value = manilla
    }

    fun setEvento(evento: Evento) {
        this.evento.value = evento
    }

    fun setAdministrador(administrador: Cliente) {
        this.administrador.value = administrador
    }

    fun setAdministradorId(id: String?) {
        administradorId.value = id
    }



}