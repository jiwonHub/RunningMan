package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    private val _imageUrls = MutableLiveData<List<String>>()
    val imageUrls: LiveData<List<String>> get() = _imageUrls
    fun fetchImage() {
        val storageReference = firebaseStorage.reference.child("ex/")
        storageReference.listAll()
            .addOnSuccessListener { result ->
                val urls = mutableListOf<String>()
                result.items.forEach { item ->
                    item.downloadUrl.addOnSuccessListener { uri ->
                        urls.add(uri.toString())
                        Log.e("fetImage",uri.toString())
                        if (urls.size == result.items.size) {
                            _imageUrls.value = urls
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("FeedViewModel", "Error fetching download URL", exception)
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("FeedViewModel", "Error listing images", exception)
            }
    }
}