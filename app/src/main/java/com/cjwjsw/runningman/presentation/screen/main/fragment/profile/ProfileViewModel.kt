package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fbManager:FirebaseStorage
): ViewModel() {
    fun upLoadImage(fileUri : List<Uri>){
        fileUri.forEach {fileUri ->
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val fileReference = fbManager.reference.child("images/$fileName")

            fileReference.putFile(fileUri)
                .addOnSuccessListener {
                    Log.d("upLoadImage",fileUri.toString())
                }
                .addOnFailureListener{
                    Log.d("upLoadImage","failed + $fileUri")
                }
        }
    }
}