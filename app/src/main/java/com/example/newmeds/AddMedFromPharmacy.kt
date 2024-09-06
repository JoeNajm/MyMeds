package com.example.newmeds

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.Med
import com.example.digitalassistant.data.MedViewModel
import com.example.digitalassistant.data.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@ExperimentalMaterial3Api
@Composable
fun AddMedFromPharmacy(navController: NavController, sharedViewModel: SharedViewModel) {

    val currentPharmacy by sharedViewModel.currentPharmacy.collectAsState()
    var text_name by remember { mutableStateOf(currentPharmacy!!.pharmacy_name) }
    var text_cate by remember { mutableStateOf(currentPharmacy!!.pharmacy_category) }
    var text_date by remember { mutableStateOf("") }
    var text_quantity by remember { mutableStateOf("") }

    val medViewModel: MedViewModel = viewModel()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current


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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green),
                    modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "From Pharmacy")
                }
                Button(
                    onClick = {navController.navigate("addNewMed")},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red),
                    modifier = Modifier.padding(end = 16.dp)) {
                    Text(text = "Brand New Med")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = text_name,
                    onValueChange = { newText -> text_name = newText },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
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
                    singleLine = true,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )


                if(currentPharmacy!!.pharmacy_image.contains("drawable", ignoreCase = true)){
                    Image(
                        painter = painterResource(id = R.drawable.medication),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(200.dp)
                            .fillMaxSize())
                }
                else{
                    LaunchedEffect(currentPharmacy!!.pharmacy_image) {
                        withContext(Dispatchers.IO) {
                            bitmap = BitmapFactory.decodeFile(currentPharmacy!!.pharmacy_image)
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

            }
        }

        Button(
            onClick = {
                if(text_name.isNotEmpty() && text_date.isNotEmpty() && text_cate.isNotEmpty()){

                    val imagePath = currentPharmacy!!.pharmacy_image



                    val med = Med(med_name = text_name, med_date = text_date, med_quantity = text_quantity.toFloat(),
                        med_category = text_cate, med_image = imagePath, med_current = true)
                    medViewModel.addMed(med)

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