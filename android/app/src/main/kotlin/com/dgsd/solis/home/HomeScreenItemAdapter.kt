package com.dgsd.solis.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.ksol.core.model.PublicKey

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ACCOUNT = 1

class HomeScreenItemAdapter(
  private val onAccountClicked: (PublicKey) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val savedItems = mutableListOf<HomeScreenItem.SavedAccount>()

  init {
    setHasStableIds(true)
  }

  fun update(
    saved: List<HomeScreenItem.SavedAccount>
  ) {
    savedItems.clear()
    this.savedItems.addAll(saved)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerView.ViewHolder {
    return when (viewType) {
      VIEW_TYPE_HEADER -> HomeScreenSavedAccountHeaderViewHolder.create(parent)
      VIEW_TYPE_ACCOUNT -> HomeScreenSavedAccountViewHolder.create(parent, onAccountClicked)
      else -> error("Unknown view type: $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is HomeScreenSavedAccountViewHolder -> holder.bind(savedItems[getSavedItemPosition(position)])
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (position == 0) {
      VIEW_TYPE_HEADER
    } else {
      VIEW_TYPE_ACCOUNT
    }
  }

  override fun getItemCount(): Int {
    return if (savedItems.isEmpty()) {
      0
    } else {
      savedItems.size + 1
    }
  }

  override fun getItemId(position: Int): Long {
    return if (position == 0) {
      -1
    } else {
      savedItems[getSavedItemPosition(position)].accountKey.hashCode().toLong()
    }
  }

  private fun getSavedItemPosition(adapterPosition: Int): Int {
    return adapterPosition - 1
  }
}