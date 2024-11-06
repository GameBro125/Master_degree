package com.example.mastersdegree.domain.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManagers {
    val body: Retrofit = Retrofit.Builder()
        .baseUrl("http://GameBro125.pythonanywhere.com") // Укажите IP-адрес сервера
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}