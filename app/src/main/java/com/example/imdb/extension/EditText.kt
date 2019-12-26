package com.example.imdb.extension

import android.content.Context
import android.widget.EditText
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager


fun EditText.setOnDrawableEndClick(onClickListener: View.OnClickListener) {
    setOnTouchListener(object : OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= right - compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    onClickListener.onClick(this@setOnDrawableEndClick)

                    return true
                }
            }

            return false
        }
    })
}

fun EditText.openKeyboardWithFocus() {
    requestFocus()
    post {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}