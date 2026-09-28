package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.CouponDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.PayoutDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.ReviewDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AdminSettingsEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.PayoutEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.RiderProfileEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SellerProfileEntity::class,
        RiderProfileEntity::class,
        AddressEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CouponEntity::class,
        ReviewEntity::class,
        NotificationEntity::class,
        PayoutEntity::class,
        AdminSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun couponDao(): CouponDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao
    abstract fun payoutDao(): PayoutDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tt_food_delivery_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Initialize ONLY system settings and the secure master Admin account.
                // NO fake products, restaurants, customers, riders, orders, or prices!
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateSystemDefaults(database)
                    }
                }
            }
        }

        private suspend fun populateSystemDefaults(database: AppDatabase) {
            val settingsDao = database.settingsDao()
            val existingSettings = settingsDao.getSettingsDirect()
            if (existingSettings == null) {
                settingsDao.insertSettings(
                    AdminSettingsEntity(
                        id = 1,
                        fixedDeliveryFee = 30.0,
                        freeDeliveryThreshold = 300.0,
                        isCodEnabled = true,
                        platformCommissionPercent = 10.0,
                        riderDeliveryPay = 25.0,
                        serviceCity = "Ajara",
                        serviceState = "Maharashtra",
                        servicePincodes = "416505,416506",
                        serviceRadiusKm = 12.0,
                        smsGatewayConfigured = false,
                        paymentGatewayConfigured = false,
                        thermalPrinterConfigured = true
                    )
                )
            }

            val userDao = database.userDao()
            val existingAdmin = userDao.findUserByEmail("admin@ttfood.com")
            if (existingAdmin == null) {
                userDao.insertUser(
                    UserEntity(
                        id = "admin_master_1",
                        role = UserRole.ADMIN,
                        name = "Authorized Administrator",
                        phone = "9876543210",
                        email = "admin@ttfood.com",
                        passwordHash = "admin123",
                        isApproved = true,
                        isActive = true,
                        profileImageUri = null
                    )
                )
            }
        }
    }
}
