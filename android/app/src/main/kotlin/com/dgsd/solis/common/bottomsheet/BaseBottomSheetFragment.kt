package com.dgsd.solis.common.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.dgsd.solis.common.ui.enableBackgroundBlur
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetFragment : BottomSheetDialogFragment() {

  @SuppressLint("RestrictedApi")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dialog?.window?.enableBackgroundBlur()
    (dialog as? BottomSheetDialog)?.behavior?.disableShapeAnimations()
  }
}