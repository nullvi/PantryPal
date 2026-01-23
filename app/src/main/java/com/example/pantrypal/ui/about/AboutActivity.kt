package com.example.pantrypal.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pantrypal.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // ActionBar başlığını ayarla ve geri butonunu aç
        supportActionBar?.title = "About Developer"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Sol üstteki geri butonuna basınca çalışır
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}