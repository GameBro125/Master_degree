package com.example.mastersdegree.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val REMOTE_SERVER_URL: String = "http://GameBro125.pythonanywhere.com"
const val LOCAL_HOST_URL:String = "http://192.168.31.81:8080"

object MagneticRetrofitStore {
    val body: Retrofit = Retrofit.Builder()
        .baseUrl(LOCAL_HOST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}