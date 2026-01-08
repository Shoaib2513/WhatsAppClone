package com.example.whatsapp.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PhoneAuthUser(
    val userId: String = "",
    val phoneNumber: String = "",
    val name: String = "",
    val status: String = "",

    // ✅ Firebase Storage download URL
    val profileImageUrl: String = "",

    // ✅ Presence
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L
)
