package com.dgsd.solis.transactions.cache

import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.data.cache.InMemoryCache

/**
 * Cache for holding single transactions in memory
 */
class TransactionInMemoryCache : InMemoryCache<TransactionSignature, EnrichedTransaction>(),
  TransactionCache