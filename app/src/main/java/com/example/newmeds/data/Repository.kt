package com.example.digitalassistant.data

import androidx.lifecycle.LiveData

class MedRepository(private val medDao: MedDao) {
    val readAllMed: LiveData<List<Med>> = medDao.readAllMed()

    suspend fun addMed(med: Med) {
        medDao.addMed(med)
    }

    suspend fun updateMed(med: Med) {
        medDao.updateMed(med)
    }

    suspend fun deleteMed(med: Med){
        medDao.deleteMed(med)
    }
}

class PharmacyRepository(private val pharmacyDao: PharmacyDao) {
    val readAllPharmacy: LiveData<List<Pharmacy>> = pharmacyDao.readAllPharmacy()

    suspend fun addPharmacy(event: Pharmacy) {
        pharmacyDao.addPharmacy(event)
    }

    suspend fun updatePharmacy(event: Pharmacy) {
        pharmacyDao.updatePharmacy(event)
    }

    suspend fun deletePharmacy(event: Pharmacy){
        pharmacyDao.deletePharmacy(event)
    }
}