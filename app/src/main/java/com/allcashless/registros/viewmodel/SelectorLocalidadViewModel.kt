package com.allcashless.registros.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allcashless.registros.repositories.AccesosRepository
import kotlinx.coroutines.launch

class SelectorLocalidadViewModel() : ViewModel() {

    private val _localidadSeleccionada = MutableLiveData<Long?>()
    val localidadSeleccionada: LiveData<Long?> = _localidadSeleccionada

    private val _contadores = MutableLiveData<Map<Long, Long>>() // Mapa de localidadId a count
    val contadores: LiveData<Map<Long, Long>> = _contadores

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val accesosRepository = AccesosRepository()

    init {
        cargarContadoresTodasLocalidades()
    }

    fun seleccionarLocalidad(localidadId: Long) {
        _localidadSeleccionada.value = localidadId
    }

    fun resetSeleccion() {
        _localidadSeleccionada.value = null
    }

    private fun cargarContadoresTodasLocalidades() {
        viewModelScope.launch {
            _loading.value = true
            try {
                // IDs de las localidades (ajusta según tus necesidades)
                val localidadesIds = listOf(1L, 2L, 3L, 4L) // GENERAL, VIP, BACKSTAGE, ARTISTAS

                val contadoresMap = mutableMapOf<Long, Long>()

                localidadesIds.forEach { localidadId ->
                    val response = accesosRepository.contarAcreditacionesPorLocalidad(localidadId)
                    if (response.isSuccessful) {
                        contadoresMap[localidadId] = response.body()?.count ?: 0L
                    } else {
                        _error.value = "Error al obtener conteo para localidad $localidadId"
                    }
                }

                _contadores.value = contadoresMap

            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getContadorLocalidad(localidadId: Long): Long {
        return _contadores.value?.get(localidadId) ?: 0L
    }
}