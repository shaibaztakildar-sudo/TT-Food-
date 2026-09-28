package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val display: String) {
    ORDER_PLACED("Order Placed"),
    SELLER_ACCEPTED("Seller Accepted"),
    PREPARING("Preparing"),
    READY_FOR_PICKUP("Ready for Pickup"),
    RIDER_ASSIGNED("Rider Assigned"),
    PICKED_UP("Picked Up"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

enum class PaymentMethod(val display: String) {
    ONLINE("Online Payment (UPI/Card)"),
    COD("Cash on Delivery (COD)")
}

enum class PaymentStatus(val display: String) {
    PAID("Paid"),
    PENDING("Pending"),
    COD_DUE("COD Due"),
    REFUNDED("Refunded"),
    FAILED("Failed")
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val sellerId: String,
    val restaurantName: String,
    val riderId: String? = null,
    val riderName: String? = null,
    val riderPhone: String? = null,
    val deliveryAddress: String,
    val deliveryCity: String = "Ajara",
    val deliveryPincode: String = "416505",
    val subtotal: Double,
    val deliveryCharge: Double,
    val discount: Double = 0.0,
    val totalAmount: Double,
    val couponCode: String? = null,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val transactionId: String? = null,
    val orderStatus: OrderStatus = OrderStatus.ORDER_PLACED,
    val cancelReason: String? = null,
    val customerInstructions: String? = null,
    val deliveryOtp: String = "",
    val isOtpVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val isVeg: Boolean = true
)
