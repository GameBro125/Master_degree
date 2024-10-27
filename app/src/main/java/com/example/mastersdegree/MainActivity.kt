package com.example.mastersdegree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.domain.MagneticField
import com.example.mastersdegree.domain.MagneticSensorManager
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val magneticSensorManager = MagneticSensorManager(context = this)

        enableEdgeToEdge()
        setContent {
            MastersDegreeTheme {
                Surface(Modifier.fillMaxSize()) {
                    val magneticField by remember { magneticSensorManager.magneticField }

                    MagneticFieldInfo(
                        magneticField = magneticField
                    )
                }
            }
        }
    }
}

@Composable
fun MagneticFieldInfo(
    modifier: Modifier = Modifier,
    magneticField: MagneticField
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "X: ${magneticField.x}",
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Y: ${magneticField.y}",
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Z: ${magneticField.z}",
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MastersDegreeTheme {
        MagneticFieldInfo(
            magneticField = MagneticField()
        )
    }
}