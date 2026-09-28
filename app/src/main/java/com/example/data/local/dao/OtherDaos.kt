package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AdminSettingsEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.PayoutEntity
import com.example.data.local.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons ORDER BY id DESC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE isActive = 1")
    fun getActiveCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1 LIMIT 1")
    suspend fun findActiveCouponByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Update
    suspend fun updateCoupon(coupon: CouponEntity)

    @Query("DELETE FROM coupons WHERE id = :id")
    suspend fun deleteCoupon(id: Long)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getReviewsForRestaurant(restaurantId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE targetUserId = :userId OR targetRole = :role ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String, role: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE targetUserId = :userId OR targetRole = :role")
    suspend fun markAllAsRead(userId: String, role: String)
}

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payouts ORDER BY createdAt DESC")
    fun getAllPayouts(): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts WHERE beneficiaryId = :beneficiaryId ORDER BY createdAt DESC")
    fun getPayoutsForBeneficiary(beneficiaryId: String): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)

    @Update
    suspend fun updatePayout(payout: PayoutEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AdminSettingsEntity?>

    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AdminSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AdminSettingsEntity)
}
