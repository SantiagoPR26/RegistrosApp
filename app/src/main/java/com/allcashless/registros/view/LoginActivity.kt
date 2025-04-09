package com.allcashless.registros.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.allcashless.registros.RegistrosApplication
import com.allcashless.registros.databinding.ActivityLoginBinding
import com.allcashless.registros.models.Usuario
import com.allcashless.registros.repositories.AdminisitradorRepository
import com.allcashless.registros.util.SessionManager
import com.allcashless.registros.viewmodel.GlobalDataViewModel
import com.allcashless.registros.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java
import kotlin.let
import kotlin.text.isEmpty

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val administradorRepository = AdminisitradorRepository()
    private val globalDataViewModel: GlobalDataViewModel by lazy {
        (application as RegistrosApplication).globalDataViewModel
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupClickListeners()
        setupObservers()
        setupKeyboardAction()

    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.usernameField.text.toString()
            val password = binding.passwordField.text.toString()

            hideKeyboard()

            if (validateFields(username, password)) {
                showLoading(true)
                loginViewModel.login(Usuario(username, password,"",""))
            }
        }
    }

    private fun setupKeyboardAction() {
        // Acción para cuando se presiona "Enter" en el campo de usuario
        binding.usernameField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Mueve el foco al campo de la contraseña
                binding.passwordField.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        // Acción para cuando se presiona "Enter" en el campo de contraseña
        binding.passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val username = binding.usernameField.text.toString()
                val password = binding.passwordField.text.toString()

                hideKeyboard()

                if (validateFields(username, password)) {
                    showLoading(true)
                    loginViewModel.login(Usuario(username, password, "", ""))
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupObservers() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            showLoading(isLoading)
        }

        loginViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.auth.observe(this) { auth ->
            auth?.let {
                val payload = loginViewModel.jwtPayload.value!!

                SessionManager.saveAuthData(
                    cc = payload.cc,
                    nombre = payload.nombre,
                    token = it.access_token
                )

                lifecycleScope.launch {
                    try {
                        val administrador = withContext(Dispatchers.IO) {
                            val response = administradorRepository.getCliente(payload.cc)
                            if (response.isSuccessful) response.body() else null
                        }

                        if (administrador != null) {
                            globalDataViewModel.setAdministrador(administrador)
                            globalDataViewModel.setAdministradorId(payload.cc)

                            startActivity(Intent(this@LoginActivity, EventosActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "No se pudo obtener el coordinador", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Exception in fetch Coordinador: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateFields(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.usernameField.error = "Campo requerido"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordField.error = "Campo requerido"
            return false
        }
        return true
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.darkBackground.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}