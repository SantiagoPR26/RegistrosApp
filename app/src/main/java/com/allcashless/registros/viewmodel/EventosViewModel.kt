package com.allcashless.registros.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allcashless.registros.models.Evento
import com.allcashless.registros.repositories.AdminisitradorRepository
import kotlinx.coroutines.launch

class EventosViewModel() : ViewModel() {

    private val repository = AdminisitradorRepository()

    private val _evento = MutableLiveData<Evento>()
    val evento: LiveData<Evento> = _evento

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun cargarEvento(eventoId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getEventoAT(eventoId)
                if (response.isSuccessful) {
                    response.body()?.let { evento ->
                        _evento.value = evento
                    } ?: run {
                        _error.value = "Evento no encontrado"
                    }
                } else {
                    _error.value = "Error al cargar evento: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}