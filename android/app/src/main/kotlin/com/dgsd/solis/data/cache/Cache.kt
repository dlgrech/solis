package com.dgsd.solis.data.cache

import kotlinx.coroutines.flow.StateFlow

/**
 * Simple/generic cache interface, for saving and retrieving data by key
 */
interface Cache<K, V> {

  val allKeys: Set<K>

  suspend fun clear()

  suspend fun set(key: K, value: V)

  fun get(key: K): StateFlow<CacheEntry<V>?>

}