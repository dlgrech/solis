package com.dgsd.solis.common.ui

import com.dgsd.ksol.core.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.core.model.Lamports
import java.math.BigDecimal
import java.text.NumberFormat

object SolTokenFormatter {

  private const val SOL_SYMBOL = "◎"

  private val numberFormatter = NumberFormat.getNumberInstance().apply {
    maximumFractionDigits = 9
    minimumFractionDigits = 2
    minimumIntegerDigits = 1
  }

  fun format(lamports: Lamports): CharSequence {
    val formattedNumber = numberFormatter.format(
      lamports.toBigDecimal().divide(LAMPORTS_IN_SOL)
    )

    return "$SOL_SYMBOL$formattedNumber"
  }

  fun format(bigDecimal: BigDecimal): CharSequence {
    return numberFormatter.format(bigDecimal)
  }
}