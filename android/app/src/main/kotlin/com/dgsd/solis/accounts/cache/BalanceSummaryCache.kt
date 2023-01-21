package com.dgsd.solis.accounts.cache

import com.dgsd.khelius.balance.model.BalanceSummary
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.data.cache.Cache

interface BalanceSummaryCache : Cache<PublicKey, BalanceSummary>