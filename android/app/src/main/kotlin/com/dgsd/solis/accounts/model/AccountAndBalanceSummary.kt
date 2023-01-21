package com.dgsd.solis.accounts.model

import com.dgsd.khelius.balance.model.BalanceSummary
import com.dgsd.ksol.core.model.AccountInfo

data class AccountAndBalanceSummary(
  val accountInfo: AccountInfo,
  val balanceSummary: BalanceSummary,
)