package com.cjwjsw.runningman.presentation.screen.feed

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class feedViewModel @Inject constructor(
    @ApplicationContext val context : Context,
    val firebaseStorage: FirebaseStorage
) : ViewModel() {

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl


    fun fetchImage() {
        var storageRef = firebaseStorage.reference
        var spaceRef = storageRef.child("running.png")
        spaceRef.downloadUrl.addOnSuccessListener { uri ->
            _imageUrl.value = uri.toString()
        }.addOnFailureListener { e ->
            Log.e("FeedViewModel",e.cause.toString())
        }

    }


}