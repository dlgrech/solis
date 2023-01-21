package com.dgsd.solis.home

import android.content.Context
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.R
import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.home.HomeScreenItem.SearchResult
import com.dgsd.solis.programs.model.NativePrograms
import com.dgsd.solis.transactions.model.asTransactionSignatureOrNull
import com.dgsd.solis.util.asPublicKeyOrNull

class HomeSearchResultFactory(
  private val context: Context,
  private val publicKeyFormatter: PublicKeyFormatter
) {

  fun generate(
    searchTerm: String,
    clipboardContents: String,
  ): List<SearchResult> {
    val clipboardResults = generateClipboardResults(context, clipboardContents)
    val searchTermResults = generateSearchTermResults(context, searchTerm)

    return clipboardResults + searchTermResults
  }

  private fun generateClipboardResults(
    context: Context,
    searchTerm: String
  ): List<SearchResult> {
    val transactionSignature = searchTerm.asTransactionSignatureOrNull()
    val clipboardItem = if (transactionSignature != null) {
      SearchResult.TransactionResult(
        transactionSignature = transactionSignature,
        displayText = context.getString(
          R.string.home_search_result_display_prefix_transaction,
          transactionSignature
        )
      )
    } else {
      searchTerm.asPublicKeyOrNull()?.let {
        SearchResult.AccountResult(
          accountKey = it,
          displayText = context.getString(
            R.string.home_search_result_display_prefix_account,
            publicKeyFormatter.format(it)
          )
        )
      }
    }

    return if (clipboardItem == null) {
      emptyList()
    } else {
      return listOf(
        SearchResult.CategoryHeader(
          context.getString(R.string.home_search_result_category_header_clipboard)
        ),
        clipboardItem
      )
    }
  }

  private fun generateSearchTermResults(
    context: Context,
    searchTerm: String
  ): List<SearchResult> {
    val publicKey = searchTerm.asPublicKeyOrNull()
    val transactionResults = generateTransactionResults(context, searchTerm)
    val programResults = generateProgramResults(context, searchTerm, publicKey)
    val accountResults = if (programResults.isNotEmpty()) {
      emptyList()
    } else {
      generateAccountResults(context, publicKey)
    }

    return transactionResults + programResults + accountResults
  }

  private fun generateProgramResults(
    context: Context,
    searchTerm: String,
    publicKey: PublicKey?
  ): List<SearchResult> {
    val matchingPrograms = when {
      publicKey == null && searchTerm.isBlank() -> {
        emptyList()
      }

      publicKey == null -> {
        // Check if our search term matches a friendly program name
        NativePrograms.nativeProgramKeys.filter {
          val displayName = publicKeyFormatter.getProgramDisplayName(it)
          displayName?.startsWith(searchTerm, ignoreCase = true) == true
        }
      }

      NativePrograms.isNativeProgram(publicKey) -> {
        listOf(publicKey)
      }

      else -> {
        emptyList()
      }
    }

    return if (matchingPrograms.isEmpty()) {
      emptyList()
    } else {
      mutableListOf<SearchResult>().apply {
        add(
          SearchResult.CategoryHeader(
            context.getString(R.string.home_search_result_category_header_programs)
          )
        )

        matchingPrograms
          .map { SearchResult.ProgramResult(it, publicKeyFormatter.format(it)) }
          .sortedBy { it.displayText.toString() }
          .forEach(::add)
      }
    }
  }

  private fun generateAccountResults(
    context: Context,
    publicKey: PublicKey?,
  ): List<SearchResult> {
    return if (publicKey == null) {
      emptyList()
    } else {
      listOf(
        SearchResult.CategoryHeader(
          context.getString(R.string.home_search_result_category_header_accounts)
        ),
        SearchResult.AccountResult(
          accountKey = publicKey,
          displayText = publicKeyFormatter.format(publicKey)
        )
      )
    }
  }

  private fun generateTransactionResults(
    context: Context,
    searchTerm: String
  ): List<SearchResult> {
    val transactionSignature = searchTerm.asTransactionSignatureOrNull()
    return if (transactionSignature == null) {
      emptyList()
    } else {
      listOf(
        SearchResult.CategoryHeader(
          context.getString(R.string.home_search_result_category_header_transactions)
        ),
        SearchResult.TransactionResult(
          transactionSignature = transactionSignature,
          displayText = transactionSignature
        )
      )
    }
  }
}