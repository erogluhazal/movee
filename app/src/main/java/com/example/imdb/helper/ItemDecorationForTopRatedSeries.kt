package com.example.imdb.helper

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

open class ItemDecorationForTopRatedSeries : RecyclerView.ItemDecoration {
    private val topInset: Int
    private val bottomInset: Int
    private val leftInset: Int
    private val rightInset: Int

    constructor(
        context: Context,
        @Px top: Int, @Px bottom: Int, @Px left: Int, @Px right: Int
    )
            : this(
        top,
        bottom,
        left,
        right
    )

    constructor(
        @Px top: Int,
        @Px bottom: Int,
        @Px left: Int,
        @Px right: Int

    ) {
        topInset = top
        bottomInset = bottom
        leftInset = left
        rightInset = right
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(leftInset, topInset, rightInset, bottomInset)
    }

}