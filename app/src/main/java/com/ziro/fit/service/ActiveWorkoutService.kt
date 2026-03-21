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
import com.ziro.fit.model.SetStatus
import com.ziro.fit.model.WorkoutSetUi
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
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_LOG_SET -> logSetFromNotification()
            ACTION_TOGGGLE_TIMER -> toggleTimer()
            ACTION_END_WORKOUT -> endWorkoutFromNotification()
        }

        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        val state = workoutStateManager.state.value
        val notification = buildNotification(
            title = state.activeSession?.title ?: "Active Workout",
            content = formatSeconds(state.elapsedSeconds),
            isResting = state.isRestActive
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                notification, 
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) 
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH 
                else 
                    0
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
                    stopSelf()
                    return@collect
                }

                val timeString = formatSeconds(state.elapsedSeconds)
                var title = state.activeSession.title
                var content = "Duration: $timeString"

                if (state.isRestActive) {
                    title = "Resting..."
                    content = "Time remaining: ${state.restSecondsRemaining}s"
                    wasResting = true
                } else if (wasResting) {
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
            action = ACTION_OPEN_WORKOUT
        }
        val pendingIntent = PendingIntent.getActivity(
            this, REQUEST_CODE_CONTENT, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        addNotificationActions(builder, isResting)

        if (isResting) {
            builder.setColor(getColor(android.R.color.holo_blue_light))
        }

        return builder.build()
    }

    private fun addNotificationActions(builder: NotificationCompat.Builder, isResting: Boolean) {
        val logSetIntent = Intent(this, WorkoutActionReceiver::class.java).apply {
            action = ACTION_LOG_SET
        }
        val logSetPendingIntent = PendingIntent.getBroadcast(
            this, REQUEST_CODE_LOG_SET, logSetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            R.drawable.ic_action_log_set,
            getString(R.string.notification_action_log_set),
            logSetPendingIntent
        )

        val toggleIntent = Intent(this, WorkoutActionReceiver::class.java).apply {
            action = ACTION_TOGGGLE_TIMER
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            this, REQUEST_CODE_TOGGGLE, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (isResting) {
            builder.addAction(
                R.drawable.ic_action_resume,
                getString(R.string.notification_action_resume),
                togglePendingIntent
            )
        } else {
            builder.addAction(
                R.drawable.ic_action_pause,
                getString(R.string.notification_action_pause),
                togglePendingIntent
            )
        }

        val endIntent = Intent(this, WorkoutActionReceiver::class.java).apply {
            action = ACTION_END_WORKOUT
        }
        val endPendingIntent = PendingIntent.getBroadcast(
            this, REQUEST_CODE_END, endIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            R.drawable.ic_action_end,
            getString(R.string.notification_action_end_workout),
            endPendingIntent
        )
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
            
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            manager.createNotificationChannel(serviceChannel)

            val alertChannel = NotificationChannel(
                REST_CHANNEL_ID,
                getString(R.string.rest_timer_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.rest_timer_channel_description)
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

    private fun logSetFromNotification() {
        serviceScope.launch {
            val session = workoutStateManager.state.value.activeSession ?: return@launch
            
            val targetExercise = session.exercises.firstOrNull { ex ->
                ex.sets.any { !it.isCompleted }
            } ?: session.exercises.firstOrNull()

            if (targetExercise != null) {
                val targetSet = targetExercise.sets.firstOrNull { !it.isCompleted }
                    ?: targetExercise.sets.lastOrNull()

                if (targetSet != null) {
                    val weight = targetSet.weight.ifEmpty { "0" }
                    val reps = targetSet.reps.ifEmpty { "0" }
                    
                    val newSet = WorkoutSetUi(
                        logId = java.util.UUID.randomUUID().toString(),
                        setNumber = targetExercise.sets.size + 1,
                        weight = weight,
                        reps = reps,
                        isCompleted = true,
                        order = targetExercise.sets.maxOfOrNull { it.order }?.plus(1) ?: 0,
                        rpe = null,
                        status = SetStatus.NORMAL
                    )
                    
                    val updatedExercises = session.exercises.map { ex ->
                        if (ex.exerciseId == targetExercise.exerciseId) {
                            val updatedSets = ex.sets + newSet
                            ex.copy(sets = updatedSets)
                        } else ex
                    }
                    workoutStateManager.updateSession(session.copy(exercises = updatedExercises))
                }
            }
        }
    }

    private fun toggleTimer() {
        val state = workoutStateManager.state.value
        if (state.isRestActive) {
            workoutStateManager.stopRestTimer()
        } else {
            workoutStateManager.startRestTimer(60)
        }
    }

    private fun endWorkoutFromNotification() {
        serviceScope.launch {
            workoutStateManager.updateSession(null)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_STOP_SERVICE = "com.ziro.fit.STOP_WORKOUT_SERVICE"
        const val ACTION_LOG_SET = "com.ziro.fit.ACTION_LOG_SET"
        const val ACTION_TOGGGLE_TIMER = "com.ziro.fit.ACTION_TOGGGLE_TIMER"
        const val ACTION_END_WORKOUT = "com.ziro.fit.ACTION_END_WORKOUT"
        const val ACTION_OPEN_WORKOUT = "com.ziro.fit.OPEN_LIVE_WORKOUT"

        private const val REQUEST_CODE_CONTENT = 0
        private const val REQUEST_CODE_LOG_SET = 1
        private const val REQUEST_CODE_TOGGGLE = 2
        private const val REQUEST_CODE_END = 3
    }
}

class WorkoutActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, ActiveWorkoutService::class.java).apply {
            action = intent.action
        }
        context.startService(serviceIntent)
    }
}
