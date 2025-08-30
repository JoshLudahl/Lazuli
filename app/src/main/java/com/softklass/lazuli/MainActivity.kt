package com.softklass.lazuli

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.softklass.lazuli.ui.navigation.AppNavHost
import com.softklass.lazuli.ui.theme.AppTheme
import com.softklass.lazuli.ui.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestNotifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var aut: Task<AppUpdateInfo>
    private val updateType = AppUpdateType.FLEXIBLE
    private lateinit var themeManager: ThemeManager

    val listener =
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {

                Log.i("MainActivity", "Update has been downloaded.")
                Toast
                    .makeText(
                        this,
                        "Update Completed. Restarting application.",
                        Toast.LENGTH_SHORT,
                    ).show()
                lifecycleScope.launch {
                    appUpdateManager.completeUpdate()
                }
            }
        }

    @androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)

        themeManager = ThemeManager.getInstance(this)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        aut = appUpdateManager.appUpdateInfo
        checkIsUpdateAvailable()

        enableEdgeToEdge()
        ensureNotificationPermission()
        setContent {
            AppTheme(
                darkTheme = themeManager.isDarkTheme(),
                dynamicColor = themeManager.dynamicColor.collectAsState().value,
            ) {
                Surface(
                    modifier =
                        Modifier
                             .windowInsetsPadding(WindowInsets.statusBars)
                            .fillMaxSize(),
                ) {
                    val itemId = intent?.getIntExtra(com.softklass.lazuli.data.device.ReminderScheduler.EXTRA_ITEM_ID, -1).takeIf { it != null && it > 0 }
                    val windowSizeClass = calculateWindowSizeClass(this)
                    AppNavHost(initialItemId = itemId, windowSizeClass = windowSizeClass)
                }
            }
        }
    }

    private fun ensureNotificationPermission() {
        // Request POST_NOTIFICATIONS on Android 13+ if not yet granted
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val nm =
                androidx.core.app.NotificationManagerCompat
                    .from(this)
            val granted = nm.areNotificationsEnabled()
            if (!granted) {
                requestNotifPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkIsUpdateAvailable() {
        val activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(),
            ) { result ->
                if (result.resultCode != RESULT_OK) {
                    Log.i("MainActivity", "The Update has failed.")
                }
            }

        aut.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                Log.i("MainActivity", "Update is available.")

                appUpdateManager.registerListener(listener)
                Log.i("MainActivity", "Starting Update.")
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build(),
                )
            } else {
                Log.i("MainActivity", "No Update Available.")
            }
        }
    }
}
