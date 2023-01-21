package com.dgsd.solis.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dgsd.solis.R
import com.dgsd.solis.common.fragment.navigateBack
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppSettingsFragment : Fragment(R.layout.frag_app_settings) {

    private val viewModel: AppSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.requireViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            navigateBack()
        }
    }

    companion object {

        fun newInstance(): AppSettingsFragment {
            return AppSettingsFragment()
        }
    }
}