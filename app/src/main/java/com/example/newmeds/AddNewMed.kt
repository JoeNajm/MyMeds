package com.example.newmeds

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.Med
import com.example.digitalassistant.data.MedViewModel
import com.example.digitalassistant.data.Pharmacy
import com.example.digitalassistant.data.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewMed(navController: NavController, sharedViewModel: SharedViewModel) {

    val medViewModel: MedViewModel = viewModel()

    val pharmacy = medViewModel.readAllPharmacy.observeAsState(listOf())
    val new_meds = mutableListOf<Pharmacy>()

    var nb_list = 0
    for (med in pharmacy.value) {
        new_meds.add(med)
        nb_list += 1
    }

    var selected_new by remember { mutableStateOf(true) }
    var selected_lib by remember { mutableStateOf(false) }

    var text_name by remember { mutableStateOf("") }
    var text_date by remember { mutableStateOf("") }
    var text_cate by remember { mutableStateOf("") }
    var text_quantity by remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    BackHandler(enabled = true, onBack = {
        navController.navigate("availableMeds")
    })

    // Launcher for picking image from the gallery
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data  // Get URI of selected image
            imageUri?.let { uri ->
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val originalBitmap = BitmapFactory.decodeStream(stream)
                    // Resize the bitmap to 800x800
                    imageBitmap = Bitmap.createScaledBitmap(originalBitmap, 800, 800, true)
                }
            }
        }
    }


    // text on top middle of the screen
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Text(
                text = "Add New Med",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { if (!selected_lib){
                        selected_lib = !selected_lib
                        selected_new = !selected_new
                    }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected_lib) Color.Green else Color.Red),
                    modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "From Library")
                }
                Button(
                    onClick = { if (!selected_new){
                        selected_lib = !selected_lib
                        selected_new = !selected_new
                    }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected_new) Color.Green else Color.Red),
                    modifier = Modifier.padding(end = 16.dp)) {
                    Text(text = "Brand New Med")
                }
            }

            if(selected_new){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        modifier = Modifier.padding(16.dp),
                        value = text_name,
                        onValueChange = { newText -> text_name = newText },
                        label = { Text("Name") },
                        singleLine = true
                    )

                    TextField(
                        value = text_quantity,
                        onValueChange = { newText -> text_quantity = newText },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                    )

                    text_date = SimpleDatePickerWithDay()


                    TextField(
                        value = text_cate,
                        onValueChange = { newText -> text_cate = newText },
                        label = { Text("Category") },
                        singleLine = true
                    )
                }
                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } ?: Button(onClick = {
                    // Launch the image picker
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickImageLauncher.launch(intent)
                },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)) {
                    Text("Change from default image")
                }
            }
            else{
                if(nb_list == 0){
                    // text in middle vertically and horizontally of the screen
                    Text("No meds in the pharmacy",
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
                ){
                    // new list called curr_events that only contains the current events

                    items(new_meds) { med ->

                        Column(
                            modifier = Modifier
                                .background(Color.Cyan)
                                .clickable(onClick = {
                                    sharedViewModel.setCurrentPharmacy(med)
                                    navController.navigate("addMedFromPharmacy")
                                }),

                            ) {

                            if(med.pharmacy_image.contains("drawable", ignoreCase = true)){
                                Image(
                                    painter = painterResource(id = R.drawable.medication),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(200.dp)
                                        .fillMaxSize()
                                        .padding(4.dp))
                            }
                            else{
                                var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                                LaunchedEffect(med.pharmacy_image) {
                                    withContext(Dispatchers.IO) {
                                        bitmap = BitmapFactory.decodeFile(med.pharmacy_image)
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
                                text = med.pharmacy_name,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
        if(selected_new){
            Button(
                onClick = {
                    if(text_name.isNotEmpty() && text_date.isNotEmpty() && text_cate.isNotEmpty()){

                        imagePath = "drawable://" + R.drawable.medication.toString()
                        // Save image and path
                        imageBitmap?.let { bitmap ->
                            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${System.currentTimeMillis()}.jpg")
                            FileOutputStream(file).use { outputStream ->
                                // Compress the bitmap to JPEG format with 90% quality
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                                imagePath = file.absolutePath  // Store the file path
                            }
                        }
                        val med = Med(med_name = text_name, med_date = text_date, med_quantity = text_quantity.toFloat(),
                            med_category = text_cate, med_image = imagePath,
                            med_current = true)
                        medViewModel.addMed(med)

//                        createNotif(text_date, text_hour, text_name, context)

                        navController.navigate("availableMeds")
                    }
                    else{
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("Add Med")
            }
        }

    }
}

@Composable
fun SimpleDatePickerWithDay(): String {
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Button(onClick = {
        // Create DatePickerDialog with current date as default
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format and update the selected date
                selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        // Show the dialog
        datePickerDialog.show()
    }) {
        Text(if (selectedDate.isEmpty()) "Select Expiry Date" else selectedDate)
    }
    return selectedDate
}