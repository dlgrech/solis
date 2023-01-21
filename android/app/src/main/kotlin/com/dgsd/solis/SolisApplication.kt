package com.dgsd.solis

import android.app.Application
import android.os.StrictMode
import com.dgsd.solis.di.AppModule
import com.dgsd.solis.di.ViewModelModule
import com.dgsd.solis.push.PushTokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SolisApplication : Application() {

  private val pushTokenManager by inject<PushTokenManager>()

  override fun onCreate() {
    super.onCreate()

    startKoin {
      if (BuildConfig.DEBUG) {
        androidLogger()
      }

      androidContext(this@SolisApplication)

      modules(
        AppModule.create(),
        ViewModelModule.create(),
      )
    }

    if (BuildConfig.DEBUG) {
      StrictMode.enableDefaults()
    }

    CoroutineScope(Dispatchers.IO).launch {
      pushTokenManager.onCreate()
    }
  }
}