package com.example.pantrypal.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantrypal.R
import com.example.pantrypal.databinding.ActivityDashboardBinding
import com.example.pantrypal.ui.about.AboutActivity
import com.example.pantrypal.ui.add_product.AddProductActivity
import com.example.pantrypal.ui.login.LoginActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar başlığı
        supportActionBar?.title = "PantryPal Dashboard"

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    // --- MENÜ ENTEGRASYONU BAŞLANGICI ---

    // 1. Menüyü sağ üste yerleştir (dashboard_menu.xml dosyasını bağlar)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    // 2. Menü elemanlarına tıklanınca ne olacağını seç
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                // About ekranına git
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                // DÜZELTİLDİ: Çıkış yaparken hafızayı (SharedPreferences) temizle
                // LoginActivity'deki isimle AYNISI olmalı: "PantryPalParams"
                val sharedPrefs = getSharedPreferences("PantryPalParams", MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                editor.clear() // Tüm kayıtlı verileri sil (Beni hatırla iptal)
                editor.apply()

                // Şimdi Login ekranına dön
                val intent = Intent(this, LoginActivity::class.java)
                // Geri tuşuna basınca tekrar Dashboard'a dönmemesi için geçmişi temizle
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // --- MENÜ ENTEGRASYONU BİTİŞİ ---

    // Listeyi tazelemek için (Başka ekrandan dönünce çalışır)
    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        // Adapter'ı başlatıyoruz. İkinci parametre (lambda) silme işlemi için.
        adapter = ProductAdapter(emptyList()) { productToDelete ->
            showDeleteConfirmation(productToDelete)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Ürün listesini dinle
        viewModel.products.observe(this) { productList ->
            adapter.updateList(productList)

            // Liste boşsa uyarı yazısını göster
            if (productList.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
            } else {
                binding.tvEmptyState.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            // AddProductActivity'ye geçiş yap
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDeleteConfirmation(product: com.example.pantrypal.data.model.Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteProduct(product)
            }
            .setNegativeButton("No", null)
            .show()
    }
}