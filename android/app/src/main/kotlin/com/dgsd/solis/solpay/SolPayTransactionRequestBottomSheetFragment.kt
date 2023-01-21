package com.dgsd.solis.solpay

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.dgsd.solis.R
import com.dgsd.solis.common.bottomsheet.BaseBottomSheetFragment
import com.dgsd.solis.common.flow.onEach
import com.dgsd.solis.common.ui.setUrl
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARGS_SOL_PAY_URL = "solpay_url"

class SolPayTransactionRequestBottomSheetFragment : BaseBottomSheetFragment() {

  private val viewModel by viewModel<SolPayTransactionRequestViewModel> {
    parametersOf(checkNotNull(requireArguments().getString(ARGS_SOL_PAY_URL)))
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.frag_sol_pay_transaction_request, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val loadingContainer = view.requireViewById<ViewGroup>(R.id.loading_container)
    val contentContainer = view.requireViewById<ViewGroup>(R.id.content_container)
    val errorContainer = view.requireViewById<ViewGroup>(R.id.error_container)
    val errorText = view.requireViewById<TextView>(R.id.error_message)
    val tryAgainButton = view.requireViewById<TextView>(R.id.try_again_button)
    val logo = view.requireViewById<ImageView>(R.id.logo)
    val label = view.requireViewById<TextView>(R.id.label)
    val headerMessage = view.requireViewById<TextView>(R.id.header_message)
    val valueMessage = view.requireViewById<TextView>(R.id.value_message)
    val headerAmount = view.requireViewById<TextView>(R.id.header_amount)
    val valueAmount = view.requireViewById<TextView>(R.id.value_amount)
    val headerLink = view.requireViewById<TextView>(R.id.header_link)
    val valueLink = view.requireViewById<TextView>(R.id.value_link)

    toolbar.setNavigationOnClickListener {
      dismissAllowingStateLoss()
    }

    tryAgainButton.setOnClickListener {
      viewModel.onTryAgainClicked()
    }

    valueLink.setOnClickListener {
      viewModel.onLinkClicked()
    }

    valueLink.setOnLongClickListener {
      viewModel.onLinkLongClicked()
      true
    }

    onEach(
      combine(viewModel.isLoading, viewModel.errorMessage) { isLoading, errorMessage ->
        isLoading to errorMessage
      }
    ) { (isLoading, errorMessage) ->
      if (isLoading && errorMessage == null) {
        errorContainer.isVisible = false
        loadingContainer.isVisible = true
        contentContainer.isVisible = false
      } else if (errorMessage != null) {
        errorContainer.isVisible = true
        loadingContainer.isVisible = false
        contentContainer.isVisible = false
      } else {
        errorContainer.isVisible = false
        loadingContainer.isVisible = false
        contentContainer.isVisible = true
      }

      errorText.text = errorMessage
    }

    onEach(viewModel.logoUrl) {
      if (it.isNullOrEmpty()) {
        logo.isVisible = false
      } else {
        logo.isVisible = true
        logo.setUrl(it)
      }
    }

    onEach(viewModel.message) {
      headerMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.text = it
    }

    onEach(viewModel.label) {
      label.isVisible = !it.isNullOrEmpty()
      label.text = it
    }

    onEach(viewModel.amountText) {
      headerAmount.isVisible = it != null
      valueAmount.isVisible = it != null
      valueAmount.text = it
    }

    onEach(viewModel.linkText) {
      valueLink.isVisible = true
      headerLink.isVisible = true
      valueLink.text = it
    }

    onEach(viewModel.showMessage) {
      Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(
      context: Context,
      request: SolPayTransactionRequest
    ): SolPayTransactionRequestBottomSheetFragment {
      val solPay = (context.applicationContext as Application).getKoin().get<SolPay>()
      val url = solPay.createUrl(request)
      return SolPayTransactionRequestBottomSheetFragment().apply {
        arguments = bundleOf(ARGS_SOL_PAY_URL to url)
      }
    }
  }
}