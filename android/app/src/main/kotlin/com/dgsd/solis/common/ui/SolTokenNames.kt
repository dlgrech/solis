package com.dgsd.solis.common.ui

import android.content.Context
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.solis.R
import java.io.BufferedReader
import java.io.InputStreamReader

class SolTokenNames(
  private val context: Context,
) {

  private val accountToDisplayNameMap by lazy {
    val map = mutableMapOf<String, String>()
    context.resources.openRawResource(R.raw.token_names)
      .use { inputStream ->
        InputStreamReader(inputStream).use { inputStreamReader ->
          BufferedReader(inputStreamReader).use { br ->
            var entry: String?
            do {
              entry = br.readLine()
              if (entry != null) {
                val (account, name) = checkNotNull(entry).split(":", limit = 2)
                map[account] = name
              }
            } while (entry != null)
          }
        }
      }

    map
  }

  fun getDisplayName(token: PublicKey): String? {
    return accountToDisplayNameMap[token.toBase58String()]
  }
}