package com.example.whatsapp.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.whatsapp.presentation.bottomnavigation.BottomNavigation
import com.example.whatsapp.presentation.callscreen.CallScreen
import com.example.whatsapp.presentation.communitiesscreen.CommunitiesScreen
import com.example.whatsapp.presentation.homescreen.HomeScreen
import com.example.whatsapp.presentation.updatescreen.UpdateScreen

@Composable
fun MainTabScreen(rootNavController: NavHostController) {

    val tabNavController = rememberNavController()

    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedIndex = when (currentRoute) {
        "home" -> 0
        "update" -> 1
        "communities" -> 2
        "call" -> 3
        else -> 0
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                navHostController = tabNavController,
                SelectedItem = selectedIndex,
                onClick = { index ->
                    when (index) {
                        0 -> tabNavController.navigate("home")
                        1 -> tabNavController.navigate("update")
                        2 -> tabNavController.navigate("communities")
                        3 -> tabNavController.navigate("call")
                    }
                }
            )
        }
    ) { paddingValues ->

        NavHost(
            navController = tabNavController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("home") {
                HomeScreen(navHostController = rootNavController)
            }

            composable("update") {
                UpdateScreen(navHostController = rootNavController)
            }

            composable("communities") {
                CommunitiesScreen(navHostController = rootNavController)
            }

            composable("call") {
                CallScreen(navHostController = rootNavController)
            }
        }
    }
}
