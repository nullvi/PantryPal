package com.example.pantrypal.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantrypal.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    // --- KRİTİK EKLEME ---
    // Başka bir ekrandan (örn: Ekleme Ekranı) buraya geri dönüldüğünde çalışır.
    // Listeyi tazelemek için şarttır.
    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }
    // ---------------------

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
            val intent = android.content.Intent(this, com.example.pantrypal.ui.add_product.AddProductActivity::class.java)
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