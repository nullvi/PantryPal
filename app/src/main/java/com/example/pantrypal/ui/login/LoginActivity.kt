package com.example.pantrypal.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pantrypal.databinding.ActivityLoginBinding
import com.example.pantrypal.ui.dashboard.DashboardActivity
import com.example.pantrypal.ui.register.RegisterActivity // YENİ: Register importu eklendi

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Oturum Kontrolü
        val sharedPref = getSharedPreferences("PantryPalParams", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            navigateToDashboard()
        }

        // 2. Observer'ları Kur
        setupObservers()

        // 3. Login Butonu Tıklaması
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. (YENİ) Kayıt Ol Ekranına Geçiş Linki
        // XML dosyanızda bu ID'ye sahip bir TextView olduğundan emin olun (tvRegisterLink)
        binding.tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        // Başarılı/Başarısız durumu
        viewModel.loginResult.observe(this) { isSuccess ->
            if (isSuccess) {
                saveSession()
                navigateToDashboard()
            }
        }

        // Hata mesajları
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Yükleniyor animasyonu
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }
    }

    private fun saveSession() {
        val sharedPref = getSharedPreferences("PantryPalParams", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("username", binding.etUsername.text.toString())
            apply()
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish() // Geri tuşuna basınca login'e dönmesin
    }
}