package com.allcashless.registros.view

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.allcashless.registros.R
import com.allcashless.registros.RegistrosApplication
import com.allcashless.registros.databinding.ActivityLecturaManillaBinding
import com.allcashless.registros.viewmodel.GlobalDataViewModel
import com.allcashless.registros.viewmodel.LecturaManillaViewModel
import kotlin.also
import kotlin.apply
import kotlin.collections.joinToString
import kotlin.jvm.java
import kotlin.let
import kotlin.text.format

class LecturaManillaActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityLecturaManillaBinding
    private val viewModel: LecturaManillaViewModel by viewModels()
    private val globalDataViewModel: GlobalDataViewModel by lazy {
        (application as RegistrosApplication).globalDataViewModel
    }
    private var nfcAdapter: NfcAdapter? = null
    private var localidadId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLecturaManillaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localidadId = intent.getLongExtra("localidadId", -1L)

        if (localidadId == -1L) {
            Toast.makeText(this, "Error: Datos no válidos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Configurar el TextView con el nombre de la localidad
        val nombreLocalidad = obtenerNombreLocalidad(localidadId)
        binding.textView.text = getString(R.string.realiza_lectura_manilla_localidad, nombreLocalidad)

        setupViews()
        setupNfcAdapter()
        setupButton()
        setupClearButton()
        checkNfcStatus()
        observeViewModel()
        setupBackButton()

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isReadingNfc.value == true) {
            enableReaderMode()
        }
    }

    override fun onPause() {
        super.onPause()
        disableReaderMode()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setupClearButton()
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRegistrar.text = getString(R.string.escanear)
        binding.materialCardView.visibility = View.GONE
        binding.cardInfoRegistrada.visibility = View.GONE

    }

    private fun obtenerNombreLocalidad(id: Long): String {
        return when(id) {
            1L -> getString(R.string.localidad_general)
            2L -> getString(R.string.localidad_vip)
            3L -> getString(R.string.localidad_backstage)
            4L -> getString(R.string.localidad_artistas)
            else -> getString(R.string.localidad_desconocida)
        }
    }

    private fun setupNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta NFC", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun enableReaderMode() {
        nfcAdapter?.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
            null
        )
    }

    private fun disableReaderMode() {
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag?.let { safeTag ->
            val stringId = bytesToHex(safeTag.id)
            runOnUiThread {
                viewModel.handleNfcTag(stringId)
            }
        }
    }

    private fun setupButton() {
        binding.btnRegistrar.setOnClickListener {
            when {
                viewModel.codigoNFC.value == null -> {
                    nfcAdapter?.let { adapter ->
                        if (adapter.isEnabled) {
                            viewModel.startNfcReading()
                        } else {
                            showNfcEnableDialog()
                        }
                    }
                }
                else -> {
                    viewModel.crearAcceso(1157, localidadId)
                }
            }
        }
    }

    private fun setupClearButton() {
        binding.btnLimpiar.setOnClickListener {
            viewModel.resetData()
            resetUI()
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            viewModel.resetData()
            val intent = Intent(this, EventosActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.isReadingNfc.observe(this) { isReading ->
            showLoadingState(isReading)
            if (isReading) enableReaderMode() else disableReaderMode()
        }

        viewModel.codigoNFC.observe(this) { codigo ->
            codigo?.let {
                globalDataViewModel.setManillaId(it)
                binding.apply {
                    tvManilla.text = it
                    materialCardView.visibility = View.VISIBLE
                    btnRegistrar.text = getString(R.string.registrar)
                    btnLimpiar.visibility = View.GONE
                }
            }
        }

        viewModel.registroExitoso.observe(this) { exito ->
            if (exito) {
                mostrarRegistroExitoso()
            }
        }

        viewModel.manillaRegistrada.observe(this) { manilla ->
            binding.tvManilla2.text = manilla
        }

        viewModel.localidadRegistrada.observe(this) { localidad ->
            binding.tvLocalidad.text = localidad
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.darkBackground.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegistrar.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                binding.apply {
                    errorContainer.visibility = View.VISIBLE
                    btnRegistrar.visibility = View.GONE
                    btnLimpiar.visibility = View.VISIBLE
                    materialCardView.visibility = View.GONE
                }
            }
        }


    }

    private fun mostrarRegistroExitoso() {
        binding.apply {
            cardInfoRegistrada.visibility = View.VISIBLE
            materialCardView.visibility = View.GONE
            btnRegistrar.visibility = View.GONE
            btnLimpiar.visibility = View.VISIBLE
            btnLimpiar.text = "Volver a Escanear"
            progressBar.visibility = View.GONE
            darkBackground.visibility = View.GONE
        }
    }

    private fun resetUI() {
        binding.apply {
            materialCardView.visibility = View.GONE
            cardInfoRegistrada.visibility = View.GONE
            errorContainer.visibility = View.GONE
            btnRegistrar.text = getString(R.string.escanear)
            btnRegistrar.visibility = View.VISIBLE
            btnLimpiar.visibility = View.GONE
            tvManilla.text = ""
            tvManilla2.text = ""
            tvLocalidad.text = ""
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString(":") { byte -> "%02X".format(byte) }
    }

    //Metodos UI

    private fun showLoadingState(show: Boolean) {
        binding.apply {
            tvLoadingMessage.visibility = if (show) View.VISIBLE else View.GONE
            btnRegistrar.isEnabled = !show
            darkBackground.visibility = if (show) View.VISIBLE else View.GONE
            if (show) tvLoadingMessage.text = getString(R.string.acercar_manilla)
        }
    }

    private fun checkNfcStatus() {
        nfcAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                showNfcEnableDialog()
            }
        }
    }

    private fun showNfcEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle("NFC Desactivado")
            .setMessage("El NFC es necesario para esta aplicación. ¿Deseas activarlo?")
            .setPositiveButton("Activar") { _, _ ->
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    this,
                    "NFC es necesario para usar esta función",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setCancelable(false)
            .show()
    }

}