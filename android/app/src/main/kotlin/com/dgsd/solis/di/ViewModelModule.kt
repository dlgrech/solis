package com.dgsd.solis.di

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.AppCoordinator
import com.dgsd.solis.accounts.AccountDetailsViewModel
import com.dgsd.solis.home.HomeViewModel
import com.dgsd.solis.qrscanner.QrScannerViewModel
import com.dgsd.solis.settings.AppSettingsViewModel
import com.dgsd.solis.solpay.SolPayTransactionRequestViewModel
import com.dgsd.solis.solpay.SolPayTransferRequestViewModel
import com.dgsd.solis.transactions.TransactionDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModule {

  fun create(): Module {
    return module {

      viewModel<AppCoordinator>()

      viewModel<HomeViewModel>()

      viewModel<AppSettingsViewModel>()

      viewModel<QrScannerViewModel>()

      viewModel { (accountKey: PublicKey) ->
        AccountDetailsViewModel(
          application = get(),
          accountKey = accountKey,
          solTokenNames = get(),
          errorMessageFactory = get(),
          publicKeyFormatter = get(),
          solanaRepository = get(),
          dataRepository = get(),
          transactionTypeFormatter = get(),
          permissionsManager = get(),
          systemClipboard = get(),
        )
      }

      viewModel { (transactionSignature: TransactionSignature) ->
        TransactionDetailsViewModel(
          application = get(),
          transactionSignature = transactionSignature,
          publicKeyFormatter = get(),
          errorMessageFactory = get(),
          transactionTypeFormatter = get(),
          transactionSourceFormatter = get(),
          systemClipboard = get(),
          solanaRepository = get(),
        )
      }

      viewModel { (transferRequestUrl: String) ->
        SolPayTransferRequestViewModel(
          transferRequestUrl = transferRequestUrl,
          application = get(),
          publicKeyFormatter = get(),
          errorMessageFactory = get(),
          solPay = get(),
          solTokenNames = get(),
          systemClipboard = get(),
        )
      }

      viewModel { (transferRequestUrl: String) ->
        SolPayTransactionRequestViewModel(
          transferRequestUrl = transferRequestUrl,
          application = get(),
          errorMessageFactory = get(),
          solPay = get(),
          systemClipboard = get(),
        )
      }
    }
  }
}