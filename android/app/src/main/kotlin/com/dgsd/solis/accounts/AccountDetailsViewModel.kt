package com.dgsd.solis.accounts

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.khelius.common.model.TransactionType
import com.dgsd.khelius.transactions.model.EnrichedTransaction
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.programs.system.SystemProgram
import com.dgsd.solis.R
import com.dgsd.solis.accounts.model.AccountAndBalanceSummary
import com.dgsd.solis.accounts.model.TokenBalanceDisplayItem
import com.dgsd.solis.accounts.model.TransactionDisplayItem
import com.dgsd.solis.common.clipboard.SystemClipboard
import com.dgsd.solis.common.error.ErrorMessageFactory
import com.dgsd.solis.common.flow.*
import com.dgsd.solis.common.permission.PermissionsManager
import com.dgsd.solis.common.resource.ResourceFlowConsumer
import com.dgsd.solis.common.ui.*
import com.dgsd.solis.common.viewmodel.getString
import com.dgsd.solis.data.SolanaRepository
import com.dgsd.solis.data.cache.CacheStrategy
import com.dgsd.solis.programs.model.NativePrograms
import com.dgsd.solis.sync.SolisDataRepo
import com.dgsd.solis.transactions.model.asBigDecimal
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AccountDetailsViewModel(
  application: Application,
  private val accountKey: PublicKey,
  private val solTokenNames: SolTokenNames,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val transactionTypeFormatter: TransactionTypeFormatter,
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaRepository: SolanaRepository,
  private val dataRepository: SolisDataRepo,
  private val permissionsManager: PermissionsManager,
  private val systemClipboard: SystemClipboard,
) : AndroidViewModel(application) {

  private val balanceResourceFlowConsumer =
    ResourceFlowConsumer<AccountAndBalanceSummary>(viewModelScope)
  private val transactionHistoryFlowConsumer =
    ResourceFlowConsumer<List<EnrichedTransaction>>(viewModelScope)
  private val saveResourceFlowConsumer = ResourceFlowConsumer<Unit>(viewModelScope)

  private val accountAndBalanceSummary = balanceResourceFlowConsumer.data.filterNotNull()
  val isLoading = balanceResourceFlowConsumer.isLoading
  val errorMessage = balanceResourceFlowConsumer.error.map { error ->
    if (error == null) {
      null
    } else {
      errorMessageFactory.create(error, getString(R.string.error_message_fetching_account))
    }
  }
  private val _showNotificationsPermissionPrompt = MutableEventFlow<String>()
  val showNotificationsPermissionPrompt = _showNotificationsPermissionPrompt.asEventFlow()

  private val _showMessage = MutableEventFlow<CharSequence>()
  val showMessage = _showMessage.asEventFlow()

  val bottomSheetTitle = accountAndBalanceSummary.map {
    when {
      it.accountInfo.ownerHash == NativePrograms.TOKEN_PROGRAM -> {
        publicKeyFormatter.getProgramDisplayName(it.accountInfo.publicKey)
          ?: solTokenNames.getDisplayName(it.accountInfo.publicKey)
          ?: getString(R.string.account_details_bottomsheet_title_account)
      }
      it.accountInfo.isExecutable -> getString(R.string.account_details_bottomsheet_title_program)
      else -> getString(R.string.account_details_bottomsheet_title_account)
    }
  }.onStart {
    emit(
      if (accountKey in NativePrograms.nativeProgramKeys) {
        getString(R.string.account_details_bottomsheet_title_program)
      } else {
        getString(R.string.account_details_bottomsheet_title_account)
      }
    )
  }

  val accountKeyText = stateFlowOf {
    publicKeyFormatter.formatLong(accountKey)
  }

  val solBalanceText = accountAndBalanceSummary.map {
    SolTokenFormatter.format(it.balanceSummary.nativeBalance)
  }

  val ownerText = accountAndBalanceSummary.map {
    if (it.accountInfo.ownerHash == SystemProgram.PROGRAM_ID) {
      null
    } else {
      publicKeyFormatter.format(it.accountInfo.ownerHash)
    }
  }

  val executableText = accountAndBalanceSummary.map {
    if (!it.accountInfo.isExecutable) {
      null
    } else {
      getString(R.string.common_yes)
    }
  }

  val balanceItems = accountAndBalanceSummary.map { accountAndBalanceSummary ->
    accountAndBalanceSummary.balanceSummary
      .tokens
      .filter { it.balance > 0 }
      .map { token ->
        val mint = PublicKey.fromBase58(token.mint)
        val tokenAccount = PublicKey.fromBase58(token.account)
        TokenBalanceDisplayItem(
          tokenBalance = token,
          balance = SolTokenFormatter.format(token.asBigDecimal()),
          name = solTokenNames.getDisplayName(mint) ?: publicKeyFormatter.abbreviate(mint),
          account = publicKeyFormatter.format(tokenAccount),
        )
      }
  }

  val transactionHistory = transactionHistoryFlowConsumer.data.filterNotNull().map { transactions ->
    transactions.map {
      TransactionDisplayItem(
        signature = it.signature,
        type = if (it.type == TransactionType.UNKNOWN) {
          getString(R.string.account_details_transaction_history_item_type_unknown)
        } else {
          transactionTypeFormatter.format(it.type)
        },
        description = it.description ?: it.signature,
        timeText = DateTimeFormatter.formatDateAndTimeLong(
          application,
          OffsetDateTime.of(
            LocalDateTime.ofEpochSecond(it.timestamp, 0, ZoneOffset.UTC),
            ZoneOffset.UTC
          )
        )
      )
    }
  }
  val isTransactionHistoryLoading = transactionHistoryFlowConsumer.isLoading
  val transactionHistoryErrorMessage = transactionHistoryFlowConsumer.error.map { error ->
    if (error == null) {
      null
    } else {
      errorMessageFactory.create(error, getString(R.string.error_message_fetching_transactions))
    }
  }

  val isSavedAccount = dataRepository.isSavedAccount(accountKey)
    .mapNotNull { it.dataOrNull() }
    .stateIn(viewModelScope, SharingStarted.Lazily, false)

  val isSaveActionVisible = accountAndBalanceSummary.map {
    it.accountInfo.ownerHash == SystemProgram.PROGRAM_ID
  }.onStart { emit(false) }

  private val _showAccount = MutableEventFlow<PublicKey>()
  val showAccount = _showAccount.asEventFlow()

  fun onCreate() {
    reloadData(CacheStrategy.CACHE_IF_PRESENT)
  }

  fun onTryAgainClicked() {
    reloadData(CacheStrategy.NETWORK_ONLY)
  }

  @SuppressLint("InlinedApi")
  fun onSavedClicked() {
    val current = isSavedAccount.value
    val newSavedState = !current
    saveResourceFlowConsumer.collectFlow(
      dataRepository.setAccountSaved(accountKey, newSavedState)
    )

    if (newSavedState && !permissionsManager.hasCameraPermissions()) {
      _showNotificationsPermissionPrompt.tryEmit(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  fun onLoadRecentTransactionsClicked() {
    transactionHistoryFlowConsumer.collectFlow(
      solanaRepository.getTransactionHistory(
        accountKey,
        CacheStrategy.CACHE_IF_PRESENT
      )
    )
  }

  private fun reloadData(cacheStrategy: CacheStrategy) {
    balanceResourceFlowConsumer.collectFlow(
      solanaRepository.getAccountInfo(accountKey, cacheStrategy)
        .flatMapSuccess { accountInfo ->
          solanaRepository.getBalances(accountKey, cacheStrategy).mapData { balanceSummary ->
            AccountAndBalanceSummary(accountInfo, balanceSummary)
          }
        }
    )
  }

  fun onAccountKeyClicked() {
    systemClipboard.copy(accountKey.toBase58String())
    _showMessage.tryEmit(getString(R.string.common_copied))
  }

  fun onAccountKeyLongClicked() {
    onAccountKeyClicked()
  }

  fun onSolBalanceClicked() {
    val balance = balanceResourceFlowConsumer.data.value
    if (balance != null) {
      SolTokenFormatter.format(balance.balanceSummary.nativeBalance)
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }

  fun onSolBalanceLongClicked() {
    onSolBalanceClicked()
  }

  fun onOwnerClicked() {
    val owner = balanceResourceFlowConsumer.data.value?.accountInfo?.ownerHash
    if (owner != null) {
      _showAccount.tryEmit(owner)
    }
  }

  fun onOwnerLongClicked() {
    val owner = balanceResourceFlowConsumer.data.value?.accountInfo?.ownerHash
    if (owner != null) {
      systemClipboard.copy(owner.toBase58String())
      _showMessage.tryEmit(getString(R.string.common_copied))
    }
  }
}