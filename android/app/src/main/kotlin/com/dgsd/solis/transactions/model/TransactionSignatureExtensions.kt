package com.dgsd.solis.transactions.model

import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.core.utils.DecodingUtils

/**
 * The number of bytes expected to be contained in valid transaction signature
 */
const val TRANSACTION_SIGNATURE_LENGTH = 64

fun String.asTransactionSignatureOrNull(): TransactionSignature? {
  val base58 = runCatching { DecodingUtils.decodeBase58(this) }.getOrNull()
  return when {
    base58 == null -> null
    base58.size != TRANSACTION_SIGNATURE_LENGTH -> null
    else -> this
  }
}