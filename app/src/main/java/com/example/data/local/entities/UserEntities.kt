package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    SELLER,
    RIDER,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val role: UserRole,
    val name: String,
    val phone: String,
    val email: String,
    val passwordHash: String = "",
    val isApproved: Boolean = true, // Sellers and Riders require Admin approval
    val isActive: Boolean = true,
    val profileImageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_profiles")
data class SellerProfileEntity(
    @PrimaryKey
    val sellerId: String,
    val restaurantName: String,
    val ownerName: String,
    val phone: String,
    val email: String,
    val address: String,
    val city: String = "Ajara",
    val pincode: String = "416505",
    val panNumber: String = "",
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val upiId: String = "",
    val docProofUri: String? = null,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, SUSPENDED
    val isAcceptingOrders: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rider_profiles")
data class RiderProfileEntity(
    @PrimaryKey
    val riderId: String,
    val name: String,
    val phone: String,
    val email: String,
    val vehicleType: String = "Bike",
    val vehicleNumber: String = "",
    val licenseNumber: String = "",
    val licenseDocUri: String? = null,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, SUSPENDED
    val isAvailable: Boolean = true,
    val currentOrderId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: String,
    val label: String = "Home", // Home, Work, Other
    val recipientName: String,
    val phone: String,
    val addressLine: String,
    val landmark: String = "",
    val city: String = "Ajara",
    val pincode: String = "416505",
    val isDefault: Boolean = false
)
