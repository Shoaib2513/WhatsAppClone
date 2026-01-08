package com.example.whatsapp.presentation.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.whatsapp.presentation.chat_box.ChatDesignModel
import com.example.whatsapp.presentation.chat_box.ChatListBox
import com.example.whatsapp.presentation.viewmodel.BaseViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeBaseViewModel: BaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    var showPopup by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val chatData by homeBaseViewModel.chatList.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    /* ---------------- LOAD CHATS ---------------- */

    LaunchedEffect(userId) {
        userId?.let { homeBaseViewModel.getChatForUser(it) }
    }

    /* ---------------- SORT & SEARCH ---------------- */

    val sortedChats = remember(chatData) {
        chatData.sortedByDescending { it.lastMessageTime ?: 0L }
    }

    val filteredChats = remember(searchText, sortedChats) {
        if (searchText.isBlank()) {
            sortedChats
        } else {
            sortedChats.filter {
                it.name.contains(searchText, ignoreCase = true) ||
                        it.phoneNumber.contains(searchText) ||
                        it.lastMessage.contains(searchText, ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPopup = true },
                containerColor = colorResource(R.color.light_green),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_chat_icon),
                    contentDescription = "Add chat",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {

            /* ---------------- TOP BAR ---------------- */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {

                if (isSearching) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                } else {
                    Text(
                        text = "WhatsApp",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.light_green)
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {

                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) searchText = ""
                    }) {
                        Icon(
                            painter = painterResource(
                                if (isSearching) R.drawable.cross else R.drawable.search
                            ),
                            contentDescription = "Search"
                        )
                    }

                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            painter = painterResource(R.drawable.more),
                            contentDescription = "Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                navHostController.navigate("settingScreen")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Logout", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                FirebaseAuth.getInstance().signOut()
                                navHostController.navigate("welcome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            /* ---------------- ADD USER POPUP ---------------- */

            if (showPopup) {
                AddUserPoppup(
                    onDismiss = { showPopup = false },
                    onUserAdd = { chat ->
                        homeBaseViewModel.addChat(chat)
                        showPopup = false
                    },
                    baseViewModel = homeBaseViewModel
                )
            }

            /* ---------------- CHAT LIST ---------------- */

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = filteredChats,
                    key = { it.otherUserId } // stable key
                ) { chat ->
                    ChatListBox(
                        chatDesignModel = chat,
                        onClick = {
                            navHostController.navigate("chat/${chat.otherUserId}")
                        },
                        baseViewModel = homeBaseViewModel
                    )
                }
            }
        }
    }
}

/* ---------------- ADD USER POPUP ---------------- */

@Composable
fun AddUserPoppup(
    onDismiss: () -> Unit,
    onUserAdd: (ChatDesignModel) -> Unit,
    baseViewModel: BaseViewModel
) {

    var phoneNumber by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var userFound by remember { mutableStateOf<ChatDesignModel?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Add User",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    isSearching = true
                    baseViewModel.searchUserByPhoneNumber(phoneNumber) { user ->
                        isSearching = false
                        userFound = user
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            Spacer(Modifier.height(8.dp))

            when {
                isSearching -> {
                    Text("Searching...", color = Color.Gray)
                }

                userFound != null -> {
                    Text(
                        text = if (userFound!!.name.isNotBlank())
                            "User found: ${userFound!!.name}"
                        else
                            "User found: ${userFound!!.phoneNumber}"
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onUserAdd(userFound!!)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Chat")
                    }
                }

                else -> {
                    Text("No user found", color = Color.Gray)
                }
            }
        }
    }
}
