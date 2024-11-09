package com.example.mastersdegree.feature.magnetic.shared.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MagneticRetrofitStore {
    val body: Retrofit = Retrofit.Builder()
        .baseUrl("http://GameBro125.pythonanywhere.com") // Укажите IP-адрес сервера
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}