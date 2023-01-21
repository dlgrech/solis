package com.dgsd.solis.sync

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.common.resource.model.Resource
import kotlinx.coroutines.flow.Flow

interface SolisDataRepo {

  fun savePushToken(token: String): Flow<Resource<Unit>>

  fun setAccountSaved(account: PublicKey, saved: Boolean): Flow<Resource<Unit>>

  fun getSavedAccounts(): Flow<Resource<List<PublicKey>>>

  fun isSavedAccount(account: PublicKey): Flow<Resource<Boolean>>
}