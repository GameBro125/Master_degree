package com.example.mastersdegree.feature.magnetic.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

@Composable
fun MagneticFieldNumbers(
    modifier: Modifier = Modifier,
    magneticField: MagneticFieldEntity, // TODO: Presentation
) {
    Column(modifier = modifier) {
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
}

@Preview
@Composable
private fun MagneticFieldNumbersPreview() {
    MastersDegreeTheme {
        MagneticFieldNumbers(
            magneticField = MagneticFieldEntity()
        )
    }
}