package com.dgsd.solis.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import com.dgsd.khelius.balance.BalanceApi
import com.dgsd.khelius.transactions.EnhancedTransactionsApi
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.model.Cluster
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.solis.BuildConfig
import com.dgsd.solis.accounts.cache.*
import com.dgsd.solis.common.clipboard.SystemClipboard
import com.dgsd.solis.common.error.ErrorMessageFactory
import com.dgsd.solis.common.intent.IntentFactory
import com.dgsd.solis.common.permission.PermissionsManager
import com.dgsd.solis.common.ui.PublicKeyFormatter
import com.dgsd.solis.common.ui.SolTokenNames
import com.dgsd.solis.common.ui.TransactionSourceFormatter
import com.dgsd.solis.common.ui.TransactionTypeFormatter
import com.dgsd.solis.data.SolanaRepository
import com.dgsd.solis.data.SolanaRepositoryImpl
import com.dgsd.solis.data.util.HeliusDescriptionSanitizer
import com.dgsd.solis.deeplink.DeeplinkExtractor
import com.dgsd.solis.deeplink.DeeplinkManager
import com.dgsd.solis.home.HomeSearchResultFactory
import com.dgsd.solis.notifications.SolisNotificationManager
import com.dgsd.solis.push.PushTokenManager
import com.dgsd.solis.push.PushTokenStorage
import com.dgsd.solis.sync.SolisDataRepo
import com.dgsd.solis.sync.SolisDataRepoImpl
import com.dgsd.solis.sync.SolisDataStore
import com.dgsd.solis.transactions.cache.TransactionCache
import com.dgsd.solis.transactions.cache.TransactionInMemoryCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object AppModule {

  fun create(): Module {
    return module {

      single<OkHttpClient> {
        val context = get<Context>()
        OkHttpClient.Builder()
          .callTimeout(60, TimeUnit.SECONDS)
          .connectTimeout(60, TimeUnit.SECONDS)
          .readTimeout(60, TimeUnit.SECONDS)
          .cache(Cache(context.cacheDir, 10 * 1024 * 1024))
          .build()
      }

      single<ImageLoader> {
        ImageLoader.Builder(get())
          .crossfade(true)
          .okHttpClient(get<OkHttpClient>())
          .build()
      }

      single<FirebaseDatabase> {
        Firebase.database.apply {
          setPersistenceEnabled(true)
        }
      }

      single<FirebaseAuth> {
        Firebase.auth
      }

      single<SolisDataRepo> {
        SolisDataRepoImpl(
          dataStore = get()
        )
      }

      single<EnhancedTransactionsApi> {
        EnhancedTransactionsApi(
          apiKey = BuildConfig.HELIUS_API_KEY,
          okHttpClient = get()
        )
      }

      single<BalanceApi> {
        BalanceApi(
          apiKey = BuildConfig.HELIUS_API_KEY,
          okHttpClient = get()
        )
      }

      single<TransactionCache> {
        TransactionInMemoryCache()
      }

      single<AccountInfoCache> {
        AccountInfoInMemoryCache()
      }

      single<MultipleAccountInfoCache> {
        MultipleAccountInfoInMemoryCache(get())
      }

      single<BalanceSummaryCache> {
        BalanceSummaryInMemoryCache()
      }


      single<TransactionHistoryCache> {
        TransactionHistoryInMemoryCache()
      }

      single<SolanaApi> {
        SolanaApi(
          Cluster.Custom(
            rpcUrl = "https://rpc.helius.xyz/?api-key=${BuildConfig.HELIUS_API_KEY}",
            webSocketUrl = "ws://rpc.helius.xyz/?api-key=${BuildConfig.HELIUS_API_KEY}"
          ),
          okHttpClient = get(),
        )
      }

      single<SolPay> {
        SolPay(okHttpClient = get(), solanaApi = get())
      }

      single<SolanaRepository> {
        SolanaRepositoryImpl(
          enhancedTransactionsApi = get(),
          balanceApi = get(),
          solanaApi = get(),
          transactionCache = get(),
          transactionHistoryCache = get(),
          balanceSummaryCache = get(),
          multipleAccountInfoCache = get(),
          accountInfoCache = get(),
          publicKeyFormatter = get(),
          descriptionSanitizer = get(),
        )
      }

      single<DeeplinkManager> {
        DeeplinkManager(
          listOf(
            DeeplinkExtractor.Solis,
            DeeplinkExtractor.SolanaExplorer,
            DeeplinkExtractor.Solscan,
            DeeplinkExtractor.SolanaBeach,
            DeeplinkExtractor.SolanaFm,
          )
        )
      }

      single<SharedPreferences> {
        get<Application>().getSharedPreferences("solis_prefs", Context.MODE_PRIVATE)
      }

      single<NotificationManagerCompat> {
        NotificationManagerCompat.from(get<Context>())
      }

      single<PushTokenManager> {
        PushTokenManager(
          coroutineScope = GlobalScope,
          pushTokenStorage = get(),
          dataRepo = get(),
        )
      }

      singleOf(::IntentFactory)
      singleOf(::SolTokenNames)
      singleOf(::SolisDataStore)
      singleOf(::TransactionTypeFormatter)
      singleOf(::TransactionSourceFormatter)
      singleOf(::PublicKeyFormatter)
      singleOf(::HeliusDescriptionSanitizer)
      singleOf(::SystemClipboard)
      singleOf(::ErrorMessageFactory)
      singleOf(::PermissionsManager)
      singleOf(::HomeSearchResultFactory)
      singleOf(::PushTokenStorage)
      singleOf(::SolisNotificationManager)
    }
  }
}