package com.dgsd.solis.data

import com.dgsd.khelius.balance.model.BalanceSummary
import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.core.model.AccountInfo
import com.dgsd.ksol.core.model.Lamports
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.common.resource.model.Resource
import com.dgsd.solis.data.cache.CacheStrategy
import kotlinx.coroutines.flow.Flow

interface SolanaRepository {

  fun getTransaction(
    transactionSignature: TransactionSignature,
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
  ): Flow<Resource<EnrichedTransaction>>

  fun getAccountInfo(
    accountKey: PublicKey,
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
  ): Flow<Resource<AccountInfo>>

  fun getTransactionHistory(
    accountKey: PublicKey,
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
  ): Flow<Resource<List<EnrichedTransaction>>>

  fun getBalances(
    accountKey: PublicKey,
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
  ): Flow<Resource<BalanceSummary>>

  fun getSimpleBalances(
    accountKeys: List<PublicKey>,
    cacheStrategy: CacheStrategy = CacheStrategy.CACHE_IF_PRESENT,
  ): Flow<Resource<Map<PublicKey, Lamports?>>>
}