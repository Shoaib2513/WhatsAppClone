package com.example.whatsapp.presentation.callscreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp.R

@Composable
//@Preview(showSystemUi = true)
fun FavouriteSection() {

    val sampleFavourite = listOf(
        FavouriteContact(R.drawable.mrbeast,"Mr Beast"),
        FavouriteContact(R.drawable.sharukh_khan,"Sharukh Khan"),
        FavouriteContact(R.drawable.rashmika,"Rahmika"),
        FavouriteContact(R.drawable.salman_khan,"Salman Khan"),
        FavouriteContact(R.drawable.hrithik_roshan,"Hritik Roshan")

    )

    Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
        Text(
            "Favourites",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            sampleFavourite.forEach {
                FavouriteItem(it)
            }
        }
    }
}

data class FavouriteContact(val image:Int,val name:String)