package com.dgsd.solis.common.fragment

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

fun DialogFragment.showFrom(parentFragment: Fragment) {
    show(parentFragment.childFragmentManager, generateTag())
}