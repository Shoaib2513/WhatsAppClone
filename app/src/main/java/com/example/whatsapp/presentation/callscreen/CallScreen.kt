package com.example.whatsapp.presentation.callscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun CallScreen(navHostController: NavHostController) {
    val sampleCall = listOf(
        CallData(R.drawable.bhuvan_bam,"Bhuvan Bam","yesterday 8:30 AM",true),
        CallData(R.drawable.sharukh_khan,"Sharukh Khan","today 2:50 PM",false),
        CallData(R.drawable.akshay_kumar,"Akshay Kumar","yesterday 9:30 AM",false),
        CallData(R.drawable.salman_khan,"Salman Khan","20 Dec 4:30 PM",true),
        CallData(R.drawable.rashmika,"Rashmika","13 Dec 6:40 PM",false),
        CallData(R.drawable.rajkummar_rao,"Rajkumar Rao","2 Dec 2:30 PM",true),
        CallData(R.drawable.sharadha_kapoor,"Shraddha Kapoor","8 Nov 9:40 AM",true)
    )

    var isSearching by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        if (isSearching) {
                            TextField(
                                value = search,
                                onValueChange = { search = it },
                                placeholder = { Text("Search") },
                                singleLine = true,
                                modifier = Modifier.padding(start = 12.dp),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                )
                            )
                        } else {
                            Text(
                                text = "Call",
                                fontSize = 28.sp,
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (isSearching) {
                            IconButton(onClick = {
                                isSearching = false
                                search = ""
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.cross),
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        } else {

                            IconButton(onClick = { isSearching = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.more),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = { showMenu = false }
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider()
                }
            }

        },
//            bottomBar = {
//                BottomNavigation(navHostController,SelectedItem=0, onClick={index->
//                    when(index){
//                        0 -> navHostController.navigate("home")
//                        1 -> navHostController.navigate("update")
//                        2 -> navHostController.navigate("communities")
//                        3 -> navHostController.navigate("call")
//                    }
//                })
//            },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = colorResource(R.color.light_green),
                modifier = Modifier.size(65.dp),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_call),
                    contentDescription = null
                )
            }
        }
        ) {
        Column(Modifier.padding(it)) {
            Spacer(modifier = Modifier.height(16.dp))
            FavouriteSection()
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_green)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Start a new call",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Recent Calls",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn {
                items(sampleCall){ data->
                    CallItemDesign(data)
                }
            }
        }
    }
}