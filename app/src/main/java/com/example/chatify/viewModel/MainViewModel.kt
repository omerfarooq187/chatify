package com.example.chatify.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatify.USER_NODE
import com.example.chatify.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val auth: FirebaseAuth, private val db: FirebaseFirestore):ViewModel() {
    val signIn = mutableStateOf(false)
    val inProcess = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser!=null
        val uid = currentUser?.uid
        getUserData(uid!!)
    }
    fun createUser(name: String,email:String, password:String,context:Context) {
        inProcess.value = true
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                inProcess.value = false
                signIn.value = true
                createOrUpdateUser(name)
            }
            else {
                inProcess.value = false
                Toast.makeText(context,"${it.exception}",Toast.LENGTH_SHORT).show()
                Log.d("User Creation", "createUser: ${it.exception}")
            }
        }
    }

    fun createOrUpdateUser(name:String?) {
        inProcess.value = true
        val userData = UserData(
            name
        )
        db.collection(USER_NODE)
            .add(userData)
            .addOnSuccessListener {
                Log.d("Database","${it.id}")
            }
            .addOnFailureListener {
                Log.d("Database", "createOrUpdateUser: ${it.message}")
            }
    }

    fun signInUser(email: String,password: String,context: Context) {
        inProcess.value = true
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                inProcess.value = false
                signIn.value = true

            }
            .addOnFailureListener {
                inProcess.value = false
                Toast.makeText(context,"${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    fun getUserData(uid:String) {
        inProcess.value = true
        db.collection(USER_NODE)
            .document(uid)
            .addSnapshotListener {
                value, error ->
                run {
                    if (error!=null) {
                        Log.d("Db document", "getUserData: ${error.message}")
                    }
                    if (value!=null) {
                        val user = value.toObject<UserData>()
                        userData.value = user
                        inProcess.value = false
                    }
                }
        }
    }
}