package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.cjwjsw.runningman.databinding.DialogCommentBottomSheet2thModalBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Comment2thBottomSheet(val feedUid: String, val commentKey: String) : BottomSheetDialogFragment(){

    lateinit var binding : DialogCommentBottomSheet2thModalBinding
    val viewModel : CommentViewModel by viewModels()

    override fun onCreateView( // 뷰 초기화
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCommentBottomSheet2thModalBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()

        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT // 다이얼로그 height 조정
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val oneFifthScreenHeight = (screenHeight / 10) //초기 펼쳐진 크기 조정
        behavior.peekHeight = oneFifthScreenHeight
        behavior.isHideable = false
        behavior.isDraggable = false

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }
        behavior.addBottomSheetCallback(bottomSheetCallback)

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.deleteBtn.setOnClickListener{
            Log.d("Comment2thBottomSheet","피드 UID : ${feedUid}")
            viewModel.deleteComment(feedUid,commentKey)
            dismiss()
        }
    }

    companion object{
        const val TAG = "SecondModalBottomSheet"
    }
}