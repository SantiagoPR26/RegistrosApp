package com.allcashless.registros.viewmodel



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allcashless.registros.repositories.AccesosRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LecturaManillaViewModel : ViewModel() {

    // Estados para NFC
    private val _isReadingNfc = MutableLiveData<Boolean>(false)
    val isReadingNfc: LiveData<Boolean> = _isReadingNfc

    // Datos del brazalete
    private val _codigoNFC = MutableLiveData<String?>(null)
    val codigoNFC: LiveData<String?> = _codigoNFC

    // Estados del registro
    private val _registroExitoso = MutableLiveData<Boolean>(false)
    val registroExitoso: LiveData<Boolean> = _registroExitoso

    // Datos para mostrar en la card
    private val _manillaRegistrada = MutableLiveData<String>()
    val manillaRegistrada: LiveData<String> = _manillaRegistrada

    private val _localidadRegistrada = MutableLiveData<String>()
    val localidadRegistrada: LiveData<String> = _localidadRegistrada

    // Estados de error y carga
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var timeoutJob: Job? = null
    private val accesosRepository = AccesosRepository()

    fun startNfcReading() {
        _isReadingNfc.value = true
        _error.value = null
        startTimeout()
    }

    fun stopNfcReading() {
        timeoutJob?.cancel()
        _isReadingNfc.value = false
    }

    fun handleNfcTag(id: String) {
        stopNfcReading()
        _codigoNFC.value = id
    }

    fun crearAcceso(eventoId: Long, localidadId: Long) {
        val codigo = _codigoNFC.value ?: run {
            _error.value = "No se ha escaneado ningún brazalete"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = accesosRepository.crearAcceso(
                    codigoNFC = codigo,
                    eventoId = eventoId,
                    localidadId = localidadId
                )

                if (response.isSuccessful) {
                    _manillaRegistrada.value = codigo
                    _localidadRegistrada.value = getNombreLocalidad(localidadId)
                    _registroExitoso.value = true
                } else {
                    _error.value = "Error al registrar: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getNombreLocalidad(localidadId: Long): String {
        return when(localidadId) {
            1L -> "GENERAL"
            2L -> "VIP"
            3L -> "BACKSTAGE"
            4L -> "ARTISTAS"
            else -> "Localidad $localidadId"
        }
    }

    fun resetData() {
        _codigoNFC.value = null
        _error.value = null
        _registroExitoso.value = false
        _isReadingNfc.value = false
    }

    private fun startTimeout() {
        timeoutJob = viewModelScope.launch {
            delay(20000)
            if (_isReadingNfc.value == true) {
                stopNfcReading()

            }
        }
    }

}