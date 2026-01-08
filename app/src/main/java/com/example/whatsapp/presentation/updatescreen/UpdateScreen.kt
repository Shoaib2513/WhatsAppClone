package com.example.whatsapp.presentation.updatescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.whatsapp.presentation.bottomnavigation.BottomNavigation


@Composable
//@Preview(showSystemUi = true)
fun UpdateScreen(navHostController: NavHostController) {

    val sampleStatus = listOf(
        StatusData(R.drawable.disha_patani, "Disha Patani", "10 min ago"),
        StatusData(R.drawable.akshay_kumar, "Akshay Kumar", "2 min ago"),
        StatusData(R.drawable.carryminati, "Carry Minati", "5 min ago")
    )

    val sampleChannel = listOf(
        Channel(R.drawable.neat_roots, "Neat roots", "Latest news in tech"),
        Channel(R.drawable.img, "Food Lover", "Discover new recipe"),
        Channel(R.drawable.meta, "Meta", "Explore the world")
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = colorResource(R.color.light_green),
                modifier = Modifier.size(65.dp),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_photo_camera_24),
                    contentDescription = null
                )
            }
        },
//        bottomBar = {
//            BottomNavigation(navHostController,SelectedItem=0, onClick={index->
//                when(index){
//                    0 -> navHostController.navigate("home")
//                    1 -> navHostController.navigate("update")
//                    2 -> navHostController.navigate("communities")
//                    3 -> navHostController.navigate("call")
//                }
//            })
//        },
        topBar = {
            TopBar()
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            item {
                Text(
                    "Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            item {
                MyStatus()
            }

            items(sampleStatus) {
                StatusItem(statusData = it)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.Gray)
            }

            item {
                Text(
                    "Channels",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Stay updated on topics that matter to you. Find channels to follow below")
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Find channels to follow")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(sampleChannel) {
                ChannelItemDesign(channel = it)
            }
        }
    }
}
