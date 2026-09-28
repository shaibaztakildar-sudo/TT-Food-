package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconName: String = "restaurant"
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val sellerId: String,
    val restaurantName: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val isVeg: Boolean = true,
    val isAvailable: Boolean = true,
    val imageUri: String? = null,
    val preparationTimeMinutes: Int = 20,
    val createdAt: Long = System.currentTimeMillis()
)
