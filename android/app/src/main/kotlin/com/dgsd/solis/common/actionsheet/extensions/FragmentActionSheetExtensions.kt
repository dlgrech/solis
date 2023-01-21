package com.dgsd.solis.common.actionsheet.extensions

import androidx.fragment.app.Fragment
import com.dgsd.solis.common.actionsheet.ActionSheetFragment
import com.dgsd.solis.common.actionsheet.model.ActionSheetItem
import com.dgsd.solis.common.fragment.showFrom

fun Fragment.showActionSheet(
  title: CharSequence? = null,
  vararg items: ActionSheetItem,
) {
  val fragment = ActionSheetFragment()
  fragment.sheetTitle = title
  fragment.actionSheetItems = items.toList()

  fragment.showFrom(this)
}