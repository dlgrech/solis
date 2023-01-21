package com.dgsd.solis.accounts.cache

import com.dgsd.ksol.core.model.AccountInfo
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.data.cache.Cache

interface AccountInfoCache : Cache<PublicKey, AccountInfo>