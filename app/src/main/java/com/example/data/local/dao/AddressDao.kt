package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE customerId = :customerId ORDER BY isDefault DESC, id DESC")
    fun getAddressesByCustomer(customerId: String): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE id = :id LIMIT 1")
    suspend fun getAddressById(id: Long): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: Long)

    @Query("UPDATE addresses SET isDefault = 0 WHERE customerId = :customerId")
    suspend fun clearDefaultAddress(customerId: String)
}
