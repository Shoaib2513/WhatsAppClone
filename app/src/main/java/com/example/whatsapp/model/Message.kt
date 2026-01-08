package com.example.whatsapp.model


data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val status: Int = 0
)
