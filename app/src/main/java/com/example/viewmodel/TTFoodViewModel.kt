package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AdminSettingsEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PayoutEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.RiderProfileEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.CartItem
import com.example.data.repository.TTFoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TTFoodViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    val repository = TTFoodRepository(db)

    // Current Logged-in User
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getUserById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentSellerProfile: StateFlow<SellerProfileEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getSellerProfile(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentRiderProfile: StateFlow<RiderProfileEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getRiderProfile(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Settings
    val settings: StateFlow<AdminSettingsEntity?> = repository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressEntity?>(null)
    val selectedAddress: StateFlow<AddressEntity?> = _selectedAddress.asStateFlow()

    // Customer Addresses
    val customerAddresses: StateFlow<List<AddressEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getAddressesByCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Products & Sellers
    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableProducts: StateFlow<List<ProductEntity>> = repository.getAvailableProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSellerProfiles: StateFlow<List<SellerProfileEntity>> = repository.getAllSellerProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Only approved sellers
    val approvedSellers: StateFlow<List<SellerProfileEntity>> = allSellerProfiles.combine(flowOf(Unit)) { sellers, _ ->
        sellers.filter { it.status == "APPROVED" && it.isAcceptingOrders }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Coupons
    val activeCoupons: StateFlow<List<CouponEntity>> = repository.getActiveCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customer Orders
    val customerOrders: StateFlow<List<OrderEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getOrdersByCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Seller Specific Data
    val sellerProducts: StateFlow<List<ProductEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getProductsBySeller(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sellerOrders: StateFlow<List<OrderEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getOrdersBySeller(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sellerPayouts: StateFlow<List<PayoutEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getPayoutsForBeneficiary(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rider Specific Data
    val availableRiderOrders: StateFlow<List<OrderEntity>> = repository.getAvailableOrdersForRiders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignedRiderOrders: StateFlow<List<OrderEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getOrdersByRider(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val riderEarnings: StateFlow<List<PayoutEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getPayoutsForBeneficiary(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRiders: StateFlow<List<RiderProfileEntity>> = repository.getAllRiderProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Data
    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayouts: StateFlow<List<PayoutEntity>> = repository.getAllPayouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = combine(currentUser, flowOf(Unit)) { user, _ ->
        user
    }.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.getNotificationsForUser(user.id, user.role.name)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Operation Messages
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun setUiMessage(msg: String) {
        _uiMessage.value = msg
    }

    // --- AUTHENTICATION METHODS ---

    fun login(emailOrPhone: String, passwordOrOtp: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.findUserByEmail(emailOrPhone.trim())
                ?: repository.findUserByPhone(emailOrPhone.trim())

            if (user == null) {
                onResult(false, "User not found. Please register.")
                return@launch
            }

            if (!user.isActive) {
                onResult(false, "Account is disabled. Please contact Admin.")
                return@launch
            }

            // Role specific checks
            if (user.role == UserRole.SELLER) {
                val profile = repository.findSellerProfile(user.id)
                if (profile?.status == "REJECTED") {
                    onResult(false, "Your seller application has been rejected by Admin.")
                    return@launch
                }
            } else if (user.role == UserRole.RIDER) {
                val profile = repository.findRiderProfile(user.id)
                if (profile?.status == "REJECTED") {
                    onResult(false, "Your rider application has been rejected by Admin.")
                    return@launch
                }
            }

            // Simple hash/equality check
            if (user.passwordHash.isNotEmpty() && user.passwordHash != passwordOrOtp.trim()) {
                onResult(false, "Invalid password or verification code.")
                return@launch
            }

            _currentUserId.value = user.id
            onResult(true, "Welcome back, ${user.name}!")
        }
    }

    fun signupCustomer(name: String, phone: String, email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (repository.findUserByEmail(email.trim()) != null) {
                onResult(false, "Email already registered.")
                return@launch
            }
            if (repository.findUserByPhone(phone.trim()) != null) {
                onResult(false, "Phone number already registered.")
                return@launch
            }

            val userId = UUID.randomUUID().toString()
            val user = UserEntity(
                id = userId,
                role = UserRole.CUSTOMER,
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                passwordHash = pass.trim(),
                isApproved = true,
                isActive = true
            )
            repository.insertUser(user)
            _currentUserId.value = userId
            onResult(true, "Customer account created successfully!")
        }
    }

    fun signupSeller(
        restaurantName: String,
        ownerName: String,
        phone: String,
        email: String,
        pass: String,
        address: String,
        pan: String,
        bankAcc: String,
        ifsc: String,
        upi: String,
        docProofUri: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (repository.findUserByEmail(email.trim()) != null) {
                onResult(false, "Email already registered.")
                return@launch
            }
            if (repository.findUserByPhone(phone.trim()) != null) {
                onResult(false, "Phone number already registered.")
                return@launch
            }

            val userId = UUID.randomUUID().toString()
            val user = UserEntity(
                id = userId,
                role = UserRole.SELLER,
                name = ownerName.trim(),
                phone = phone.trim(),
                email = email.trim(),
                passwordHash = pass.trim(),
                isApproved = false, // Must be approved by Admin
                isActive = true
            )
            repository.insertUser(user)

            val profile = SellerProfileEntity(
                sellerId = userId,
                restaurantName = restaurantName.trim(),
                ownerName = ownerName.trim(),
                phone = phone.trim(),
                email = email.trim(),
                address = address.trim(),
                panNumber = pan.trim(),
                bankAccountNumber = bankAcc.trim(),
                bankIfsc = ifsc.trim(),
                upiId = upi.trim(),
                docProofUri = docProofUri,
                status = "PENDING",
                isAcceptingOrders = true
            )
            repository.insertSellerProfile(profile)
            _currentUserId.value = userId
            onResult(true, "Seller application submitted! Status: PENDING Admin review.")
        }
    }

    fun signupRider(
        name: String,
        phone: String,
        email: String,
        pass: String,
        vehicleType: String,
        vehicleNumber: String,
        licenseNumber: String,
        licenseDocUri: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (repository.findUserByEmail(email.trim()) != null) {
                onResult(false, "Email already registered.")
                return@launch
            }
            if (repository.findUserByPhone(phone.trim()) != null) {
                onResult(false, "Phone number already registered.")
                return@launch
            }

            val userId = UUID.randomUUID().toString()
            val user = UserEntity(
                id = userId,
                role = UserRole.RIDER,
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                passwordHash = pass.trim(),
                isApproved = false, // Must be approved by Admin
                isActive = true
            )
            repository.insertUser(user)

            val riderProfile = RiderProfileEntity(
                riderId = userId,
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                vehicleType = vehicleType.trim(),
                vehicleNumber = vehicleNumber.trim(),
                licenseNumber = licenseNumber.trim(),
                licenseDocUri = licenseDocUri,
                status = "PENDING",
                isAvailable = true
            )
            repository.insertRiderProfile(riderProfile)
            _currentUserId.value = userId
            onResult(true, "Rider application submitted! Status: PENDING Admin approval.")
        }
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    fun logout() {
        _currentUserId.value = null
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    // --- PROFILE & ADDRESS ---

    fun updateProfile(name: String, phone: String, imageUri: String?) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(name = name, phone = phone, profileImageUri = imageUri ?: user.profileImageUri)
            repository.updateUser(updated)
            _uiMessage.value = "Profile updated successfully."
        }
    }

    fun addCustomerAddress(
        label: String,
        recipient: String,
        phone: String,
        addressLine: String,
        landmark: String,
        city: String,
        pincode: String,
        isDefault: Boolean
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val address = AddressEntity(
                customerId = user.id,
                label = label,
                recipientName = recipient,
                phone = phone,
                addressLine = addressLine,
                landmark = landmark,
                city = city,
                pincode = pincode,
                isDefault = isDefault
            )
            repository.insertAddress(address)
            _selectedAddress.value = address
            _uiMessage.value = "Delivery address saved."
        }
    }

    fun selectAddress(address: AddressEntity) {
        _selectedAddress.value = address
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
            if (_selectedAddress.value?.id == id) {
                _selectedAddress.value = null
            }
        }
    }

    // --- CART & ORDERING ---

    fun addToCart(product: ProductEntity) {
        val current = _cartItems.value.toMutableList()
        // Ensure cart has items from only ONE restaurant at a time
        if (current.isNotEmpty() && current.first().product.sellerId != product.sellerId) {
            _uiMessage.value = "Cart contains items from another restaurant. Please empty cart first."
            return
        }

        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(product, 1))
        }
        _cartItems.value = current
        _uiMessage.value = "Added to cart"
    }

    fun decreaseQuantity(productId: String) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val existing = current[index]
            if (existing.quantity > 1) {
                current[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val coupon = repository.findActiveCouponByCode(code.trim().uppercase())
            if (coupon == null) {
                _uiMessage.value = "Invalid or expired coupon code."
                return@launch
            }
            val subtotal = _cartItems.value.sumOf { it.product.price * it.quantity }
            if (subtotal < coupon.minOrderAmount) {
                _uiMessage.value = "Minimum order of ₹${coupon.minOrderAmount} required for this coupon."
                return@launch
            }
            _appliedCoupon.value = coupon
            _uiMessage.value = "Coupon applied successfully!"
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _uiMessage.value = "Coupon removed."
    }

    fun placeCustomerOrder(
        paymentMethod: PaymentMethod,
        instructions: String?,
        onSuccess: (OrderEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = currentUser.value
        if (user == null || user.role != UserRole.CUSTOMER) {
            onError("Please log in as a customer to place an order.")
            return
        }

        val items = _cartItems.value
        if (items.isEmpty()) {
            onError("Your cart is empty.")
            return
        }

        val address = _selectedAddress.value
        if (address == null) {
            onError("Please select or add a delivery address.")
            return
        }

        val firstProduct = items.first().product
        viewModelScope.launch {
            val result = repository.placeOrder(
                customer = user,
                sellerId = firstProduct.sellerId,
                restaurantName = firstProduct.restaurantName,
                items = items,
                deliveryAddress = "${address.addressLine}, ${address.landmark}".trimEnd(',', ' '),
                deliveryCity = address.city,
                deliveryPincode = address.pincode,
                paymentMethod = paymentMethod,
                appliedCoupon = _appliedCoupon.value,
                customerInstructions = instructions
            )

            result.fold(
                onSuccess = { order ->
                    clearCart()
                    onSuccess(order)
                },
                onFailure = { ex ->
                    onError(ex.message ?: "Failed to place order.")
                }
            )
        }
    }

    // --- SELLER PRODUCT MANAGEMENT ---

    fun addProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        isVeg: Boolean,
        imageUri: String?,
        prepTime: Int
    ) {
        val seller = currentSellerProfile.value ?: return
        if (seller.status != "APPROVED") {
            _uiMessage.value = "Your seller account is not yet approved by Admin."
            return
        }

        viewModelScope.launch {
            val product = ProductEntity(
                id = UUID.randomUUID().toString(),
                sellerId = seller.sellerId,
                restaurantName = seller.restaurantName,
                name = name.trim(),
                description = description.trim(),
                price = price,
                category = category.trim(),
                isVeg = isVeg,
                isAvailable = true,
                imageUri = imageUri,
                preparationTimeMinutes = prepTime
            )
            repository.insertProduct(product)
            _uiMessage.value = "Product added successfully."
        }
    }

    fun updateProduct(product: ProductEntity) {
        val seller = currentSellerProfile.value ?: return
        if (product.sellerId != seller.sellerId) {
            _uiMessage.value = "Unauthorized: You can only edit your own products."
            return
        }
        viewModelScope.launch {
            repository.updateProduct(product)
            _uiMessage.value = "Product updated."
        }
    }

    fun deleteProduct(product: ProductEntity) {
        val seller = currentSellerProfile.value ?: return
        if (product.sellerId != seller.sellerId) {
            _uiMessage.value = "Unauthorized."
            return
        }
        viewModelScope.launch {
            repository.deleteProduct(product.id)
            _uiMessage.value = "Product deleted."
        }
    }

    fun toggleProductAvailability(product: ProductEntity) {
        val seller = currentSellerProfile.value ?: return
        if (product.sellerId != seller.sellerId) return
        viewModelScope.launch {
            repository.updateProduct(product.copy(isAvailable = !product.isAvailable))
        }
    }

    // --- SELLER ORDER ACTIONS ---

    fun sellerAcceptOrder(orderId: String) {
        viewModelScope.launch {
            val res = repository.sellerAcceptOrder(orderId)
            res.fold(
                onSuccess = { _uiMessage.value = "Order accepted." },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    fun sellerRejectOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            val res = repository.sellerRejectOrder(orderId, reason)
            res.fold(
                onSuccess = { _uiMessage.value = "Order cancelled." },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    fun sellerStartPreparing(orderId: String) {
        viewModelScope.launch {
            val res = repository.sellerStartPreparing(orderId)
            res.fold(
                onSuccess = { _uiMessage.value = "Order status updated to Preparing." },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    fun sellerMarkReady(orderId: String) {
        viewModelScope.launch {
            val res = repository.sellerMarkReady(orderId)
            res.fold(
                onSuccess = { _uiMessage.value = "Order marked Ready for Pickup!" },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    // --- RIDER ACTIONS ---

    fun riderAcceptOrder(orderId: String) {
        val rider = currentRiderProfile.value
        if (rider == null || rider.status != "APPROVED") {
            _uiMessage.value = "Rider account must be approved by Admin."
            return
        }
        viewModelScope.launch {
            val res = repository.riderAcceptOrder(orderId, rider)
            res.fold(
                onSuccess = { _uiMessage.value = "You accepted this delivery task!" },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    fun riderPickUpOrder(orderId: String) {
        val rider = currentRiderProfile.value ?: return
        viewModelScope.launch {
            val res = repository.riderPickUpOrder(orderId, rider.riderId)
            res.fold(
                onSuccess = { _uiMessage.value = "Order marked as Picked Up." },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    fun riderOutForDelivery(orderId: String) {
        val rider = currentRiderProfile.value ?: return
        viewModelScope.launch {
            val res = repository.riderGoOutForDelivery(orderId, rider.riderId)
            res.fold(
                onSuccess = { _uiMessage.value = "Order is now Out for Delivery." },
                onFailure = { _uiMessage.value = it.message }
            )
        }
    }

    fun riderDeliverOrder(orderId: String, otp: String, onResult: (Boolean, String) -> Unit) {
        val rider = currentRiderProfile.value
        if (rider == null) {
            onResult(false, "Rider not authenticated.")
            return
        }
        viewModelScope.launch {
            val res = repository.riderDeliverOrder(orderId, rider.riderId, otp)
            res.fold(
                onSuccess = {
                    onResult(true, "Order delivered and verified successfully!")
                },
                onFailure = {
                    onResult(false, it.message ?: "Failed to verify OTP.")
                }
            )
        }
    }

    // --- ADMIN ACTIONS ---

    fun adminApproveSeller(sellerId: String) {
        viewModelScope.launch {
            val profile = repository.findSellerProfile(sellerId) ?: return@launch
            val user = repository.findUserById(sellerId) ?: return@launch
            repository.updateSellerProfile(profile.copy(status = "APPROVED"))
            repository.updateUser(user.copy(isApproved = true))
            _uiMessage.value = "Seller '${profile.restaurantName}' approved."
        }
    }

    fun adminRejectSeller(sellerId: String) {
        viewModelScope.launch {
            val profile = repository.findSellerProfile(sellerId) ?: return@launch
            val user = repository.findUserById(sellerId) ?: return@launch
            repository.updateSellerProfile(profile.copy(status = "REJECTED"))
            repository.updateUser(user.copy(isApproved = false))
            _uiMessage.value = "Seller application rejected."
        }
    }

    fun adminToggleSellerStatus(sellerId: String) {
        viewModelScope.launch {
            val profile = repository.findSellerProfile(sellerId) ?: return@launch
            val nextStatus = if (profile.status == "APPROVED") "SUSPENDED" else "APPROVED"
            repository.updateSellerProfile(profile.copy(status = nextStatus))
            _uiMessage.value = "Seller status set to $nextStatus."
        }
    }

    fun adminApproveRider(riderId: String) {
        viewModelScope.launch {
            val profile = repository.findRiderProfile(riderId) ?: return@launch
            val user = repository.findUserById(riderId) ?: return@launch
            repository.updateRiderProfile(profile.copy(status = "APPROVED"))
            repository.updateUser(user.copy(isApproved = true))
            _uiMessage.value = "Rider '${profile.name}' approved."
        }
    }

    fun adminRejectRider(riderId: String) {
        viewModelScope.launch {
            val profile = repository.findRiderProfile(riderId) ?: return@launch
            val user = repository.findUserById(riderId) ?: return@launch
            repository.updateRiderProfile(profile.copy(status = "REJECTED"))
            repository.updateUser(user.copy(isApproved = false))
            _uiMessage.value = "Rider application rejected."
        }
    }

    fun adminToggleUserActive(userId: String) {
        viewModelScope.launch {
            val user = repository.findUserById(userId) ?: return@launch
            repository.updateUser(user.copy(isActive = !user.isActive))
            _uiMessage.value = "User status toggled."
        }
    }

    fun adminUpdateSettings(newSettings: AdminSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            _uiMessage.value = "System settings updated."
        }
    }

    fun adminAddCoupon(code: String, percent: Double, flat: Double, minAmt: Double, maxDisc: Double, desc: String) {
        viewModelScope.launch {
            val coupon = CouponEntity(
                code = code.trim().uppercase(),
                discountPercent = percent,
                flatDiscount = flat,
                minOrderAmount = minAmt,
                maxDiscount = maxDisc,
                description = desc,
                isActive = true
            )
            repository.insertCoupon(coupon)
            _uiMessage.value = "Coupon created."
        }
    }

    fun adminDeleteCoupon(id: Long) {
        viewModelScope.launch {
            repository.deleteCoupon(id)
            _uiMessage.value = "Coupon deleted."
        }
    }

    fun adminProcessPayout(payout: PayoutEntity, refId: String) {
        viewModelScope.launch {
            repository.updatePayout(
                payout.copy(
                    status = "PAID",
                    referenceId = refId,
                    paidAt = System.currentTimeMillis()
                )
            )
            _uiMessage.value = "Payout marked as PAID (Ref: $refId)."
        }
    }

    // --- REVIEWS & FEEDBACK ---

    fun submitReview(orderId: String, restaurantId: String, restaurantName: String, rating: Int, comment: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val review = ReviewEntity(
                orderId = orderId,
                customerId = user.id,
                customerName = user.name,
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                rating = rating,
                comment = comment.trim()
            )
            repository.insertReview(review)
            _uiMessage.value = "Thank you for rating your food!"
        }
    }
}
