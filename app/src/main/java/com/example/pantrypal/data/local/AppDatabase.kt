package com.example.pantrypal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pantrypal.data.model.Product

// entities = [Product::class] -> Bu veritabanında hangi tablolar var?
// version = 1 -> Veritabanı şemasını değiştirirsen bunu arttırmalısın (Migration)
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // DAO'ya erişim noktası
    abstract fun productDao(): ProductDao

    // Singleton Pattern: Uygulama boyunca tek bir veritabanı bağlantısı olmalı.
    // Aksi takdirde "Database locked" hatası alırsın.
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Eğer bağlantı varsa onu döndür, yoksa yeni oluştur
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pantry_pal_database" // Telefonun içindeki dosya adı
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}