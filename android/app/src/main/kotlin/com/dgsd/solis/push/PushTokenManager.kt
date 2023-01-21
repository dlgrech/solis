package com.dgsd.solis.push

import com.dgsd.solis.common.resource.model.Resource
import com.dgsd.solis.sync.SolisDataRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

private val RETRY_BACKOFF_DELAY = TimeUnit.SECONDS.toMillis(20)

class PushTokenManager(
  private val coroutineScope: CoroutineScope,
  private val pushTokenStorage: PushTokenStorage,
  private val dataRepo: SolisDataRepo,
) {

  fun onNewToken(token: String) {
    pushTokenStorage.setPushTokenSynced(false)
    pushTokenStorage.savePushToken(token)

    sync()
  }

  fun onCreate() {
    sync()
  }

  fun sync() {
    if (pushTokenStorage.isPushTokenSynced()) {
      return
    }

    val token = pushTokenStorage.getPushToken()
    if (token == null) {
      return
    }

    dataRepo.savePushToken(token)
      .onEach { resource ->
        if (resource is Resource.Error) {
          delay(RETRY_BACKOFF_DELAY)
          sync()
        } else if (resource is Resource.Success) {
          pushTokenStorage.setPushTokenSynced(true)
        }
      }
      .launchIn(coroutineScope)
  }
}