package com.example.pantrypal.ui.add_product

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pantrypal.data.model.Product
import com.example.pantrypal.databinding.ActivityAddProductBinding
import com.example.pantrypal.ui.scanner.BarcodeScannerActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()

    // --- DÜZENLEME MODU DEĞİŞKENLERİ ---
    private var isEditMode = false
    private var currentProduct: Product? = null

    // 1. Barkod Sonucu
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val barcode = result.data?.getStringExtra("scanned_barcode")
            barcode?.let {
                binding.etBarcode.setText(it)
                Toast.makeText(this, "Searching online...", Toast.LENGTH_SHORT).show()
                viewModel.searchBarcode(it)
            }
        }
    }

    // 2. Kamera İzni
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) openScanner()
        else Toast.makeText(this, "Camera permission needed!", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        observeViewModel()

        // --- EDİT MODU KONTROLÜ ---
        if (intent.hasExtra("p_uid")) {
            isEditMode = true
            setupEditMode()
        }

        binding.btnClose.setOnClickListener { finish() }

        binding.btnSave.setOnClickListener {
            if (isEditMode) {
                updateExistingProduct()
            } else {
                saveNewProduct()
            }
        }

        binding.btnScan.setOnClickListener {
            checkCameraPermissionAndOpenScanner()
        }
    }

    private fun setupEditMode() {
        val uid = intent.getIntExtra("p_uid", 0)
        val id = intent.getStringExtra("p_id")
        val name = intent.getStringExtra("p_name") ?: ""
        val quantity = intent.getIntExtra("p_quantity", 1)
        val dateLong = intent.getLongExtra("p_date", System.currentTimeMillis())
        val barcode = intent.getStringExtra("p_barcode")
        val ownerId = intent.getStringExtra("p_owner") ?: ""

        // Formu Doldur
        binding.etName.setText(name)
        binding.etQuantity.setText(quantity.toString())
        binding.etBarcode.setText(barcode)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(sdf.format(Date(dateLong)))

        // UI Başlıklarını Değiştir
        binding.tvTitle.text = "Edit Product"
        binding.btnSave.text = "Update Product"

        currentProduct = Product(
            uid = uid,
            id = id,
            name = name,
            quantity = quantity,
            expiryDate = dateLong,
            barcode = barcode,
            ownerId = ownerId,
            status = 1
        )
    }

    private fun saveNewProduct() {
        val name = binding.etName.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val barcode = binding.etBarcode.text.toString().trim()

        if (validateInput(name, quantityStr)) {
            val quantity = quantityStr.toInt()
            viewModel.addProduct(name, quantity, date, barcode) { success ->
                if (success) {
                    Toast.makeText(this, "Product Saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateExistingProduct() {
        val name = binding.etName.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val barcode = binding.etBarcode.text.toString().trim()

        if (validateInput(name, quantityStr) && currentProduct != null) {
            val quantity = quantityStr.toInt()
            viewModel.updateProduct(currentProduct!!, name, quantity, date, barcode) { success ->
                if (success) {
                    Toast.makeText(this, "Product Updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error updating product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInput(name: String, quantityStr: String): Boolean {
        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter Name and Quantity", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.scannedProductName.observe(this) { productName ->
            if (productName != null) {
                binding.etName.setText(productName)
                Toast.makeText(this, "Product Found: $productName", Toast.LENGTH_SHORT).show()
                binding.etQuantity.requestFocus()
            } else {
                Toast.makeText(this, "Not found. Enter manually.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDate.setText(formattedDate)
            }, year, month, day)
            datePicker.show()
        }
    }

    private fun checkCameraPermissionAndOpenScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openScanner()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openScanner() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        barcodeLauncher.launch(intent)
    }
}