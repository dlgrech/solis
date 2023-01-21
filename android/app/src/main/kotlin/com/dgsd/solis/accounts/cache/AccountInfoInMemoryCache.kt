package com.dgsd.solis.accounts.cache

import com.dgsd.ksol.core.model.AccountInfo
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.data.cache.InMemoryCache

class AccountInfoInMemoryCache: InMemoryCache<PublicKey, AccountInfo>(),
  AccountInfoCache