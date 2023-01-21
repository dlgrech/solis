package com.dgsd.solis.deeplink

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature

sealed interface DeeplinkTarget {

  data class Transaction(val signature: TransactionSignature) : DeeplinkTarget

  data class Account(val accountKey: PublicKey) : DeeplinkTarget
}