package com.dgsd.solis.deeplink

import android.content.Intent
import android.net.Uri
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature

class DeeplinkManager(
  private val deeplinkExtractors: List<DeeplinkExtractor>,
) {

  fun extractDeeplinkTarget(intent: Intent): DeeplinkTarget? {
    val targetUri = intent.data
    if (targetUri != null) {
      for (extractor in deeplinkExtractors) {
        val target = extractor.extractDeeplinkTarget(targetUri)
        if (target != null) {
          return target
        }
      }
    }

    return null
  }

  fun createDeeplinkForTransaction(
    transactionSignature: TransactionSignature
  ): Uri {
    return DeeplinkExtractor.Solis.createDeeplink(transactionSignature)
  }

  fun createDeeplinkForAccount(
    account: PublicKey
  ): Uri {
    return DeeplinkExtractor.Solis.createDeeplink(account)
  }
}