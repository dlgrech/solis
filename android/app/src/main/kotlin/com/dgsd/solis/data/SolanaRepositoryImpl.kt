package com.dgsd.solis.data

import com.dgsd.khelius.balance.BalanceApi
import com.dgsd.khelius.balance.model.BalanceSummary
import com.dgsd.khelius.transactions.EnhancedTransactionsApi
import com.dgsd.khelius.transactions.model.Commitment
import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.model.AccountInfo
import com.dgsd.ksol.core.model.Lamports
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.accounts.cache.AccountInfoCache
import com.dgsd.solis.accounts.cache.BalanceSummaryCache
import com.dgsd.solis.accounts.cache.MultipleAccountInfoCache
import com.dgsd.solis.accounts.cache.TransactionHistoryCache
import com.dgsd.solis.common.flow.executeWithCache
import com.dgsd.solis.common.flow.mapData
import com.dgsd.solis.common.flow.onEachSuccess
import com.dgsd.solis.common.flow.resourceFlowOf
import com.dgsd.solis.common.resource.model.Resource
import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.data.cache.CacheStrategy
import com.dgsd.solis.data.util.HeliusDescriptionSanitizer
import com.dgsd.solis.transactions.cache.TransactionCache
import com.dgsd.solis.util.asPublicKeyOrNull
import kotlinx.coroutines.flow.Flow

class SolanaRepositoryImpl(
  private val enhancedTransactionsApi: EnhancedTransactionsApi,
  private val balanceApi: BalanceApi,
  private val solanaApi: SolanaApi,
  private val transactionCache: TransactionCache,
  private val transactionHistoryCache: TransactionHistoryCache,
  private val balanceSummaryCache: BalanceSummaryCache,
  private val multipleAccountInfoCache: MultipleAccountInfoCache,
  private val accountInfoCache: AccountInfoCache,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val descriptionSanitizer: HeliusDescriptionSanitizer,
) : SolanaRepository {

  override fun getTransaction(
    transactionSignature: TransactionSignature,
    cacheStrategy: CacheStrategy
  ): Flow<Resource<EnrichedTransaction>> {
    return executeWithCache(
      cacheKey = transactionSignature,
      cacheStrategy = cacheStrategy,
      cache = transactionCache,
      networkFlowProvider = {
        resourceFlowOf {
          enhancedTransactionsApi.parseTransactions(
            listOf(transactionSignature),
            Commitment.CONFIRMED
          ).single()
        }.mapData {
          sanitizeEnrichedTransactionDescription(it)
        }
      }
    )
  }

  override fun getAccountInfo(
    accountKey: PublicKey,
    cacheStrategy: CacheStrategy
  ): Flow<Resource<AccountInfo>> {
    return executeWithCache(
      cacheKey = accountKey,
      cacheStrategy = cacheStrategy,
      cache = accountInfoCache,
      networkFlowProvider = {
        resourceFlowOf {
          checkNotNull(solanaApi.getAccountInfo(accountKey))
        }
      }
    )
  }

  override fun getTransactionHistory(
    accountKey: PublicKey,
    cacheStrategy: CacheStrategy
  ): Flow<Resource<List<EnrichedTransaction>>> {
    return executeWithCache(
      cacheKey = accountKey,
      cacheStrategy = cacheStrategy,
      cache = transactionHistoryCache,
      networkFlowProvider = {
        resourceFlowOf {
          enhancedTransactionsApi.getTransactionHistory(accountKey.toBase58String())
        }.mapData { transactions ->
          transactions.map(::sanitizeEnrichedTransactionDescription)
        }.onEachSuccess { transactions ->
          transactions.onEach { transaction ->
            transactionCache.set(transaction.signature, transaction)
          }
        }
      }
    )
  }

  override fun getBalances(
    accountKey: PublicKey,
    cacheStrategy: CacheStrategy
  ): Flow<Resource<BalanceSummary>> {
    return executeWithCache(
      cacheKey = accountKey,
      cacheStrategy = cacheStrategy,
      cache = balanceSummaryCache,
      networkFlowProvider = {
        resourceFlowOf {
          balanceApi.getBalanceSummary(accountKey.toBase58String())
        }
      }
    )
  }

  override fun getSimpleBalances(
    accountKeys: List<PublicKey>,
    cacheStrategy: CacheStrategy
  ): Flow<Resource<Map<PublicKey, Lamports?>>> {
    return executeWithCache(
      cacheKey = accountKeys,
      cacheStrategy = cacheStrategy,
      cache = multipleAccountInfoCache,
      networkFlowProvider = {
        resourceFlowOf {
          solanaApi.getMultipleAccounts(accountKeys)
        }
      }
    ).mapData { accountInfoMap ->
      accountInfoMap.mapValues { it.value?.lamports }
    }
  }

  /**
   * The descriptions returned from Helius are quite long/verbose.
   *
   * Try and shorten any mentioned public key addresses down
   */
  private fun sanitizeEnrichedTransactionDescription(
    input: EnrichedTransaction
  ): EnrichedTransaction {
    val description = input.description
    return if (description == null) {
      return input
    } else {
      return input.copy(description = descriptionSanitizer.sanitize(description))
    }
  }
}