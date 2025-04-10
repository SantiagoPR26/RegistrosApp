package com.allcashless.registros.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectorLocalidadViewModel : ViewModel() {

    private val _localidadSeleccionada = MutableLiveData<Long?>()
    val localidadSeleccionada: LiveData<Long?> = _localidadSeleccionada

    fun seleccionarLocalidad(localidadId: Long) {
        _localidadSeleccionada.value = localidadId
    }

    fun resetSeleccion() {
        _localidadSeleccionada.value = null
    }

}