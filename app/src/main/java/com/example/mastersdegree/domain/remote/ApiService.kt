package com.example.mastersdegree.domain.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastersdegree.domain.MagneticField
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    fun sendMagneticFieldData(magneticField: MagneticField) {
        viewModelScope.launch {
            try {
                val response: Response<VectorResponse> = apiService.sendMagneticField(magneticField)
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    val vector = response.body()?.vector
                    println("Успешно отправлено, полученный вектор: $vector")
                } else {
                    println("Ошибка при отправке данных")
                }
            } catch (e: Exception) {
                println("Ошибка: ${e.localizedMessage}")
            }
        }
    }
}
