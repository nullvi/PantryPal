package com.example.pantrypal.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pantrypal.databinding.ActivityLoginBinding
import com.example.pantrypal.ui.dashboard.DashboardActivity // İleride oluşturacağız

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels() // KTX kütüphanesi gerektirir

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Oturum kontrolü: Zaten giriş yapmışsa direkt Dashboard'a at
        val sharedPref = getSharedPreferences("PantryPalParams", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            navigateToDashboard()
        }

        setupObservers()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.loginUser(username, password)
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
            // İleride 'username' de lazım olabilir
            putString("username", binding.etUsername.text.toString())
            apply()
        }
    }

    private fun navigateToDashboard() {
        // DashboardActivity henüz yoksa burayı yorum satırı yap veya boş bir Activity oluştur.
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish() // Geri tuşuna basınca login'e dönmesin
    }
}