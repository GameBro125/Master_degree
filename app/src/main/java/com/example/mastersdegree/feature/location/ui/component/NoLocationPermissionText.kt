package com.example.mastersdegree.feature.location.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.R
import com.example.mastersdegree.ui.util.modifier.clickable

@Composable
fun NoLocationPermissionText(
    modifier: Modifier = Modifier,
    angryMode: Boolean = false,
    superAngryMode: Boolean = false,
    requestPermission: () -> Unit
) {
    val emoji =
        if (superAngryMode) "💀"
        else
            if (angryMode) "👿"
            else "😇"

    Text(
        modifier = modifier
            .padding(horizontal = 32.dp)
            .clickable(showRipple = false) { requestPermission() },
        text = stringResource(R.string.locationPermissionAsk) + emoji,
        fontSize = 22.sp,
        textAlign = TextAlign.Center
    )
}