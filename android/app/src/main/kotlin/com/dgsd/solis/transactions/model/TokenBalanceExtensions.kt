package com.dgsd.solis.transactions.model

import com.dgsd.khelius.balance.model.TokenBalance
import java.math.BigDecimal
import java.math.RoundingMode

fun TokenBalance.asBigDecimal(): BigDecimal {
  return BigDecimal.valueOf(this.balance)
    .movePointLeft(this.decimals)
    .setScale(this.decimals, RoundingMode.UNNECESSARY)
}