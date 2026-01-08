package com.example.whatsapp.presentation.userregistrationscreen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.whatsapp.R
import com.example.whatsapp.presentation.viewmodel.AuthState
import com.example.whatsapp.presentation.viewmodel.PhoneAuthViewModel

@Composable
fun UserRegistrationScreen(
    navController: NavHostController,
    phoneAuthViewModel: PhoneAuthViewModel = hiltViewModel()
) {

    val authState by phoneAuthViewModel.authState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity   // ✅ SAFE CAST

    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("India") }
    var countryCode by remember { mutableStateOf("+91") }
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    /* ---------------- HANDLE AUTH STATE SAFELY ---------------- */

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                phoneAuthViewModel.resetAuthState()
                navController.navigate("user_profile_set") {
                    popUpTo("register") { inclusive = true }
                }
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                phoneAuthViewModel.resetAuthState()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Enter your phone number",
            fontSize = 20.sp,
            color = colorResource(R.color.light_green),
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = buildAnnotatedString {
                append("WhatsApp will need to verify your number ")
                withStyle(style = SpanStyle(color = colorResource(R.color.dark_green))) {
                    append("What's my number?")
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { expanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(selectedCountry)
                Icon(Icons.Default.ArrowDropDown, null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            mapOf(
                "India" to "+91",
                "USA" to "+1",
                "Canada" to "+1"
            ).forEach { (country, code) ->
                DropdownMenuItem(
                    text = { Text(country) },
                    onClick = {
                        selectedCountry = country
                        countryCode = code
                        expanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (authState !is AuthState.CodeSent) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = countryCode,
                    onValueChange = {},
                    modifier = Modifier.width(80.dp),
                    enabled = false
                )

                Spacer(Modifier.width(8.dp))

                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = { Text("Phone Number") },
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (phoneNumber.isNotBlank() && activity != null) {
                        phoneAuthViewModel.sendVerificationCode(
                            "$countryCode$phoneNumber",
                            activity
                        )
                    }
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.dark_green)
                )
            ) {
                Text("Send OTP")
            }

        } else {

            TextField(
                value = otp,
                onValueChange = { otp = it },
                placeholder = { Text("OTP") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (otp.isNotBlank()) {
                        phoneAuthViewModel.verifyCode(otp, context)
                    }
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.dark_green)
                )
            ) {
                Text("Verify OTP")
            }
        }

        if (authState is AuthState.Loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
