package com.example.mastersdegree

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.domain.magneticField.MagneticField
import com.example.mastersdegree.domain.remote.VectorResponse
import com.example.mastersdegree.domain.remote.createApiService
import com.example.mastersdegree.feature.location.LocationManager
import com.example.mastersdegree.feature.magnetic.MagneticSensorManager
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(
    private val locationManager: LocationManager,
    private val magneticSensorManager: MagneticSensorManager
) : ViewModel() {

    // TODO Пренести в конструктор
    private val apiService by lazy { createApiService() }

    // TODO убрать context
    fun sendMagneticFieldData(magneticField: MagneticField, context: Context) {
        viewModelScope.launch {
            try {
                val response: Response<VectorResponse> = apiService.sendMagneticField(magneticField)
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    val vector = response.body()?.vector
                    Toast.makeText(context, context.getString(R.string.data_sent), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context,
                        context.getString(R.string.sending_error), Toast.LENGTH_LONG).show()

                }
            } catch (e: Exception) {
                println("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        val LOCATION_MANAGER_KEY = object : CreationExtras.Key<LocationManager> {}
        val MAGNETIC_SENSOR_MANAGER_KEY = object : CreationExtras.Key<MagneticSensorManager> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val locationManager = this[LOCATION_MANAGER_KEY] as LocationManager
                val magneticSensorManager = this[MAGNETIC_SENSOR_MANAGER_KEY] as MagneticSensorManager
                MainViewModel(locationManager, magneticSensorManager)
            }
        }
    }
}
