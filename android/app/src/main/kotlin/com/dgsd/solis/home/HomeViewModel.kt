package com.dgsd.solis.home

import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.R
import com.dgsd.solis.common.clipboard.SystemClipboard
import com.dgsd.solis.common.error.ErrorMessageFactory
import com.dgsd.solis.common.flow.*
import com.dgsd.solis.common.permission.PermissionsManager
import com.dgsd.solis.common.resource.ResourceFlowConsumer
import com.dgsd.solis.common.resource.model.Resource
import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.common.ui.SolTokenFormatter
import com.dgsd.solis.common.viewmodel.getString
import com.dgsd.solis.common.viewmodel.onEach
import com.dgsd.solis.data.SolanaRepository
import com.dgsd.solis.home.HomeScreenItem.SavedAccount.BalanceInfo
import com.dgsd.solis.sync.SolisDataRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
  application: Application,
  private val homeSearchResultFactory: HomeSearchResultFactory,
  private val errorMessageFactory: ErrorMessageFactory,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val systemClipboard: SystemClipboard,
  private val solanaRepository: SolanaRepository,
  private val dataRepository: SolisDataRepo,
  private val permissionsManager: PermissionsManager,
) : AndroidViewModel(application) {

  private val savedAccountsResourceFlowConsumer =
    ResourceFlowConsumer<List<PublicKey>>(viewModelScope)

  private val searchInputText = MutableStateFlow("")
  private val clipboardContents = MutableStateFlow("")

  private val _searchResults = MutableStateFlow(emptyList<HomeScreenItem.SearchResult>())
  val searchResults = _searchResults.asStateFlow()

  private val _showNotificationsPermissionPrompt = MutableEventFlow<String>()
  val showNotificationsPermissionPrompt = _showNotificationsPermissionPrompt.asEventFlow()

  val savedAccountsLoading = savedAccountsResourceFlowConsumer.isLoading

  val errorMessage = savedAccountsResourceFlowConsumer.error
    .map { error ->
      if (error == null) {
        null
      } else {
        errorMessageFactory.create(error, getString(R.string.error_message_fetching_saved_account))
      }
    }

  private val accountToBalanceFlow = MutableStateFlow(mapOf<PublicKey, BalanceInfo>())

  val savedAccounts = combine(
    savedAccountsResourceFlowConsumer.data.filterNotNull(),
    accountToBalanceFlow,
  ) { accountKeys, balanceMap ->
    accountKeys to balanceMap
  }.map { (accountKeys, balances) ->
    accountKeys.map { accountKey ->
      HomeScreenItem.SavedAccount(
        accountKey,
        publicKeyFormatter.format(accountKey),
        balances.getOrDefault(accountKey, BalanceInfo.Loading)
      )
    }
  }

  private val hasNotificationPermission =
    MutableStateFlow(permissionsManager.hasNotificationsPermission())

  val shouldShowNotificationPermissionPrompt =
    combine(
      savedAccounts,
      hasNotificationPermission
    ) { accounts, hasPermission ->
      accounts.isNotEmpty() && !hasPermission
    }

  init {
    val searchSources = combine(
      searchInputText,
      clipboardContents
    ) { searchInputValue, clipboardValue ->
      searchInputValue to clipboardValue
    }

    onEach(searchSources) { (searchText, clipboard) ->
      _searchResults.value = homeSearchResultFactory.generate(searchText, clipboard)
    }

    onEach(
      savedAccountsResourceFlowConsumer.data.filterNotNull().distinctUntilChanged()
    ) { savedAccounts ->
      onEach(solanaRepository.getSimpleBalances(savedAccounts)) { resource ->
        when (resource) {
          is Resource.Error -> {
            accountToBalanceFlow.value = accountToBalanceFlow.value.toMutableMap().apply {
              for (account in savedAccounts) {
                put(account, BalanceInfo.Error)
              }
            }
          }
          is Resource.Loading -> {
            accountToBalanceFlow.value = accountToBalanceFlow.value.toMutableMap().apply {
              for (account in savedAccounts) {
                put(account, BalanceInfo.Loading)
              }
            }
          }
          is Resource.Success -> {
            accountToBalanceFlow.value = accountToBalanceFlow.value.toMutableMap().apply {
              for (account in savedAccounts) {
                val amount = resource.data[account]
                put(
                  account, if (amount == null) {
                    BalanceInfo.Error
                  } else {
                    BalanceInfo.Loaded(SolTokenFormatter.format(amount))
                  }
                )
              }
            }
          }
        }
      }
    }
  }

  fun onSearchInputChanged(input: String) {
    searchInputText.value = input.trim()
  }

  fun onTryAgainClicked() {
    reloadData()
  }

  fun onRequestNotificationPermissionsClicked() {
    _showNotificationsPermissionPrompt.tryEmit(Manifest.permission.POST_NOTIFICATIONS)
  }

  @TargetApi(33)
  fun onNotificationPermissionChanged() {
    refreshNotificationPermissionState()
  }

  fun onResume() {
    reloadData()
    refreshNotificationPermissionState()

    viewModelScope.launch {
      // We add a slight delay, as we're not allowed to fetch the clipboard contents
      // until the Android OS considers us completely in the foreground
      delay(200)
      clipboardContents.value = systemClipboard.currentContents().orEmpty()
    }
  }

  private fun refreshNotificationPermissionState() {
    hasNotificationPermission.value = permissionsManager.hasNotificationsPermission()
  }

  private fun reloadData() {
    savedAccountsResourceFlowConsumer.collectFlow(dataRepository.getSavedAccounts())
  }
}