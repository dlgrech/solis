package com.dgsd.solis.common.ui

import android.content.Context
import com.dgsd.khelius.common.model.TransactionType
import com.dgsd.solis.R

class TransactionTypeFormatter(
  private val context: Context
) {

  fun format(transactionType: TransactionType): CharSequence {
    val resId = when (transactionType) {
      TransactionType.UNKNOWN -> R.string.transaction_type_unknown
      TransactionType.NFT_BID -> R.string.transaction_type_nft_bid
      TransactionType.NFT_BID_CANCELLED -> R.string.transaction_type_nft_bid_cancelled
      TransactionType.NFT_LISTING -> R.string.transaction_type_nft_listing
      TransactionType.NFT_CANCEL_LISTING -> R.string.transaction_type_nft_cancel_listing
      TransactionType.NFT_SALE -> R.string.transaction_type_nft_sale
      TransactionType.NFT_MINT -> R.string.transaction_type_nft_mint
      TransactionType.NFT_AUCTION_CREATED -> R.string.transaction_type_nft_auction_created
      TransactionType.NFT_AUCTION_UPDATED -> R.string.transaction_type_nft_auction_updated
      TransactionType.NFT_AUCTION_CANCELLED -> R.string.transaction_type_nft_auction_cancelled
      TransactionType.NFT_PARTICIPATION_REWARD -> R.string.transaction_type_nft_participation_reward
      TransactionType.NFT_MINT_REJECTED -> R.string.transaction_type_nft_mint_rejected
      TransactionType.CREATE_STORE -> R.string.transaction_type_create_store
      TransactionType.WHITELIST_CREATOR -> R.string.transaction_type_whitelist_creator
      TransactionType.ADD_TO_WHITELIST -> R.string.transaction_type_add_to_whitelist
      TransactionType.REMOVE_FROM_WHITELIST -> R.string.transaction_type_remove_from_whitelist
      TransactionType.AUCTION_MANAGER_CLAIM_BID -> R.string.transaction_type_auction_manager_claim_bid
      TransactionType.EMPTY_PAYMENT_ACCOUNT -> R.string.transaction_type_empty_payment_account
      TransactionType.UPDATE_PRIMARY_SALE_METADATA -> R.string.transaction_type_update_primary_sale_metadata
      TransactionType.ADD_TOKEN_TO_VAULT -> R.string.transaction_type_add_token_to_vault
      TransactionType.ACTIVATE_VAULT -> R.string.transaction_type_activate_vault
      TransactionType.INIT_VAULT -> R.string.transaction_type_init_vault
      TransactionType.INIT_BANK -> R.string.transaction_type_init_bank
      TransactionType.INIT_STAKE -> R.string.transaction_type_init_stake
      TransactionType.MERGE_STAKE -> R.string.transaction_type_merge_stake
      TransactionType.SPLIT_STAKE -> R.string.transaction_type_split_stake
      TransactionType.SET_BANK_FLAGS -> R.string.transaction_type_set_bank_flags
      TransactionType.SET_VAULT_LOCK -> R.string.transaction_type_set_vault_lock
      TransactionType.UPDATE_VAULT_OWNER -> R.string.transaction_type_update_vault_owner
      TransactionType.UPDATE_BANK_MANAGER -> R.string.transaction_type_update_bank_manager
      TransactionType.RECORD_RARITY_POINTS -> R.string.transaction_type_record_rarity_points
      TransactionType.ADD_RARITIES_TO_BANK -> R.string.transaction_type_add_rarities_to_bank
      TransactionType.INIT_FARM -> R.string.transaction_type_init_farm
      TransactionType.INIT_FARMER -> R.string.transaction_type_init_farmer
      TransactionType.REFRESH_FARMER -> R.string.transaction_type_refresh_farmer
      TransactionType.UPDATE_FARM -> R.string.transaction_type_update_farm
      TransactionType.AUTHORIZE_FUNDER -> R.string.transaction_type_authorize_funder
      TransactionType.DEAUTHORIZE_FUNDER -> R.string.transaction_type_deauthorize_funder
      TransactionType.FUND_REWARD -> R.string.transaction_type_fund_reward
      TransactionType.CANCEL_REWARD -> R.string.transaction_type_cancel_reward
      TransactionType.LOCK_REWARD -> R.string.transaction_type_lock_reward
      TransactionType.PAYOUT -> R.string.transaction_type_payout
      TransactionType.VALIDATE_SAFETY_DEPOSIT_BOX_V2 -> R.string.transaction_type_validate_safety_deposit_box_v2
      TransactionType.SET_AUTHORITY -> R.string.transaction_type_set_authority
      TransactionType.INIT_AUCTION_MANAGER_V2 -> R.string.transaction_type_init_auction_manager_v2
      TransactionType.UPDATE_EXTERNAL_PRICE_ACCOUNT -> R.string.transaction_type_update_external_price_account
      TransactionType.AUCTION_HOUSE_CREATE -> R.string.transaction_type_auction_house_create
      TransactionType.CLOSE_ESCROW_ACCOUNT -> R.string.transaction_type_close_escrow_account
      TransactionType.WITHDRAW -> R.string.transaction_type_withdraw
      TransactionType.DEPOSIT -> R.string.transaction_type_deposit
      TransactionType.TRANSFER -> R.string.transaction_type_transfer
      TransactionType.BURN -> R.string.transaction_type_burn
      TransactionType.BURN_NFT -> R.string.transaction_type_burn_nft
      TransactionType.PLATFORM_FEE -> R.string.transaction_type_platform_fee
      TransactionType.LOAN -> R.string.transaction_type_loan
      TransactionType.REPAY_LOAN -> R.string.transaction_type_repay_loan
      TransactionType.ADD_TO_POOL -> R.string.transaction_type_add_to_pool
      TransactionType.REMOVE_FROM_POOL -> R.string.transaction_type_remove_from_pool
      TransactionType.CLOSE_POSITION -> R.string.transaction_type_close_position
      TransactionType.UNLABELED -> R.string.transaction_type_unlabeled
      TransactionType.CLOSE_ACCOUNT -> R.string.transaction_type_close_account
      TransactionType.WITHDRAW_GEM -> R.string.transaction_type_withdraw_gem
      TransactionType.DEPOSIT_GEM -> R.string.transaction_type_deposit_gem
      TransactionType.STAKE_TOKEN -> R.string.transaction_type_stake_token
      TransactionType.UNSTAKE_TOKEN -> R.string.transaction_type_unstake_token
      TransactionType.STAKE_SOL -> R.string.transaction_type_stake_sol
      TransactionType.UNSTAKE_SOL -> R.string.transaction_type_unstake_sol
      TransactionType.CLAIM_REWARDS -> R.string.transaction_type_claim_rewards
      TransactionType.BUY_SUBSCRIPTION -> R.string.transaction_type_buy_subscription
      TransactionType.SWAP -> R.string.transaction_type_swap
      TransactionType.INIT_SWAP -> R.string.transaction_type_init_swap
      TransactionType.CANCEL_SWAP -> R.string.transaction_type_cancel_swap
      TransactionType.REJECT_SWAP -> R.string.transaction_type_reject_swap
      TransactionType.INITIALIZE_ACCOUNT -> R.string.transaction_type_initialize_account
      TransactionType.TOKEN_MINT -> R.string.transaction_type_token_mint
    }

    return context.getString(resId)
  }
}
