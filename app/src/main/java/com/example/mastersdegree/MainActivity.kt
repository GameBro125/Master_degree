package com.example.mastersdegree

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.summaryUserInfo.location.ui.SendButton
import com.example.mastersdegree.feature.summaryUserInfo.location.ui.LocationSummaryFields
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.datastore.MagneticSensorDataStore
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.entity.MagneticFieldEntity
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.ui.component.MagneticFieldSummaryFields
import com.example.mastersdegree.feature.summaryUserInfo.view.MagneticFieldInfoContent
import com.example.mastersdegree.feature.summaryUserInfo.view.MagneticFieldInfoScreen
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

class MainActivity : ComponentActivity() {

    val locationDataStore by lazy { LocationDataStore(activity = this) }
    val magneticSensorDataStore by lazy { MagneticSensorDataStore(context = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MastersDegreeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MagneticFieldInfoScreen()
                }
            }
        }
    }
}
