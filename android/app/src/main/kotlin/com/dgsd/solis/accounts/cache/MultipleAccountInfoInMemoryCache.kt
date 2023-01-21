package com.dgsd.solis.accounts.cache

import com.dgsd.ksol.core.model.AccountInfo
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.data.cache.InMemoryCache

class MultipleAccountInfoInMemoryCache(
  private val accountInfoCache: AccountInfoCache
) : InMemoryCache<List<PublicKey>, Map<PublicKey, AccountInfo?>>(),
  MultipleAccountInfoCache {

  override suspend fun set(key: List<PublicKey>, value: Map<PublicKey, AccountInfo?>) {
    super.set(key, value)

    value.entries.forEach { (account, accountInfo) ->
      if (accountInfo != null) {
        accountInfoCache.set(account, accountInfo)
      }
    }
  }
}