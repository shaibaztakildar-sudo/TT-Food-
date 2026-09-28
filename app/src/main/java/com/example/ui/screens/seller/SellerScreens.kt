package com.example.ui.screens.seller

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.ProductEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.OrderStickerPrintHelper
import com.example.ui.components.ThermalStickerPreviewDialog
import com.example.ui.components.VegIndicator
import com.example.viewmodel.TTFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    viewModel: TTFoodViewModel
) {
    val sellerProfile by viewModel.currentSellerProfile.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    if (sellerProfile == null) {
        EmptyStateView(
            icon = Icons.Default.Business,
            title = "Seller profile not found",
            description = "Please register or login with your restaurant account."
        )
        return
    }

    val profile = sellerProfile!!

    // Handle Pending, Rejected, or Suspended Status
    if (profile.status != "APPROVED") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (profile.status == "PENDING") Color(0xFFFFF3E0) else Color(0xFFFFEBEE),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (profile.status == "PENDING") Icons.Default.HourglassEmpty else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (profile.status == "PENDING") Color(0xFFE65100) else Color(0xFFC62828),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (profile.status == "PENDING") "Application Under Review" else "Account ${profile.status}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (profile.status == "PENDING") {
                    "Your restaurant '${profile.restaurantName}' application is awaiting approval from TT Food Delivery Admin. You will be able to manage your menu and receive orders once approved."
                } else {
                    "Your restaurant partner account has been ${profile.status.lowercase()}. Please contact administrator for assistance."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Restaurant: ${profile.restaurantName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Owner: ${profile.ownerName} • ${profile.phone}", fontSize = 12.sp)
                    Text("City: ${profile.city} (${profile.pincode})", fontSize = 12.sp)
                }
            }
        }
        return
    }

    // Approved Seller Dashboard
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(profile.restaurantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Partner Kitchen • Ajara, MH", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                    }
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("APPROVED", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Orders") },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("seller_tab_orders")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Menu") },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("seller_tab_menu")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Earnings") },
                icon = { Icon(Icons.Default.Money, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("seller_tab_earnings")
            )
        }

        when (selectedTab) {
            0 -> SellerOrdersView(viewModel = viewModel)
            1 -> SellerProductsView(viewModel = viewModel)
            2 -> SellerEarningsView(viewModel = viewModel)
        }
    }
}

@Composable
fun SellerOrdersView(viewModel: TTFoodViewModel) {
    val orders by viewModel.sellerOrders.collectAsStateWithLifecycle()
    var stickerOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var stickerItems by remember { mutableStateOf<List<OrderItemEntity>>(emptyList()) }
    var rejectDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    val context = LocalContext.current

    if (orders.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Receipt,
            title = "No orders received yet.",
            description = "Live incoming orders from customers in Ajara will appear here in real time."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            val orderItems by viewModel.repository.getOrderItems(order.id).collectAsStateWithLifecycle(emptyList())

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_order_card_${order.id}"),
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
                            Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Customer: ${order.customerName} (${order.customerPhone})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OrderStatusBadge(status = order.orderStatus)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Delivery: ${order.deliveryAddress}", fontSize = 12.sp)
                    if (!order.customerInstructions.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text("Note: ${order.customerInstructions}", fontSize = 11.sp, color = Color(0xFF795548), modifier = Modifier.padding(6.dp))
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Text("Items (${orderItems.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    orderItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                VegIndicator(isVeg = item.isVeg)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${item.quantity}x ${item.productName}", fontSize = 13.sp)
                            }
                            Text("₹${"%.2f".format(item.price * item.quantity)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total: ₹${"%.2f".format(order.totalAmount)}", fontWeight = FontWeight.Black, fontSize = 15.sp)
                            Text("${order.paymentMethod.display} • ${order.paymentStatus.display}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                        }

                        // CRITICAL: PRINT STICKER BUTTON
                        Button(
                            onClick = {
                                stickerOrder = order
                                stickerItems = orderItems
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                            modifier = Modifier.testTag("print_sticker_btn_${order.id}")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PRINT STICKER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Order State Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (order.orderStatus) {
                            OrderStatus.ORDER_PLACED -> {
                                OutlinedButton(
                                    onClick = { rejectDialogOrder = order },
                                    modifier = Modifier.weight(1f).testTag("seller_reject_btn_${order.id}"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                                ) {
                                    Text("REJECT")
                                }
                                Button(
                                    onClick = { viewModel.sellerAcceptOrder(order.id) },
                                    modifier = Modifier.weight(1.5f).testTag("seller_accept_btn_${order.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Text("ACCEPT ORDER")
                                }
                            }
                            OrderStatus.SELLER_ACCEPTED -> {
                                Button(
                                    onClick = { viewModel.sellerStartPreparing(order.id) },
                                    modifier = Modifier.fillMaxWidth().testTag("seller_start_prep_btn_${order.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17))
                                ) {
                                    Text("START PREPARING FOOD")
                                }
                            }
                            OrderStatus.PREPARING -> {
                                Button(
                                    onClick = { viewModel.sellerMarkReady(order.id) },
                                    modifier = Modifier.fillMaxWidth().testTag("seller_mark_ready_btn_${order.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8))
                                ) {
                                    Text("MARK READY FOR PICKUP")
                                }
                            }
                            OrderStatus.READY_FOR_PICKUP -> {
                                Surface(
                                    color = Color(0xFFEDE7F6),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Packed & Ready! Waiting for delivery rider assignment.",
                                        color = Color(0xFF512DA8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(10.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                            else -> {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Status: ${order.orderStatus.display} • Rider: ${order.riderName ?: "Assigned"}",
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Thermal Sticker Dialog
    if (stickerOrder != null) {
        ThermalStickerPreviewDialog(
            order = stickerOrder!!,
            items = stickerItems,
            onDismiss = { stickerOrder = null }
        )
    }

    // Reject Dialog
    if (rejectDialogOrder != null) {
        var reason by remember { mutableStateOf("Item out of stock") }
        AlertDialog(
            onDismissRequest = { rejectDialogOrder = null },
            title = { Text("Reject Order #${rejectDialogOrder!!.orderNumber}") },
            text = {
                Column {
                    Text("Select or enter reason for rejection:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Cancellation Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sellerRejectOrder(rejectDialogOrder!!.id, reason)
                        rejectDialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Confirm Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectDialogOrder = null }) { Text("Back") }
            }
        )
    }
}

@Composable
fun SellerProductsView(viewModel: TTFoodViewModel) {
    val products by viewModel.sellerProducts.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Restaurant Menu (${products.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744)),
                modifier = Modifier.testTag("add_product_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Dish")
            }
        }

        if (products.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.MenuBook,
                title = "No products added yet.",
                description = "Add real food items to your menu so customers in Ajara can order from you.",
                actionText = "Add Your First Dish",
                onActionClick = { showAddDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VegIndicator(isVeg = product.isVeg)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Text("₹${"%.2f".format(product.price)} • ${product.category}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (product.description.isNotBlank()) {
                                    Text(product.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = product.isAvailable,
                                    onCheckedChange = { viewModel.toggleProductAvailability(product) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = { editingProduct = product }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductDialog(
            existing = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, desc, price, cat, isVeg, img, prep ->
                viewModel.addProduct(name, desc, price, cat, isVeg, img, prep)
                showAddDialog = false
            }
        )
    }

    if (editingProduct != null) {
        ProductDialog(
            existing = editingProduct,
            onDismiss = { editingProduct = null },
            onSave = { name, desc, price, cat, isVeg, img, prep ->
                viewModel.updateProduct(
                    editingProduct!!.copy(
                        name = name,
                        description = desc,
                        price = price,
                        category = cat,
                        isVeg = isVeg,
                        imageUri = img,
                        preparationTimeMinutes = prep
                    )
                )
                editingProduct = null
            }
        )
    }
}

@Composable
fun ProductDialog(
    existing: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String, price: Double, category: String, isVeg: Boolean, image: String?, prep: Int) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var priceText by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "Main Course") }
    var isVeg by remember { mutableStateOf(existing?.isVeg ?: true) }
    var prepTimeText by remember { mutableStateOf(existing?.preparationTimeMinutes?.toString() ?: "20") }
    var imageUri by remember { mutableStateOf(existing?.imageUri) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add New Food Item" else "Edit Dish") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dish Name") },
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹)") },
                        modifier = Modifier.weight(1f).testTag("product_price_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = prepTimeText,
                        onValueChange = { prepTimeText = it },
                        label = { Text("Prep (mins)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Biryani, Snacks, Thali)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VegIndicator(isVeg = isVeg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isVeg) "Vegetarian" else "Non-Vegetarian", fontWeight = FontWeight.SemiBold)
                    }
                    Switch(checked = isVeg, onCheckedChange = { isVeg = it })
                }
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    val prep = prepTimeText.toIntOrNull() ?: 20
                    if (name.isBlank() || price == null || price <= 0) {
                        error = "Please enter a valid name and price."
                    } else {
                        onSave(name, description, price, category, isVeg, imageUri, prep)
                    }
                },
                modifier = Modifier.testTag("save_product_button")
            ) {
                Text("Save Dish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SellerEarningsView(viewModel: TTFoodViewModel) {
    val payouts by viewModel.sellerPayouts.collectAsStateWithLifecycle()
    val orders by viewModel.sellerOrders.collectAsStateWithLifecycle()

    val totalGross = payouts.sumOf { it.grossAmount }
    val totalCommission = payouts.sumOf { it.commissionDeducted }
    val netPending = payouts.filter { it.status == "PENDING" }.sumOf { it.netPayable }
    val netPaid = payouts.filter { it.status == "PAID" }.sumOf { it.netPayable }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Restaurant Payouts & Earnings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Calculated from actual completed order records", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // Earnings Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pending Payout", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${"%.2f".format(netPending)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Paid Out", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${"%.2f".format(netPaid)}", fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gross Order Sales", fontSize = 13.sp)
                    Text("₹${"%.2f".format(totalGross)}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Platform Commission (10%)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("-₹${"%.2f".format(totalCommission)}", fontSize = 13.sp, color = Color(0xFFC62828))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Payout History Records", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (payouts.isEmpty()) {
            Text("No payout records yet. Earnings generate automatically upon order delivery.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            payouts.forEach { payout ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(payout.referenceId ?: "Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Gross: ₹${"%.2f".format(payout.grossAmount)} • Fee: -₹${"%.2f".format(payout.commissionDeducted)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${"%.2f".format(payout.netPayable)}", fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Surface(
                                color = if (payout.status == "PAID") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    payout.status,
                                    color = if (payout.status == "PAID") Color(0xFF2E7D32) else Color(0xFFE65100),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
