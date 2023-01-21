package com.dgsd.solis.accounts.cache

import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.data.cache.Cache

interface TransactionHistoryCache : Cache<PublicKey, List<EnrichedTransaction>>