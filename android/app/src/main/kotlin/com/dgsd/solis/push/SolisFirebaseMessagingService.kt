package com.dgsd.solis.push

import com.dgsd.solis.common.permission.PermissionsManager
import com.dgsd.solis.notifications.SolisNotificationManager
import com.dgsd.solis.transactions.model.asTransactionSignatureOrNull
import com.dgsd.solis.transactions.model.asTransactionTypeOrNull
import com.dgsd.solis.util.asPublicKeyOrNull
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject


class SolisFirebaseMessagingService : FirebaseMessagingService() {

  private val pushTokenManager by inject<PushTokenManager>()
  private val permissionManager by inject<PermissionsManager>()
  private val solisNotificationManager by inject<SolisNotificationManager>()

  override fun onNewToken(token: String) {
    pushTokenManager.onNewToken(token)
  }

  override fun onMessageReceived(message: RemoteMessage) {
    val account = message.data["account"]?.asPublicKeyOrNull()
    val transaction = message.data["transaction"]?.asTransactionSignatureOrNull()
    val type = message.data["type"]?.asTransactionTypeOrNull()
    val description = message.data["description"]

    if (account == null || transaction == null || type == null) {
      return
    }

    if (permissionManager.hasNotificationsPermission()) {
      solisNotificationManager.showNotification(account, transaction, type, description)
    }
  }
}