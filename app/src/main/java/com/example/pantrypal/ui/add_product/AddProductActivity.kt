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
import com.example.pantrypal.databinding.ActivityAddProductBinding
import com.example.pantrypal.ui.scanner.BarcodeScannerActivity
import java.util.Calendar
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()

    // 1. Barkod Tarayıcıdan gelen sonucu yakalayan launcher
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val barcode = result.data?.getStringExtra("scanned_barcode")
            barcode?.let {
                // Barkodu ekrana yaz
                binding.etBarcode.setText(it)

                // Kullanıcıya bilgi ver ve internetten aramayı başlat
                Toast.makeText(this, "Searching online...", Toast.LENGTH_SHORT).show()
                viewModel.searchBarcode(it)
            }
        }
    }

    // 2. Kamera İznini isteyen launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openScanner()
        } else {
            Toast.makeText(this, "Camera permission needed to scan!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()

        // ViewModel'i dinlemeye başla (İsim gelirse dolduracak)
        observeViewModel()

        // --- YENİ EKLENEN KISIM: Kapat (X) Butonu ---
        binding.btnClose.setOnClickListener {
            // Aktiviteyi sonlandır ve önceki ekrana dön
            finish()
        }
        // --------------------------------------------

        binding.btnSave.setOnClickListener {
            saveProduct()
        }

        binding.btnScan.setOnClickListener {
            checkCameraPermissionAndOpenScanner()
        }
    }

    // 3. ViewModel'den gelen verileri (Ürün Adı) dinle
    private fun observeViewModel() {
        viewModel.scannedProductName.observe(this) { productName ->
            if (productName != null) {
                // 1. Durum: Ürün Bulundu
                binding.etName.setText(productName)

                // Kullanıcıya şık bir mesaj ver
                Toast.makeText(this, "Product Found: $productName", Toast.LENGTH_SHORT).show()

                // Kolaylık olsun diye imleci direkt "Adet" (Quantity) kısmına at
                binding.etQuantity.requestFocus()

            } else {
                // 2. Durum: Ürün Bulunamadı
                // Kullanıcıyı uyar
                Toast.makeText(this, "Product not found. Please enter name manually.", Toast.LENGTH_LONG).show()

                // İsim alanını temizle
                binding.etName.setText("")

                // İmleci "İsim" kutusuna odakla
                binding.etName.requestFocus()

                // Klavyeyi aç
                val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(binding.etName, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
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
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openScanner()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openScanner() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        barcodeLauncher.launch(intent)
    }

    private fun saveProduct() {
        val barcode = binding.etBarcode.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val date = binding.etDate.text.toString().trim()

        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter at least Name and Quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 1

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