package com.example.digitalassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Med::class], version = 1, exportSchema = false)
abstract class MedDatabase: RoomDatabase() {

    abstract fun medDao(): MedDao

    companion object {
        @Volatile
        private var INSTANCE: MedDatabase? = null

        fun getDatabase(context: Context): MedDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedDatabase::class.java,
                    "med_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}


@Database(entities = [Pharmacy::class], version = 1, exportSchema = false)
abstract class PharmacyDatabase: RoomDatabase() {

    abstract fun pharmacyDao(): PharmacyDao

    companion object {
        @Volatile
        private var INSTANCE: PharmacyDatabase? = null

        fun getDatabase(context: Context): PharmacyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PharmacyDatabase::class.java,
                    "pharmacy_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}