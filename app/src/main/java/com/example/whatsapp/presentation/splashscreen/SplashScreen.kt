package com.example.whatsapp.presentation.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navHostController: NavHostController) {

    LaunchedEffect(Unit) {
        delay(1000)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // ✅ User already logged in → go directly to Home
            navHostController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }

        } else {
            // ✅ New user → go to Welcome → Register (OTP)
            navHostController.navigate("welcome") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(R.drawable.whatsapp_icon),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("From", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.meta),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = colorResource(R.color.light_green)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Meta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
