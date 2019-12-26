package com.example.imdb.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast

tailrec fun Context?.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun Context?.showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}