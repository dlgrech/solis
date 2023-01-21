package com.dgsd.solis.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.R
import com.dgsd.solis.accounts.AccountDetailsBottomSheetFragment
import com.dgsd.solis.common.bottomsheet.BaseBottomSheetFragment
import com.dgsd.solis.common.flow.onEach
import com.dgsd.solis.common.fragment.showFrom
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARG_TRANSACTION_SIGNATURE = "transaction_signature"

class TransactionDetailsBottomSheetFragment : BaseBottomSheetFragment() {

  private val viewModel: TransactionDetailsViewModel by viewModel {
    val transactionSignature = requireArguments().getString(ARG_TRANSACTION_SIGNATURE)
    parametersOf(transactionSignature)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.frag_transaction_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val loadingContainer = view.requireViewById<ViewGroup>(R.id.loading_container)
    val contentContainer = view.requireViewById<ViewGroup>(R.id.content_container)
    val titleTransactionType = view.requireViewById<TextView>(R.id.title_transaction_type)
    val titleTransactionSource = view.requireViewById<TextView>(R.id.title_transaction_source)
    val titleDescription = view.requireViewById<TextView>(R.id.title_description)
    val valueDescription = view.requireViewById<TextView>(R.id.value_description)
    val valueSignature = view.requireViewById<TextView>(R.id.value_signature)
    val valueTime = view.requireViewById<TextView>(R.id.value_time)
    val valueTransactionType = view.requireViewById<TextView>(R.id.value_transaction_type)
    val valueTransactionSource = view.requireViewById<TextView>(R.id.value_transaction_source)
    val valueFee = view.requireViewById<TextView>(R.id.value_fee)
    val valueFeePayer = view.requireViewById<TextView>(R.id.value_fee_payer)
    val valueSlot = view.requireViewById<TextView>(R.id.value_slot)
    val errorContainer = view.requireViewById<ViewGroup>(R.id.error_container)
    val errorText = view.requireViewById<TextView>(R.id.error_message)
    val tryAgainButton = view.requireViewById<TextView>(R.id.try_again_button)

    toolbar.setNavigationOnClickListener {
      dismissAllowingStateLoss()
    }

    tryAgainButton.setOnClickListener {
      viewModel.onTryAgainClicked()
    }

    valueSignature.setOnClickListener {
      viewModel.onSignatureClicked()
    }

    valueSignature.setOnLongClickListener {
      viewModel.onSignatureLongClicked()
      true
    }

    valueFeePayer.setOnClickListener {
      viewModel.onFeePayerClicked()
    }

    valueFeePayer.setOnLongClickListener {
      viewModel.onFeePayerLongClicked()
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

    onEach(viewModel.description) {
      valueDescription.isVisible = !it.isNullOrBlank()
      titleDescription.isVisible = !it.isNullOrBlank()
      valueDescription.text = it
    }

    onEach(viewModel.signature) {
      valueSignature.text = it
    }

    onEach(viewModel.transactionTypeText) {
      titleTransactionType.isVisible = !it.isNullOrBlank()
      valueTransactionType.isVisible = !it.isNullOrBlank()
      valueTransactionType.text = it
    }

    onEach(viewModel.transactionSourceText) {
      titleTransactionSource.isVisible = !it.isNullOrBlank()
      valueTransactionSource.isVisible = !it.isNullOrBlank()
      valueTransactionSource.text = it
    }

    onEach(viewModel.feeText) {
      valueFee.text = it
    }

    onEach(viewModel.feePayerText) {
      valueFeePayer.text = it
    }

    onEach(viewModel.timeText) {
      valueTime.text = it
    }

    onEach(viewModel.slotText) {
      valueSlot.text = it
    }

    onEach(viewModel.showMessage) {
      Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
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

    fun newInstance(transactionSignature: TransactionSignature): TransactionDetailsBottomSheetFragment {
      return TransactionDetailsBottomSheetFragment().apply {
        arguments = bundleOf(
          ARG_TRANSACTION_SIGNATURE to transactionSignature
        )
      }
    }
  }
}