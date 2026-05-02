package com.vishal2376.snaptick

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishal2376.snaptick.presentation.common.CustomSnackBar
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.viewmodel.MainViewModel
import com.vishal2376.snaptick.presentation.navigation.AppNavigation
import com.vishal2376.snaptick.presentation.navigation.Routes
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.BackupManager
import com.vishal2376.snaptick.util.LocaleHelper
import com.vishal2376.snaptick.util.NotificationHelper
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.util.SplashThemeMirror
import com.vishal2376.snaptick.widget.presentation.EXTRA_NAVIGATE_TO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val mainViewModel by viewModels<MainViewModel>()

	@Inject
	lateinit var backupManager: BackupManager

	private lateinit var notificationHelper: NotificationHelper
	lateinit var backupPickerLauncher: ActivityResultLauncher<Intent>
	lateinit var restorePickerLauncher: ActivityResultLauncher<Intent>
	lateinit var calendarPermissionLauncher: ActivityResultLauncher<Array<String>>
	lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
	lateinit var icsPickerLauncher: ActivityResultLauncher<Intent>

	/** Navigation destination from widget intent */
	var widgetNavigateTo: String? = null
		private set

	/** Most recently picked `.ics` file URI; consumed by the import sheet. */
	var lastPickedIcsUri: Uri? = null

	/** When true, next .ics pick auto-imports all events instead of showing preview. */
	var pendingIcsAutoImport: Boolean = false

	/** Compose-observable notification-permission state. */
	val notificationGrantedState = mutableStateOf(false)

	override fun attachBaseContext(newBase: Context) {
		// Resources (and therefore Compose stringResource) are bound at activity
		// inflation time. The persisted language must be applied here so the
		// initial composition uses the right locale; later changes call
		// recreate() so this runs again with the new value.
		val lang = runCatching {
			runBlocking { SettingsStore(newBase).languageKey.first() }
		}.getOrDefault("en")
		super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(SplashThemeMirror.startingThemeRes(this))
		installSplashScreen()

		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		handleWidgetIntent(intent)

		notificationHelper = NotificationHelper(applicationContext)
		notificationHelper.createNotificationChannel()

		backupPickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri: Uri ->
						mainViewModel.onAction(
							MainAction.CreateBackup(uri, mainViewModel.backupData.value)
						)
					}
				}
			}

		restorePickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri: Uri ->
						mainViewModel.onAction(MainAction.LoadBackup(uri))
					}
				}
			}

		calendarPermissionLauncher =
			registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
				val granted = grants.values.all { it }
				if (granted) {
					mainViewModel.onAction(MainAction.RefreshWritableCalendars)
					mainViewModel.onAction(MainAction.SetCalendarSyncEnabled(true))
				}
			}

		notificationPermissionLauncher =
			registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
				notificationGrantedState.value = granted
			}

		notificationGrantedState.value =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
						PackageManager.PERMISSION_GRANTED
			} else true

		icsPickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri: Uri ->
						lastPickedIcsUri = uri
						if (pendingIcsAutoImport) {
							pendingIcsAutoImport = false
							mainViewModel.onAction(MainAction.ImportIcsFile(uri))
						} else {
							mainViewModel.onAction(MainAction.ParseIcsFile(uri))
						}
					}
				}
			}

		setContent {
			val mainState by mainViewModel.state.collectAsStateWithLifecycle()
			SnaptickTheme(
				theme = mainState.theme,
				dynamicColor = mainState.dynamicTheme
			) {
				AppNavigation(
					mainViewModel = mainViewModel,
					startDestination = widgetNavigateTo
				)
				CustomSnackBar()
			}
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		handleWidgetIntent(intent)
	}

	private fun handleWidgetIntent(intent: Intent?) {
		val raw = intent?.getStringExtra(EXTRA_NAVIGATE_TO)
		// Allow widget routes (top-level only) and the Pomodoro deep-link
		// (parameterized: "PomodoroScreen/<id>") used by the foreground-service
		// notification. Anything else falls through to the default start
		// destination. Without this allowlist a third-party app could craft an
		// Intent(MAIN).putExtra("navigate_to", ...) to open a screen we never
		// intended to expose externally.
		widgetNavigateTo = when {
			raw == null -> null
			raw in WIDGET_ALLOWED_ROUTES -> raw
			raw.startsWith(Routes.PomodoroScreen.name + "/") -> raw
			else -> null
		}
	}

	companion object {
		private val WIDGET_ALLOWED_ROUTES: Set<String> = setOf(
			Routes.AddTaskScreen.name,
		)
	}
}
