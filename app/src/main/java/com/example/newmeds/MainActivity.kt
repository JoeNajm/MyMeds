package com.example.newmeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newmeds.ui.theme.NewMedsTheme
import androidx.navigation.compose.rememberNavController
import com.example.digitalassistant.data.SharedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Navigation()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val sharedViewModel = viewModel<SharedViewModel>()
    NavHost(navController = navController, startDestination = "availableMeds") {
        composable("availableMeds") { AvailableMeds(navController, sharedViewModel) }
        composable("addNewMed") { AddNewMed(navController, sharedViewModel) }
        composable("pastMeds") { PastMeds(navController, sharedViewModel) }
        composable("medDetails") { MedDetailsScreen(navController, sharedViewModel) }
        composable("pharmacy") { PharmacyScreen(navController, sharedViewModel) }
        composable("addToPharmacy") { AddToPharmacyScreen(navController, sharedViewModel) }
        composable("addMedFromPharmacy") { AddMedFromPharmacy(navController, sharedViewModel) }

    }
}
