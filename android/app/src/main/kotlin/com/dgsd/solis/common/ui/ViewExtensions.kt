package com.dgsd.solis.common.ui

import android.content.Context
import android.graphics.Outline
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Px

@Suppress("DEPRECATION")
fun View.showKeyboard() {
  val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
  this.requestFocus()
}

fun View.hideKeyboard() {
  val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.performHapticFeedback() {
  performHapticFeedback(
    HapticFeedbackConstants.VIRTUAL_KEY_RELEASE,
    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
  )
}

fun View.roundedCorners(@Px radius: Float) {
  clipToOutline = true
  outlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline?) {
      outline?.setRoundRect(0, 0, view.width, view.height, radius)
    }
  }
}