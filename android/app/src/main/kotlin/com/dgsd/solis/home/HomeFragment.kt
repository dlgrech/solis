package com.dgsd.solis.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.solis.AppCoordinator
import com.dgsd.solis.R
import com.dgsd.solis.accounts.AccountDetailsBottomSheetFragment
import com.dgsd.solis.common.flow.onEach
import com.dgsd.solis.common.fragment.showFrom
import com.dgsd.solis.common.intent.IntentFactory
import com.dgsd.solis.common.ui.hideKeyboard
import com.dgsd.solis.common.ui.showKeyboard
import com.dgsd.solis.transactions.TransactionDetailsBottomSheetFragment
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.frag_home) {

  private val appCoordinator: AppCoordinator by activityViewModel()
  private val viewModel: HomeViewModel by viewModel()
  private val intentFactory by inject<IntentFactory>()

  private val notificationPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) {
    viewModel.onNotificationPermissionChanged()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val searchWrapper = view.requireViewById<View>(R.id.search_wrapper)
    val searchInputField = view.requireViewById<EditText>(R.id.search_input_field)
    val qrScannerButton = view.requireViewById<View>(R.id.qr_scanner)
    val settingsButton = view.requireViewById<View>(R.id.settings)
    val recyclerView = view.requireViewById<RecyclerView>(R.id.recycler_view)
    val searchResultsCard = view.requireViewById<View>(R.id.search_results_card)
    val searchResultsContainer = view.requireViewById<LinearLayout>(R.id.search_results_container)
    val loadingIndicator = view.requireViewById<View>(R.id.loading_indicator)
    val errorContainer = view.requireViewById<ViewGroup>(R.id.error_container)
    val errorText = view.requireViewById<TextView>(R.id.error_message)
    val tryAgainButton = view.requireViewById<TextView>(R.id.try_again_button)
    val emptyContainer = view.requireViewById<ViewGroup>(R.id.empty_container)
    val notificationPermissionPrompt =
      view.requireViewById<View>(R.id.notification_permission_prompt)

    val adapter = HomeScreenItemAdapter(onAccountClicked = ::openAccount)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(
      HomeDividerItemDecoration(requireContext())
    )

    searchWrapper.setOnClickListener {
      searchInputField.showKeyboard()
    }

    qrScannerButton.setOnClickListener {
      appCoordinator.navigateToQrScanner()
    }

    settingsButton.setOnClickListener {
      appCoordinator.navigateToAppSettings()
    }

    tryAgainButton.setOnClickListener {
      viewModel.onTryAgainClicked()
    }

    searchInputField.doAfterTextChanged {
      viewModel.onSearchInputChanged(it?.toString().orEmpty())
    }

    notificationPermissionPrompt.setOnClickListener {
      viewModel.onRequestNotificationPermissionsClicked()
    }

    onEach(
      viewModel.searchResults
        .map { it.isNotEmpty() }
        .distinctUntilChanged()
    ) { hasSearchResults ->
      view.beginTransition()
      searchResultsCard.isVisible = hasSearchResults
    }

    onEach(viewModel.searchResults) {
      addViewsForSearchResults(searchResultsContainer, it)
    }

    onEach(viewModel.savedAccounts) {
      adapter.update(it)
    }

    onEach(viewModel.shouldShowNotificationPermissionPrompt) {
      view.beginTransition()
      notificationPermissionPrompt.isVisible = it
    }

    onEach(viewModel.showNotificationsPermissionPrompt) {
      if (shouldShowRequestPermissionRationale(it)) {
        startActivity(intentFactory.createAppNotificationSettingsIntent())
      } else {
        notificationPermissionLauncher.launch(it)
      }
    }

    onEach(
      combine(
        viewModel.savedAccountsLoading,
        viewModel.errorMessage,
        viewModel.savedAccounts.onStart { emit(emptyList()) },
      ) { isLoading, errorMessage, savedAccounts, ->
        Triple(isLoading, errorMessage, savedAccounts.isNotEmpty())
      }
    ) { (isLoading, errorMessage, hasSavedAccounts) ->
      if (isLoading && errorMessage == null) {
        errorContainer.isVisible = false
        loadingIndicator.isVisible = true
        recyclerView.isVisible = false
        emptyContainer.isVisible = false
      } else if (errorMessage != null) {
        errorContainer.isVisible = true
        loadingIndicator.isVisible = false
        recyclerView.isVisible = false
        emptyContainer.isVisible = false
      } else {
        errorContainer.isVisible = false
        loadingIndicator.isVisible = false
        recyclerView.isVisible = hasSavedAccounts
        emptyContainer.isVisible = !hasSavedAccounts
      }

      errorText.text = errorMessage
    }
  }

  private fun addViewsForSearchResults(
    searchResultsContainer: LinearLayout,
    results: List<HomeScreenItem.SearchResult>
  ) {
    searchResultsContainer.removeAllViews()

    val layoutInflater = LayoutInflater.from(requireContext())
    for (item in results) {
      val view = when (item) {
        is HomeScreenItem.SearchResult.CategoryHeader -> {
          layoutInflater.inflate(
            R.layout.li_home_screen_search_result_header,
            searchResultsContainer,
            false
          ).apply {
            requireViewById<TextView>(R.id.text).text = item.displayText
          }
        }

        is HomeScreenItem.SearchResult.AccountResult -> {
          createSearchResultView(layoutInflater, searchResultsContainer, item.displayText) {
            openAccount(item.accountKey)
          }
        }

        is HomeScreenItem.SearchResult.ProgramResult -> {
          createSearchResultView(layoutInflater, searchResultsContainer, item.displayText) {
            openAccount(item.programAddress)
          }
        }

        is HomeScreenItem.SearchResult.TransactionResult -> {
          createSearchResultView(layoutInflater, searchResultsContainer, item.displayText) {
            openTransaction(item.transactionSignature)
          }
        }
      }

      searchResultsContainer.addView(view)
    }
  }

  private fun openTransaction(transactionSignature: TransactionSignature) {
    TransactionDetailsBottomSheetFragment
      .newInstance(transactionSignature)
      .showFrom(this)
  }

  private fun openAccount(accountKey: PublicKey) {
    AccountDetailsBottomSheetFragment
      .newInstance(accountKey)
      .showFrom(this)
  }

  private fun createSearchResultView(
    layoutInflater: LayoutInflater,
    container: ViewGroup,
    displayText: CharSequence,
    onClickAction: View.OnClickListener
  ): View {
    val view = layoutInflater.inflate(
      R.layout.li_home_screen_search_result_item, container, false
    )

    view.requireViewById<TextView>(R.id.text).text = displayText
    view.setOnClickListener(onClickAction)

    return view
  }

  override fun onResume() {
    super.onResume()
    viewModel.onResume()
  }

  override fun onPause() {
    view?.hideKeyboard()
    super.onPause()
  }

  private fun View.beginTransition() {
    TransitionManager.beginDelayedTransition(
      this as ViewGroup,
      AutoTransition()
        .setOrdering(AutoTransition.ORDERING_TOGETHER)
        .setDuration(200)
    )
  }

  companion object {

    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}