package com.example.whatsapp.presentation.chat_box

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.whatsapp.R
import com.example.whatsapp.presentation.viewmodel.BaseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatListBox(
    chatDesignModel: ChatDesignModel,
    onClick: () -> Unit,
    baseViewModel: BaseViewModel
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // -------- PROFILE IMAGE (FIXED) --------
        AsyncImage(
            model = chatDesignModel.profileImageUrl,
            contentDescription = "Profile image",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.user_placeholder),
            error = painterResource(R.drawable.user_placeholder)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // -------- CHAT INFO --------
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = if (chatDesignModel.name.isNotBlank())
                        chatDesignModel.name
                    else
                        chatDesignModel.phoneNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {

                    // ✅ TIME
                    Text(
                        text = formatChatTime(chatDesignModel.lastMessageTime),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ✅ UNREAD MESSAGE COUNT
                    if (chatDesignModel.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chatDesignModel.unreadCount.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chatDesignModel.lastMessage,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
    }
}

/* ---------------- TIME FORMATTER ---------------- */
fun formatChatTime(time: Long?): String {
    if (time == null || time == 0L) return ""

    val timeMillis =
        if (time < 1_000_000_000_000L) time * 1000 else time

    val now = Calendar.getInstance()
    val msg = Calendar.getInstance().apply {
        timeInMillis = timeMillis
    }

    return when {
        now.get(Calendar.YEAR) == msg.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == msg.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(Date(timeMillis))
        }

        now.get(Calendar.YEAR) == msg.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - msg.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "Yesterday"
        }

        else -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                .format(Date(timeMillis))
        }
    }
}
