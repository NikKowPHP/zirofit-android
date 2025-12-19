package com.ziro.fit.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ziro.fit.MainActivity
import com.ziro.fit.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ActiveWorkoutService : Service() {

    @Inject
    lateinit var workoutStateManager: WorkoutStateManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "active_workout_channel"
    private val REST_CHANNEL_ID = "rest_timer_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeWorkoutState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = buildNotification(
            title = "Active Workout",
            content = "00:00",
            isResting = false
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                notification, 
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) 
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH 
                else 
                    0 // Fallback to default/manifest for older versions
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun observeWorkoutState() {
        serviceScope.launch {
            var wasResting = false
            
            workoutStateManager.state.collect { state ->
                if (state.activeSession == null) {
                    stopSelf() // Stop service if no session
                    return@collect
                }

                val timeString = formatSeconds(state.elapsedSeconds)
                var title = state.activeSession.title
                var content = "Duration: $timeString"
                var showChronometer = true

                if (state.isRestActive) {
                    title = "Resting..."
                    content = "Time remaining: ${state.restSecondsRemaining}s"
                    showChronometer = false
                    wasResting = true
                } else if (wasResting) {
                    // Rest just finished
                    notifyRestFinished()
                    wasResting = false
                }

                val notification = buildNotification(title, content, state.isRestActive)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun buildNotification(title: String, content: String, isResting: Boolean): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = "OPEN_LIVE_WORKOUT"
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action to Finish (sends broadcast)
        // Note: Implementing functionality via BroadcastReceiver
        /* 
        val finishIntent = Intent(this, WorkoutActionReceiver::class.java).apply { action = ACTION_FINISH }
        val finishPendingIntent = PendingIntent.getBroadcast(this, 1, finishIntent, PendingIntent.FLAG_IMMUTABLE) 
        */

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this resource exists or use a generic one
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true) // Prevent sound on every update
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (isResting) {
            builder.setColor(getColor(android.R.color.holo_blue_light))
        }

        return builder.build()
    }

    private fun notifyRestFinished() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, REST_CHANNEL_ID)
            .setContentTitle("Rest Finished!")
            .setContentText("Get back to your set.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            
            // 1. Service Channel (Silent updates)
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Active Workout",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active workout timer"
                setShowBadge(false)
            }
            manager.createNotificationChannel(serviceChannel)

            // 2. Alert Channel (Rest Timer)
            val alertChannel = NotificationChannel(
                REST_CHANNEL_ID,
                "Workout Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for rest timer completion"
                enableVibration(true)
                enableLights(true)
            }
            manager.createNotificationChannel(alertChannel)
        }
    }

    private fun formatSeconds(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_STOP_SERVICE = "STOP_WORKOUT_SERVICE"
        const val ACTION_FINISH = "FINISH_WORKOUT"
    }
}

// Receiver for notification actions (Future expansion)
class WorkoutActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle pause/finish actions here by communicating with Manager/Repository
    }
}
