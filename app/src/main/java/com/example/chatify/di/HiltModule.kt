package com.example.chatify.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object HiltModule {

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun providesFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    fun providesFirebaseDatabase(): FirebaseDatabase = Firebase.database
}