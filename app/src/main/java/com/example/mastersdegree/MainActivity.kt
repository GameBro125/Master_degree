package com.example.mastersdegree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.mastersdegree.data.remote.MagneticRetrofitStore
import com.example.mastersdegree.data.remote.api.DataService
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.feature.summaryUserInfo.location.viewModel.LocationViewModel
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.datastore.MagneticSensorDataStore
import com.example.mastersdegree.feature.summaryUserInfo.view.MagneticFieldInfoScreen
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val locationDataStore by lazy { LocationDataStore(
        context = this
    ) }
    private val magneticSensorDataStore by lazy { MagneticSensorDataStore(context = this) }
    private val apiDataService by lazy { MagneticRetrofitStore.body.create(DataService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Создание фабрики с передачей зависимостей
        val factory = MainViewModel.provideFactory(
            locationDataStore,
            magneticSensorDataStore,
            apiDataService
        )

        // Создание ViewModel через ViewModelProvider
        val mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val locationViewModel: LocationViewModel by viewModels  {
            LocationViewModel.provideFactory(
                locationDataStore = locationDataStore
            )
        }

        setContent {
            MastersDegreeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MagneticFieldInfoScreen(
                        mainViewModel,
                        locationViewModel = locationViewModel
                    )
                }
            }
        }
    }
}

