package com.example.chatify.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatify.ChatMessage
import com.example.chatify.MESSAGE
import com.example.chatify.USER_NODE
import com.example.chatify.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel() {
    val signIn = mutableStateOf(false)
    val inProcess = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val currentUserId = mutableStateOf("")
    val messages = mutableListOf<ChatMessage>()

    private val authStateListener = FirebaseAuth.AuthStateListener {
        val user = it.currentUser
        if (user == null) {
            signIn.value = false
            inProcess.value = false
        } else {
            signIn.value = true
            currentUserId.value = user.uid
            getUserData(currentUserId.value)
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun createUser(name: String, email: String, password: String, number: String,context: Context) {
        inProcess.value = true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                inProcess.value = false
                signIn.value = true
                createOrUpdateUser(name = name,number = number,imageUrl = null)
            } else {
                inProcess.value = false
                Toast.makeText(context, "${it.exception}", Toast.LENGTH_SHORT).show()
                Log.d("User Creation", "createUser: ${it.exception}")
            }
        }
    }

    fun uploadData(uri: Uri, name: String, number: String) {
        uploadImage(uri) {
            createOrUpdateUser(name,number, it.toString())
        }
    }
    fun uploadData(name: String,number: String) {
        createOrUpdateUser(name,number,userData.value?.imageUrl)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProcess.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        imageRef.putFile(uri).addOnSuccessListener {
            it.metadata?.reference?.downloadUrl?.addOnSuccessListener(onSuccess)
            inProcess.value = false
        }
    }
    private fun createOrUpdateUser(name: String?, number: String?, imageUrl: String?) {
        inProcess.value = true
        val uid = auth.currentUser?.uid
        userData.value?.name = name
        userData.value?.number = number
        userData.value?.imageUrl = imageUrl
        userData.value?.uid = uid
        val user = hashMapOf(
            "uid" to uid,
            "name" to name,
            "number" to number,
            "imageUrl" to imageUrl
        )
        uid?.let {
            db.collection(USER_NODE)
                .document(uid)
                .set(user)
                .addOnSuccessListener {
                    inProcess.value = false
                }
                .addOnFailureListener { e ->
                    Log.d("Collection", "createOrUpdateUser: A$e")
                    inProcess.value = false
                }

        }
    }

    fun signInUser(email: String, password: String, context: Context) {
        inProcess.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                inProcess.value = false
                signIn.value = true
            }
            .addOnFailureListener {
                inProcess.value = false
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE)
            .document(uid)
            .addSnapshotListener { value, error ->
                run {
                    if (error != null) {
                        Log.d("Db document", "getUserData: ${error.message}")
                    }
                    if (value != null) {
                        val user = value.toObject<UserData>()
                        userData.value = user
                        inProcess.value = false
                    }
                }
            }
    }

    fun getAllUsers(onSuccess: (List<UserData>) -> Unit, onError: (Exception) -> Unit) {
        inProcess.value = true
        db.collection(USER_NODE)
            .get()
            .addOnSuccessListener { result ->
                val userList = result.documents.mapNotNull { document ->
                    document.toObject(UserData::class.java)
                }
                onSuccess(userList)
                inProcess.value = false
            }
            .addOnFailureListener { e ->
                onError(e)
                inProcess.value = false
            }
    }

    fun sendMessage(selectedUserId: String, message: String) {
        // Get a reference to the current user's messages with the selected user
        val messageRef = if (currentUserId.value < selectedUserId) {
            database.getReference(MESSAGE)
                .child(currentUserId.value)
                .child(selectedUserId)
                .push()
        } else {
            database.getReference(MESSAGE)
                .child(selectedUserId)
                .child(currentUserId.value)
                .push()
        }

        // Set the message content
        val messageMap = mapOf(
            "senderId" to currentUserId.value,
            "message" to message
            // Add other message properties as needed
        )
        messageRef.setValue(messageMap).addOnSuccessListener {
            messages.add(ChatMessage(sender = currentUserId.value, message))
        }
    }

    fun realAllMessages(selectedUserId: String) {
        inProcess.value = true
        messages.clear()
        val messageRef = if (currentUserId.value < selectedUserId) {
            database.getReference(MESSAGE).child(currentUserId.value).child(selectedUserId)
        } else {
            database.getReference(MESSAGE).child(selectedUserId).child(currentUserId.value)
        }
        messageRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val sender = childSnapshot.child("senderId").getValue(String::class.java)
                    val message = childSnapshot.child("message").getValue(String::class.java)
                    sender?.let {
                        message?.let {
                            messages.add(ChatMessage(sender, message))
                        }
                    }
                }
                inProcess.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Message Error", "onCancelled: ${error.message}")
                inProcess.value = false
            }
        })
    }


    fun logoutUser() {
        inProcess.value = true
        auth.signOut()
        signIn.value = false
    }
}