package com.dgsd.solis.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.khelius.common.model.TransactionSource
import com.dgsd.khelius.common.model.TransactionType
import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.R
import com.dgsd.solis.common.clipboard.SystemClipboard
import com.dgsd.solis.common.error.ErrorMessageFactory
import com.dgsd.solis.common.flow.MutableEventFlow
import com.dgsd.solis.common.flow.asEventFlow
import com.dgsd.solis.common.resource.ResourceFlowConsumer
import com.dgsd.solis.common.ui.*
import com.dgsd.solis.common.viewmodel.getString
import com.dgsd.solis.data.SolanaRepository
import com.dgsd.solis.data.cache.CacheStrategy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TransactionDetailsViewModel(
  application: Application,
  private val transactionSignature: TransactionSignature,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val transactionTypeFormatter: TransactionTypeFormatter,
  private val transactionSourceFormatter: TransactionSourceFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val systemClipboard: SystemClipboard,
  private val solanaRepository: SolanaRepository,
) : AndroidViewModel(application) {

  private val transactionResourceConsumer =
    ResourceFlowConsumer<EnrichedTransaction>(viewModelScope)

  private val transaction =
    transactionResourceConsumer.data.filterNotNull()

  val isLoading = transactionResourceConsumer.isLoading

  val errorMessage = transactionResourceConsumer.error
    .map { error ->
      if (error == null) {
        null
      } else {
        errorMessageFactory.create(error, getString(R.string.error_message_fetching_transactions))
      }
    }

  val description = transaction.map { it.description }

  val signature = transaction.map { it.signature }

  val transactionTypeText = transaction.map {
    if (it.type == TransactionType.UNKNOWN) {
      null
    } else {
      transactionTypeFormatter.format(it.type)
    }
  }

  val transactionSourceText = transaction.map {
    if (it.source == TransactionSource.UNKNOWN) {
      null
    } else {
      transactionSourceFormatter.format(it.source)
    }
  }

  val feeText = transaction.map {
    SolTokenFormatter.format(it.fee)
  }

  val feePayerText = transaction.map {
    publicKeyFormatter.formatLong(PublicKey.fromBase58(it.feePayer))
  }

  val timeText = transaction.map {
    val localDateTime = LocalDateTime.ofEpochSecond(it.timestamp, 0, ZoneOffset.UTC)
    DateTimeFormatter.formatDateAndTimeLong(
      application,
      OffsetDateTime.of(localDateTime, ZoneOffset.UTC)
    )
  }

  val slotText = transaction.map {
    NumberFormat.getNumberInstance().format(it.slot)
  }

  private val _showMessage = MutableEventFlow<CharSequence>()
  val showMessage = _showMessage.asEventFlow()

  private val _showAccount = MutableEventFlow<PublicKey>()
  val showAccount = _showAccount.asEventFlow()

  fun onCreate() {
    reloadData(CacheStrategy.CACHE_IF_PRESENT)
  }

  fun onTryAgainClicked() {
    reloadData(CacheStrategy.NETWORK_ONLY)
  }

  private fun reloadData(cacheStrategy: CacheStrategy) {
    transactionResourceConsumer.collectFlow(
      solanaRepository.getTransaction(
        transactionSignature,
        cacheStrategy,
      )
    )
  }

  fun onSignatureClicked() {
    val transaction = transactionResourceConsumer.data.value
    if (transaction != null) {
      systemClipboard.copy(transaction.signature)
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }

  fun onSignatureLongClicked() {
    onSignatureClicked()
  }

  fun onFeePayerClicked() {
    val transaction = transactionResourceConsumer.data.value
    if (transaction != null) {
      _showAccount.tryEmit(PublicKey.fromBase58(transaction.feePayer))
    }
  }

  fun onFeePayerLongClicked() {
    val transaction = transactionResourceConsumer.data.value
    if (transaction != null) {
      systemClipboard.copy(transaction.feePayer)
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }
}