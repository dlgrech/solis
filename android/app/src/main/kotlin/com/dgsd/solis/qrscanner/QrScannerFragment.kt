package com.dgsd.solis.qrscanner

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.solis.R
import com.dgsd.solis.common.flow.onEach
import com.dgsd.solis.common.fragment.navigateBack
import com.dgsd.solis.common.fragment.showFrom
import com.dgsd.solis.common.intent.IntentFactory
import com.dgsd.solis.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.solis.common.ui.performHapticFeedback
import com.dgsd.solis.common.ui.roundedCorners
import com.dgsd.solis.solpay.SolPayTransactionRequestBottomSheetFragment
import com.dgsd.solis.solpay.SolPayTransferRequestBottomSheetFragment
import com.google.android.material.appbar.MaterialToolbar
import com.journeyapps.barcodescanner.CameraPreview
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class QrScannerFragment : Fragment(R.layout.frag_qr_scanner) {

  private val viewModel: QrScannerViewModel by viewModel()
  private val intentFactory by inject<IntentFactory>()

  private var barcodeScannerView: DecoratedBarcodeView? = null

  private val cameraPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) {
    viewModel.onCameraPermissionStateChanged()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<MaterialToolbar>(R.id.toolbar)
    val permissionsContainer = view.requireViewById<View>(R.id.permission_container)
    val pageInstructions = view.requireViewById<View>(R.id.page_instructions)
    barcodeScannerView = view.requireViewById(R.id.barcode_scanner)

    toolbar.setNavigationOnClickListener {
      navigateBack()
    }

    permissionsContainer.setOnClickListener {
      viewModel.onRequestCameraPermissionClicked()
    }

    barcodeScannerView?.setStatusText(null)
    barcodeScannerView?.roundedCorners(
      resources.getDimensionPixelSize(R.dimen.qr_scanner_radius).toFloat()
    )
    barcodeScannerView?.barcodeView?.addStateListener(
      object : CameraPreview.StateListener {
        override fun previewSized() = Unit
        override fun previewStarted() = Unit
        override fun previewStopped() = Unit
        override fun cameraClosed() = Unit
        override fun cameraError(error: Exception?) {
          viewModel.onCameraStateError()
        }
      }
    )

    addDecoderCallback()

    onEach(viewModel.showMissingCameraPermissionsState) {
      barcodeScannerView?.isInvisible = it
      permissionsContainer.isInvisible = !it
      pageInstructions.isInvisible = it

      if (!it) {
        barcodeScannerView?.resume()
      }
    }

    onEach(viewModel.showSystemCameraPermissionPrompt) {
      if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
        startActivity(intentFactory.createAppDetailsSettingsIntent())
      } else {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }

    onEach(viewModel.showCameraError) {
      showModalFromErrorMessage(it)
    }

    onEach(viewModel.showInvalidBarcodeError) {
      showModalFromErrorMessage(it)
    }

    onEach(viewModel.continueWithSolPayRequest) {
      val fragment = when (it) {
        is SolPayTransactionRequest ->
          SolPayTransactionRequestBottomSheetFragment.newInstance(requireContext(), it)
        is SolPayTransferRequest ->
          SolPayTransferRequestBottomSheetFragment.newInstance(requireContext(), it)
      }

      childFragmentManager.registerFragmentLifecycleCallbacks(
        object : FragmentLifecycleCallbacks() {
          override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            if (f == fragment) {
              fm.unregisterFragmentLifecycleCallbacks(this)
              addDecoderCallback()
            }
          }
        },
        false
      )

      fragment.showFrom(this)
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.onResume()

    if (!viewModel.showMissingCameraPermissionsState.value) {
      barcodeScannerView?.resume()
    }
  }

  override fun onPause() {
    barcodeScannerView?.pause()
    super.onPause()
  }

  override fun onDestroyView() {
    barcodeScannerView = null
    super.onDestroyView()
  }

  private fun addDecoderCallback() {
    barcodeScannerView?.barcodeView?.decodeSingle { result ->
      view?.performHapticFeedback()
      viewModel.onBarcodeScanResult(result.text.orEmpty())
    }
  }

  companion object {

    fun newInstance(): QrScannerFragment {
      return QrScannerFragment()
    }
  }
}