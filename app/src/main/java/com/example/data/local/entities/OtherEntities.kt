package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val discountPercent: Double = 0.0,
    val flatDiscount: Double = 0.0,
    val minOrderAmount: Double = 100.0,
    val maxDiscount: Double = 50.0,
    val description: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val customerId: String,
    val customerName: String,
    val restaurantId: String,
    val restaurantName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetUserId: String, // or role identifier
    val targetRole: String, // "CUSTOMER", "SELLER", "RIDER", "ADMIN"
    val title: String,
    val message: String,
    val orderId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val beneficiaryId: String,
    val beneficiaryName: String,
    val role: String, // "SELLER", "RIDER"
    val orderId: String? = null,
    val grossAmount: Double,
    val commissionDeducted: Double,
    val netPayable: Double,
    val status: String = "PENDING", // PENDING, PAID
    val referenceId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
)

@Entity(tableName = "admin_settings")
data class AdminSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val fixedDeliveryFee: Double = 30.0,
    val freeDeliveryThreshold: Double = 300.0,
    val isCodEnabled: Boolean = true,
    val platformCommissionPercent: Double = 10.0, // 10%
    val riderDeliveryPay: Double = 25.0, // ₹25 per delivery
    val serviceCity: String = "Ajara",
    val serviceState: String = "Maharashtra",
    val servicePincodes: String = "416505,416506",
    val serviceRadiusKm: Double = 12.0,
    val smsGatewayConfigured: Boolean = false,
    val paymentGatewayConfigured: Boolean = false,
    val thermalPrinterConfigured: Boolean = true
)
