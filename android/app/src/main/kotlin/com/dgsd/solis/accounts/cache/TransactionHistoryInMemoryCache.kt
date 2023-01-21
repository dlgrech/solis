package com.dgsd.solis.accounts.cache

import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.data.cache.InMemoryCache

class TransactionHistoryInMemoryCache : InMemoryCache<PublicKey, List<EnrichedTransaction>>(),
  TransactionHistoryCache