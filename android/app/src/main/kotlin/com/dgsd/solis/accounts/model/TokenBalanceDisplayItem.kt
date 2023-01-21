package com.dgsd.solis.accounts.model

import com.dgsd.khelius.balance.model.TokenBalance

data class TokenBalanceDisplayItem(
  val tokenBalance: TokenBalance,
  val name: CharSequence,
  val balance: CharSequence,
  val account: CharSequence,
)