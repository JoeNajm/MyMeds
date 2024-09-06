package com.example.newmeds

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.MedViewModel
import com.example.digitalassistant.data.Pharmacy
import com.example.digitalassistant.data.SharedViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@ExperimentalMaterial3Api
@Composable
fun AddToPharmacyScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val medViewModel: MedViewModel = viewModel()

    var text_name by remember { mutableStateOf("") }
    var text_cate by remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

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
                    modifier = Modifier.padding(top = 16.dp)
                )



                TextField(
                    value = text_cate,
                    onValueChange = { newText -> text_cate = newText },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.padding(top = 16.dp)
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
        Button(
            onClick = {
                if(text_name.isNotEmpty() && text_cate.isNotEmpty()){

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



                    val med = Pharmacy(pharmacy_name = text_name, pharmacy_category = text_cate,
                        pharmacy_image = imagePath)
                    medViewModel.addPharmacy(med)

                    navController.navigate("pharmacy")
                }
                else{
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Add Med to Pharmacy")
        }
    }
}