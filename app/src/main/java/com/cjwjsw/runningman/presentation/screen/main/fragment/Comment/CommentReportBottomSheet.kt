package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.cjwjsw.runningman.databinding.DialogCommentBottomCheckReportSheetModalBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentReportBottomSheet(feedUid : String, userUid : String) : BottomSheetDialogFragment() {


    //피드 , 유저 키
    val feedUid = feedUid
    val userUid = userUid

    //바인딩 선언
    lateinit var binding : DialogCommentBottomCheckReportSheetModalBinding

    //신고 타입 설정 변수
    private var selectedReportType: String? = ""

    //체크박스 상태 관리 변수
    private var lastSelectedBox : CheckBox? = null


    val viewModel : CommentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCommentBottomCheckReportSheetModalBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         val bottomSheetDialog = super.onCreateDialog(savedInstanceState)


        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()

        //바텀다이얼로그 초기화
        initBottomSheet()

        // 체크박스 클릭 리스너 설정
        binding.sexcualBox.setOnClickListener { handleCheckboxSelection(binding.sexcualBox) }
        binding.violetBox.setOnClickListener { handleCheckboxSelection(binding.violetBox) }
        binding.hateredBox.setOnClickListener { handleCheckboxSelection(binding.hateredBox) }
        binding.etcBox.setOnClickListener { handleCheckboxSelection(binding.etcBox) }

        //신고 버튼 클릭 리스너 설정
        binding.reportBtn.setOnClickListener {
            if (selectedReportType != null) {
                handleReportClick(selectedReportType!!)
            } else {
                // 신고 카테고리 선택하지 않고 누르면 안내 메시지 오픈
                Toast.makeText(requireContext(), "신고 카테고리를 선택하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initBottomSheet(){ //바텀 다이얼로그 초기화
        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT // 다이얼로그 height 조정
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val oneFifthScreenHeight = (screenHeight / 3) //초기 펼쳐진 크기 조정
        behavior.peekHeight = oneFifthScreenHeight
        behavior.isDraggable = false
        behavior.isHideable = false

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
    }

    private fun handleReportClick(commentText: String) {
        // 로딩창 띄우기
        binding.reportProgressBar.visibility = View.VISIBLE
        // 뷰모델 신고 기능 호출
        viewModel.reportComment(feedUid, userUid, commentText, requireContext())


        // 신고 완료 상태 관리
        viewModel.reportStatus.observe(viewLifecycleOwner) { isSuccess ->
            binding.reportProgressBar.visibility = View.GONE
            if (isSuccess) {
                dismiss()  // 신고 완료 시 다이얼로그 종료
            } else {
                // 신고 실패
                Toast.makeText(requireContext(), "신고에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 체크박스 선택 처리 함수
    private fun handleCheckboxSelection(checkbox: CheckBox) {
        if (lastSelectedBox == checkbox) {
            // 이미 선택된 체크박스를 다시 클릭하면 선택 해제
            checkbox.isChecked = false
            selectedReportType = null
            lastSelectedBox = null
            Log.d(TAG, "선택이 해제되었습니다. selectedReportType 값: $selectedReportType")
        } else {
            // 다른 체크박스를 선택했을 경우
            lastSelectedBox?.isChecked = false // 이전 선택된 체크박스를 해제
            checkbox.isChecked = true // 현재 체크박스를 선택
            lastSelectedBox = checkbox
            selectedReportType = checkbox.text.toString()
            Log.d(TAG, "현재 selectedReportType 값: $selectedReportType")
        }
    }


    companion object {
        const val TAG = "CommentReportBottomSheet"
    }
}