package com.example.mastersdegree.domain.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManagers {
    val body: Retrofit = Retrofit.Builder()
        .baseUrl("http://38.180.93.89:8080") // Укажите IP-адрес сервера
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}