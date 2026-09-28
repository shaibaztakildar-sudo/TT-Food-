package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.VegIndicator
import com.example.viewmodel.TTFoodViewModel

@Composable
fun CustomerHomeScreen(
    viewModel: TTFoodViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToOrderDetails: (String) -> Unit
) {
    val availableProducts by viewModel.availableProducts.collectAsStateWithLifecycle()
    val approvedSellers by viewModel.approvedSellers.collectAsStateWithLifecycle()
    val activeCoupons by viewModel.activeCoupons.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isVegOnly by remember { mutableStateOf(false) }

    val categories = remember(availableProducts) {
        listOf("All") + availableProducts.map { it.category }.distinct()
    }

    val filteredProducts = remember(availableProducts, searchQuery, selectedCategory, isVegOnly) {
        availableProducts.filter { product ->
            val matchSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.restaurantName.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
            val matchCategory = selectedCategory == "All" || product.category.equals(selectedCategory, ignoreCase = true)
            val matchVeg = !isVegOnly || product.isVeg
            matchSearch && matchCategory && matchVeg
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 88.dp else 24.dp)
        ) {
            // Location Header
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE23744))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Delivering to", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ajara, Maharashtra", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Service Area: Ajara Town & Surrounds (416505)", fontSize = 11.sp, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search dishes, cuisines, or restaurants...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("customer_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
            }

            // Coupons Carousel if available
            if (activeCoupons.isNotEmpty()) {
                item {
                    Text(
                        "Offers & Discounts",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 6.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeCoupons) { coupon ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            color = Color(0xFFE65100),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                coupon.code,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            if (coupon.discountPercent > 0) "${coupon.discountPercent.toInt()}% OFF"
                                            else "₹${coupon.flatDiscount.toInt()} OFF",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        coupon.description.ifBlank { "Min order ₹${coupon.minOrderAmount.toInt()}" },
                                        fontSize = 11.sp,
                                        color = Color(0xFF4E342E),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Categories Filter Chips
            if (categories.size > 1) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 12.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = isVegOnly,
                            onClick = { isVegOnly = !isVegOnly },
                            label = { Text("Veg Only", fontSize = 12.sp) },
                            leadingIcon = { VegIndicator(isVeg = true) }
                        )
                    }
                }
            }

            // Real Approved Restaurants Section
            if (approvedSellers.isNotEmpty()) {
                item {
                    Text(
                        "Featured Restaurants in Ajara",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(approvedSellers) { seller ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.width(180.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Surface(
                                        color = Color(0xFFFFEBEE),
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFFE23744))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(seller.restaurantName, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                                    Text(seller.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            // Products Section Header
            item {
                Text(
                    if (selectedCategory == "All") "Available Foods & Dishes" else "$selectedCategory Dishes",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // Empty State strictly when no real products exist
            if (filteredProducts.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Fastfood,
                        title = "No restaurants or products available yet.",
                        description = "Once approved restaurants add menu items in Ajara, they will appear here live."
                    )
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductItemCard(
                        product = product,
                        cartQuantity = cartItems.find { it.product.id == product.id }?.quantity ?: 0,
                        onAddToCart = { viewModel.addToCart(product) },
                        onDecrease = { viewModel.decreaseQuantity(product.id) }
                    )
                }
            }
        }

        // Floating Cart Bottom Pill
        if (cartItems.isNotEmpty()) {
            val totalCount = cartItems.sumOf { it.quantity }
            val subtotal = cartItems.sumOf { it.product.price * it.quantity }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE23744),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onNavigateToCart)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("$totalCount ITEM${if (totalCount > 1) "S" else ""}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("₹${"%.2f".format(subtotal)} plus taxes", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("View Cart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("product_item_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VegIndicator(isVeg = product.isVeg)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        product.restaurantName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    "₹${"%.2f".format(product.price)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (product.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        product.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!product.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (cartQuantity == 0) {
                    OutlinedButton(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("add_cart_${product.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE23744))
                    ) {
                        Text("ADD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFE23744), RoundedCornerShape(8.dp))
                            .height(34.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text("$cartQuantity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp))
                        IconButton(onClick = onAddToCart, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    viewModel: TTFoodViewModel,
    onOrderPlaced: (OrderEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val addresses by viewModel.customerAddresses.collectAsStateWithLifecycle()
    val selectedAddress by viewModel.selectedAddress.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var couponInput by remember { mutableStateOf("") }
    var customerInstructions by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.ONLINE) }
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    // Auto select first address if none selected
    if (selectedAddress == null && addresses.isNotEmpty()) {
        viewModel.selectAddress(addresses.first())
    }

    if (cartItems.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.ShoppingBag,
            title = "Your cart is empty",
            description = "Explore real dishes from local Ajara kitchens and add them here!",
            actionText = "Browse Menu",
            onActionClick = onNavigateBack
        )
        return
    }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val freeThreshold = settings?.freeDeliveryThreshold ?: 300.0
    val deliveryFee = if (subtotal >= freeThreshold) 0.0 else (settings?.fixedDeliveryFee ?: 30.0)

    var discount = 0.0
    if (appliedCoupon != null) {
        val c = appliedCoupon!!
        if (subtotal >= c.minOrderAmount) {
            discount = if (c.discountPercent > 0) {
                minOf(subtotal * (c.discountPercent / 100.0), c.maxDiscount)
            } else {
                minOf(c.flatDiscount, subtotal)
            }
        }
    }
    val finalTotal = maxOf(0.0, subtotal + deliveryFee - discount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Your Food Cart", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Restaurant: ${cartItems.first().product.restaurantName}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Order Items List
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                cartItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            VegIndicator(isVeg = item.product.isVeg)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(item.product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("₹${"%.2f".format(item.product.price)} each", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.decreaseQuantity(item.product.id) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }
                            Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            IconButton(
                                onClick = { viewModel.addToCart(item.product) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("₹${"%.2f".format(item.product.price * item.quantity)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    if (index < cartItems.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cooking / Delivery Instructions
        OutlinedTextField(
            value = customerInstructions,
            onValueChange = { customerInstructions = it },
            label = { Text("Special cooking or delivery instructions") },
            placeholder = { Text("e.g. Leave at gate, less spicy, extra tissue") },
            modifier = Modifier.fillMaxWidth().testTag("cart_instructions_input"),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Coupon Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (appliedCoupon != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Coupon '${appliedCoupon!!.code}' Applied (-₹${"%.2f".format(discount)})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        TextButton(onClick = { viewModel.removeCoupon() }) {
                            Text("Remove", color = Color(0xFFC62828))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = couponInput,
                            onValueChange = { couponInput = it.uppercase() },
                            label = { Text("Coupon Code") },
                            modifier = Modifier.weight(1f).testTag("coupon_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (couponInput.isNotBlank()) {
                                    viewModel.applyCoupon(couponInput)
                                    couponInput = ""
                                }
                            },
                            modifier = Modifier.testTag("apply_coupon_button")
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Delivery Address Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Delivery Address (Ajara, MH)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    TextButton(onClick = { showAddAddressDialog = true }) {
                        Text("+ Add Address")
                    }
                }

                if (selectedAddress != null) {
                    val addr = selectedAddress!!
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE23744), modifier = Modifier.padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("${addr.recipientName} (${addr.label}) - ${addr.phone}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("${addr.addressLine}, ${addr.landmark}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${addr.city}, Maharashtra - ${addr.pincode}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                } else {
                    Text(
                        "No address saved yet. Please add your delivery address in Ajara.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPaymentMethod = PaymentMethod.ONLINE }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPaymentMethod == PaymentMethod.ONLINE,
                        onClick = { selectedPaymentMethod = PaymentMethod.ONLINE }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Online Payment (UPI / Cards / Netbanking)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Instant payment confirmation via secure gateway", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (settings?.isCodEnabled == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentMethod = PaymentMethod.COD }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == PaymentMethod.COD,
                            onClick = { selectedPaymentMethod = PaymentMethod.COD }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Cash on Delivery (COD)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Pay cash to delivery rider when food arrives", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bill Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Bill Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Item Subtotal", fontSize = 13.sp)
                    Text("₹${"%.2f".format(subtotal)}", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Fee", fontSize = 13.sp)
                    if (deliveryFee == 0.0) {
                        Text("FREE", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Text("₹${"%.2f".format(deliveryFee)}", fontSize = 13.sp)
                    }
                }

                if (discount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Coupon Discount", fontSize = 13.sp, color = Color(0xFF2E7D32))
                        Text("-₹${"%.2f".format(discount)}", fontSize = 13.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TO PAY", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("₹${"%.2f".format(finalTotal)}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFE23744))
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Place Order Button
        Button(
            onClick = {
                errorMessage = null
                isPlacingOrder = true
                viewModel.placeCustomerOrder(
                    paymentMethod = selectedPaymentMethod,
                    instructions = customerInstructions,
                    onSuccess = { order ->
                        isPlacingOrder = false
                        onOrderPlaced(order)
                    },
                    onError = { err ->
                        isPlacingOrder = false
                        errorMessage = err
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("place_order_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744)),
            enabled = !isPlacingOrder && selectedAddress != null
        ) {
            Text(
                if (isPlacingOrder) "Processing Payment & Order..." else "PLACE ORDER • ₹${"%.2f".format(finalTotal)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismiss = { showAddAddressDialog = false },
            onSave = { label, name, phone, line, landmark, city, pin, isDef ->
                viewModel.addCustomerAddress(label, name, phone, line, landmark, city, pin, isDef)
                showAddAddressDialog = false
            }
        )
    }
}

@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onSave: (label: String, name: String, phone: String, line: String, landmark: String, city: String, pin: String, isDef: Boolean) -> Unit
) {
    var label by remember { mutableStateOf("Home") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Ajara") }
    var pincode by remember { mutableStateOf("416505") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Delivery Address") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Home", "Work", "Other").forEach { lbl ->
                        FilterChip(
                            selected = label == lbl,
                            onClick = { label = lbl },
                            label = { Text(lbl) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Recipient Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = addressLine, onValueChange = { addressLine = it }, label = { Text("House / Street / Area") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = landmark, onValueChange = { landmark = it }, label = { Text("Landmark (Optional)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = pincode, onValueChange = { pincode = it }, label = { Text("Pincode") }, modifier = Modifier.weight(1f))
                }
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank() || phone.isBlank() || addressLine.isBlank() || city.isBlank() || pincode.isBlank()) {
                    error = "Please fill in all mandatory fields"
                } else {
                    onSave(label, name, phone, addressLine, landmark, city, pincode, true)
                }
            }) {
                Text("Save Address")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: TTFoodViewModel,
    onBackToHome: () -> Unit
) {
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val order = orders.find { it.id == orderId }

    if (order == null) {
        EmptyStateView(
            icon = Icons.Default.Fastfood,
            title = "Order not found",
            description = "The requested order could not be located.",
            actionText = "Back to Home",
            onActionClick = onBackToHome
        )
        return
    }

    val orderItems by viewModel.repository.getOrderItems(order.id).collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Order #${order.orderNumber}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("From ${order.restaurantName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            OrderStatusBadge(status = order.orderStatus)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prominent Delivery OTP Card for customer
        if (order.orderStatus != OrderStatus.DELIVERED && order.orderStatus != OrderStatus.CANCELLED) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E7D32)))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("CONFIDENTIAL DELIVERY OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.deliveryOtp,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B5E20),
                        letterSpacing = 6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Share this 4-digit code ONLY with your delivery partner when they arrive with your order.",
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFC8E6C9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "SMS Gateway Status: Direct In-App Verification Mode",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B5E20),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Order Lifecycle Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Order Status Journey", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(14.dp))

                val steps = listOf(
                    OrderStatus.ORDER_PLACED to "Order Placed",
                    OrderStatus.SELLER_ACCEPTED to "Restaurant Accepted",
                    OrderStatus.PREPARING to "Food Being Prepared",
                    OrderStatus.READY_FOR_PICKUP to "Ready for Pickup",
                    OrderStatus.RIDER_ASSIGNED to "Rider Assigned (${order.riderName ?: "Finding rider..."})",
                    OrderStatus.PICKED_UP to "Picked Up by Rider",
                    OrderStatus.OUT_FOR_DELIVERY to "Out for Delivery",
                    OrderStatus.DELIVERED to "Delivered Successfully"
                )

                val currentStepIndex = steps.indexOfFirst { it.first == order.orderStatus }

                steps.forEachIndexed { index, pair ->
                    val isDone = index <= currentStepIndex && order.orderStatus != OrderStatus.CANCELLED
                    val isCurrent = index == currentStepIndex && order.orderStatus != OrderStatus.CANCELLED

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDone) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isDone) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text("${index + 1}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(if (index < currentStepIndex && order.orderStatus != OrderStatus.CANCELLED) Color(0xFF2E7D32) else Color(0xFFE0E0E0))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                pair.second,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) Color(0xFFE23744) else if (isDone) Color.Black else Color.Gray,
                                fontSize = 13.sp
                            )
                            if (isCurrent) {
                                Text("In Progress...", fontSize = 11.sp, color = Color(0xFFE23744), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                if (order.orderStatus == OrderStatus.CANCELLED) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Order Cancelled: ${order.cancelReason ?: "Cancelled by seller"}",
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delivery details & items
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Delivery Destination", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${order.customerName} • ${order.customerPhone}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(order.deliveryAddress, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${order.deliveryCity} - ${order.deliveryPincode}", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Items Ordered (${orderItems.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                orderItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.quantity}x ${item.productName}", fontSize = 13.sp)
                        Text("₹${"%.2f".format(item.price * item.quantity)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Paid / Due", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("₹${"%.2f".format(order.totalAmount)} (${order.paymentMethod.display})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFE23744))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
fun CustomerOrdersHistoryScreen(
    viewModel: TTFoodViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val orders by viewModel.customerOrders.collectAsStateWithLifecycle()
    var ratingOrder by remember { mutableStateOf<OrderEntity?>(null) }

    if (orders.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Fastfood,
            title = "No orders yet.",
            description = "Your placed orders will show up here in real time."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDetails(order.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(order.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Order #${order.orderNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OrderStatusBadge(status = order.orderStatus)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total: ₹${"%.2f".format(order.totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Payment: ${order.paymentStatus.display}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                        }

                        Row {
                            if (order.orderStatus == OrderStatus.DELIVERED) {
                                OutlinedButton(
                                    onClick = { ratingOrder = order },
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFA000))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rate", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Button(
                                onClick = { onNavigateToDetails(order.id) },
                                modifier = Modifier.height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))
                            ) {
                                Text("Track", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (ratingOrder != null) {
        ReviewDialog(
            order = ratingOrder!!,
            onDismiss = { ratingOrder = null },
            onSubmit = { rating, comment ->
                viewModel.submitReview(
                    orderId = ratingOrder!!.id,
                    restaurantId = ratingOrder!!.sellerId,
                    restaurantName = ratingOrder!!.restaurantName,
                    rating = rating,
                    comment = comment
                )
                ratingOrder = null
            }
        )
    }
}

@Composable
fun ReviewDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate ${order.restaurantName}") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("How was your meal?", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) Color(0xFFFFA000) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Write your review (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment) }) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
