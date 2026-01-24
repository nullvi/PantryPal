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

    // --- MENÜ ENTEGRASYONU ---
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)

        val sharedPrefs = getSharedPreferences("PantryPalParams", MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "Guest")

        val userItem = menu?.findItem(R.id.action_user_info)
        userItem?.title = "Hi, $username"

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            R.id.action_logout -> {
                val sharedPrefs = getSharedPreferences("PantryPalParams", MODE_PRIVATE)
                sharedPrefs.edit().clear().apply()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Listeyi tazelemek ve Sync başlatmak için
    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
        viewModel.syncPendingData()
    }

    private fun setupRecyclerView() {
        // GÜNCELLENDİ: Adapter artık 2 parametre alıyor (Silme ve Tıklama)
        adapter = ProductAdapter(
            products = emptyList(),
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            },
            onItemClick = { product ->
                // Karta tıklayınca DÜZENLEME EKRANINI (AddProductActivity) aç
                // Ve mevcut verileri gönder
                val intent = Intent(this, AddProductActivity::class.java)
                intent.putExtra("p_uid", product.uid)
                intent.putExtra("p_id", product.id) // Cloud ID
                intent.putExtra("p_name", product.name)
                intent.putExtra("p_quantity", product.quantity)
                intent.putExtra("p_date", product.expiryDate)
                intent.putExtra("p_barcode", product.barcode)
                intent.putExtra("p_owner", product.ownerId)
                startActivity(intent)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { productList ->
            adapter.updateList(productList)

            if (productList.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
            } else {
                binding.tvEmptyState.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
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