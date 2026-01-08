package com.example.whatsapp.presentation.chat_box

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.whatsapp.model.Message
import com.example.whatsapp.presentation.viewmodel.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    otherUserId: String,
    navController: NavHostController,
    baseViewModel: BaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }
    var lastSeen by remember { mutableStateOf(0L) }

    /* ---------------- LOAD DATA ---------------- */

    LaunchedEffect(otherUserId) {
        baseViewModel.getUserInfo(otherUserId) { name, phone, imageUrl ->
            userName = name
            userPhone = phone
            profileImageUrl = imageUrl
        }

        baseViewModel.observeUserPresence(otherUserId) { online, seen ->
            isOnline = online
            lastSeen = seen
        }

        baseViewModel.markMessagesSeen(otherUserId, currentUserId)
        baseViewModel.resetUnreadCount(currentUserId, otherUserId)
        baseViewModel.setChatOpen(otherUserId)
    }

    DisposableEffect(Unit) {
        onDispose { baseViewModel.clearChatOpen() }
    }

    /* ---------------- MESSAGE LISTENER ---------------- */

    LaunchedEffect(Unit) {
        baseViewModel.getMessages(currentUserId, otherUserId) { msg ->
            messages.add(0, msg) // newest first for reverseLayout
        }
    }

    /* ---------------- UI ---------------- */

    Scaffold(
        topBar = {
            ChatTopBar(
                userName,
                userPhone,
                profileImageUrl,
                isOnline,
                lastSeen
            ) { navController.popBackStack() }
        },
        containerColor = Color(0xFFE5DDD5)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding() // ✅ ONLY content reacts to keyboard
        ) {

            /* ---------------- MESSAGE LIST ---------------- */

            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(messages) { msg ->

                    if (msg.senderId == otherUserId && msg.status == 0) {
                        baseViewModel.markMessagesDelivered(otherUserId, currentUserId)
                    }

                    MessageBubble(
                        message = msg.message,
                        time = formatTime(msg.timestamp),
                        isMe = msg.senderId == currentUserId,
                        status = msg.status
                    )
                }
            }

            /* ---------------- INPUT BAR ---------------- */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color(0xFFF0F0F0))
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Message") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.width(6.dp))

                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            baseViewModel.sendMessage(
                                currentUserId,
                                otherUserId,
                                messageText
                            )
                            messageText = ""
                        }
                    },
                    containerColor = Color(0xFF25D366),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    }
}

/* ---------------- TOP BAR ---------------- */

@Composable
fun ChatTopBar(
    userName: String,
    userPhone: String,
    profileImageUrl: String,
    isOnline: Boolean,
    lastSeen: Long,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF075E54))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }

        AsyncImage(
            model = profileImageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
        )

        Spacer(Modifier.width(8.dp))

        Column {
            Text(
                text = if (userName.isNotBlank()) userName else userPhone,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = if (isOnline) "online"
                else "last seen ${formatLastSeen(lastSeen)}",
                color = Color.White.copy(0.7f),
                fontSize = 12.sp
            )
        }
    }
}

/* ---------------- MESSAGE BUBBLE ---------------- */

@Composable
fun MessageBubble(
    message: String,
    time: String,
    isMe: Boolean,
    status: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMe) 64.dp else 8.dp,
                end = if (isMe) 8.dp else 64.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = if (isMe) Color(0xFFDCF8C6) else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                    )
                )
        ) {

            // 🔹 MESSAGE TEXT (extra padding reserved)
            Text(
                text = message,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(
                        start = 10.dp,
                        top = 6.dp,
                        end = 54.dp,   // ✅ reserve space for time + ticks
                        bottom = 18.dp // ✅ reserve space vertically
                    )
            )

            // 🔹 TIME + TICKS (separate layer)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                if (isMe) {
                    Spacer(Modifier.width(4.dp))
                    when (status) {
                        0 -> Icon(
                            Icons.Default.Done,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        1 -> Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        2 -> Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = Color(0xFF34B7F1),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- TIME HELPERS ---------------- */

fun formatTime(time: Long): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time))

fun formatLastSeen(time: Long): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time))
