package com.dgsd.solis.data.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [Cache] implementation that just holds values in memory
 */
abstract class InMemoryCache<K, V> : Cache<K, V> {

  private val keyToValueFlowMap =
    mutableMapOf<K, MutableStateFlow<CacheEntry<V>?>>()


  override val allKeys: Set<K>
    get() {
      return keyToValueFlowMap.keys
    }

  override suspend fun set(key: K, value: V) {
    getOrCreateMutableFlow(key).value = CacheEntry.of(value)
  }

  override fun get(key: K): StateFlow<CacheEntry<V>?> {
    return getOrCreateMutableFlow(key)
  }

  override suspend fun clear() {
    keyToValueFlowMap.clear()
  }

  private fun getOrCreateMutableFlow(
    key: K
  ): MutableStateFlow<CacheEntry<V>?> {
    return keyToValueFlowMap.getOrPut(key) { MutableStateFlow(null) }
  }
}