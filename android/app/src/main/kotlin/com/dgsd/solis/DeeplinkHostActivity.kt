package com.dgsd.solis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import com.dgsd.solis.accounts.AccountDetailsBottomSheetFragment
import com.dgsd.solis.deeplink.DeeplinkManager
import com.dgsd.solis.deeplink.DeeplinkTarget
import com.dgsd.solis.transactions.TransactionDetailsBottomSheetFragment
import org.koin.android.ext.android.inject

class DeeplinkHostActivity : AppCompatActivity() {

  private val deeplinkManager by inject<DeeplinkManager>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val targetFragment =
      when (val deeplinkTarget = deeplinkManager.extractDeeplinkTarget(intent)) {
        is DeeplinkTarget.Account -> {
          AccountDetailsBottomSheetFragment.newInstance(deeplinkTarget.accountKey)
        }
        is DeeplinkTarget.Transaction -> {
          TransactionDetailsBottomSheetFragment.newInstance(deeplinkTarget.signature)
        }
        null -> null
      }

    if (targetFragment != null) {
      showDialogFragment(targetFragment)
    }
  }

  private fun showDialogFragment(dialogFragment: DialogFragment) {
    supportFragmentManager.registerFragmentLifecycleCallbacks(
      object : FragmentLifecycleCallbacks() {
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
          if (f == dialogFragment) {
            fm.unregisterFragmentLifecycleCallbacks(this)
            finish()
          }
        }
      },
      false
    )

    dialogFragment.show(supportFragmentManager, null)
  }
}