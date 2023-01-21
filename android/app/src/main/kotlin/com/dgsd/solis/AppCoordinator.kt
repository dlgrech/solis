package com.dgsd.solis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.AppCoordinator.Destination.BottomSheetDestination
import com.dgsd.solis.common.flow.MutableEventFlow
import com.dgsd.solis.common.flow.asEventFlow

class AppCoordinator(
  application: Application,
) : AndroidViewModel(application) {

  sealed interface Destination {

    sealed interface InlineDestination : Destination {
      object Home : InlineDestination
      object AppSettings : InlineDestination
      object QrScanner : InlineDestination
    }

    sealed interface BottomSheetDestination : Destination {
      data class ViewAccount(val accountKey: PublicKey) : BottomSheetDestination
      data class ViewTransaction(val transactionSignature: TransactionSignature) : BottomSheetDestination
    }
  }

  private val _destination = MutableEventFlow<Destination>()
  val destination = _destination.asEventFlow()

  fun onCreate() {
    _destination.tryEmit(Destination.InlineDestination.Home)
  }

  fun navigateToAppSettings() {
    _destination.tryEmit(Destination.InlineDestination.AppSettings)
  }

  fun navigateToQrScanner() {
    _destination.tryEmit(Destination.InlineDestination.QrScanner)
  }

  fun navigateToAccountDetails(accountKey: PublicKey) {
    _destination.tryEmit(BottomSheetDestination.ViewAccount(accountKey))
  }

  fun navigateToTransactionDetails(transactionSignature: TransactionSignature) {
    _destination.tryEmit(BottomSheetDestination.ViewTransaction(transactionSignature))
  }
}
