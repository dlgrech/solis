package com.dgsd.solis.data.util

import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.util.asPublicKeyOrNull

class HeliusDescriptionSanitizer(
  private val publicKeyFormatter: PublicKeyFormatter,
) {

  fun sanitize(input: String): String {
    return input.splitToSequence(" ").joinToString(" ") { word ->
      val publicKey = word.removeSuffix(".").asPublicKeyOrNull()
      if (publicKey == null) {
        word
      } else {
        publicKeyFormatter.getProgramDisplayName(publicKey) ?: publicKeyFormatter.abbreviate(
          publicKey
        )
      }
    }
  }
}