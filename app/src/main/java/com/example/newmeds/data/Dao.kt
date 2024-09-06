package com.example.digitalassistant.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMed(med: Med)

    @Query("SELECT * FROM med_table ORDER BY id DESC")
    fun readAllMed(): LiveData<List<Med>>

    @Update
    suspend fun updateMed(med: Med)

    @Delete
    suspend fun deleteMed(med: Med)

}

@Dao
interface PharmacyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPharmacy(pharmacy: Pharmacy)

    @Query("SELECT * FROM pharmacy_table ORDER BY pharmacy_name ASC")
    fun readAllPharmacy(): LiveData<List<Pharmacy>>

    @Update
    suspend fun updatePharmacy(pharmacy: Pharmacy)

    @Delete
    suspend fun deletePharmacy(pharmacy: Pharmacy)

}