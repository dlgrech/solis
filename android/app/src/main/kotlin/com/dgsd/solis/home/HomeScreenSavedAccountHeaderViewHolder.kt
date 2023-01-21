package com.dgsd.solis.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.R

class HomeScreenSavedAccountHeaderViewHolder private constructor(
  itemView: View,
) : RecyclerView.ViewHolder(itemView) {


  fun bind(item: HomeScreenItem.SavedAccount) {
    (itemView as TextView).text = item.displayText
  }

  companion object {
    fun create(
      parent: ViewGroup,
    ): HomeScreenSavedAccountHeaderViewHolder {
      val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.li_home_screen_saved_account_header, parent, false)
      return HomeScreenSavedAccountHeaderViewHolder(view)
    }
  }
}