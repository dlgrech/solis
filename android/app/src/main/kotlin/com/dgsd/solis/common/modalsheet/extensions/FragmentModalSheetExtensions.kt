package com.dgsd.solis.common.modalsheet.extensions

import androidx.fragment.app.Fragment
import com.dgsd.solis.R
import com.dgsd.solis.common.fragment.showFrom
import com.dgsd.solis.common.modalsheet.ModalSheetFragment
import com.dgsd.solis.common.modalsheet.model.ModalInfo

fun Fragment.showModal(modalInfo: ModalInfo) {
  val fragment = ModalSheetFragment()
  fragment.modalInfo = modalInfo
  fragment.showFrom(this)
}

fun Fragment.showModalFromErrorMessage(message: CharSequence) {
  showModal(
    modalInfo = ModalInfo(
      title = getString(R.string.error_modal_default_title),
      message = message,
      positiveButton = ModalInfo.ButtonInfo(
        getString(android.R.string.ok)
      )
    )
  )
}