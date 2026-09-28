package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RiderProfileEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.CartItem
import com.example.data.repository.TTFoodRepository
import com.example.ui.components.OrderStickerPrintHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TTFoodDeliveryWorkflowTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: TTFoodRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TTFoodRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testEmptyDatabaseInitialization() = runBlocking {
        // Strict requirement: No demo products, fake sellers, or orders
        val products = repository.getAllProducts().first()
        val orders = repository.getAllOrders().first()
        val sellers = repository.getAllSellerProfiles().first()

        assertTrue("Products list must start empty", products.isEmpty())
        assertTrue("Orders list must start empty", orders.isEmpty())
        assertTrue("Sellers list must start empty", sellers.isEmpty())
    }

    @Test
    fun testCompleteFoodDeliveryCUJWorkflow() = runBlocking {
        // 1. Customer registration
        val customer = UserEntity(
            id = "cust_1",
            role = UserRole.CUSTOMER,
            name = "Ramesh Patil",
            phone = "9876543210",
            email = "ramesh@example.com",
            isApproved = true,
            isActive = true
        )
        repository.insertUser(customer)

        // 2. Seller registration (Status: PENDING)
        val sellerUser = UserEntity(
            id = "seller_1",
            role = UserRole.SELLER,
            name = "Suresh Kulkarni",
            phone = "9822011223",
            email = "suresh@ajarakitchen.com",
            isApproved = false,
            isActive = true
        )
        repository.insertUser(sellerUser)

        val sellerProfile = SellerProfileEntity(
            sellerId = "seller_1",
            restaurantName = "Hotel Ajara Darbar",
            ownerName = "Suresh Kulkarni",
            phone = "9822011223",
            email = "suresh@ajarakitchen.com",
            address = "Station Road, Ajara",
            city = "Ajara",
            pincode = "416505",
            status = "PENDING"
        )
        repository.insertSellerProfile(sellerProfile)

        // 3. Admin approves seller
        repository.updateSellerProfile(sellerProfile.copy(status = "APPROVED"))
        repository.updateUser(sellerUser.copy(isApproved = true))

        // 4. Seller adds real product
        val biryani = ProductEntity(
            id = "prod_1",
            sellerId = "seller_1",
            restaurantName = "Hotel Ajara Darbar",
            name = "Chicken Dum Biryani",
            description = "Authentic Kolhapuri style spiced biryani with raita",
            price = 220.0,
            category = "Biryani",
            isVeg = false,
            isAvailable = true
        )
        repository.insertProduct(biryani)

        val availableProducts = repository.getAvailableProducts().first()
        assertEquals(1, availableProducts.size)
        assertEquals("Chicken Dum Biryani", availableProducts[0].name)

        // 5. Customer places order
        val cartItems = listOf(CartItem(product = biryani, quantity = 2))
        val orderResult = repository.placeOrder(
            customer = customer,
            sellerId = "seller_1",
            restaurantName = "Hotel Ajara Darbar",
            items = cartItems,
            deliveryAddress = "Near Gandhi Chowk, Ajara",
            deliveryCity = "Ajara",
            deliveryPincode = "416505",
            paymentMethod = PaymentMethod.ONLINE,
            appliedCoupon = null,
            customerInstructions = "Make it spicy"
        )

        assertTrue(orderResult.isSuccess)
        val placedOrder = orderResult.getOrThrow()
        assertEquals(OrderStatus.ORDER_PLACED, placedOrder.orderStatus)
        assertEquals(440.0, placedOrder.subtotal, 0.01)
        assertEquals(PaymentStatus.PAID, placedOrder.paymentStatus)
        assertNotNull(placedOrder.deliveryOtp)
        assertEquals(4, placedOrder.deliveryOtp.length)

        // 6. Seller receives and accepts order
        val acceptRes = repository.sellerAcceptOrder(placedOrder.id)
        assertTrue(acceptRes.isSuccess)

        // 7. Verify thermal sticker generation
        val items = repository.findOrderItems(placedOrder.id)
        val thermalText = OrderStickerPrintHelper.generateThermalText(placedOrder, items)
        assertTrue(thermalText.contains("TT FOOD DELIVERY"))
        assertTrue(thermalText.contains("Hotel Ajara Darbar"))
        assertTrue(thermalText.contains("Chicken Dum Biryani"))
        assertTrue(thermalText.contains("Ramesh Patil"))
        assertTrue(thermalText.contains(placedOrder.orderNumber))

        // 8. Seller prepares and marks ready
        repository.sellerStartPreparing(placedOrder.id)
        repository.sellerMarkReady(placedOrder.id)

        var readyOrder = repository.findOrderById(placedOrder.id)
        assertEquals(OrderStatus.READY_FOR_PICKUP, readyOrder?.orderStatus)

        // 9. Rider registration & assignment
        val rider = RiderProfileEntity(
            riderId = "rider_1",
            name = "Vikas Shinde",
            phone = "9123456780",
            email = "vikas@rider.com",
            status = "APPROVED",
            isAvailable = true
        )
        repository.insertRiderProfile(rider)

        val riderAcceptRes = repository.riderAcceptOrder(placedOrder.id, rider)
        assertTrue(riderAcceptRes.isSuccess)

        // 10. Rider transitions
        repository.riderPickUpOrder(placedOrder.id, "rider_1")
        repository.riderGoOutForDelivery(placedOrder.id, "rider_1")

        // 11. Delivery OTP Verification (wrong OTP fails, correct OTP succeeds)
        val wrongOtpResult = repository.riderDeliverOrder(placedOrder.id, "rider_1", "0000")
        assertFalse(wrongOtpResult.isSuccess)

        val correctOtpResult = repository.riderDeliverOrder(placedOrder.id, "rider_1", placedOrder.deliveryOtp)
        assertTrue(correctOtpResult.isSuccess)

        // 12. Verify Delivered status & Payouts generated
        val completedOrder = repository.findOrderById(placedOrder.id)
        assertEquals(OrderStatus.DELIVERED, completedOrder?.orderStatus)
        assertTrue(completedOrder?.isOtpVerified == true)

        val sellerPayouts = repository.getPayoutsForBeneficiary("seller_1").first()
        assertEquals(1, sellerPayouts.size)
        assertEquals("SELLER", sellerPayouts[0].role)
        assertEquals(440.0, sellerPayouts[0].grossAmount, 0.01)
        assertTrue(sellerPayouts[0].commissionDeducted > 0)
    }
}
