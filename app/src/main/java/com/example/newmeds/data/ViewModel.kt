package com.example.digitalassistant.data

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedViewModel(application: Application) : AndroidViewModel(application) {


    val readAllMed: LiveData<List<Med>>
    val readAllPharmacy: LiveData<List<Pharmacy>>
    private val repository: MedRepository
    private val repositoryPharmacy: PharmacyRepository

    init {
        val medDao = MedDatabase.getDatabase(application).medDao()
        repository = MedRepository(medDao)
        readAllMed = repository.readAllMed

        val pharmacyDao = PharmacyDatabase.getDatabase(application).pharmacyDao()
        repositoryPharmacy = PharmacyRepository(pharmacyDao)
        readAllPharmacy = repositoryPharmacy.readAllPharmacy
    }

    fun addMed(med: Med) {
        viewModelScope.launch(Dispatchers.IO){
            repository.addMed(med)
        }
    }

    fun updateMed(med: Med) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMed(med)
        }
    }

    fun deleteMed(med: Med) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMed(med)
        }
    }


    fun addPharmacy(pharmacy: Pharmacy) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryPharmacy.addPharmacy(pharmacy)
        }
    }

    fun updatePharmacy(pharmacy: Pharmacy) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryPharmacy.updatePharmacy(pharmacy)
        }
    }

    fun deletePharmacy(pharmacy: Pharmacy) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryPharmacy.deletePharmacy(pharmacy)
        }
    }
}

fun compareDates(event1: String, event2: String): Int {
    val date1 = event1.split("/")
    val date2 = event2.split("/")
    val year1 = date1[2].toInt()
    val month1 = date1[1].toInt()
    val day1 = date1[0].toInt()
    val year2 = date2[2].toInt()
    val month2 = date2[1].toInt()
    val day2 = date2[0].toInt()
    if (year1 < year2) {
        return 1
    } else if (year1 == year2) {
        if (month1 < month2) {
            return 1
        } else if (month1 == month2) {
            if (day1 < day2) {
                return 1
            } else if(day1 == day2){
                return 0
            } else {
                return -1
            }
        } else{
            return -1
        }
    }

    return -1
}


fun compareDatesColor(event1: String, event2: String): Int {

    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    val event1_dt = LocalDate.parse(event1, formatter)
    val event1_later = LocalDate.from(event1_dt).plusMonths(1)

    val cmp = event1_later.compareTo(LocalDate.parse(event2, formatter))

    return if (cmp >= 0){
        0
    }
    else{
        1
    }
}

class SharedViewModel: ViewModel()  {
    private val _currentMed = MutableStateFlow<Med?>(null)
    val currentMed = _currentMed.asStateFlow()

    fun setCurrentMed(med: Med) {
        viewModelScope.launch {
            _currentMed.value = med
        }
    }

    private val _currentPharmacy = MutableStateFlow<Pharmacy?>(null)
    val currentPharmacy = _currentPharmacy.asStateFlow()

    fun setCurrentPharmacy(pharmacy: Pharmacy) {
        viewModelScope.launch {
            _currentPharmacy.value = pharmacy
        }
    }
}

