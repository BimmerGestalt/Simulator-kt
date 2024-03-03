package io.bimmergestalt.headunit.bcl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.bimmergestalt.headunit.MainActivity
import io.bimmergestalt.headunit.R
import kotlinx.coroutines.flow.MutableStateFlow

class ServerService: Service() {
	companion object {
		val active = MutableStateFlow(false)
		val NOTIFICATION_CHANNEL_ID = "ServerNotification"
		val ONGOING_NOTIFICATION_ID = 20503

		const val TAG = "ServerService"
		const val ACTION_START = "io.bimmergestalt.headunit.bcl.ServerService.start"
		const val ACTION_STOP = "io.bimmergestalt.headunit.bcl.ServerService.stop"

		fun startService(context: Context) {
			try {
				Log.i(TAG, "Received request to start service")
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					// this is a clear signal of car connection, we can confidently startForeground
					context.startForegroundService(Intent(context, ServerService::class.java).setAction(ACTION_START))
				} else {
					context.startService(Intent(context, ServerService::class.java).setAction(ACTION_START))
				}
			} catch (e: IllegalStateException) {
				// Android Oreo strenuously objects to starting the service if the activity isn't visible
				// for example, when Android Studio tries to start the Activity with the screen off
			}
		}
		fun stopService(context: Context) {
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					// this is a clear signal of car connection, we can confidently startForeground
					context.startForegroundService(Intent(context, ServerService::class.java).setAction(ACTION_STOP))
				} else {
					context.startService(Intent(context, ServerService::class.java).setAction(ACTION_STOP))
				}
			} catch (e: IllegalStateException) {
				// Android Oreo strenuously objects to starting the service if the activity isn't visible
				// for example, when Android Studio tries to start the Activity with the screen off
			}
		}
	}

	private var foregroundNotification: Notification? = null
	private var serverHost: ServerHost? = null

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val action = intent?.action ?: ""
		Log.i(TAG, "Handling request to start service $action")
		if (action == ACTION_START) {
			active.value = true
			createNotificationChannel()
			startServiceNotification()

			startServer()
		} else if (action == ACTION_STOP) {
			stopSelf()
		}
		return START_STICKY
	}

	override fun onDestroy() {
		active.value = false
		stopServiceNotification()
		serverHost?.stopServer()
		serverHost = null
		super.onDestroy()
	}
	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				NOTIFICATION_CHANNEL_ID,
				getString(R.string.notification_channel_connection),
				NotificationManager.IMPORTANCE_MIN)

			val notificationManager = getSystemService(NotificationManager::class.java)
			notificationManager.createNotificationChannel(channel)
		}
	}

	private fun startServiceNotification() {
		val notifyIntent = Intent(applicationContext, io.bimmergestalt.headunit.MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
		}
		val shutdownIntent = Intent(applicationContext, ServerService::class.java).apply {
			action = ACTION_STOP
		}
		val shutdownAction = NotificationCompat.Action.Builder(null, getString(R.string.notification_shutdown),
			PendingIntent.getService(this, 40, shutdownIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
		).build()

		val foregroundNotificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setOngoing(true)
			.setContentTitle(getText(R.string.notification_title))
			.setContentText(getText(R.string.notification_description))
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.setContentIntent(PendingIntent.getActivity(applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
			.addAction(shutdownAction)

		val foregroundNotification = foregroundNotificationBuilder.build()
		Log.i(TAG, "Creating foreground notification")
		startForeground(ONGOING_NOTIFICATION_ID, foregroundNotification)
		this.foregroundNotification = foregroundNotification
	}

	private fun stopServiceNotification() {
		Log.i(TAG, "Hiding foreground notification")
		stopForeground(STOP_FOREGROUND_REMOVE)
		foregroundNotification = null
	}

	private fun startServer() {
		if (serverHost == null) {
			serverHost = ServerHost()
			serverHost?.startServer()
		}
	}
}