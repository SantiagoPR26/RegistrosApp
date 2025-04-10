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
    private var eventoId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLecturaManillaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventoId = intent.getLongExtra("eventoId", -1L).also {
            if (it == -1L) {
                Toast.makeText(this, "Error: Evento no válido", Toast.LENGTH_LONG).show()
                finish()
            }
        }

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
        disableNfcReading()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isReadingNfc.value == true) {
            disableNfcReading()
        }
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
    }

    private fun setupNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta NFC", Toast.LENGTH_LONG).show()
            finish()
            return
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
        binding.btnRegistrar.text = getString(R.string.registrar)
        binding.btnRegistrar.setOnClickListener {
            when {
                viewModel.codigoNFC.value == null -> {
                    // Modo escaneo inicial
                    nfcAdapter?.let { adapter ->
                        if (adapter.isEnabled) {
                            viewModel.startNfcReading()
                        } else {
                            showNfcEnableDialog()
                        }
                    }
                }
                binding.LocalidadRadioGroup.checkedRadioButtonId == -1 -> {
                    Toast.makeText(this, "Seleccione una localidad", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.btnRegistrar.visibility = View.VISIBLE
                    binding.containerLocalidades.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.darkBackground.visibility = View.VISIBLE

                    val selectedId = binding.LocalidadRadioGroup.checkedRadioButtonId
                    val localidadId = when (selectedId) {
                        R.id.RadioButton1 -> 1L
                        R.id.RadioButton2 -> 2L
                        R.id.RadioButton3 -> 3L
                        R.id.RadioButton4 -> 4L
                        else -> 0L
                    }

                    viewModel.crearAcceso(eventoId, localidadId)
                }
            }
        }
    }

    private fun setupClearButton() {
        binding.btnLimpiar.setOnClickListener {
            viewModel.resetData()
            binding.materialCardView.visibility = View.GONE
            binding.tvManilla.visibility = View.GONE
            binding.cardInfoRegistrada.visibility = View.GONE
            binding.btnLimpiar.visibility = View.GONE
            binding.btnRegistrar.text = getString(R.string.registrar)
            binding.btnRegistrar.visibility = View.VISIBLE
            globalDataViewModel.setManillaId(null)
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            viewModel.resetData()
            binding.materialCardView.visibility = View.GONE
            binding.etNumeroManilla.visibility = View.GONE
            binding.btnLimpiar.visibility = View.GONE
            binding.btnRegistrar.text = getString(R.string.registrar)
            binding.etNumeroManilla.text?.clear()
            globalDataViewModel.setManillaId(null)

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

        viewModel.showCard.observe(this) { show ->
            if (show) {
                updateCardInfo()
            } else {
                binding.materialCardView.visibility = View.GONE
            }
        }

        viewModel.codigoNFC.observe(this) { codigo ->
            codigo?.let {
                globalDataViewModel.setManillaId(it)
                binding.apply {
                    tvManilla.setText(it)
                    materialCardView.visibility = View.VISIBLE
                    containerLocalidades.visibility = View.VISIBLE
                    btnRegistrar.text = getString(R.string.registrar)
                    btnRegistrar.visibility = View.VISIBLE
                    btnLimpiar.visibility = View.VISIBLE
                }

            }
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                binding.apply {
                    btnRegistrar.visibility = View.GONE
                    btnLimpiar.visibility = View.VISIBLE
                    materialCardView.visibility = View.GONE
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
            binding.btnRegistrar.isEnabled = !isLoading
            binding.containerLocalidades.visibility = View.GONE
            binding.materialCardView.visibility = View.GONE
        }

    }

    private fun mostrarRegistroExitoso() {
        binding.apply {
            // Mostrar card de éxito
            cardInfoRegistrada.visibility = View.VISIBLE

            // Ocultar elementos innecesarios
            containerLocalidades.visibility = View.GONE
            materialCardView.visibility = View.GONE
            darkBackground.visibility = View.GONE
            progressBar.visibility = View.GONE

            // Actualizar botones
            btnRegistrar.visibility = View.GONE
            btnLimpiar.visibility = View.VISIBLE
        }
    }


    private fun disableNfcReading() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString(":") { byte -> "%02X".format(byte) }
    }

    //Metodos UI

    private fun updateCardInfo() {
        binding.apply {
            materialCardView.visibility = View.VISIBLE
            tvManilla.text = globalDataViewModel.manillaId.value ?: "N/A"
            btnRegistrar.visibility = View.VISIBLE
            btnLimpiar.visibility = View.VISIBLE
        }
    }

    private fun showLoadingState(show: Boolean) {
        binding.apply {
            tvLoadingMessage.visibility = if (show) View.VISIBLE else View.GONE
            btnRegistrar.isEnabled = !show
            darkBackground.visibility = if (show) View.VISIBLE else View.GONE
            if (show) tvLoadingMessage.text = getString(R.string.acercar_manilla)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.darkBackground.visibility = if (isLoading) View.VISIBLE else View.GONE
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