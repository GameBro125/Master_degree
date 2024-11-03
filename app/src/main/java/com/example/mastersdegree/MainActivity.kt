package com.example.mastersdegree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.domain.MagneticField
import com.example.mastersdegree.domain.MagneticSensorManager
import com.example.mastersdegree.domain.remote.ApiService
import com.example.mastersdegree.domain.remote.MagneticFieldViewModel
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val magneticSensorManager = MagneticSensorManager(context = this)

        enableEdgeToEdge()
        setContent {
            MastersDegreeTheme {
                Surface (Modifier.fillMaxSize().padding(16.dp))
                {
                    val magneticField by remember { magneticSensorManager.magneticField }
                    MagneticFieldInfo(
                        magneticField = magneticField,
                        modifier = Modifier
                    )
                }
            }
        }

    }
}

@Composable
fun MagneticFieldInfo(
    modifier: Modifier,
    magneticField: MagneticField
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth(0.4F)
                .fillMaxHeight(0.9F)
        )
        {
            Text(
                text = "X: " + magneticField.x,
                fontSize = 24.sp,
                fontWeight = FontWeight.W300
            )
            Text(
                text = "Y: " + magneticField.y,
                fontSize = 24.sp,
                fontWeight = FontWeight.W300
            )
            Text(
                text = "Z: " + magneticField.z,
                fontSize = 24.sp,
                fontWeight = FontWeight.W300
            )
            Text(
                text = "|B|: " + magneticField.getVector(),
                fontSize = 24.sp,
                fontWeight = FontWeight.W300
            )
        }
        StateButton(modifier = Modifier, magneticField = magneticField)
    }
}

@Composable
fun StateButton(modifier: Modifier,     magneticField: MagneticField
) {
    Row() {
        Button(
            onClick = { println("херня включена") },
        ) {
            Text("Включить")
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = {
                val viewModel: MagneticFieldViewModel = MagneticFieldViewModel()
                viewModel.sendMagneticFieldData(magneticField)
            },
        ) {
            Text("Выключить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MastersDegreeTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MagneticFieldInfo(
                magneticField = MagneticField(),
                modifier = Modifier
            )
        }
    }
}