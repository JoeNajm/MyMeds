package com.example.digitalassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "med_table")
data class Med(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val med_name: String,
    val med_date: String,
    val med_quantity: Float,
    val med_category: String,
    val med_image: String,
    var med_current: Boolean,
    var med_done: Boolean = false
)

@Entity(tableName = "pharmacy_table")
data class Pharmacy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pharmacy_name: String,
    val pharmacy_category: String,
    val pharmacy_image: String,
)
