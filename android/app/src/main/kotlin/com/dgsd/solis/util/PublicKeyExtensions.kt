package com.dgsd.solis.util

import com.dgsd.ksol.core.model.PublicKey

fun String.asPublicKeyOrNull(): PublicKey? {
  return if (length < PublicKey.PUBLIC_KEY_LENGTH) {
    null
  } else {
    runCatching { PublicKey.fromBase58(this) }.getOrNull()
  }
}