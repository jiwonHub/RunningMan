package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.DialogCommentBottomEdittextBinding
import com.cjwjsw.runningman.databinding.DialogCommentBottomSheetModalBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CommentModalBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: DialogCommentBottomSheetModalBinding
    lateinit var editBinding: DialogCommentBottomEdittextBinding
    private val viewModel: DetailFeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogCommentBottomSheetModalBinding.inflate(inflater, container, false)
        editBinding = DialogCommentBottomEdittextBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.dialog_comment_bottom_sheet_modal, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            val constraintLayout =
                dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.container)

            val editTextBinding =
                DialogCommentBottomEdittextBinding.inflate(LayoutInflater.from(context))

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )


            constraintLayout?.addView(editTextBinding.root, layoutParams)
            editTextBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    editTextBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val height = editTextBinding.root.measuredHeight
                    binding.root.setPadding(0, 0, 0, height)
                }
            })
        }

        return bottomSheetDialog

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //버튼 리스너 등록
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        behavior.peekHeight = 0
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

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}