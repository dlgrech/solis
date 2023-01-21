package com.dgsd.solis.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.solis.R
import com.google.android.material.color.MaterialColors

class HomeDividerItemDecoration(
  context: Context,
) : RecyclerView.ItemDecoration() {

  private val paint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
    color = MaterialColors.getColor(context, R.attr.colorOutline, Color.BLACK)
    alpha = 125
  }

  private val startInset = context.resources.getDimensionPixelSize(R.dimen.padding_large)

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDrawOver(c, parent, state)

    val adapter = parent.adapter
    if (adapter == null) {
      return
    }

    val childCount = parent.childCount
    for (childIndex in 0 until childCount) {
      val child = parent[childIndex]
      val adapterPosition = parent.getChildAdapterPosition(child)

      val viewHolder = parent.findViewHolderForAdapterPosition(adapterPosition)
      if (viewHolder is HomeScreenSavedAccountViewHolder &&
        adapterPosition != adapter.itemCount - 1
      ) {
        c.drawLine(
          (child.left + startInset).toFloat(),
          child.bottom.toFloat(),
          parent.right.toFloat(),
          child.bottom.toFloat(),
          paint
        )
      }
    }
  }
}