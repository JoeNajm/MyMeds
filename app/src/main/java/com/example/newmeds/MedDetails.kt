package com.example.newmeds

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.MedViewModel
import com.example.digitalassistant.data.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedDetailsScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val currentMed by sharedViewModel.currentMed.collectAsState()
    var text_complete by remember { mutableStateOf(
        value = if(currentMed?.med_done == true) "Yes" else "No")
    }

    var text_delete by remember { mutableStateOf("Delete Med") }

    val medViewModel: MedViewModel = viewModel()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var text_quantity by remember { mutableStateOf(currentMed?.med_quantity) }
    var text = remember { mutableStateOf(currentMed?.med_quantity.toString()) }

    // text on top middle of the screen
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Text(
                text = "Med Details",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Name: ${currentMed?.med_name}",
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally))

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    TextField(
                        value = text.value,
                        onValueChange = {
                            text.value = it
                        },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(100.dp)
                    )
                    Button(onClick = {
                        medViewModel.updateMed(currentMed?.copy(med_quantity = text.value.toFloat())!!)
                        navController.navigate("availableMeds")
                    },
                        modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = "Update")
                    }
                }

                Text(text = "Expiry Date: ${currentMed?.med_date}",
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally))
                Text(text = "Category: ${currentMed?.med_category}",
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally))
                Text(text = "Completed: $text_complete",
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 16.dp)
                        .align(Alignment.CenterHorizontally))


                if(currentMed!!.med_image.contains("drawable", ignoreCase = true)){
                    Image(
                        painter = painterResource(id = R.drawable.medication),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(200.dp)
                            .fillMaxSize())
                }
                else{
                    LaunchedEffect(currentMed!!.med_image) {
                        withContext(Dispatchers.IO) {
                            bitmap = BitmapFactory.decodeFile(currentMed!!.med_image)
                        }
                    }
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(200.dp)
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } ?: Text("Image not found")
                }
                Button(onClick = {
                    if(text_complete == "Yes"){
                        text_complete = "No"
                        medViewModel.updateMed(currentMed?.copy(med_done = false)!!)
                    } else {
                        text_complete = "Yes"
                        medViewModel.updateMed(currentMed?.copy(med_done = true)!!)
                    }
                    navController.navigate("availableMeds")
                },
                    modifier = Modifier.padding(16.dp)) {
                    Text(text = "Switch Completeness")
                }
            }
            Button(onClick = {
                if(text_delete == "Click again to confirm"){
                    medViewModel.deleteMed(currentMed!!)
                    navController.navigate("availableMeds")
                } else{
                    text_delete = "Click again to confirm"
                }
            },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red),
            ) {
                Text(text = text_delete)
            }
        }
    }
}