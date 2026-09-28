package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.RiderProfileEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun findUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role ORDER BY createdAt DESC")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Seller Profiles
    @Query("SELECT * FROM seller_profiles ORDER BY createdAt DESC")
    fun getAllSellerProfiles(): Flow<List<SellerProfileEntity>>

    @Query("SELECT * FROM seller_profiles WHERE sellerId = :sellerId LIMIT 1")
    fun getSellerProfile(sellerId: String): Flow<SellerProfileEntity?>

    @Query("SELECT * FROM seller_profiles WHERE sellerId = :sellerId LIMIT 1")
    suspend fun findSellerProfile(sellerId: String): SellerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSellerProfile(profile: SellerProfileEntity)

    @Update
    suspend fun updateSellerProfile(profile: SellerProfileEntity)

    // Rider Profiles
    @Query("SELECT * FROM rider_profiles ORDER BY createdAt DESC")
    fun getAllRiderProfiles(): Flow<List<RiderProfileEntity>>

    @Query("SELECT * FROM rider_profiles WHERE riderId = :riderId LIMIT 1")
    fun getRiderProfile(riderId: String): Flow<RiderProfileEntity?>

    @Query("SELECT * FROM rider_profiles WHERE riderId = :riderId LIMIT 1")
    suspend fun findRiderProfile(riderId: String): RiderProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiderProfile(profile: RiderProfileEntity)

    @Update
    suspend fun updateRiderProfile(profile: RiderProfileEntity)
}
