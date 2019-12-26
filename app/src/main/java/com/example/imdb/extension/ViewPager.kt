package com.example.imdb.extension

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SwipeLockableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var swipeEnabled = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnabled) super.onTouchEvent(event) else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnabled)
            super.onInterceptTouchEvent(event) else false
    }

    fun setSwipePagingEnabled(swipeEnabled: Boolean) {
        this.swipeEnabled = swipeEnabled
    }
}