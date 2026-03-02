package com.example.mastersdegree.data.remote

import REMOTE_SERVER_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object MagneticRetrofitStore {
    val body: Retrofit = Retrofit.Builder()
        .baseUrl(REMOTE_SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}