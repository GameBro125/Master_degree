package com.example.mastersdegree

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.mastersdegree.data.remote.RetrofitStore
import com.example.mastersdegree.data.remote.api.SummaryApi
import com.example.mastersdegree.data.repository.DeviceSensorsRepositoryImpl
import com.example.mastersdegree.data.repository.SummaryRepositoryImpl
import com.example.mastersdegree.data.sensor.location.LocationSensorDataSource
import com.example.mastersdegree.data.sensor.magnetic.MagneticSensorDataSource
import com.example.mastersdegree.domain.usecase.ObserveLocationUseCase
import com.example.mastersdegree.domain.usecase.ObserveMagneticFieldUseCase
import com.example.mastersdegree.domain.usecase.SendSummaryDataUseCase
import com.example.mastersdegree.domain.usecase.StartLocationUpdatesUseCase
import com.example.mastersdegree.domain.usecase.StopLocationUpdatesUseCase
import com.example.mastersdegree.feature.summary.presentation.SummaryViewModel
import com.example.mastersdegree.feature.summary.ui.SummaryScreen
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

class MainActivity : ComponentActivity() {

    private val locationDataSource by lazy { LocationSensorDataSource(context = this) }
    private val magneticDataSource by lazy { MagneticSensorDataSource(context = this) }
    private val summaryApi by lazy { RetrofitStore.retrofit.create(SummaryApi::class.java) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sensorsRepository = DeviceSensorsRepositoryImpl(
            locationDataSource = locationDataSource,
            magneticDataSource = magneticDataSource
        )
        val summaryRepository = SummaryRepositoryImpl(summaryApi = summaryApi)

        val factory = SummaryViewModel.provideFactory(
            observeLocationUseCase = ObserveLocationUseCase(sensorsRepository),
            observeMagneticFieldUseCase = ObserveMagneticFieldUseCase(sensorsRepository),
            startLocationUpdatesUseCase = StartLocationUpdatesUseCase(sensorsRepository),
            stopLocationUpdatesUseCase = StopLocationUpdatesUseCase(sensorsRepository),
            sendSummaryDataUseCase = SendSummaryDataUseCase(summaryRepository)
        )

        val summaryViewModel = ViewModelProvider(this, factory)[SummaryViewModel::class.java]

        fun createNotificationChannel() {
            val channelId = "ForegroundServiceChannelId"
            val name = "Location foreground service"
            val descriptionText = "Channel for location notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        createNotificationChannel()

        setContent {
            MastersDegreeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SummaryScreen(viewModel = summaryViewModel)
                }
            }
        }
    }
}
