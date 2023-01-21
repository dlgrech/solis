package com.dgsd.solis.push

import android.content.SharedPreferences
import androidx.core.content.edit

private const val PREF_KEY_PUSH_TOKEN = "push_token"
private const val PREF_KEY_PUSH_TOKEN_SYNCED = "push_token_synced"

class PushTokenStorage(
  private val sharedPreferences: SharedPreferences,
) {

  fun setPushTokenSynced(isSynced: Boolean) {
    sharedPreferences.edit {
      putBoolean(PREF_KEY_PUSH_TOKEN_SYNCED, isSynced)
    }
  }

  fun isPushTokenSynced(): Boolean {
    return sharedPreferences.getBoolean(PREF_KEY_PUSH_TOKEN_SYNCED, false)
  }

  fun savePushToken(token: String) {
    sharedPreferences.edit {
      putString(PREF_KEY_PUSH_TOKEN, token)
    }
  }

  fun getPushToken(): String? {
    return sharedPreferences.getString(PREF_KEY_PUSH_TOKEN, null)
  }
}