package com.example.whatsapp.presentation.chat_box

data class ChatDesignModel(
    val chatId: String = "",
    val otherUserId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long? = null,

    // ✅ UNREAD COUNT
    val unreadCount: Int = 0,

    // ✅ Firebase Storage download URL
    val profileImageUrl: String = ""
)
