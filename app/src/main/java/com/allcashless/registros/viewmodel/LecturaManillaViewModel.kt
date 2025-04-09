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

    private val _isReadingNfc = MutableLiveData<Boolean>(false)
    val isReadingNfc: LiveData<Boolean> = _isReadingNfc

    private val _codigoNFC = MutableLiveData<String?>(null)
    val codigoNFC: LiveData<String?> = _codigoNFC

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _showCard = MutableLiveData<Boolean>(false)
    val showCard: LiveData<Boolean> = _showCard

    private val _isCreatingAccess = MutableLiveData<Boolean>(false)
    val isCreatingAccess: LiveData<Boolean> = _isCreatingAccess

    private val _accessCreated = MutableLiveData<Boolean?>(null)
    val accessCreated: LiveData<Boolean?> = _accessCreated

    private val _manillaRegistrada = MutableLiveData<String?>(null)
    val manillaRegistrada: LiveData<String?> = _manillaRegistrada

    private val _registroExitoso = MutableLiveData<Boolean>(false)
    val registroExitoso: LiveData<Boolean> = _registroExitoso

    private val _localidadRegistrada = MutableLiveData<String?>(null)
    val localidadRegistrada: LiveData<String?> = _localidadRegistrada

    private var timeoutJob: Job? = null
    private val accesosRepository = AccesosRepository()

    private val localidadesMap = mapOf(
        "GENERAL" to 1L,
        "VIP" to 2L,
        "BACKSTAGE" to 3L,
        "ARTISTAS" to 4L
    )

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
        _showCard.value = true
    }

    fun resetData() {
        _codigoNFC.value = null
        _error.value = null
        _showCard.value = false
        _isReadingNfc.value = false
    }

    private fun startTimeout() {
        timeoutJob = viewModelScope.launch {
            delay(20000)
            if (_isReadingNfc.value == true) {
                stopNfcReading()
                _error.value = "Tiempo de lectura agotado"
            }
        }
    }

    fun crearAcceso(eventoId: Long, localidadId: Long) {
        val codigoNFC = _codigoNFC.value

        if (codigoNFC == null || codigoNFC.isEmpty()) {
            _error.value = "No se ha escaneado ningún brazalete"
            return
        }

        if (localidadId == 0L) {
            _error.value = "Seleccione una localidad válida"
            return
        }

        _isCreatingAccess.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = accesosRepository.crearAcceso(
                    codigoNFC = codigoNFC,
                    eventoId = eventoId,
                    localidad = localidadId
                )

                if (response.isSuccessful) {
                    val acceso = response.body()
                    _manillaRegistrada.value = acceso?.codigoNFC
                    _localidadRegistrada.value  = acceso?.localidad?.nombre
                    _accessCreated.value = true
                } else {
                    _error.value = "Error al crear acceso: ${response.message()}"
                    _accessCreated.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                _accessCreated.value = false
            } finally {
                _isCreatingAccess.value = false
            }
        }
    }

}