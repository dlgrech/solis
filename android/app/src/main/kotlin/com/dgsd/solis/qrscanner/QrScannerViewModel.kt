package com.dgsd.solis.qrscanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayRequest
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.solis.R
import com.dgsd.solis.common.flow.MutableEventFlow
import com.dgsd.solis.common.flow.SimpleMutableEventFlow
import com.dgsd.solis.common.flow.asEventFlow
import com.dgsd.solis.common.flow.call
import com.dgsd.solis.common.permission.PermissionsManager
import com.dgsd.solis.common.viewmodel.getString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QrScannerViewModel(
  application: Application,
  private val permissionsManager: PermissionsManager,
  private val solPay: SolPay,
) : AndroidViewModel(application) {

  private val _showMissingCameraPermissionsState =
    MutableStateFlow(!permissionsManager.hasCameraPermissions())
  val showMissingCameraPermissionsState = _showMissingCameraPermissionsState.asStateFlow()

  private val _showSystemCameraPermissionPrompt = SimpleMutableEventFlow()
  val showSystemCameraPermissionPrompt = _showSystemCameraPermissionPrompt.asEventFlow()

  private val _showCameraError = MutableEventFlow<CharSequence>()
  val showCameraError = _showCameraError.asEventFlow()

  private val _showInvalidBarcodeError = MutableEventFlow<CharSequence>()
  val showInvalidBarcodeError = _showInvalidBarcodeError.asEventFlow()

  private val _continueWithSolPayRequest = MutableEventFlow<SolPayRequest>()
  val continueWithSolPayRequest = _continueWithSolPayRequest.asEventFlow()

  fun onResume() {
    onCameraPermissionStateChanged()
  }

  fun onCameraPermissionStateChanged() {
    _showMissingCameraPermissionsState.value = !permissionsManager.hasCameraPermissions()
  }

  fun onRequestCameraPermissionClicked() {
    _showSystemCameraPermissionPrompt.call()
  }

  fun onCameraStateError() {
    _showCameraError.tryEmit(getString(R.string.qr_scanner_camera_error))
  }

  fun onBarcodeScanResult(text: String) {
    when (val result = solPay.parseUrl(text)) {
      null -> {
        runCatching {
          SolPayTransferRequest(recipient = PublicKey.fromBase58(text))
        }.onSuccess {
          _continueWithSolPayRequest.tryEmit(it)
        }.onFailure {
          _showInvalidBarcodeError.tryEmit(
            getString(R.string.qr_scanner_barcode_error)
          )
        }
      }
      is SolPayTransactionRequest -> {
        _continueWithSolPayRequest.tryEmit(result)
      }
      is SolPayTransferRequest -> {
        _continueWithSolPayRequest.tryEmit(result)
      }
    }
  }
}