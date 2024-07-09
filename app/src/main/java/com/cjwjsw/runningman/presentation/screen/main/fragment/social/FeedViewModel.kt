package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.domain.model.FeedModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _feedArr = MutableLiveData<MutableList<FeedModel>>()
    val feedArr: LiveData<MutableList<FeedModel>> get() = _feedArr
    private val arr : MutableList<FeedModel> = mutableListOf()
    fun fetchFeedData() {
        firebaseFirestore.collection("posts").orderBy("timestamp",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {documents ->
                for(i in 0 .. documents.documents.size){
                    val ref = documents.documents[i].data?.toDataClass<FeedModel>()
                    if(i == 15){
                        break;
                    }
                    if (ref != null) {
                        arr.add(ref)
                    }
                }
                _feedArr.value = arr
            }
            .addOnFailureListener {e ->
                Log.d("FeedViewModel","피드 정보 불러오기 실패 : ${e.toString()}")
            }
    }
    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        val json = JSONObject(this).toString()
        return Gson().fromJson(json, T::class.java)
    }

}