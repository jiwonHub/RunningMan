package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.BuildConfig
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.CommentModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.inject.Inject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@HiltViewModel
class CommentViewModel @Inject constructor() : ViewModel() {

    private val _reportStatus = MutableLiveData<Boolean>()
    val reportStatus: LiveData<Boolean> get() = _reportStatus

    fun uploadComment(
        comment: String,
        feedUid: String,
        userName: String,
        profileImg: String,
        userNumber: String
    ) {
        val ref = fbRef.getReference(feedUid).child("comments")
        val newCommentKey = ref.push().key //고유 키 생성
        val newComment = CommentModel( //데이터 만들기
            comment = comment,
            timestamp = System.currentTimeMillis() / 1000,
            userName = userName,
            profileUrl = profileImg,
            userUid = userid.toString(),
            userNumber = userNumber,
            newCommentKey = newCommentKey.toString()
        )

        if (newCommentKey != null) {
            ref.child(newCommentKey).setValue(newComment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Comment added successfully")
                } else {
                    Log.d(TAG, "Failed to add comment: ${task.exception}")
                }
            }
        }
    }

    fun deleteComment(feedUid : String,commentKey : String){
        Log.d(TAG,"댓글 삭제 : $commentKey")
        val ref =fbRef.getReference(feedUid).child("comments")

        ref.child(commentKey).removeValue().addOnSuccessListener {
            Log.d(TAG,"댓글 삭제 완료")
        }.addOnFailureListener { e ->
            Log.d(TAG,"댓글 삭제 실패 :${e.message}")
        }
    }

    fun isMyComment(feedUid: String,commentKey: String,userUid: String) : Boolean{ //내 댓글인지 아닌지 판단
        val ref = fbRef.getReference(feedUid).child("comments")
        var data = false

        ref.child(commentKey).get().addOnSuccessListener {snapShot ->
               data = snapShot.child(userUid).exists()
            Log.d(TAG,"본인 댓글? : $data")
        }
            .addOnFailureListener {e ->
                data = false
                Log.d(TAG,"본인 댓글인지 판단 불가 : ${e.message}")
            }
        return data
    }

    fun reportComment(
        feedUid: String,
        userUid: String,
        text: String,
        context: Context,
    ) {
        val fromEmail = BuildConfig.ReportMail
        val password = BuildConfig.ReportAppKey // 구글 앱 키
        val toEmail =  BuildConfig.TestMail // 받는 사람 이메일 주소
        val subject = "댓글 신고"  // 이메일 제목
        val messageBody = "Feed UID: $feedUid\nUser UID: $userUid\n신고 내용: $text"  // 이메일 내용

        CoroutineScope(Dispatchers.IO).launch {
            // SMTP 속성정의
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            // SMTP 세션 서버 만들기
            val session = Session.getInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(fromEmail, password)
                    }
                }
            )

            try {
                // 이메일 구성
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(fromEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                    setSubject(subject)
                    setText(messageBody)
                }

                // 이메일 전송
                Transport.send(message)

                // 토스트 메시지는 메인(UI) 스레드에서만 호출 가능하므로 토스트 메시지 호출시에만 메인 스레드로 변경
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "신고 완료되었습니다", Toast.LENGTH_SHORT).show()
                    _reportStatus.value = true
                }
                Log.d(TAG,"신고 완료")
            } catch (e: MessagingException) {
                // 토스트 메시지는 메인(UI) 스레드에서만 호출 가능하므로 토스트 메시지 호출시에만 메인 스레드로 변경
                withContext(Dispatchers.Main) {
                    _reportStatus.value = false
                }
                Log.d(TAG,"신고 실패 : ${e.stackTrace}")
            }
        }
    }
    companion object{
        val fbRef = Firebase.database
        val userInfo = UserManager.getInstance()
        val userid = userInfo?.idToken
        val userNumber = userInfo?.userNumber
        val TAG = "CommentViewModel"
    }

}