package com.dgsd.solis.solpay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.utils.solToLamports
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.solis.R
import com.dgsd.solis.common.clipboard.SystemClipboard
import com.dgsd.solis.common.error.ErrorMessageFactory
import com.dgsd.solis.common.flow.MutableEventFlow
import com.dgsd.solis.common.flow.asEventFlow
import com.dgsd.solis.common.flow.asResourceFlow
import com.dgsd.solis.common.flow.stateFlowOf
import com.dgsd.solis.common.resource.ResourceFlowConsumer
import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.common.ui.SolTokenFormatter
import com.dgsd.solis.common.ui.SolTokenNames
import com.dgsd.solis.common.viewmodel.getString
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SolPayTransferRequestViewModel(
  application: Application,
  private val transferRequestUrl: String,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val systemClipboard: SystemClipboard,
  private val solPay: SolPay,
  private val solTokenNames: SolTokenNames,
) : AndroidViewModel(application) {

  private val parseUrlResourceConsumer = ResourceFlowConsumer<SolPayTransferRequest>(viewModelScope)

  private val transferRequest = parseUrlResourceConsumer.data.filterNotNull()

  val recipientText = transferRequest.map {
    publicKeyFormatter.format(it.recipient)
  }

  val amountText = transferRequest.map {
    SolTokenFormatter.format(checkNotNull(it.amount?.solToLamports()))
  }

  val linkText = stateFlowOf { transferRequestUrl }
  val messageText = transferRequest.map { it.message }
  val labelText = transferRequest.map { it.label }
  val memoText = transferRequest.map { it.memo }
  val token = transferRequest.map { request ->
    request.splTokenMintAccount?.let {
      solTokenNames.getDisplayName(it) ?: publicKeyFormatter.format(it)
    }
  }
  val accountReferences = transferRequest.map { request ->
    request.references.map { publicKeyFormatter.format(it) }
  }

  val isLoading = parseUrlResourceConsumer.isLoading

  val errorMessage = parseUrlResourceConsumer.error.map { error ->
    if (error == null) {
      null
    } else {
      errorMessageFactory.create(error, getString(R.string.error_message_fetching_transfer_request))
    }
  }

  private val _showMessage = MutableEventFlow<CharSequence>()
  val showMessage = _showMessage.asEventFlow()

  private val _showAccount = MutableEventFlow<PublicKey>()
  val showAccount = _showAccount.asEventFlow()

  fun onCreate() {
    parseUrlResourceConsumer.collectFlow(
      flow {
        emit(solPay.parseUrl(transferRequestUrl) as SolPayTransferRequest)
      }.asResourceFlow()
    )
  }

  fun onAccountReferenceClicked(index: Int) {
    val account = parseUrlResourceConsumer.data.value?.references?.getOrNull(index)
    if (account != null) {
      _showAccount.tryEmit(account)
    }
  }

  fun onAccountReferenceLongClicked(index: Int) {
    val account = parseUrlResourceConsumer.data.value?.references?.getOrNull(index)
    if (account != null) {
      systemClipboard.copy(account.toBase58String())
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }

  fun onRecipientClicked() {
    val recipient = parseUrlResourceConsumer.data.value?.recipient
    if (recipient != null) {
      _showAccount.tryEmit(recipient)
    }
  }

  fun onRecipientLongClicked() {
    val recipient = parseUrlResourceConsumer.data.value?.recipient
    if (recipient != null) {
      systemClipboard.copy(recipient.toBase58String())
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }

  fun onTokenClicked() {
    val token = parseUrlResourceConsumer.data.value?.splTokenMintAccount
    if (token != null) {
      _showAccount.tryEmit(token)
    }
  }

  fun onTokenLongClicked() {
    val token = parseUrlResourceConsumer.data.value?.splTokenMintAccount
    if (token != null) {
      systemClipboard.copy(token.toBase58String())
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }

  fun onLinkClicked() {
    systemClipboard.copy(transferRequestUrl)
    _showMessage.tryEmit(getString(R.string.common_copied))
  }

  fun onLinkLongClicked() {
    onLinkClicked()
  }
}