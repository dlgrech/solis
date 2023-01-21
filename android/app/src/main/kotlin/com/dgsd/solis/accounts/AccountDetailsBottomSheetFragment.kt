package com.dgsd.solis.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.R
import com.dgsd.solis.common.actionsheet.extensions.showActionSheet
import com.dgsd.solis.common.actionsheet.model.ActionSheetItem
import com.dgsd.solis.common.bottomsheet.BaseBottomSheetFragment
import com.dgsd.solis.common.flow.asNullable
import com.dgsd.solis.common.flow.onEach
import com.dgsd.solis.common.fragment.showFrom
import com.dgsd.solis.common.ui.ensureViewCount
import com.dgsd.solis.transactions.TransactionDetailsBottomSheetFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARG_ACCOUNT_KEY = "account_key"

class AccountDetailsBottomSheetFragment : BaseBottomSheetFragment() {

  private val viewModel: AccountDetailsViewModel by viewModel {
    val accountKeyBase58 = requireArguments().getString(ARG_ACCOUNT_KEY)
    val accountKey = PublicKey.fromBase58(checkNotNull(accountKeyBase58))

    parametersOf(accountKey)
  }

  private val notificationPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) {}

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.frag_account_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val loadingContainer = view.requireViewById<ViewGroup>(R.id.loading_container)
    val contentContainer = view.requireViewById<ViewGroup>(R.id.content_container)
    val errorContainer = view.requireViewById<ViewGroup>(R.id.error_container)
    val errorText = view.requireViewById<TextView>(R.id.error_message)
    val tryAgainButton = view.requireViewById<TextView>(R.id.try_again_button)
    val saveButton = view.requireViewById<ImageView>(R.id.save_button)
    val titleOwner = view.requireViewById<TextView>(R.id.title_owner)
    val valueOwner = view.requireViewById<TextView>(R.id.value_owner)
    val titleExecutable = view.requireViewById<TextView>(R.id.title_executable)
    val valueExecutable = view.requireViewById<TextView>(R.id.value_executable)
    val valueAccountKey = view.requireViewById<TextView>(R.id.value_account_key)
    val valueSolBalance = view.requireViewById<TextView>(R.id.value_sol_balance)
    val tokenBalanceHeader = view.requireViewById<TextView>(R.id.title_sub_accounts)
    val tokenBalanceContainer = view.requireViewById<LinearLayout>(R.id.token_balance_container)
    val recentTransactionsContainer =
      view.requireViewById<LinearLayout>(R.id.recent_transactions_container)
    val recentTransactionsLoadButton = view.requireViewById<View>(R.id.recent_transactions_load)
    val recentTransactionsEmptyMessage =
      view.requireViewById<View>(R.id.recent_transactions_empty_message)
    val recentTransactionsErrorMessage =
      view.requireViewById<TextView>(R.id.recent_transactions_error_message)
    val recentTransactionsLoadingIndicator =
      view.requireViewById<View>(R.id.recent_transactions_loading_indicator)

    toolbar.setNavigationOnClickListener {
      dismissAllowingStateLoss()
    }

    tryAgainButton.setOnClickListener {
      viewModel.onTryAgainClicked()
    }

    saveButton.setOnClickListener {
      viewModel.onSavedClicked()
    }

    recentTransactionsLoadButton.setOnClickListener {
      viewModel.onLoadRecentTransactionsClicked()
    }

    valueAccountKey.setOnClickListener {
      viewModel.onAccountKeyClicked()
    }

    valueAccountKey.setOnLongClickListener {
      viewModel.onAccountKeyLongClicked()
      true
    }

    valueSolBalance.setOnClickListener {
      viewModel.onSolBalanceClicked()
    }

    valueSolBalance.setOnLongClickListener {
      viewModel.onSolBalanceLongClicked()
      true
    }

    valueOwner.setOnClickListener {
      viewModel.onOwnerClicked()
    }

    valueOwner.setOnLongClickListener {
      viewModel.onOwnerLongClicked()
      true
    }

    onEach(viewModel.isSaveActionVisible) {
      saveButton.isVisible = it
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

    onEach(viewModel.accountKeyText) {
      valueAccountKey.text = it
    }

    onEach(viewModel.solBalanceText) {
      valueSolBalance.text = it
    }

    onEach(viewModel.ownerText) {
      titleOwner.isVisible = !it.isNullOrEmpty()
      valueOwner.isVisible = !it.isNullOrEmpty()
      valueOwner.text = it
    }

    onEach(viewModel.executableText) {
      titleExecutable.isVisible = !it.isNullOrEmpty()
      valueExecutable.isVisible = !it.isNullOrEmpty()
      valueExecutable.text = it
    }

    onEach(viewModel.balanceItems) { balanceItems ->
      if (balanceItems.isEmpty()) {
        tokenBalanceHeader.isVisible = false
        tokenBalanceContainer.isVisible = false
      } else {
        tokenBalanceHeader.isVisible = true
        tokenBalanceContainer.isVisible = true

        tokenBalanceContainer.ensureViewCount(balanceItems.size) {
          LayoutInflater.from(requireContext()).inflate(
            R.layout.li_account_details_sub_account,
            tokenBalanceContainer,
            true
          )
        }

        balanceItems.zip(tokenBalanceContainer.children.toList())
          .onEach { (balanceItem, view) ->
            val balance = view.requireViewById<TextView>(R.id.balance)
            val tokenName = view.requireViewById<TextView>(R.id.token_name)
            val accountAddress = view.requireViewById<TextView>(R.id.account_address)

            balance.text = balanceItem.balance
            tokenName.text = balanceItem.name
            accountAddress.text = balanceItem.account

            view.setOnClickListener {
              showActionSheet(
                items = arrayOf(
                  ActionSheetItem(
                    title = getString(
                      R.string.account_details_subaccount_action_sheet_view_program_template,
                      balanceItem.name
                    ),
                    onClick = {
                      newInstance(
                        PublicKey.fromBase58(balanceItem.tokenBalance.mint)
                      ).showFrom(this)
                    }
                  ),

                  ActionSheetItem(
                    title = getString(
                      R.string.account_details_subaccount_action_sheet_view_sub_account_template,
                      balanceItem.account
                    ),
                    onClick = {
                      newInstance(
                        PublicKey.fromBase58(balanceItem.tokenBalance.account)
                      ).showFrom(this)
                    }
                  )
                )
              )
            }
          }
      }
    }

    onEach(
      combine(
        viewModel.isTransactionHistoryLoading,
        viewModel.transactionHistoryErrorMessage,
        viewModel.transactionHistory.asNullable().onStart { emit(null) },
      ) { isLoading, errorMessage, transactions ->
        Triple(isLoading, errorMessage, transactions?.isNotEmpty())
      }
    ) { (isLoading, errorMessage, hasTransactions) ->
      if (isLoading && errorMessage == null) {
        recentTransactionsLoadingIndicator.isVisible = true
        recentTransactionsLoadButton.isVisible = false
        recentTransactionsContainer.isVisible = false
        recentTransactionsEmptyMessage.isVisible = false
        recentTransactionsErrorMessage.isVisible = false
      } else if (errorMessage != null) {
        recentTransactionsLoadingIndicator.isVisible = false
        recentTransactionsLoadButton.isVisible = true
        recentTransactionsContainer.isVisible = false
        recentTransactionsEmptyMessage.isVisible = false
        recentTransactionsErrorMessage.isVisible = true
      } else {
        recentTransactionsLoadingIndicator.isVisible = false
        recentTransactionsLoadButton.isVisible = hasTransactions == null
        recentTransactionsContainer.isVisible = hasTransactions == true
        recentTransactionsEmptyMessage.isVisible = hasTransactions == false
        recentTransactionsErrorMessage.isVisible = false
      }

      recentTransactionsErrorMessage.text = errorMessage
    }

    onEach(viewModel.transactionHistory) { transactions ->
      val layoutInflater = LayoutInflater.from(requireContext())
      recentTransactionsContainer.ensureViewCount(transactions.size) {
        layoutInflater.inflate(
          R.layout.li_account_details_recent_transaction,
          recentTransactionsContainer,
          true
        )
      }

      transactions.zip(recentTransactionsContainer.children.toList())
        .forEach { (transaction, view) ->
          view.requireViewById<TextView>(R.id.type).text = transaction.type
          view.requireViewById<TextView>(R.id.description).text = transaction.description
          view.requireViewById<TextView>(R.id.timestamp).text = transaction.timeText

          view.setOnClickListener {
            TransactionDetailsBottomSheetFragment
              .newInstance(transaction.signature)
              .showFrom(this)
          }
        }
    }

    onEach(viewModel.isSavedAccount) { isSaved ->
      if (isSaved) {
        saveButton.setImageResource(R.drawable.ic_baseline_favorite_24)
      } else {
        saveButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
      }
    }

    onEach(viewModel.showNotificationsPermissionPrompt) {
      if (!shouldShowRequestPermissionRationale(it)) {
        notificationPermissionLauncher.launch(it)
      }
    }

    onEach(viewModel.showMessage) {
      Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
    }

    onEach(viewModel.showAccount) {
      newInstance(it).showFrom(this)
    }

    onEach(viewModel.bottomSheetTitle) {
      toolbar.title = it
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(accountKey: PublicKey): AccountDetailsBottomSheetFragment {
      return AccountDetailsBottomSheetFragment().apply {
        arguments = bundleOf(
          ARG_ACCOUNT_KEY to accountKey.toBase58String()
        )
      }
    }
  }
}