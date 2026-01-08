package com.example.whatsapp.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.whatsapp.model.PhoneAuthUser
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Ideal)
    val authState = _authState.asStateFlow()

    private val userRef = database.reference.child("users")

    /* ---------------- SEND OTP ---------------- */

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {

        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                _authState.value = AuthState.CodeSent(verificationId)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredential(credential, activity)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /* ---------------- VERIFY OTP ---------------- */

    fun verifyCode(otp: String, context: Context) {
        val state = _authState.value
        if (state !is AuthState.CodeSent) {
            _authState.value = AuthState.Error("OTP not requested")
            return
        }

        val credential = PhoneAuthProvider.getCredential(state.verificationId, otp)
        signInWithCredential(credential, context)
    }

    /* ---------------- SIGN IN ---------------- */

    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        context: Context
    ) {
        _authState.value = AuthState.Loading

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = firebaseAuth.currentUser ?: return@addOnSuccessListener
                markUserAsSignedIn(context)
                fetchUserProfile(user.uid)
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
            }
    }

    /* ---------------- SAVE PROFILE (UPDATED) ---------------- */

    fun saveUserProfile(
        userId: String,
        name: String,
        status: String,
        profileImageUri: Uri?
    ) {
        val phone = firebaseAuth.currentUser?.phoneNumber ?: ""

        // Save text fields first
        val baseUserData = mapOf(
            "userId" to userId,
            "phoneNumber" to phone,
            "name" to name,
            "status" to status,
            "isOnline" to true,
            "lastSeen" to System.currentTimeMillis()
        )

        userRef.child(userId).updateChildren(baseUserData)

        // Upload image if selected
        if (profileImageUri != null) {
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("profile_images/$userId.jpg")

            storageRef.putFile(profileImageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        userRef.child(userId)
                            .child("profileImageUrl")
                            .setValue(uri.toString())
                    }
                }
                .addOnFailureListener {
                    Log.e("PhoneAuthVM", "Image upload failed", it)
                }
        }
    }

    /* ---------------- FETCH PROFILE ---------------- */

    fun fetchUserProfile(userId: String) {
        userRef.child(userId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = snapshot.getValue(PhoneAuthUser::class.java)
                    if (user != null) {
                        _authState.value = AuthState.Success(user)
                    }
                }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to load profile")
            }
    }

    /* ---------------- ONLINE / OFFLINE ---------------- */

    fun setOnline(userId: String) {
        userRef.child(userId).child("isOnline").setValue(true)
    }

    fun setOffline(userId: String) {
        userRef.child(userId).apply {
            child("isOnline").setValue(false)
            child("lastSeen").setValue(System.currentTimeMillis())
        }
    }

    /* ---------------- HELPERS ---------------- */

    fun markUserAsSignedIn(context: Context) {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("isSignedIn", true)
            .apply()
    }

    fun resetAuthState() {
        _authState.value = AuthState.Ideal
    }

    fun signOut(activity: Activity) {
        firebaseAuth.signOut()
        activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}

/* ---------------- AUTH STATE ---------------- */

sealed class AuthState {
    object Ideal : AuthState()
    object Loading : AuthState()
    data class CodeSent(val verificationId: String) : AuthState()
    data class Success(val user: PhoneAuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
