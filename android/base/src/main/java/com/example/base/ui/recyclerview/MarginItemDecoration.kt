package com.mobile.core.ui.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.base.model.UiDimension


sealed class MarginItemDecoration : RecyclerView.ItemDecoration() {

    sealed class Linear(
    ) : MarginItemDecoration() {
        class Vertical(private val spaceHeight: UiDimension) : Linear() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = spaceHeight.getDimension(parent.context).toInt()
                }
                outRect.bottom = spaceHeight.getDimension(parent.context).toInt()
            }
        }

        class Horizontal(private val spaceHeight: UiDimension) : Linear() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = spaceHeight.getDimension(parent.context).toInt()
                }
                outRect.right = spaceHeight.getDimension(parent.context).toInt()
            }
        }

        class Spacer(
            private val spaceVertical: UiDimension? = null,
            private val spaceHorizontal: UiDimension? = null
        ) : Linear() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (spaceVertical != null) {
                    outRect.top = spaceVertical.getDimension(parent.context).toInt()
                    outRect.bottom = spaceVertical.getDimension(parent.context).toInt()
                }
                if (spaceHorizontal != null) {
                    outRect.left = spaceHorizontal.getDimension(parent.context).toInt()
                    outRect.right = spaceHorizontal.getDimension(parent.context).toInt()
                }
            }
        }
    }

    class Grid(private val spaceHeight: UiDimension, val spanCount: Int) :
        MarginItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val posItem = parent.getChildAdapterPosition(view) + 1
            if (posItem % spanCount != 0) {
                outRect.right = spaceHeight.getDimension(parent.context).toInt()
            }
            if (posItem <= spanCount) {
                outRect.top = spaceHeight.getDimension(parent.context).toInt()
            }
            outRect.bottom = spaceHeight.getDimension(parent.context).toInt()
        }
    }
}
