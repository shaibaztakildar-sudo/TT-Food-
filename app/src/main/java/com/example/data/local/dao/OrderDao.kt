package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getOrdersByCustomer(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getOrdersBySeller(sellerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE riderId = :riderId ORDER BY createdAt DESC")
    fun getOrdersByRider(riderId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderStatus = 'READY_FOR_PICKUP' AND riderId IS NULL ORDER BY createdAt ASC")
    fun getAvailableOrdersForRiders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun findOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, updatedAt: Long = System.currentTimeMillis())

    // Order items
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun findOrderItems(orderId: String): List<OrderItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)
}
