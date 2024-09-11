package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import com.cjwjsw.runningman.databinding.DialogCommentBottomSheet2thModalBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Comment2thBottomSheet(feedUid: String,commentKey: String, userUid: String) : BottomSheetDialogFragment(){

    lateinit var binding : DialogCommentBottomSheet2thModalBinding
    val viewModel : CommentViewModel by viewModels()
    val feedUid = feedUid
    val commentKey = commentKey
    val userUid = userUid

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

        //본인 소유의 댓글이 아니라면 삭제 버튼 비활성화
        if(!viewModel.isMyComment(feedUid = feedUid,
                userUid = userUid,
                commentKey = commentKey)){
            binding.deleteBtn.visibility = View.GONE

            //없어진 버튼 비율 맞추기 위해 가중치 조정
            val layoutParamsReport = binding.reportBtn.layoutParams as LinearLayout.LayoutParams
            layoutParamsReport.weight = 1.5f
            binding.reportBtn.layoutParams = layoutParamsReport

            val layoutParamsCancel = binding.cancelBtn.layoutParams as LinearLayout.LayoutParams
            layoutParamsCancel.weight = 1.5f
            binding.cancelBtn.layoutParams = layoutParamsCancel

        }

        val modal = CommentReportBottomSheet(feedUid, userUid)

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


        //취소 버튼
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        //삭제 버튼
        binding.deleteBtn.setOnClickListener{
           viewModel.deleteComment(feedUid,commentKey)
            dismiss()
        }

        //신고 버튼
        binding.reportBtn.setOnClickListener {
              modal.show(childFragmentManager,CommentReportBottomSheet.TAG)
        }
    }




    companion object{
        const val TAG = "SecondModalBottomSheet"
    }
}