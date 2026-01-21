package com.example.pantrypal.ui.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.databinding.ItemProductBinding
import java.text.SimpleDateFormat
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

        // Ekrana yazdırma
        holder.binding.tvProductName.text = product.name
        holder.binding.tvQuantity.text = "x${product.quantity}"
        holder.binding.tvExpiryDate.text = "Expires: ${product.expiryDate}"

        // --- TARİH HESAPLAMA VE RENKLENDİRME ---
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            // String tarihi ("12/05/2026") Sayıya çeviriyoruz
            val dateObject = sdf.parse(product.expiryDate)

            if (dateObject != null) {
                val expiryTime = dateObject.time
                val currentTime = System.currentTimeMillis()

                // Artık ikisi de sayı (Long) olduğu için çıkarma yapabiliriz
                val diff = expiryTime - currentTime
                val threeDaysInMillis = 3L * 24 * 60 * 60 * 1000

                if (diff < 0) {
                    holder.binding.containerLayout.setBackgroundColor(Color.parseColor("#FFCDD2")) // Tarihi geçmiş (Kırmızı)
                } else if (diff <= threeDaysInMillis) {
                    holder.binding.containerLayout.setBackgroundColor(Color.parseColor("#FFEBEE")) // Az kalmış (Açık Kırmızı)
                } else {
                    holder.binding.containerLayout.setBackgroundColor(Color.WHITE)
                }
            }
        } catch (e: Exception) {
            holder.binding.containerLayout.setBackgroundColor(Color.WHITE)
        }

        // Silme işlemi için tıklama
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