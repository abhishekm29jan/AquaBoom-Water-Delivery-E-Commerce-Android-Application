package com.example.aquaboom.data

data class PincodeResponse(
    val PostOffice: List<PostOffice>
)

data class PostOffice(
    val District: String,
    val State: String
)
