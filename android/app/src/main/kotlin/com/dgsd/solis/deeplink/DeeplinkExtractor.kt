package com.dgsd.solis.deeplink

import android.net.Uri
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.transactions.model.asTransactionSignatureOrNull
import com.dgsd.solis.util.asPublicKeyOrNull

sealed interface DeeplinkExtractor {

  fun extractDeeplinkTarget(uri: Uri): DeeplinkTarget?

  object Solis : DeeplinkExtractor {

    private const val SCHEME = "solis"
    private const val HOST_TRANSACTION = "transaction"
    private const val HOST_ADDRESS = "address"

    override fun extractDeeplinkTarget(uri: Uri): DeeplinkTarget? {
      if (uri.scheme == SCHEME && uri.pathSegments.size >= 1) {
        when (uri.host) {
          HOST_TRANSACTION -> return maybeCreateTransactionDeeplink(uri.pathSegments[0])
          HOST_ADDRESS -> return maybeCreateAccountDeeplink(uri.pathSegments[0])
        }
      }

      return null
    }

    fun createDeeplink(transactionSignature: TransactionSignature): Uri {
      return Uri.Builder()
        .scheme(SCHEME)
        .authority(HOST_TRANSACTION)
        .appendPath(transactionSignature)
        .build()
    }

    fun createDeeplink(accountKey: PublicKey): Uri {
      return Uri.Builder()
        .scheme(SCHEME)
        .authority(HOST_ADDRESS)
        .appendPath(accountKey.toBase58String())
        .build()
    }
  }

  object SolanaExplorer : DeeplinkExtractor {
    override fun extractDeeplinkTarget(uri: Uri): DeeplinkTarget? {
      if (uri.host == "explorer.solana.com" && uri.pathSegments.size >= 2) {
        when (uri.pathSegments[0]) {
          "tx" -> return maybeCreateTransactionDeeplink(uri.pathSegments[1])
          "address" -> return maybeCreateAccountDeeplink(uri.pathSegments[1])
        }
      }

      return null
    }
  }

  object Solscan : DeeplinkExtractor {
    override fun extractDeeplinkTarget(uri: Uri): DeeplinkTarget? {
      if (uri.host == "solscan.io" && uri.pathSegments.size >= 2) {
        when (uri.pathSegments[0]) {
          "tx" -> return maybeCreateTransactionDeeplink(uri.pathSegments[1])
          "account" -> return maybeCreateAccountDeeplink(uri.pathSegments[1])
        }
      }

      return null
    }
  }

  object SolanaFm : DeeplinkExtractor {
    override fun extractDeeplinkTarget(uri: Uri): DeeplinkTarget? {
      if (uri.host == "solana.fm" && uri.pathSegments.size >= 2) {
        when (uri.pathSegments[0]) {
          "tx" -> return maybeCreateTransactionDeeplink(uri.pathSegments[1])
          "address" -> return maybeCreateAccountDeeplink(uri.pathSegments[1])
        }
      }

      return null
    }
  }

  object SolanaBeach : DeeplinkExtractor {
    override fun extractDeeplinkTarget(uri: Uri): DeeplinkTarget? {
      if (uri.host == "solanabeach.io" && uri.pathSegments.size >= 2) {
        when (uri.pathSegments[0]) {
          "transaction" -> return maybeCreateTransactionDeeplink(uri.pathSegments[1])
          "address" -> return maybeCreateAccountDeeplink(uri.pathSegments[1])
        }
      }

      return null
    }
  }

  companion object {
    private fun maybeCreateTransactionDeeplink(candidate: String): DeeplinkTarget.Transaction? {
      val signature = candidate.asTransactionSignatureOrNull()
      return if (signature == null) {
        null
      } else {
        DeeplinkTarget.Transaction(signature)
      }
    }

    private fun maybeCreateAccountDeeplink(candidate: String): DeeplinkTarget.Account? {
      val accountKey = candidate.asPublicKeyOrNull()
      return if (accountKey == null) {
        null
      } else {
        DeeplinkTarget.Account(accountKey)
      }
    }
  }
}