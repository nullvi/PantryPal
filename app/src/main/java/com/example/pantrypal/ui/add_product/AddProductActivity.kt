package com.example.pantrypal.ui.add_product

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pantrypal.databinding.ActivityAddProductBinding
import java.util.Calendar
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()

        binding.btnSave.setOnClickListener {
            saveProduct()
        }

        binding.btnScan.setOnClickListener {
            Toast.makeText(this, "Camera feature coming next step!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format: DD/MM/YYYY (Tek haneli günlerin başına 0 koyar)
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDate.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }
    }

    private fun saveProduct() {
        val name = binding.etName.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val date = binding.etDate.text.toString().trim()

        if (name.isEmpty() || quantityStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 1

        viewModel.addProduct(name, quantity, date) { success ->
            if (success) {
                Toast.makeText(this, "Product Saved!", Toast.LENGTH_SHORT).show()
                finish() // Ekranı kapat ve Dashboard'a dön
            } else {
                Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}