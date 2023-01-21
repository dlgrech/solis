package com.dgsd.solis.transactions.model

import com.dgsd.khelius.common.model.TransactionType

fun String.asTransactionTypeOrNull(): TransactionType? {
  return when (this) {
    "NFT_BID" -> TransactionType.NFT_BID
    "NFT_BID_CANCELLED" -> TransactionType.NFT_BID_CANCELLED
    "NFT_LISTING" -> TransactionType.NFT_LISTING
    "NFT_CANCEL_LISTING" -> TransactionType.NFT_CANCEL_LISTING
    "NFT_SALE" -> TransactionType.NFT_SALE
    "NFT_MINT" -> TransactionType.NFT_MINT
    "NFT_AUCTION_CREATED" -> TransactionType.NFT_AUCTION_CREATED
    "NFT_AUCTION_UPDATED" -> TransactionType.NFT_AUCTION_UPDATED
    "NFT_AUCTION_CANCELLED" -> TransactionType.NFT_AUCTION_CANCELLED
    "NFT_PARTICIPATION_REWARD" -> TransactionType.NFT_PARTICIPATION_REWARD
    "NFT_MINT_REJECTED" -> TransactionType.NFT_MINT_REJECTED
    "CREATE_STORE" -> TransactionType.CREATE_STORE
    "WHITELIST_CREATOR" -> TransactionType.WHITELIST_CREATOR
    "ADD_TO_WHITELIST" -> TransactionType.ADD_TO_WHITELIST
    "REMOVE_FROM_WHITELIST" -> TransactionType.REMOVE_FROM_WHITELIST
    "AUCTION_MANAGER_CLAIM_BID" -> TransactionType.AUCTION_MANAGER_CLAIM_BID
    "EMPTY_PAYMENT_ACCOUNT" -> TransactionType.EMPTY_PAYMENT_ACCOUNT
    "UPDATE_PRIMARY_SALE_METADATA" -> TransactionType.UPDATE_PRIMARY_SALE_METADATA
    "ADD_TOKEN_TO_VAULT" -> TransactionType.ADD_TOKEN_TO_VAULT
    "ACTIVATE_VAULT" -> TransactionType.ACTIVATE_VAULT
    "INIT_VAULT" -> TransactionType.INIT_VAULT
    "INIT_BANK" -> TransactionType.INIT_BANK
    "INIT_STAKE" -> TransactionType.INIT_STAKE
    "MERGE_STAKE" -> TransactionType.MERGE_STAKE
    "SPLIT_STAKE" -> TransactionType.SPLIT_STAKE
    "SET_BANK_FLAGS" -> TransactionType.SET_BANK_FLAGS
    "SET_VAULT_LOCK" -> TransactionType.SET_VAULT_LOCK
    "UPDATE_VAULT_OWNER" -> TransactionType.UPDATE_VAULT_OWNER
    "UPDATE_BANK_MANAGER" -> TransactionType.UPDATE_BANK_MANAGER
    "RECORD_RARITY_POINTS" -> TransactionType.RECORD_RARITY_POINTS
    "ADD_RARITIES_TO_BANK" -> TransactionType.ADD_RARITIES_TO_BANK
    "INIT_FARM" -> TransactionType.INIT_FARM
    "INIT_FARMER" -> TransactionType.INIT_FARMER
    "REFRESH_FARMER" -> TransactionType.REFRESH_FARMER
    "UPDATE_FARM" -> TransactionType.UPDATE_FARM
    "AUTHORIZE_FUNDER" -> TransactionType.AUTHORIZE_FUNDER
    "DEAUTHORIZE_FUNDER" -> TransactionType.DEAUTHORIZE_FUNDER
    "FUND_REWARD" -> TransactionType.FUND_REWARD
    "CANCEL_REWARD" -> TransactionType.CANCEL_REWARD
    "LOCK_REWARD" -> TransactionType.LOCK_REWARD
    "PAYOUT" -> TransactionType.PAYOUT
    "VALIDATE_SAFETY_DEPOSIT_BOX_V2" -> TransactionType.VALIDATE_SAFETY_DEPOSIT_BOX_V2
    "SET_AUTHORITY" -> TransactionType.SET_AUTHORITY
    "INIT_AUCTION_MANAGER_V2" -> TransactionType.INIT_AUCTION_MANAGER_V2
    "UPDATE_EXTERNAL_PRICE_ACCOUNT" -> TransactionType.UPDATE_EXTERNAL_PRICE_ACCOUNT
    "AUCTION_HOUSE_CREATE" -> TransactionType.AUCTION_HOUSE_CREATE
    "CLOSE_ESCROW_ACCOUNT" -> TransactionType.CLOSE_ESCROW_ACCOUNT
    "WITHDRAW" -> TransactionType.WITHDRAW
    "DEPOSIT" -> TransactionType.DEPOSIT
    "TRANSFER" -> TransactionType.TRANSFER
    "BURN" -> TransactionType.BURN
    "BURN_NFT" -> TransactionType.BURN_NFT
    "PLATFORM_FEE" -> TransactionType.PLATFORM_FEE
    "LOAN" -> TransactionType.LOAN
    "REPAY_LOAN" -> TransactionType.REPAY_LOAN
    "ADD_TO_POOL" -> TransactionType.ADD_TO_POOL
    "REMOVE_FROM_POOL" -> TransactionType.REMOVE_FROM_POOL
    "CLOSE_POSITION" -> TransactionType.CLOSE_POSITION
    "UNLABELED" -> TransactionType.UNLABELED
    "CLOSE_ACCOUNT" -> TransactionType.CLOSE_ACCOUNT
    "WITHDRAW_GEM" -> TransactionType.WITHDRAW_GEM
    "DEPOSIT_GEM" -> TransactionType.DEPOSIT_GEM
    "STAKE_TOKEN" -> TransactionType.STAKE_TOKEN
    "UNSTAKE_TOKEN" -> TransactionType.UNSTAKE_TOKEN
    "STAKE_SOL" -> TransactionType.STAKE_SOL
    "UNSTAKE_SOL" -> TransactionType.UNSTAKE_SOL
    "CLAIM_REWARDS" -> TransactionType.CLAIM_REWARDS
    "BUY_SUBSCRIPTION" -> TransactionType.BUY_SUBSCRIPTION
    "SWAP" -> TransactionType.SWAP
    "INIT_SWAP" -> TransactionType.INIT_SWAP
    "CANCEL_SWAP" -> TransactionType.CANCEL_SWAP
    "REJECT_SWAP" -> TransactionType.REJECT_SWAP
    "INITIALIZE_ACCOUNT" -> TransactionType.INITIALIZE_ACCOUNT
    "TOKEN_MINT" -> TransactionType.TOKEN_MINT
    else -> TransactionType.UNKNOWN
  }
}