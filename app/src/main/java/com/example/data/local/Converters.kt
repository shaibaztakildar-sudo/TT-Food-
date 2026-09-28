package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = try {
        UserRole.valueOf(value)
    } catch (_: Exception) {
        UserRole.CUSTOMER
    }

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = try {
        OrderStatus.valueOf(value)
    } catch (_: Exception) {
        OrderStatus.ORDER_PLACED
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try {
        PaymentMethod.valueOf(value)
    } catch (_: Exception) {
        PaymentMethod.COD
    }

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = try {
        PaymentStatus.valueOf(value)
    } catch (_: Exception) {
        PaymentStatus.PENDING
    }
}
