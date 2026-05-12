package com.example.aquaboom.data

import com.example.aquaboom.model.Address

object AddressManager {

    val addresses = mutableListOf<Address>()

    fun updateAddress(index: Int, updatedAddress: Address) {
        addresses[index] = updatedAddress
    }

    fun deleteAddress(index: Int) {
        addresses.removeAt(index)
    }

}