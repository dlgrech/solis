package com.dgsd.solis.home

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.R

class HomeScreenSavedAccountViewHolder private constructor(
  itemView: View,
  private val onAccountClicked: (PublicKey) -> Unit,
) : RecyclerView.ViewHolder(itemView) {

  private val accountName = itemView.requireViewById<TextView>(R.id.account_name)
  private val balanceText = itemView.requireViewById<TextView>(R.id.balance)
  private val balanceLoadingIndicator =
    itemView.requireViewById<View>(R.id.balance_loading_indicator)

  fun bind(item: HomeScreenItem.SavedAccount) {
    accountName.text = item.displayText
    when (item.balanceInfo) {
      HomeScreenItem.SavedAccount.BalanceInfo.Error -> {
        balanceLoadingIndicator.isInvisible = true
        balanceText.isInvisible = false
        balanceText.text = "-"
      }
      HomeScreenItem.SavedAccount.BalanceInfo.Loading -> {
        balanceLoadingIndicator.isInvisible = false
        balanceText.isInvisible = true
      }
      is HomeScreenItem.SavedAccount.BalanceInfo.Loaded -> {
        balanceLoadingIndicator.isInvisible = true
        balanceText.isInvisible = false
        balanceText.text = item.balanceInfo.displayText
      }
    }

    itemView.setOnClickListener {
      onAccountClicked.invoke(item.accountKey)
    }
  }

  companion object {
    fun create(
      parent: ViewGroup,
      onAccountClicked: (PublicKey) -> Unit,
    ): HomeScreenSavedAccountViewHolder {
      val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.li_home_screen_saved_account, parent, false)
      return HomeScreenSavedAccountViewHolder(view, onAccountClicked)
    }
  }
}