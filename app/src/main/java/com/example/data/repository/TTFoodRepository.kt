package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AdminSettingsEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.PayoutEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.RiderProfileEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
)

class TTFoodRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val productDao = db.productDao()
    private val orderDao = db.orderDao()
    private val addressDao = db.addressDao()
    private val couponDao = db.couponDao()
    private val reviewDao = db.reviewDao()
    private val notificationDao = db.notificationDao()
    private val payoutDao = db.payoutDao()
    private val settingsDao = db.settingsDao()

    // Users
    fun getUserById(id: String): Flow<UserEntity?> = userDao.getUserById(id)
    suspend fun findUserById(id: String): UserEntity? = userDao.findUserById(id)
    suspend fun findUserByEmail(email: String): UserEntity? = userDao.findUserByEmail(email)
    suspend fun findUserByPhone(phone: String): UserEntity? = userDao.findUserByPhone(phone)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>> = userDao.getUsersByRole(role)
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    // Seller Profiles
    fun getAllSellerProfiles(): Flow<List<SellerProfileEntity>> = userDao.getAllSellerProfiles()
    fun getSellerProfile(sellerId: String): Flow<SellerProfileEntity?> = userDao.getSellerProfile(sellerId)
    suspend fun findSellerProfile(sellerId: String): SellerProfileEntity? = userDao.findSellerProfile(sellerId)
    suspend fun insertSellerProfile(profile: SellerProfileEntity) = userDao.insertSellerProfile(profile)
    suspend fun updateSellerProfile(profile: SellerProfileEntity) = userDao.updateSellerProfile(profile)

    // Rider Profiles
    fun getAllRiderProfiles(): Flow<List<RiderProfileEntity>> = userDao.getAllRiderProfiles()
    fun getRiderProfile(riderId: String): Flow<RiderProfileEntity?> = userDao.getRiderProfile(riderId)
    suspend fun findRiderProfile(riderId: String): RiderProfileEntity? = userDao.findRiderProfile(riderId)
    suspend fun insertRiderProfile(profile: RiderProfileEntity) = userDao.insertRiderProfile(profile)
    suspend fun updateRiderProfile(profile: RiderProfileEntity) = userDao.updateRiderProfile(profile)

    // Addresses
    fun getAddressesByCustomer(customerId: String): Flow<List<AddressEntity>> = addressDao.getAddressesByCustomer(customerId)
    suspend fun insertAddress(address: AddressEntity): Long {
        if (address.isDefault) {
            addressDao.clearDefaultAddress(address.customerId)
        }
        return addressDao.insertAddress(address)
    }
    suspend fun deleteAddress(id: Long) = addressDao.deleteAddress(id)

    // Products & Categories
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getAvailableProducts(): Flow<List<ProductEntity>> = productDao.getAvailableProducts()
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> = productDao.getProductsBySeller(sellerId)
    fun getProductById(id: String): Flow<ProductEntity?> = productDao.getProductById(id)
    suspend fun findProductById(id: String): ProductEntity? = productDao.findProductById(id)
    suspend fun insertProduct(product: ProductEntity) = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(id: String) = productDao.deleteProductById(id)
    fun getAllCategories(): Flow<List<CategoryEntity>> = productDao.getAllCategories()
    suspend fun insertCategory(category: CategoryEntity) = productDao.insertCategory(category)

    // Orders
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getOrdersByCustomer(customerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomer(customerId)
    fun getOrdersBySeller(sellerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersBySeller(sellerId)
    fun getOrdersByRider(riderId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByRider(riderId)
    fun getAvailableOrdersForRiders(): Flow<List<OrderEntity>> = orderDao.getAvailableOrdersForRiders()
    fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    suspend fun findOrderById(orderId: String): OrderEntity? = orderDao.findOrderById(orderId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = orderDao.getOrderItems(orderId)
    suspend fun findOrderItems(orderId: String): List<OrderItemEntity> = orderDao.findOrderItems(orderId)

    // Coupons
    fun getAllCoupons(): Flow<List<CouponEntity>> = couponDao.getAllCoupons()
    fun getActiveCoupons(): Flow<List<CouponEntity>> = couponDao.getActiveCoupons()
    suspend fun findActiveCouponByCode(code: String): CouponEntity? = couponDao.findActiveCouponByCode(code)
    suspend fun insertCoupon(coupon: CouponEntity) = couponDao.insertCoupon(coupon)
    suspend fun updateCoupon(coupon: CouponEntity) = couponDao.updateCoupon(coupon)
    suspend fun deleteCoupon(id: Long) = couponDao.deleteCoupon(id)

    // Reviews
    fun getReviewsForRestaurant(restaurantId: String): Flow<List<ReviewEntity>> = reviewDao.getReviewsForRestaurant(restaurantId)
    fun getAllReviews(): Flow<List<ReviewEntity>> = reviewDao.getAllReviews()
    suspend fun insertReview(review: ReviewEntity) = reviewDao.insertReview(review)

    // Notifications
    fun getNotificationsForUser(userId: String, role: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId, role)
    suspend fun insertNotification(notification: NotificationEntity) =
        notificationDao.insertNotification(notification)
    suspend fun markAllNotificationsAsRead(userId: String, role: String) =
        notificationDao.markAllAsRead(userId, role)

    // Payouts
    fun getAllPayouts(): Flow<List<PayoutEntity>> = payoutDao.getAllPayouts()
    fun getPayoutsForBeneficiary(beneficiaryId: String): Flow<List<PayoutEntity>> =
        payoutDao.getPayoutsForBeneficiary(beneficiaryId)
    suspend fun insertPayout(payout: PayoutEntity) = payoutDao.insertPayout(payout)
    suspend fun updatePayout(payout: PayoutEntity) = payoutDao.updatePayout(payout)

    // Settings
    fun getSettings(): Flow<AdminSettingsEntity?> = settingsDao.getSettings()
    suspend fun getSettingsDirect(): AdminSettingsEntity? = settingsDao.getSettingsDirect()
    suspend fun updateSettings(settings: AdminSettingsEntity) = settingsDao.insertSettings(settings)

    // Create New Order
    suspend fun placeOrder(
        customer: UserEntity,
        sellerId: String,
        restaurantName: String,
        items: List<CartItem>,
        deliveryAddress: String,
        deliveryCity: String,
        deliveryPincode: String,
        paymentMethod: PaymentMethod,
        appliedCoupon: CouponEntity?,
        customerInstructions: String?
    ): Result<OrderEntity> {
        if (items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Cart is empty"))
        }

        val settings = getSettingsDirect() ?: AdminSettingsEntity()

        // Validate Service Area (Ajara, Maharashtra)
        val allowedPincodes = settings.servicePincodes.split(",").map { it.trim() }
        if (!deliveryCity.contains(settings.serviceCity, ignoreCase = true) &&
            !allowedPincodes.contains(deliveryPincode.trim())
        ) {
            return Result.failure(
                IllegalArgumentException("Delivery address ($deliveryCity, $deliveryPincode) is outside the service area (${settings.serviceCity}). We currently serve Ajara, Maharashtra.")
            )
        }

        val subtotal = items.sumOf { it.product.price * it.quantity }
        val deliveryCharge = if (subtotal >= settings.freeDeliveryThreshold) 0.0 else settings.fixedDeliveryFee
        var discount = 0.0

        if (appliedCoupon != null) {
            if (subtotal >= appliedCoupon.minOrderAmount) {
                if (appliedCoupon.discountPercent > 0) {
                    val calculated = (subtotal * (appliedCoupon.discountPercent / 100.0))
                    discount = minOf(calculated, appliedCoupon.maxDiscount)
                } else if (appliedCoupon.flatDiscount > 0) {
                    discount = minOf(appliedCoupon.flatDiscount, subtotal)
                }
            }
        }

        val totalAmount = maxOf(0.0, subtotal + deliveryCharge - discount)

        // Generate 4-digit Delivery OTP
        val deliveryOtp = (1000 + Random.nextInt(9000)).toString()
        val orderId = UUID.randomUUID().toString()
        val orderNumber = "TT-${1000 + Random.nextInt(9000)}"

        val paymentStatus = if (paymentMethod == PaymentMethod.ONLINE) {
            PaymentStatus.PAID
        } else {
            PaymentStatus.COD_DUE
        }

        val transactionId = if (paymentMethod == PaymentMethod.ONLINE) {
            "TXN-TT-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}"
        } else null

        val order = OrderEntity(
            id = orderId,
            orderNumber = orderNumber,
            customerId = customer.id,
            customerName = customer.name,
            customerPhone = customer.phone,
            sellerId = sellerId,
            restaurantName = restaurantName,
            deliveryAddress = deliveryAddress,
            deliveryCity = deliveryCity,
            deliveryPincode = deliveryPincode,
            subtotal = subtotal,
            deliveryCharge = deliveryCharge,
            discount = discount,
            totalAmount = totalAmount,
            couponCode = appliedCoupon?.code,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            transactionId = transactionId,
            orderStatus = OrderStatus.ORDER_PLACED,
            customerInstructions = customerInstructions,
            deliveryOtp = deliveryOtp,
            isOtpVerified = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        orderDao.insertOrder(order)

        val orderItems = items.map {
            OrderItemEntity(
                orderId = orderId,
                productId = it.product.id,
                productName = it.product.name,
                price = it.product.price,
                quantity = it.quantity,
                isVeg = it.product.isVeg
            )
        }
        orderDao.insertOrderItems(orderItems)

        // Notify Seller
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = sellerId,
                targetRole = "SELLER",
                title = "New Order Received! (#$orderNumber)",
                message = "${customer.name} placed an order of ₹$totalAmount from $restaurantName.",
                orderId = orderId
            )
        )

        // Notify Customer
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = customer.id,
                targetRole = "CUSTOMER",
                title = "Order Placed Successfully! (#$orderNumber)",
                message = "Your order from $restaurantName has been placed.",
                orderId = orderId
            )
        )

        // Notify Admin
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = "admin_master_1",
                targetRole = "ADMIN",
                title = "New Order #$orderNumber",
                message = "New order placed for $restaurantName. Amount: ₹$totalAmount.",
                orderId = orderId
            )
        )

        return Result.success(order)
    }

    // Seller State Transitions
    suspend fun sellerAcceptOrder(orderId: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.orderStatus != OrderStatus.ORDER_PLACED) {
            return Result.failure(Exception("Invalid status transition from ${order.orderStatus} to SELLER_ACCEPTED"))
        }

        val updated = order.copy(
            orderStatus = OrderStatus.SELLER_ACCEPTED,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Order Accepted!",
                message = "${order.restaurantName} accepted your order #${order.orderNumber}.",
                orderId = orderId
            )
        )
        return Result.success(Unit)
    }

    suspend fun sellerRejectOrder(orderId: String, reason: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        val updated = order.copy(
            orderStatus = OrderStatus.CANCELLED,
            cancelReason = reason,
            paymentStatus = if (order.paymentStatus == PaymentStatus.PAID) PaymentStatus.REFUNDED else order.paymentStatus,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Order Cancelled",
                message = "Your order #${order.orderNumber} was cancelled by restaurant. Reason: $reason",
                orderId = orderId
            )
        )
        return Result.success(Unit)
    }

    suspend fun sellerStartPreparing(orderId: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.orderStatus != OrderStatus.SELLER_ACCEPTED) {
            return Result.failure(Exception("Cannot start preparing from state: ${order.orderStatus}"))
        }

        val updated = order.copy(
            orderStatus = OrderStatus.PREPARING,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Preparing Your Food",
                message = "${order.restaurantName} is now preparing your delicious food!",
                orderId = orderId
            )
        )
        return Result.success(Unit)
    }

    suspend fun sellerMarkReady(orderId: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.orderStatus != OrderStatus.PREPARING) {
            return Result.failure(Exception("Cannot mark ready from state: ${order.orderStatus}"))
        }

        val updated = order.copy(
            orderStatus = OrderStatus.READY_FOR_PICKUP,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Order Ready for Pickup!",
                message = "Your order #${order.orderNumber} is packed and ready. Waiting for delivery rider.",
                orderId = orderId
            )
        )

        // Notify Riders
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = "ALL_RIDERS",
                targetRole = "RIDER",
                title = "Delivery Order Available!",
                message = "Order #${order.orderNumber} is ready for pickup at ${order.restaurantName}.",
                orderId = orderId
            )
        )

        return Result.success(Unit)
    }

    // Rider Transitions
    suspend fun riderAcceptOrder(orderId: String, rider: RiderProfileEntity): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.orderStatus != OrderStatus.READY_FOR_PICKUP) {
            return Result.failure(Exception("Order is not ready for pickup"))
        }
        if (order.riderId != null) {
            return Result.failure(Exception("Order has already been assigned to another rider"))
        }

        val updated = order.copy(
            riderId = rider.riderId,
            riderName = rider.name,
            riderPhone = rider.phone,
            orderStatus = OrderStatus.RIDER_ASSIGNED,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        // Mark rider busy
        userDao.updateRiderProfile(rider.copy(isAvailable = false, currentOrderId = orderId))

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Delivery Partner Assigned!",
                message = "${rider.name} is on the way to pick up your order.",
                orderId = orderId
            )
        )

        return Result.success(Unit)
    }

    suspend fun riderPickUpOrder(orderId: String, riderId: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.riderId != riderId) {
            return Result.failure(Exception("Unauthorized rider for this order"))
        }
        if (order.orderStatus != OrderStatus.RIDER_ASSIGNED) {
            return Result.failure(Exception("Cannot pick up from status: ${order.orderStatus}"))
        }

        val updated = order.copy(
            orderStatus = OrderStatus.PICKED_UP,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Order Picked Up!",
                message = "${order.riderName} has picked up your food from ${order.restaurantName}.",
                orderId = orderId
            )
        )
        return Result.success(Unit)
    }

    suspend fun riderGoOutForDelivery(orderId: String, riderId: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.riderId != riderId) {
            return Result.failure(Exception("Unauthorized rider for this order"))
        }
        if (order.orderStatus != OrderStatus.PICKED_UP) {
            return Result.failure(Exception("Cannot go out for delivery from status: ${order.orderStatus}"))
        }

        val updated = order.copy(
            orderStatus = OrderStatus.OUT_FOR_DELIVERY,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Out for Delivery!",
                message = "${order.riderName} is arriving at your delivery location. Your Delivery OTP is ${order.deliveryOtp}.",
                orderId = orderId
            )
        )
        return Result.success(Unit)
    }

    suspend fun riderDeliverOrder(orderId: String, riderId: String, enteredOtp: String): Result<Unit> {
        val order = orderDao.findOrderById(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.riderId != riderId) {
            return Result.failure(Exception("Unauthorized rider for this order"))
        }
        if (order.orderStatus != OrderStatus.OUT_FOR_DELIVERY) {
            return Result.failure(Exception("Cannot deliver order when status is ${order.orderStatus}"))
        }

        // Strict OTP verification
        if (enteredOtp.trim() != order.deliveryOtp.trim()) {
            return Result.failure(IllegalArgumentException("Invalid Delivery OTP. Please ask customer for the correct 4-digit code shown on their tracking screen."))
        }

        val updated = order.copy(
            orderStatus = OrderStatus.DELIVERED,
            isOtpVerified = true,
            paymentStatus = PaymentStatus.PAID,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        // Free the rider
        val riderProfile = userDao.findRiderProfile(riderId)
        if (riderProfile != null) {
            userDao.updateRiderProfile(riderProfile.copy(isAvailable = true, currentOrderId = null))
        }

        // Calculate and create payout entries for Seller and Rider
        val settings = getSettingsDirect() ?: AdminSettingsEntity()
        val commissionRate = settings.platformCommissionPercent / 100.0
        val grossSellerAmount = order.subtotal - order.discount
        val platformCommission = grossSellerAmount * commissionRate
        val netSellerPayable = grossSellerAmount - platformCommission

        payoutDao.insertPayout(
            PayoutEntity(
                beneficiaryId = order.sellerId,
                beneficiaryName = order.restaurantName,
                role = "SELLER",
                orderId = order.id,
                grossAmount = grossSellerAmount,
                commissionDeducted = platformCommission,
                netPayable = netSellerPayable,
                status = "PENDING",
                referenceId = "PAYOUT-SELLER-${order.orderNumber}",
                createdAt = System.currentTimeMillis()
            )
        )

        payoutDao.insertPayout(
            PayoutEntity(
                beneficiaryId = riderId,
                beneficiaryName = order.riderName ?: "Rider",
                role = "RIDER",
                orderId = order.id,
                grossAmount = settings.riderDeliveryPay,
                commissionDeducted = 0.0,
                netPayable = settings.riderDeliveryPay,
                status = "PENDING",
                referenceId = "PAYOUT-RIDER-${order.orderNumber}",
                createdAt = System.currentTimeMillis()
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.customerId,
                targetRole = "CUSTOMER",
                title = "Order Delivered!",
                message = "Your order #${order.orderNumber} was delivered safely. Enjoy your meal!",
                orderId = orderId
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = order.sellerId,
                targetRole = "SELLER",
                title = "Order #${order.orderNumber} Delivered",
                message = "Delivered successfully. Earnings: ₹%.2f credited to pending payout.".format(netSellerPayable),
                orderId = orderId
            )
        )

        return Result.success(Unit)
    }
}
