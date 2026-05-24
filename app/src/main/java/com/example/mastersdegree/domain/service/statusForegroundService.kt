package com.example.mastersdegree.domain.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mastersdegree.R
import com.example.mastersdegree.data.remote.RetrofitStore
import com.example.mastersdegree.data.remote.api.SummaryApi
import com.example.mastersdegree.data.repository.DeviceSensorsRepositoryImpl
import com.example.mastersdegree.data.repository.SummaryRepositoryImpl
import com.example.mastersdegree.data.sensor.location.LocationSensorDataSource
import com.example.mastersdegree.data.sensor.magnetic.MagneticSensorDataSource
import com.example.mastersdegree.domain.model.SummaryPayload
import com.example.mastersdegree.domain.usecase.ObserveLocationUseCase
import com.example.mastersdegree.domain.usecase.ObserveMagneticFieldUseCase
import com.example.mastersdegree.domain.usecase.SendSummaryDataUseCase
import com.example.mastersdegree.domain.usecase.StartLocationUpdatesUseCase
import com.example.mastersdegree.domain.usecase.StopLocationUpdatesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StatusForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var senderJob: Job? = null

    private lateinit var observeLocationUseCase: ObserveLocationUseCase
    private lateinit var observeMagneticFieldUseCase: ObserveMagneticFieldUseCase
    private lateinit var startLocationUpdatesUseCase: StartLocationUpdatesUseCase
    private lateinit var stopLocationUpdatesUseCase: StopLocationUpdatesUseCase
    private lateinit var sendSummaryDataUseCase: SendSummaryDataUseCase

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initializeDependencies()
        startForegroundInternal()
        startLocationUpdatesUseCase()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (senderJob?.isActive == true) return START_STICKY

        senderJob = serviceScope.launch {
            while (isActive) {
                sendCurrentSummary()
                delay(SEND_INTERVAL_MS)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        senderJob?.cancel()
        stopLocationUpdatesUseCase()
        serviceScope.cancel()
        super.onDestroy()
    }

    private suspend fun sendCurrentSummary() {
        val location = observeLocationUseCase().first()
        if (location == null) return

        val magnetic = observeMagneticFieldUseCase().first()
        val payload = SummaryPayload(location = location, magneticField = magnetic)
        sendSummaryDataUseCase(payload)
    }

    private fun initializeDependencies() {
        val locationDataSource = LocationSensorDataSource(this)
        val magneticDataSource = MagneticSensorDataSource(this)
        val sensorsRepository = DeviceSensorsRepositoryImpl(locationDataSource, magneticDataSource)
        val summaryApi = RetrofitStore.retrofit.create(SummaryApi::class.java)
        val summaryRepository = SummaryRepositoryImpl(summaryApi)

        observeLocationUseCase = ObserveLocationUseCase(sensorsRepository)
        observeMagneticFieldUseCase = ObserveMagneticFieldUseCase(sensorsRepository)
        startLocationUpdatesUseCase = StartLocationUpdatesUseCase(sensorsRepository)
        stopLocationUpdatesUseCase = StopLocationUpdatesUseCase(sensorsRepository)
        sendSummaryDataUseCase = SendSummaryDataUseCase(summaryRepository)
    }

    private fun startForegroundInternal() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.foreground_service_title))
            .setContentText(getString(R.string.foreground_service_text))
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ForegroundServiceChannelId"
        private const val SEND_INTERVAL_MS = 15_000L
    }
}
