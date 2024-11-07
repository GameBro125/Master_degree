package com.example.mastersdegree.domain.remote

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastersdegree.R
import com.example.mastersdegree.domain.magneticField.MagneticField
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("magnetic-field")
    suspend fun sendMagneticField(@Body magneticField: MagneticField): Response<VectorResponse>
}

class VectorResponse(val vector: Float)

fun createApiService(): ApiService {
    val retrofit = RetrofitManagers()
    return retrofit.body.create(ApiService::class.java)
}

class MagneticFieldViewModel : ViewModel() {
    private val apiService = createApiService()

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
}
