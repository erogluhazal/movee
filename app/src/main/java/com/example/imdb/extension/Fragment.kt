package com.example.imdb.extension

import androidx.fragment.app.Fragment

fun Fragment.navigateTo(target: Fragment, layoutId: Int) {
    fragmentManager?.beginTransaction()?.run {
        add(layoutId, target)
        addToBackStack(null)
        commitAllowingStateLoss()
    }
}