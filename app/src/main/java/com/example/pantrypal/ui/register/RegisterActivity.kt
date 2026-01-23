package com.example.pantrypal.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pantrypal.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel Bağlantısı
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPass = binding.etConfirmPassword.text.toString().trim()

            // 1. Basit Validasyonlar
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPass) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. ViewModel'e Gönder
            binding.btnRegister.isEnabled = false // Çift tıklamayı önle
            binding.btnRegister.text = "Registering..."
            viewModel.register(username, password)
        }

        // Giriş ekranına geri dönmek için
        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.registerResult.observe(this) { result ->
            binding.btnRegister.isEnabled = true
            binding.btnRegister.text = "REGISTER"

            if (result.isSuccess) {
                Toast.makeText(this, "Registration Successful! Please Login.", Toast.LENGTH_LONG).show()
                finish() // Aktiviteyi kapatıp Login ekranına dön
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown Error"
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}