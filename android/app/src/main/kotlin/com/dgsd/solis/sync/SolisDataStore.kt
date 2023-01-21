package com.dgsd.solis.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class SolisDataStore(
  private val auth: FirebaseAuth,
  private val database: FirebaseDatabase,
) {

  suspend fun setPushToken(token: String) {
    val userId = getUserId()
    database.getReference(userId)
      .child(DATABASE_KEY_PUSH_TOKEN)
      .setValue(token)
      .await()
  }

  suspend fun addSavedAccount(account: String) {
    val userId = getUserId()
    database.getReference(userId)
      .child(DATABASE_KEY_SAVED_ACCOUNTS)
      .child(account)
      .setValue(true)
      .await()
  }

  suspend fun removeSavedAccount(account: String) {
    val userId = getUserId()
    database.getReference(userId)
      .child(DATABASE_KEY_SAVED_ACCOUNTS)
      .child(account)
      .removeValue()
      .await()
  }

  suspend fun observeSavedAccount(account: String): Flow<Boolean> {
    val userId = getUserId()
    return database.getReference(userId)
      .child(DATABASE_KEY_SAVED_ACCOUNTS)
      .child(account)
      .snapshots
      .map { isSavedAccount(account) }
      .onStart { emit(isSavedAccount(account)) }
  }

  suspend fun observeSavedAccounts(): Flow<List<String>> {
    val userId = getUserId()
    return database.getReference(userId)
      .child(DATABASE_KEY_SAVED_ACCOUNTS)
      .childEvents
      .map { getSavedAccounts() }
      .onStart { emit(getSavedAccounts()) }
  }

  private suspend fun isSavedAccount(account: String): Boolean {
    val userId = getUserId()
    return database.getReference(userId)
      .child(DATABASE_KEY_SAVED_ACCOUNTS)
      .child(account)
      .get()
      .await()
      .value != null
  }

  private suspend fun getSavedAccounts(): List<String> {
    val userId = getUserId()
    return database.getReference(userId)
      .child(DATABASE_KEY_SAVED_ACCOUNTS)
      .get()
      .await()
      .getValue<Map<String, Boolean>>()
      .orEmpty()
      .keys
      .sorted()
  }

  private suspend fun getUserId(): String {
    return auth.currentUser?.uid ?: checkNotNull(auth.signInAnonymously().await().user?.uid)
  }

  private companion object {
    const val DATABASE_KEY_PUSH_TOKEN = "push_token"
    const val DATABASE_KEY_SAVED_ACCOUNTS = "saved_accounts"
  }
}