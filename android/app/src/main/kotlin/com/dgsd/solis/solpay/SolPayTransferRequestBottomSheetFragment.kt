package com.dgsd.solis.solpay

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import com.dgsd.solis.R
import com.dgsd.solis.accounts.AccountDetailsBottomSheetFragment
import com.dgsd.solis.common.bottomsheet.BaseBottomSheetFragment
import com.dgsd.solis.common.flow.onEach
import com.dgsd.solis.common.fragment.showFrom
import com.dgsd.solis.common.ui.ensureViewCount
import kotlinx.coroutines.flow.combine
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARGS_SOL_PAY_URL = "solpay_url"

class SolPayTransferRequestBottomSheetFragment : BaseBottomSheetFragment() {

  private val viewModel by viewModel<SolPayTransferRequestViewModel> {
    parametersOf(checkNotNull(requireArguments().getString(ARGS_SOL_PAY_URL)))
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.frag_sol_pay_transfer_request, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val loadingContainer = view.requireViewById<ViewGroup>(R.id.loading_container)
    val contentContainer = view.requireViewById<ViewGroup>(R.id.content_container)
    val errorContainer = view.requireViewById<ViewGroup>(R.id.error_container)
    val errorText = view.requireViewById<TextView>(R.id.error_message)
    val headerRecipient = view.requireViewById<TextView>(R.id.header_recipient)
    val headerAmount = view.requireViewById<TextView>(R.id.header_amount)
    val headerLabel = view.requireViewById<TextView>(R.id.header_label)
    val headerMessage = view.requireViewById<TextView>(R.id.header_message)
    val headerMemo = view.requireViewById<TextView>(R.id.header_memo)
    val valueRecipient = view.requireViewById<TextView>(R.id.value_recipient)
    val valueAmount = view.requireViewById<TextView>(R.id.value_amount)
    val valueLabel = view.requireViewById<TextView>(R.id.value_label)
    val valueMessage = view.requireViewById<TextView>(R.id.value_message)
    val valueMemo = view.requireViewById<TextView>(R.id.value_memo)
    val headerLink = view.requireViewById<TextView>(R.id.header_link)
    val valueLink = view.requireViewById<TextView>(R.id.value_link)
    val headerToken = view.requireViewById<TextView>(R.id.header_token)
    val valueToken = view.requireViewById<TextView>(R.id.value_token)
    val headerReferences = view.requireViewById<TextView>(R.id.header_references)
    val valueReferencesContainer =
      view.requireViewById<LinearLayout>(R.id.value_references_container)

    toolbar.setNavigationOnClickListener {
      dismissAllowingStateLoss()
    }

    valueRecipient.setOnClickListener {
      viewModel.onRecipientClicked()
    }

    valueRecipient.setOnLongClickListener {
      viewModel.onRecipientLongClicked()
      true
    }

    valueToken.setOnClickListener {
      viewModel.onTokenClicked()
    }

    valueToken.setOnLongClickListener {
      viewModel.onTokenLongClicked()
      true
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

    onEach(viewModel.recipientText) {
      headerRecipient.isVisible = it.isNotEmpty()
      valueRecipient.isVisible = it.isNotEmpty()
      valueRecipient.text = it
    }

    onEach(viewModel.amountText) {
      headerAmount.isVisible = it.isNotEmpty()
      valueAmount.isVisible = it.isNotEmpty()
      valueAmount.text = it
    }

    onEach(viewModel.messageText) {
      headerMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.isVisible = !it.isNullOrEmpty()
      valueMessage.text = it
    }

    onEach(viewModel.labelText) {
      headerLabel.isVisible = !it.isNullOrEmpty()
      valueLabel.isVisible = !it.isNullOrEmpty()
      valueLabel.text = it
    }

    onEach(viewModel.memoText) {
      headerMemo.isVisible = !it.isNullOrEmpty()
      valueMemo.isVisible = !it.isNullOrEmpty()
      valueMemo.text = it
    }

    onEach(viewModel.accountReferences) { accountReferences ->
      headerReferences.isVisible = accountReferences.isNotEmpty()

      valueReferencesContainer.ensureViewCount(accountReferences.size) {
        LayoutInflater.from(requireContext()).inflate(
          R.layout.li_solpay_transfer_request_account_reference,
          valueReferencesContainer,
          true
        )
      }

      accountReferences.zip(valueReferencesContainer.children.toList()) { account, view ->
        (view as TextView).text = account
        view.setOnClickListener {
          viewModel.onAccountReferenceClicked(accountReferences.indexOf(account))
        }
        view.setOnLongClickListener {
          viewModel.onAccountReferenceLongClicked(accountReferences.indexOf(account))
          true
        }
      }
    }

    onEach(viewModel.token) {
      headerToken.isVisible = !it.isNullOrEmpty()
      valueToken.isVisible = !it.isNullOrEmpty()
      valueToken.text = it
    }

    onEach(viewModel.linkText) {
      valueLink.isVisible = true
      headerLink.isVisible = true
      valueLink.text = it
    }

    onEach(viewModel.showAccount) {
      AccountDetailsBottomSheetFragment
        .newInstance(it)
        .showFrom(this)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(
      context: Context,
      request: SolPayTransferRequest
    ): SolPayTransferRequestBottomSheetFragment {
      val solPay = (context.applicationContext as Application).getKoin().get<SolPay>()
      val url = solPay.createUrl(request)
      return SolPayTransferRequestBottomSheetFragment().apply {
        arguments = bundleOf(ARGS_SOL_PAY_URL to url)
      }
    }
  }
}