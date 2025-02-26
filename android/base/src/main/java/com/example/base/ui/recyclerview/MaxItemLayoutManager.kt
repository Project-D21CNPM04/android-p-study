package com.example.base.ui.recyclerview

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager

class MaxItemLayoutManager(context: Context?) : LinearLayoutManager(context, VERTICAL, false) {
    private val maxBounds = Rect()
    var maxItem = 0

    override fun setMeasuredDimension(childrenBounds: Rect, wSpec: Int, hSpec: Int) {
        //super.setMeasuredDimension(childrenBounds, wSpec, hSpec)
        if (maxItem == 0) {
            super.setMeasuredDimension(childrenBounds, wSpec, hSpec)
            return
        }

        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE

        val max = Math.min(maxItem, childCount)
        for (i in 0 until max) {
            val child = getChildAt(i) ?: break
            getDecoratedBoundsWithMargins(child, maxBounds)
            if (maxBounds.left < minX) {
                minX = maxBounds.left
            }
            if (maxBounds.right > maxX) {
                maxX = maxBounds.right
            }
            if (maxBounds.top < minY) {
                minY = maxBounds.top
            }
            if (maxBounds.bottom > maxY) {
                maxY = maxBounds.bottom
            }
        }
        maxBounds.set(minX, minY, maxX, maxY)
        childrenBounds.set(maxBounds)
        super.setMeasuredDimension(childrenBounds, wSpec, hSpec)
    }
}