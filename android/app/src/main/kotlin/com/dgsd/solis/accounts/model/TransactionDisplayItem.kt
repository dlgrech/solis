package com.dgsd.solis.accounts.model

import com.dgsd.ksol.core.model.TransactionSignature

data class TransactionDisplayItem(
  val signature: TransactionSignature,
  val type: CharSequence,
  val description: CharSequence,
  val timeText: CharSequence,
)