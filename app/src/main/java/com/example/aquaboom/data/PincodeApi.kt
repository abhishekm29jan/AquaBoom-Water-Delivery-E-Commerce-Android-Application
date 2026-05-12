package com.example.aquaboom.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PincodeApi {

    @GET("pincode/{pincode}")
    suspend fun getPincodeDetails(
        @Path("pincode") pincode: String
    ): List<PincodeResponse>

}