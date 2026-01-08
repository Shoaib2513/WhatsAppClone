package com.example.whatsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whatsapp.presentation.chat_box.ChatScreen
import com.example.whatsapp.presentation.profile.UserProfileSetScreen
import com.example.whatsapp.presentation.settingscreen.SettingScreen
import com.example.whatsapp.presentation.splashscreen.SplashScreen
import com.example.whatsapp.presentation.userregistrationscreen.UserRegistrationScreen
import com.example.whatsapp.presentation.welcomescreen.WelcomeScreen
import com.example.whatsapp.ui.theme.MainTabScreen

@Composable
fun WhatsAppNavigationSystem() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // ---------- SPLASH ----------
        composable("splash") {
            SplashScreen(navController)
        }

        // ---------- AUTH FLOW ----------
        composable("welcome") {
            WelcomeScreen(navController)
        }

        composable("register") {
            UserRegistrationScreen(navController)
        }

        composable("user_profile_set") {
            UserProfileSetScreen(navController)
        }

        // ---------- MAIN (BOTTOM NAV) ----------
        composable("main") {
            MainTabScreen(rootNavController = navController)
        }

        // ---------- SETTINGS ----------
        composable("settingScreen") {
            SettingScreen()
        }

        // ---------- CHAT ----------
        composable(
            route = "chat/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val otherUserId =
                backStackEntry.arguments?.getString("userId") ?: return@composable

            ChatScreen(
                otherUserId = otherUserId,
                navController = navController
            )
        }
    }
}
