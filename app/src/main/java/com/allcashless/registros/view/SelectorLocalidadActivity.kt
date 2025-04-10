package com.allcashless.registros.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.allcashless.registros.R
import com.allcashless.registros.RegistrosApplication
import com.allcashless.registros.databinding.ActivitySelectorLocalidadBinding
import com.allcashless.registros.viewmodel.GlobalDataViewModel
import com.allcashless.registros.viewmodel.SelectorLocalidadViewModel

class SelectorLocalidadActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectorLocalidadBinding
    private val viewModel: SelectorLocalidadViewModel by viewModels()
    private val globalDataViewModel: GlobalDataViewModel by lazy {
        (application as RegistrosApplication).globalDataViewModel
    }
    private var localidadId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectorLocalidadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupRadioButtons()
        setupRegistrarButton()
    }


    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRadioButtons() {
        binding.LocalidadRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val localidadId = when (checkedId) {
                R.id.RadioButton1 -> 1L //GENERAL
                R.id.RadioButton2 -> 2L //VIP
                R.id.RadioButton3 -> 3L //BACKSTAGE
                R.id.RadioButton4 -> 4L //ARTISTAS
                else -> -1L
            }

            if (localidadId != -1L) {
                viewModel.seleccionarLocalidad(localidadId)
            }
        }
    }

    private fun setupRegistrarButton() {
        binding.btnRegistrar.setOnClickListener {
            viewModel.localidadSeleccionada.value?.let { localidadId ->
                // Navegar a LecturaManillaActivity con los datos necesarios
                val intent = Intent(this, LecturaManillaActivity::class.java).apply {
                    putExtra("localidadId", localidadId)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Seleccione una localidad", Toast.LENGTH_SHORT).show()
            }
        }
    }


}