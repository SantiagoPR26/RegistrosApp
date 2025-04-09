package com.allcashless.registros.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.allcashless.registros.RegistrosApplication
import com.allcashless.registros.adapters.EventosAdapter
import com.allcashless.registros.databinding.ActivityEventosBinding
import com.allcashless.registros.util.SessionManager
import com.allcashless.registros.util.toDateFormat
import com.allcashless.registros.viewmodel.EventosViewModel
import com.allcashless.registros.viewmodel.GlobalDataViewModel
import com.bumptech.glide.Glide

class EventosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosBinding
    private val viewModel: EventosViewModel by viewModels()
    private lateinit var eventosAdapter: EventosAdapter
    private val globalDataViewModel: GlobalDataViewModel by lazy {
        (application as RegistrosApplication).globalDataViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupRecyclerView()
        setupEventInfo()
        setupObservers()
        setupCardClickListener()
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        eventosAdapter = EventosAdapter { evento ->
            // Cuando se hace clic en un evento
            globalDataViewModel.setEvento(evento)
            navigateToLecturaManillaActivity(1157)
        }

        binding.recyclerViewEventos?.apply {
            layoutManager = LinearLayoutManager(this@EventosActivity)
            adapter = eventosAdapter
            setHasFixedSize(true)
        }
    }


    private fun setupEventInfo() {
        viewModel.cargarEvento(1157)
    }

    private fun setupObservers() {
        viewModel.evento.observe(this) { evento ->
            evento?.let {
                binding.textNoEventos.visibility = View.GONE
                eventosAdapter.submitList(listOf(evento))
            } ?: run {
                binding.textNoEventos.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.darkBackground.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.darkBackground.visibility = View.GONE
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
                binding.darkBackground.visibility = View.GONE
            }
        }
    }

    // Función para navegar a LecturaManillaActivity
    private fun navigateToLecturaManillaActivity(eventoId: Long) {
        val intent = Intent(this, LecturaManillaActivity::class.java).apply {
            putExtra("eventoId", eventoId)
        }
        startActivity(intent)
    }

    // Función para configurar el OnClickListener del CardView
    private fun setupCardClickListener() {

        binding.logout.setOnClickListener {
            logout()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logout()
            }
        })
    }

    private fun logout(){
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Está seguro que desea cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                SessionManager.clearSession()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()
    }
}