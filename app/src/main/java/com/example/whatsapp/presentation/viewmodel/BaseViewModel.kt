package com.example.whatsapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.whatsapp.model.Message
import com.example.whatsapp.presentation.chat_box.ChatDesignModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BaseViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _chatList = MutableStateFlow<List<ChatDesignModel>>(emptyList())
    val chatList = _chatList.asStateFlow()

    /* ------------------------------------------------ */
    /* CHAT OPEN STATE (UNREAD CONTROL)                 */
    /* ------------------------------------------------ */

    private var openChatUserId: String? = null

    fun setChatOpen(otherUserId: String) {
        openChatUserId = otherUserId
    }

    fun clearChatOpen() {
        openChatUserId = null
    }

    private fun isChatOpen(otherUserId: String): Boolean {
        return openChatUserId == otherUserId
    }

    /* ------------------------------------------------ */
    /* USER INFO (NAME / PHONE)                         */
    /* ------------------------------------------------ */

    fun getUserInfo(
        userId: String,
        onResult: (name: String, phone: String, profileImageUrl: String) -> Unit
    ) {
        database.child("users")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val name =
                        snapshot.child("name").getValue(String::class.java) ?: ""
                    val phone =
                        snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val profileImageUrl =
                        snapshot.child("profileImageUrl").getValue(String::class.java) ?: ""

                    onResult(name, phone, profileImageUrl)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", error.message)
                    onResult("", "", "")
                }
            })
    }


    /* ------------------------------------------------ */
    /* USER PRESENCE (ONLINE / OFFLINE)                 */
    /* ------------------------------------------------ */

    fun setUserOnline() {
        val uid = auth.currentUser?.uid ?: return
        database.child("users")
            .child(uid)
            .child("isOnline")
            .setValue(true)
    }

    fun setUserOffline() {
        val uid = auth.currentUser?.uid ?: return
        database.child("users")
            .child(uid)
            .apply {
                child("isOnline").setValue(false)
                child("lastSeen").setValue(System.currentTimeMillis())
            }
    }

    fun markMessagesDelivered(
        senderId: String,
        receiverId: String
    ) {
        val ref = database.child("messages")
            .child(senderId)
            .child(receiverId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { msgSnap ->
                    val status = msgSnap.child("status")
                        .getValue(Int::class.java) ?: 0

                    if (status == 0) {
                        msgSnap.ref.child("status").setValue(1)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun markMessagesSeen(
        senderId: String,
        receiverId: String
    ) {
        val ref = database.child("messages")
            .child(senderId)
            .child(receiverId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { msgSnap ->
                    val status = msgSnap.child("status")
                        .getValue(Int::class.java) ?: 0

                    if (status < 2) {
                        msgSnap.ref.child("status").setValue(2)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun observeUserPresence(
        userId: String,
        onStatus: (isOnline: Boolean, lastSeen: Long) -> Unit
    ) {
        database.child("users")
            .child(userId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val isOnline =
                        snapshot.child("isOnline").getValue(Boolean::class.java) ?: false
                    val lastSeen =
                        snapshot.child("lastSeen").getValue(Long::class.java) ?: 0L
                    onStatus(isOnline, lastSeen)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /* ------------------------------------------------ */
    /* PHONE NORMALIZATION                              */
    /* ------------------------------------------------ */

    private fun normalizePhone(phone: String): String {
        val clean = phone.replace("\\s".toRegex(), "")
        return when {
            clean.startsWith("+") -> clean
            clean.length == 10 -> "+91$clean"
            else -> clean
        }
    }

    /* ------------------------------------------------ */
    /* SEARCH USER                                     */
    /* ------------------------------------------------ */

    fun searchUserByPhoneNumber(
        phoneNumber: String,
        callback: (ChatDesignModel?) -> Unit
    ) {
        val normalized = normalizePhone(phoneNumber)

        database.child("users")
            .orderByChild("phoneNumber")
            .equalTo(normalized)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        callback(null)
                        return
                    }

                    val userSnap = snapshot.children.first()
                    val userId = userSnap.key ?: return callback(null)

                    callback(
                        ChatDesignModel(
                            otherUserId = userId,
                            name = userSnap.child("name")
                                .getValue(String::class.java) ?: "",
                            phoneNumber = userSnap.child("phoneNumber")
                                .getValue(String::class.java) ?: "",
                            profileImageUrl = userSnap.child("profileImageUrl")
                                .getValue(String::class.java) ?: "",
                            unreadCount = 0
                        )
                    )

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", error.message)
                    callback(null)
                }
            })
    }

    /* ------------------------------------------------ */
    /* ADD CHAT                                        */
    /* ------------------------------------------------ */

    fun addChat(otherUser: ChatDesignModel) {
        val currentUserId = auth.currentUser?.uid ?: return
        val now = System.currentTimeMillis()

        val chatForMe = otherUser.copy(
            lastMessage = "",
            lastMessageTime = now,
            unreadCount = 0
        )

        val chatForOther = ChatDesignModel(
            otherUserId = currentUserId,
            name = auth.currentUser?.displayName ?: "",
            phoneNumber = auth.currentUser?.phoneNumber ?: "",
            lastMessage = "",
            lastMessageTime = now,
            unreadCount = 0
        )

        database.child("userChats")
            .child(currentUserId)
            .child(otherUser.otherUserId)
            .setValue(chatForMe)

        database.child("userChats")
            .child(otherUser.otherUserId)
            .child(currentUserId)
            .setValue(chatForOther)
    }

    /* ------------------------------------------------ */
    /* LOAD CHAT LIST                                  */
    /* ------------------------------------------------ */

    fun getChatForUser(userId: String) {
        database.child("userChats")
            .child(userId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    _chatList.value = snapshot.children.mapNotNull {
                        it.getValue(ChatDesignModel::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", error.message)
                }
            })
    }

    /* ------------------------------------------------ */
    /* SEND MESSAGE                                    */
    /* ------------------------------------------------ */

    fun sendMessage(
        senderId: String,
        receiverId: String,
        messageText: String
    ) {
        val messageId = database.push().key ?: return
        val time = System.currentTimeMillis()

        val message = Message(senderId, receiverId, messageText, time)

        database.child("messages")
            .child(senderId)
            .child(receiverId)
            .child(messageId)
            .setValue(message)

        database.child("messages")
            .child(receiverId)
            .child(senderId)
            .child(messageId)
            .setValue(message)

        updateLastMessageForBothUsers(senderId, receiverId, messageText, time)

        if (!isChatOpen(senderId)) {
            incrementUnreadCount(receiverId, senderId)
        }
    }

    /* ------------------------------------------------ */
    /* LAST MESSAGE                                    */
    /* ------------------------------------------------ */

    private fun updateLastMessageForBothUsers(
        senderId: String,
        receiverId: String,
        message: String,
        time: Long
    ) {
        database.child("userChats").child(senderId)
            .child(receiverId)
            .apply {
                child("lastMessage").setValue(message)
                child("lastMessageTime").setValue(time)
            }

        database.child("userChats").child(receiverId)
            .child(senderId)
            .apply {
                child("lastMessage").setValue(message)
                child("lastMessageTime").setValue(time)
            }
    }

    /* ------------------------------------------------ */
    /* UNREAD COUNT                                    */
    /* ------------------------------------------------ */

    private fun incrementUnreadCount(userId: String, otherUserId: String) {
        val ref = database.child("userChats")
            .child(userId)
            .child(otherUserId)
            .child("unreadCount")

        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                data.value = (data.getValue(Int::class.java) ?: 0) + 1
                return Transaction.success(data)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {}
        })
    }

    fun resetUnreadCount(userId: String, otherUserId: String) {
        database.child("userChats")
            .child(userId)
            .child(otherUserId)
            .child("unreadCount")
            .setValue(0)
    }

    /* ------------------------------------------------ */
    /* RECEIVE MESSAGES                                */
    /* ------------------------------------------------ */

    fun getMessages(
        senderId: String,
        receiverId: String,
        onNewMessage: (Message) -> Unit
    ) {
        database.child("messages")
            .child(senderId)
            .child(receiverId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(Message::class.java)?.let(onNewMessage)
                }

                override fun onCancelled(error: DatabaseError) {}
                override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
            })
    }
}
