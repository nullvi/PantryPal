package com.example.pantrypal.ui.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.databinding.ItemProductBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // 1. Verileri Ekrana Yazma
        holder.binding.tvProductName.text = product.name
        holder.binding.tvQuantity.text = "x${product.quantity}"

        // DÜZELTME: Veritabanındaki Long (Sayı) tarihi, okunabilir String tarihe çeviriyoruz
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(product.expiryDate))
        holder.binding.tvExpiryDate.text = "Expires: $formattedDate"

        // 2. Renklendirme Mantığı (Artık try-catch yok, matematik var)
        val currentTime = System.currentTimeMillis()
        val diff = product.expiryDate - currentTime
        val threeDaysInMillis = 3L * 24 * 60 * 60 * 1000

        // 'root', satırın en dıştaki kapsayıcısıdır. containerLayout yerine bunu kullanmak daha garantidir.
        if (diff < 0) {
            // Tarihi geçmiş (Koyu Kırmızı)
            holder.binding.root.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else if (diff <= threeDaysInMillis) {
            // 3 günden az kalmış (Açık Pembe)
            holder.binding.root.setBackgroundColor(Color.parseColor("#FFEBEE"))
        } else {
            // Sorun yok (Beyaz)
            holder.binding.root.setBackgroundColor(Color.WHITE)
        }

        // 3. Silme İşlemi (Uzun basınca siler)
        holder.itemView.setOnLongClickListener {
            onDeleteClick(product)
            true
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}