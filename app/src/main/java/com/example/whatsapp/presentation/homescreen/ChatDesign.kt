package com.example.whatsapp.presentation.homescreen

import androidx.compose.foundation.Image
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
import com.example.whatsapp.R
import com.example.whatsapp.presentation.chat_box.ChatDesignModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatDesign(
    chatDesignModel: ChatDesignModel
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // -------- PROFILE IMAGE --------
        Image(
            painter = painterResource(id = R.drawable.user_placeholder),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // -------- CHAT DETAILS --------
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = chatDesignModel.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // ✅ ALWAYS FORMATTED (NEVER RAW NUMBER)
                Text(
                    text = formatChatTime(chatDesignModel.lastMessageTime),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chatDesignModel.lastMessage,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

/* ---------------- FINAL SAFE TIME FORMATTER ---------------- */
fun formatChatTime(time: Long?): String {
    if (time == null || time == 0L) return ""

    // ✅ AUTO HANDLE SECONDS vs MILLISECONDS
    val timeMillis = if (time < 1_000_000_000_000L) {
        time * 1000   // seconds → milliseconds
    } else {
        time          // already milliseconds
    }

    val now = Calendar.getInstance()
    val msgTime = Calendar.getInstance().apply {
        timeInMillis = timeMillis
    }

    return when {
        // Today
        now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(Date(timeMillis))
        }

        // Yesterday
        now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - msgTime.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "Yesterday"
        }

        // Older
        else -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                .format(Date(timeMillis))
        }
    }
}
