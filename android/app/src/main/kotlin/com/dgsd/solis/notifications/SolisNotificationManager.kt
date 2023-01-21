package com.dgsd.solis.notifications

import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dgsd.khelius.common.model.TransactionType
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.R
import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.data.util.HeliusDescriptionSanitizer
import com.dgsd.solis.deeplink.DeeplinkManager
import kotlin.random.Random

private const val CHANNEL_ID_TRANSACTIONS = "transaction_notifications"
private const val NOTIFICATION_EXTRA_ID = "notification_id"

class SolisNotificationManager(
  private val context: Context,
  private val notificationManager: NotificationManagerCompat,
  private val deeplinkManager: DeeplinkManager,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val descriptionSanitizer: HeliusDescriptionSanitizer,
) {

  fun showNotification(
    account: PublicKey,
    transactionSignature: TransactionSignature,
    type: TransactionType,
    description: String?
  ) {
    val channel = ensureTransactionNotificationChannelCreated()

    val title = createTitleFrom(type)
    val contentText = createContentFrom(account, description)

    val notificationId = transactionSignature.hashCode()

    val notification = NotificationCompat.Builder(context, channel.id)
      .setAutoCancel(true)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
      .setSmallIcon(R.drawable.ic_notification_icon)
      .setContentTitle(title)
      .setContentText(contentText)
      .setSubText(
        publicKeyFormatter.getProgramDisplayName(account) ?: publicKeyFormatter.abbreviate(account)
      )
      .setContentIntent(createViewTransactionPendingIntent(notificationId, transactionSignature))
      .addAction(
        R.drawable.ic_baseline_wallet_24,
        context.getString(R.string.notification_action_view_account),
        createViewAccountPendingIntent(notificationId, account)
      )
      .addAction(
        R.drawable.ic_baseline_content_copy_24,
        context.getString(R.string.notification_action_copy_signature),
        createCopyTransactionSignaturePendingIntent(notificationId, transactionSignature)
      )
      .setStyle(
        NotificationCompat.BigTextStyle().bigText(contentText)
      )
      .build()

    notificationManager.notify(
      notificationId,
      notification
    )
  }

  private fun createViewTransactionPendingIntent(
    notificationId: Int,
    transactionSignature: TransactionSignature
  ): PendingIntent {
    return PendingIntent.getActivity(
      context,
      Random.nextInt(),
      createNotificationIntent(
        notificationId, deeplinkManager.createDeeplinkForTransaction(transactionSignature)
      ),
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
  }

  private fun createCopyTransactionSignaturePendingIntent(
    notificationId: Int,
    transactionSignature: TransactionSignature,
  ): PendingIntent {
    // TODO: Proper action
    return createViewTransactionPendingIntent(notificationId, transactionSignature)
  }

  private fun createViewAccountPendingIntent(
    notificationId: Int,
    accountKey: PublicKey,
  ): PendingIntent {
    return PendingIntent.getActivity(
      context,
      Random.nextInt(),
      createNotificationIntent(
        notificationId, deeplinkManager.createDeeplinkForAccount(accountKey)
      ),
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
  }

  private fun ensureTransactionNotificationChannelCreated(): NotificationChannelCompat {
    val channel = createNotificationChannel()
    notificationManager.createNotificationChannel(channel)
    return channel
  }

  private fun createNotificationChannel(): NotificationChannelCompat {
    return NotificationChannelCompat.Builder(CHANNEL_ID_TRANSACTIONS, IMPORTANCE_DEFAULT)
      .setName(
        context.getString(R.string.notification_channel_display_transactions)
      )
      .setDescription(
        context.getString(R.string.notification_channel_display_transactions_description)
      )
      .build()
  }

  private fun createNotificationIntent(
    notificationId: Int,
    uri: Uri,
  ): Intent {
    return Intent(Intent.ACTION_VIEW, uri).apply {
      putExtra(NOTIFICATION_EXTRA_ID, notificationId)
    }
  }

  private fun createTitleFrom(type: TransactionType): String {
    val titleRes = when (type) {
      TransactionType.NFT_BID -> R.string.notification_title_transaction_type_nft_bid
      TransactionType.NFT_BID_CANCELLED -> R.string.notification_title_transaction_type_nft_bid_cancelled
      TransactionType.NFT_LISTING -> R.string.notification_title_transaction_type_nft_listing
      TransactionType.NFT_CANCEL_LISTING -> R.string.notification_title_transaction_type_nft_cancel_listing
      TransactionType.NFT_SALE -> R.string.notification_title_transaction_type_nft_sale
      TransactionType.NFT_MINT -> R.string.notification_title_transaction_type_nft_mint
      TransactionType.NFT_AUCTION_CREATED -> R.string.notification_title_transaction_type_nft_auction_created
      TransactionType.NFT_AUCTION_UPDATED -> R.string.notification_title_transaction_type_nft_auction_updated
      TransactionType.NFT_AUCTION_CANCELLED -> R.string.notification_title_transaction_type_nft_auction_cancelled
      TransactionType.WITHDRAW -> R.string.notification_title_transaction_type_withdraw
      TransactionType.DEPOSIT -> R.string.notification_title_transaction_type_deposit
      TransactionType.TRANSFER -> R.string.notification_title_transaction_type_transfer
      TransactionType.BURN -> R.string.notification_title_transaction_type_burn
      TransactionType.BURN_NFT -> R.string.notification_title_transaction_type_burn_nft
      TransactionType.CLOSE_POSITION -> R.string.notification_title_transaction_type_close_position
      TransactionType.CLOSE_ACCOUNT -> R.string.notification_title_transaction_type_close_account
      TransactionType.WITHDRAW_GEM -> R.string.notification_title_transaction_type_withdraw_gem
      TransactionType.DEPOSIT_GEM -> R.string.notification_title_transaction_type_deposit_gem
      TransactionType.STAKE_TOKEN -> R.string.notification_title_transaction_type_stake_token
      TransactionType.UNSTAKE_TOKEN -> R.string.notification_title_transaction_type_unstake_token
      TransactionType.STAKE_SOL -> R.string.notification_title_transaction_type_stake_sol
      TransactionType.UNSTAKE_SOL -> R.string.notification_title_transaction_type_unstake_sol
      TransactionType.SWAP -> R.string.notification_title_transaction_type_swap
      TransactionType.INIT_SWAP -> R.string.notification_title_transaction_type_init_swap
      TransactionType.CANCEL_SWAP -> R.string.notification_title_transaction_type_cancel_swap
      TransactionType.REJECT_SWAP -> R.string.notification_title_transaction_type_reject_swap
      TransactionType.INITIALIZE_ACCOUNT -> R.string.notification_title_transaction_type_initialize_account
      TransactionType.TOKEN_MINT -> R.string.notification_title_transaction_type_token_mint
      else -> R.string.notification_title_transaction_type_unknown
    }

    return context.getString(titleRes)
  }

  private fun createContentFrom(
    account: PublicKey,
    description: String?
  ): String {
    if (description != null) {
      return descriptionSanitizer.sanitize(description)
    }

    return context.getString(
      R.string.notification_content_default_account_template,
      publicKeyFormatter.format(account)
    )
  }
}