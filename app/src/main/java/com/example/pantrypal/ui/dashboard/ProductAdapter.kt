package com.example.pantrypal.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pantrypal.R
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.databinding.ItemProductBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProductAdapter(
    private var products: List<Product>,
    private val onDeleteClick: (Product) -> Unit,
    private val onItemClick: (Product) -> Unit // <--- YENİ EKLENDİ: Karta Tıklama Olayı
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context

        // 1. Temel Verileri Yazma
        holder.binding.tvProductName.text = product.name
        holder.binding.tvQuantity.text = "x${product.quantity}"

        // Tarihi Formatla
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(product.expiryDate))
        holder.binding.tvExpiryDate.text = "Expires: $formattedDate"

        // 2. Resim Yükleme (Glide)
        if (!product.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(product.imageUrl)
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.binding.ivProductImage)
        } else {
            holder.binding.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // 3. Renklendirme Mantığı (Şerit Rengi)
        val diff = product.expiryDate - System.currentTimeMillis()
        val daysLeft = TimeUnit.MILLISECONDS.toDays(diff)

        val colorFresh = ContextCompat.getColor(context, R.color.status_fresh)
        val colorWarning = ContextCompat.getColor(context, R.color.status_warning)
        val colorExpired = ContextCompat.getColor(context, R.color.status_expired)
        val colorTextGray = ContextCompat.getColor(context, R.color.text_secondary)

        if (daysLeft < 0) {
            // TARİHİ GEÇMİŞ
            holder.binding.viewStatusIndicator.setBackgroundColor(colorExpired)
            holder.binding.tvExpiryDate.text = "EXPIRED ($formattedDate)"
            holder.binding.tvExpiryDate.setTextColor(colorExpired)
        } else if (daysLeft <= 3) {
            // KRİTİK (3 günden az)
            holder.binding.viewStatusIndicator.setBackgroundColor(colorWarning)
            holder.binding.tvExpiryDate.setTextColor(colorWarning)
        } else {
            // TAZE
            holder.binding.viewStatusIndicator.setBackgroundColor(colorFresh)
            holder.binding.tvExpiryDate.setTextColor(colorTextGray)
        }

        // 4. SİLME İŞLEMİ (Çöp kutusuna tıklayınca)
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(product)
        }

        // 5. YENİ: DÜZENLEME İŞLEMİ (Karta tıklayınca)
        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}