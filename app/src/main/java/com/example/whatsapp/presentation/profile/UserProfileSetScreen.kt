package com.example.whatsapp.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.whatsapp.R
import com.example.whatsapp.presentation.viewmodel.PhoneAuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun UserProfileSetScreen(
    navHostController: NavHostController,
    phoneAuthViewModel: PhoneAuthViewModel = hiltViewModel()
) {

    var name by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // 🔥 bitmap logic removed (state kept but unused to avoid breaking)
    var bitmapImage by remember { mutableStateOf<Any?>(null) }

    val firebaseAuth = Firebase.auth
    val phoneNumber = firebaseAuth.currentUser?.phoneNumber ?: ""
    val userId = firebaseAuth.currentUser?.uid ?: return

    val context = LocalContext.current

    /* ---------------- IMAGE PICKER ---------------- */

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        profileImageUri = uri
    }

    /* ---------------- UI ---------------- */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- PROFILE IMAGE ----------
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.user_placeholder),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- PHONE ----------
        Text(
            text = phoneNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- NAME ----------
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.light_green)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- STATUS ----------
        TextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.light_green)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- SAVE ----------
        Button(
            onClick = {
                if (name.isBlank()) return@Button

                phoneAuthViewModel.saveUserProfile(
                    userId = userId,
                    name = name.trim(),
                    status = status.trim(),
                    profileImageUri = profileImageUri
                )

                navHostController.navigate("main") {
                    popUpTo("register") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.light_green)
            )
        ) {
            Text("Save")
        }
    }
}
