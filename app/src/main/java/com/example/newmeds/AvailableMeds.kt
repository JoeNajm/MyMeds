package com.example.newmeds

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.Med
import com.example.digitalassistant.data.MedViewModel
import com.example.digitalassistant.data.SharedViewModel
import com.example.digitalassistant.data.compareDates
import com.example.digitalassistant.data.compareDatesColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AvailableMeds(navController: NavController, sharedViewModel: SharedViewModel) {

    val medViewModel: MedViewModel = viewModel()
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val curDate = sdf.format(calendar.time)

    val meds = medViewModel.readAllMed.observeAsState(listOf())
    var newMeds by remember { mutableStateOf(listOf<Med>()) }
    var toDisplayMeds by remember { mutableStateOf(listOf<Med>()) }

    var selectedCategory by remember { mutableStateOf("All") }

    val context = LocalContext.current
    val activity = context as Activity
    BackHandler(enabled = true, onBack = {
        activity.finish()
    })

    LaunchedEffect(meds.value) {
        val filteredMeds = mutableListOf<Med>()
        for (med in meds.value) {
            val previous = med.copy()
            val isCurrent = compareDates(curDate, med.med_date)
            if (isCurrent == -1) {
                med.med_current = false
            } else {
                if (!med.med_done) {
                    filteredMeds.add(med)
                }
            }
            if (previous != med) {
                medViewModel.updateMed(med)
            }
        }
        newMeds = filteredMeds.sortedBy {
            it.med_date.substring(3, 5) + it.med_date.substring(0, 2)
        }

        toDisplayMeds = newMeds
    }

    val uniqueCategories = newMeds.map { it.med_category }.distinct().sorted().toMutableList()
    uniqueCategories.add(0, "All")

    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Text(
                text = "Your Meds",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        toDisplayMeds = toDisplayMeds.sortedBy { it.med_name }
                    },
                ) {
                    Text(text = "Sort by name")
                }

                Button(
                    onClick = {
                        toDisplayMeds = toDisplayMeds.sortedBy {
                            it.med_date.substring(3, 5) + it.med_date.substring(0, 2)
                        }
                    },
                ) {
                    Text(text = "Sort by date")
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(uniqueCategories) { category ->
                    if(category == selectedCategory) {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Green)
                                .padding(8.dp)
                        )
                    } else{
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Red)
                                .padding(8.dp)
                                .clickable(onClick = {
                                    selectedCategory = category
                                    if (category == "All") {
                                        toDisplayMeds = newMeds.sortedBy {
                                            it.med_date.substring(3, 5) + it.med_date.substring(0, 2)
                                        }
                                    } else {
                                        toDisplayMeds = newMeds.filter { it.med_category == category }
                                    }
                                })
                        )
                    }
                }
            }

            if(newMeds.size == 0){
                // text in middle vertically and horizontally of the screen
                Text("No meds available",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(48.dp)
                )
            }


            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                ),
            ) {
                items(toDisplayMeds) { med ->
                    val isCurrent = compareDatesColor(curDate, med.med_date)

                    Column(
                        modifier = Modifier
                            .background(if (isCurrent == 1) Color.Green else Color.Yellow)
                            .clickable(onClick = {
                                sharedViewModel.setCurrentMed(med)
                                navController.navigate("medDetails")
                            }),
                    ) {
                        if (med.med_image.contains("drawable", ignoreCase = true)) {
                            Image(
                                painter = painterResource(id = R.drawable.medication),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(200.dp)
                                    .fillMaxSize()
                                    .padding(4.dp)
                            )
                        } else {
                            var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(med.med_image) {
                                withContext(Dispatchers.IO) {
                                    bitmap = BitmapFactory.decodeFile(med.med_image)
                                }
                            }

                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(200.dp)
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop,
                                )
                            } ?: Text("Image not found")
                        }

                        Text(
                            text = med.med_name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = med.med_date,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("addNewMed") },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(text = "Add Med")
                }
                Button(
                    onClick = { navController.navigate("pastMeds") },
                ) {
                    Text("History")
                }
                Button(
                    onClick = { navController.navigate("pharmacy") },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(text = "Pharmacy")
                }
            }
        }
    }
}