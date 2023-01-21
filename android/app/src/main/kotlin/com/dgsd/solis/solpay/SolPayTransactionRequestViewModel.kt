package com.dgsd.solis.solpay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.ksol.core.model.LocalTransaction
import com.dgsd.ksol.core.programs.system.SystemProgram
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.solis.R
import com.dgsd.solis.common.clipboard.SystemClipboard
import com.dgsd.solis.common.error.ErrorMessageFactory
import com.dgsd.solis.common.flow.*
import com.dgsd.solis.common.resource.ResourceFlowConsumer
import com.dgsd.solis.common.ui.SolTokenFormatter
import com.dgsd.solis.common.viewmodel.getString
import com.dgsd.solis.transactions.model.getSystemProgramInstruction
import kotlinx.coroutines.flow.*

class SolPayTransactionRequestViewModel(
  application: Application,
  private val transferRequestUrl: String,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solPay: SolPay,
  private val systemClipboard: SystemClipboard,
) : AndroidViewModel(application) {


  private data class TransactionRequestInfo(
    val label: String?,
    val iconUrl: String?,
    val message: String?,
    val transaction: LocalTransaction,
  )

  private val parseUrlResourceConsumer =
    ResourceFlowConsumer<SolPayTransactionRequest>(viewModelScope)
  private val transactionDetailsResourceConsumer =
    ResourceFlowConsumer<TransactionRequestInfo>(viewModelScope)

  private val transactionRequest = parseUrlResourceConsumer.data.filterNotNull()

  private val _showMessage = MutableEventFlow<CharSequence>()
  val showMessage = _showMessage.asEventFlow()

  val isLoading = combine(
    transactionDetailsResourceConsumer.isLoading,
    parseUrlResourceConsumer.isLoading
  ) { transactionDetailsLoading, parsingLoading ->
    transactionDetailsLoading || parsingLoading
  }

  val errorMessage = combine(
    transactionDetailsResourceConsumer.error,
    parseUrlResourceConsumer.error
  ) { transactionDetailsError, parsingError ->
    transactionDetailsError ?: parsingError
  }.map { error ->
    if (error == null) {
      null
    } else {
      errorMessageFactory.create(
        error,
        getString(R.string.error_message_fetching_transaction_request)
      )
    }
  }

  val linkText = stateFlowOf { transferRequestUrl }
  val logoUrl = transactionDetailsResourceConsumer.data.map { it?.iconUrl }
  val label = transactionDetailsResourceConsumer.data.map { it?.label }
  val message = transactionDetailsResourceConsumer.data.map { it?.message }
  val amountText = transactionDetailsResourceConsumer.data.map { requestInfo ->
    requestInfo
      ?.transaction
      ?.message
      ?.getSystemProgramInstruction()
      ?.lamports
      ?.let { SolTokenFormatter.format(it) }
  }

  fun onCreate() {
    reloadData()
  }

  fun onTryAgainClicked() {
    reloadData()
  }

  fun onLinkClicked() {
    systemClipboard.copy(transferRequestUrl)
    _showMessage.tryEmit(getString(R.string.common_copied))
  }

  fun onLinkLongClicked() {
    onLinkClicked()
  }

  private fun reloadData() {
    parseUrlResourceConsumer.collectFlow(
      flow {
        emit(solPay.parseUrl(transferRequestUrl) as SolPayTransactionRequest)
      }.asResourceFlow()
    )

    transactionDetailsResourceConsumer.collectFlow(
      transactionRequest.flatMapLatest { request ->
        resourceFlowOf {
          val transactionInfo = solPay.getTransaction(SystemProgram.PROGRAM_ID, request)
          val requestDetails = solPay.getDetails(request)

          TransactionRequestInfo(
            label = requestDetails.label,
            iconUrl = requestDetails.iconUrl,
            message = transactionInfo.message,
            transaction = transactionInfo.transaction
          )
        }
      }
    )
  }
}