package com.example.imdb.helper

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

open class MyDividerItemDecoration : RecyclerView.ItemDecoration {
    private val mInterItemsGap: Int
    private val mNetOneSidedGap: Int
    private val topInset: Int
    private val bottomInset: Int

    constructor(
        context: Context, @Px itemWidth: Int,
        itemPeekingPercent: Float = .035f, @Px top: Int, @Px bottom: Int
    )
            : this(
        context.resources.displayMetrics.heightPixels,
        itemWidth,
        itemPeekingPercent,
        top,
        bottom
    )

    constructor(
        @Px totalWidth: Int,
        @Px itemWidth: Int,
        itemPeekingPercent: Float = .035f,
        @Px top: Int,
        @Px bottom: Int

    ) {
        val cardPeekingWidth = (itemWidth * itemPeekingPercent + .5f).toInt()
        mInterItemsGap = (totalWidth - itemWidth) / 2
        mNetOneSidedGap = mInterItemsGap / 2 - cardPeekingWidth
        topInset = top
        bottomInset = bottom
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        val index = recyclerView.getChildAdapterPosition(view)
        val isFirstItem = isFirstItem(index)
        val isLastItem = isLastItem(index, recyclerView)

        val leftInset = if (isFirstItem) mInterItemsGap else mNetOneSidedGap
        val rightInset = if (isLastItem) mInterItemsGap else mNetOneSidedGap

        outRect.set(leftInset, topInset, rightInset, bottomInset)
    }

    private fun isFirstItem(index: Int) = index == 0
    private fun isLastItem(index: Int, recyclerView: RecyclerView): Boolean {
        return index == (recyclerView.adapter?.itemCount ?: 0) - 1
    }
}