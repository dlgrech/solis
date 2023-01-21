package com.dgsd.solis.common.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class IntentFactory(
    private val context: Context,
) {

    fun createAppNotificationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }

    fun createAppDetailsSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setData(Uri.fromParts("package", context.packageName, null))
    }
}