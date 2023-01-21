package com.dgsd.solis.sync

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.common.flow.asResourceFlow
import com.dgsd.solis.common.flow.resourceFlowOf
import com.dgsd.solis.common.resource.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SolisDataRepoImpl(
  private val dataStore: SolisDataStore,
) : SolisDataRepo {

  override fun savePushToken(token: String): Flow<Resource<Unit>> {
    return resourceFlowOf {
      dataStore.setPushToken(token)
    }
  }

  override fun setAccountSaved(account: PublicKey, saved: Boolean): Flow<Resource<Unit>> {
    return resourceFlowOf {
      if (saved) {
        dataStore.addSavedAccount(account.toBase58String())
      } else {
        dataStore.removeSavedAccount(account.toBase58String())
      }
    }
  }

  override fun getSavedAccounts(): Flow<Resource<List<PublicKey>>> {
    return flow {
      emitAll(
        dataStore.observeSavedAccounts()
          .map { it.map(PublicKey::fromBase58) }
          .asResourceFlow()
      )
    }
  }

  override fun isSavedAccount(account: PublicKey): Flow<Resource<Boolean>> {
    return flow {
      emitAll(
        dataStore.observeSavedAccount(account.toBase58String()).asResourceFlow()
      )
    }
  }
}