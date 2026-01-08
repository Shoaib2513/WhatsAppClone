package com.example.whatsapp.presentation.welcomescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.whatsapp.R

@Composable

fun WelcomeScreen(navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.whatsapp_sticker),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )

        Text(
            "Welcome to WhatsApp",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Read our", color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Privacy Policy", color = colorResource(R.color.light_green))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tap 'Agree and Continue' to", color = Color.Gray)
        }

        Row {
            Text("accept the", color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Terms of Services", color = colorResource(R.color.light_green))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navHostController.navigate("register") },
            modifier = Modifier.size(280.dp, 43.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.dark_green)
            )
        ) {
            Text("Agree and Continue", fontSize = 16.sp)
        }
    }
}
