package com.dgsd.solis.home

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature

sealed interface HomeScreenItem {

  sealed interface SearchResult : HomeScreenItem {

    data class CategoryHeader(
      val displayText: CharSequence,
    ) : SearchResult

    data class AccountResult(
      val accountKey: PublicKey,
      val displayText: CharSequence,
    ) : SearchResult

    data class TransactionResult(
      val transactionSignature: TransactionSignature,
      val displayText: CharSequence,
    ) : SearchResult

    data class ProgramResult(
      val programAddress: PublicKey,
      val displayText: CharSequence
    ) : SearchResult
  }

  data class SavedAccount(
    val accountKey: PublicKey,
    val displayText: CharSequence,
    val balanceInfo: BalanceInfo,
  ) : HomeScreenItem {

    sealed interface BalanceInfo {
      object Loading : BalanceInfo
      object Error: BalanceInfo
      class Loaded(val displayText: CharSequence): BalanceInfo
    }
  }

}