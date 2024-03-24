package com.example.ecom.dialog

import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.ecom.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val cancelButton = view.findViewById<AppCompatButton>(R.id.cancelButtonResetPassword)
    val sendButton = view.findViewById<AppCompatButton>(R.id.sendButtonResetPassword)

    sendButton.setOnClickListener{
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }


    cancelButton.setOnClickListener{
        dialog.dismiss()
    }

}