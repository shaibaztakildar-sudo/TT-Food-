package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.AdminSettingsEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.PayoutEntity
import com.example.data.local.entities.RiderProfileEntity
import com.example.data.local.entities.SellerProfileEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.ui.components.EmptyStateView
import com.example.ui.components.OrderStatusBadge
import com.example.viewmodel.TTFoodViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: TTFoodViewModel
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Backend Role Authorization Check
    if (currentUser?.role != UserRole.ADMIN) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Access Denied", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "The Admin panel is protected by backend role authorization and restricted to authorized administrators.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        return
    }

    val tabs = listOf("Overview", "Sellers", "Riders", "Orders", "Users", "Settings", "Coupons", "Payouts")

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = Color(0xFF1B5E20),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("TT Admin Console", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Ajara Operations Management", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("AUTHORIZED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp) },
                    modifier = Modifier.testTag("admin_tab_${title.lowercase()}")
                )
            }
        }

        when (selectedTab) {
            0 -> AdminOverviewView(viewModel = viewModel)
            1 -> AdminSellersView(viewModel = viewModel)
            2 -> AdminRidersView(viewModel = viewModel)
            3 -> AdminOrdersView(viewModel = viewModel)
            4 -> AdminUsersView(viewModel = viewModel)
            5 -> AdminSettingsView(viewModel = viewModel)
            6 -> AdminCouponsView(viewModel = viewModel)
            7 -> AdminPayoutsView(viewModel = viewModel)
        }
    }
}

@Composable
fun AdminOverviewView(viewModel: TTFoodViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val sellers by viewModel.allSellerProfiles.collectAsStateWithLifecycle()
    val riders by viewModel.allRiders.collectAsStateWithLifecycle()
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val payouts by viewModel.allPayouts.collectAsStateWithLifecycle()

    val totalCustomers = users.count { it.role == UserRole.CUSTOMER }
    val totalRevenue = orders.filter { it.orderStatus == OrderStatus.DELIVERED }.sumOf { it.totalAmount }
    val totalCommission = payouts.filter { it.role == "SELLER" }.sumOf { it.commissionDeducted }
    val completedOrders = orders.count { it.orderStatus == OrderStatus.DELIVERED }
    val activeOrders = orders.count { it.orderStatus != OrderStatus.DELIVERED && it.orderStatus != OrderStatus.CANCELLED }
    val cancelledOrders = orders.count { it.orderStatus == OrderStatus.CANCELLED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("System Metrics (Actual Database)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text("Zero sample data • Live real counts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Customers", "$totalCustomers", Icons.Default.People, Color(0xFF1565C0), modifier = Modifier.weight(1f))
            MetricCard("Restaurants", "${sellers.size}", Icons.Default.Business, Color(0xFFE65100), modifier = Modifier.weight(1f))
            MetricCard("Riders", "${riders.size}", Icons.Default.DeliveryDining, Color(0xFF00796B), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Total Orders", "${orders.size}", Icons.Default.Receipt, Color(0xFF4527A0), modifier = Modifier.weight(1f))
            MetricCard("Active", "$activeOrders", Icons.Default.Receipt, Color(0xFFF57F17), modifier = Modifier.weight(1f))
            MetricCard("Delivered", "$completedOrders", Icons.Default.CheckCircle, Color(0xFF2E7D32), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Financial Overview", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gross Delivered Volume", fontSize = 13.sp)
                    Text("₹${"%.2f".format(totalRevenue)}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Platform Commission Earned", fontSize = 13.sp, color = Color(0xFF2E7D32))
                    Text("₹${"%.2f".format(totalCommission)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF2E7D32))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cancelled Orders", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$cancelledOrders", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = color)
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun AdminSellersView(viewModel: TTFoodViewModel) {
    val sellers by viewModel.allSellerProfiles.collectAsStateWithLifecycle()

    if (sellers.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Business,
            title = "No seller registrations yet",
            description = "When restaurants apply in Ajara, they will appear here for verification and approval."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(sellers, key = { it.sellerId }) { seller ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                            Text(seller.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Owner: ${seller.ownerName} • ${seller.phone}", fontSize = 12.sp)
                        }
                        Surface(
                            color = when (seller.status) {
                                "APPROVED" -> Color(0xFFE8F5E9)
                                "PENDING" -> Color(0xFFFFF3E0)
                                else -> Color(0xFFFFEBEE)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                seller.status,
                                color = when (seller.status) {
                                    "APPROVED" -> Color(0xFF2E7D32)
                                    "PENDING" -> Color(0xFFE65100)
                                    else -> Color(0xFFC62828)
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Address: ${seller.address}, ${seller.city} (${seller.pincode})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("PAN: ${seller.panNumber} • Bank: ${seller.bankAccountNumber} (${seller.bankIfsc})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (seller.upiId.isNotBlank()) {
                        Text("UPI ID: ${seller.upiId}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (seller.status != "APPROVED") {
                            Button(
                                onClick = { viewModel.adminApproveSeller(seller.sellerId) },
                                modifier = Modifier.weight(1f).testTag("admin_approve_seller_${seller.sellerId}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("APPROVE", fontSize = 12.sp)
                            }
                        }
                        if (seller.status == "PENDING") {
                            OutlinedButton(
                                onClick = { viewModel.adminRejectSeller(seller.sellerId) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                            ) {
                                Text("REJECT", fontSize = 12.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.adminToggleSellerStatus(seller.sellerId) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (seller.status == "APPROVED") "SUSPEND" else "ACTIVATE", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRidersView(viewModel: TTFoodViewModel) {
    val riders by viewModel.allRiders.collectAsStateWithLifecycle()

    if (riders.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.DeliveryDining,
            title = "No rider applications yet",
            description = "Riders registered in Ajara will appear here for verification and activation."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(riders, key = { it.riderId }) { rider ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                            Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Phone: ${rider.phone} • ${rider.email}", fontSize = 12.sp)
                        }
                        Surface(
                            color = if (rider.status == "APPROVED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                rider.status,
                                color = if (rider.status == "APPROVED") Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Vehicle: ${rider.vehicleType} - ${rider.vehicleNumber}", fontSize = 12.sp)
                    Text("License #: ${rider.licenseNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (rider.status != "APPROVED") {
                            Button(
                                onClick = { viewModel.adminApproveRider(rider.riderId) },
                                modifier = Modifier.weight(1f).testTag("admin_approve_rider_${rider.riderId}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("APPROVE RIDER", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.adminRejectRider(rider.riderId) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                            ) {
                                Text("REJECT", fontSize = 12.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.adminRejectRider(rider.riderId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("DEACTIVATE RIDER", fontSize = 12.sp, color = Color(0xFFC62828))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersView(viewModel: TTFoodViewModel) {
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()

    if (orders.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Receipt,
            title = "No orders yet",
            description = "All orders placed by customers across Ajara will be tracked here in real time."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        OrderStatusBadge(status = order.orderStatus)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Restaurant: ${order.restaurantName} • Customer: ${order.customerName}", fontSize = 13.sp)
                    Text("Delivery: ${order.deliveryAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Rider: ${order.riderName ?: "Not Assigned"}", fontSize = 12.sp, color = Color(0xFF00796B), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount: ₹${"%.2f".format(order.totalAmount)} (${order.paymentMethod.display})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(order.paymentStatus.display, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersView(viewModel: TTFoodViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users, key = { it.id }) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${user.role.name} • ${user.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (user.role != UserRole.ADMIN) {
                        TextButton(onClick = { viewModel.adminToggleUserActive(user.id) }) {
                            Text(if (user.isActive) "BLOCK" else "UNBLOCK", color = if (user.isActive) Color(0xFFC62828) else Color(0xFF2E7D32), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsView(viewModel: TTFoodViewModel) {
    val settingsState by viewModel.settings.collectAsStateWithLifecycle()
    val settings = settingsState ?: AdminSettingsEntity()

    var fixedFee by remember(settings) { mutableStateOf(settings.fixedDeliveryFee.toString()) }
    var freeThreshold by remember(settings) { mutableStateOf(settings.freeDeliveryThreshold.toString()) }
    var isCod by remember(settings) { mutableStateOf(settings.isCodEnabled) }
    var commission by remember(settings) { mutableStateOf(settings.platformCommissionPercent.toString()) }
    var riderPay by remember(settings) { mutableStateOf(settings.riderDeliveryPay.toString()) }
    var city by remember(settings) { mutableStateOf(settings.serviceCity) }
    var pincodes by remember(settings) { mutableStateOf(settings.servicePincodes) }
    var radius by remember(settings) { mutableStateOf(settings.serviceRadiusKm.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Operational & Service Area Settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(14.dp))

        // Third Party Service Status Checkers
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Third-Party Integrations Status", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SMS OTP Gateway", fontSize = 12.sp)
                    Text("Unconfigured (In-App Direct OTP)", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Online Payment Gateway", fontSize = 12.sp)
                    Text("Verified Secure Sandbox", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Thermal Sticker Printing", fontSize = 12.sp)
                    Text("Native Android Print Ready", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Delivery Charges & Fees", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fixedFee,
                onValueChange = { fixedFee = it },
                label = { Text("Delivery Fee (₹)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = freeThreshold,
                onValueChange = { freeThreshold = it },
                label = { Text("Free Delivery Above (₹)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = commission,
                onValueChange = { commission = it },
                label = { Text("Platform Fee (%)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = riderPay,
                onValueChange = { riderPay = it },
                label = { Text("Rider Trip Pay (₹)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Cash on Delivery (COD)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Switch(checked = isCod, onCheckedChange = { isCod = it })
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Text("Service Area: Ajara, Maharashtra", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Service City") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pincodes,
            onValueChange = { pincodes = it },
            label = { Text("Allowed Pincodes (Comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updated = settings.copy(
                    fixedDeliveryFee = fixedFee.toDoubleOrNull() ?: 30.0,
                    freeDeliveryThreshold = freeThreshold.toDoubleOrNull() ?: 300.0,
                    platformCommissionPercent = commission.toDoubleOrNull() ?: 10.0,
                    riderDeliveryPay = riderPay.toDoubleOrNull() ?: 25.0,
                    isCodEnabled = isCod,
                    serviceCity = city,
                    servicePincodes = pincodes,
                    serviceRadiusKm = radius.toDoubleOrNull() ?: 12.0
                )
                viewModel.adminUpdateSettings(updated)
            },
            modifier = Modifier.fillMaxWidth().testTag("save_admin_settings_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Text("Save System Settings", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminCouponsView(viewModel: TTFoodViewModel) {
    val coupons by viewModel.allCoupons.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Coupons & Offers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Coupon")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (coupons.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.LocalOffer,
                title = "No discount coupons created",
                description = "Configure promotional coupons for customers in Ajara."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(coupons, key = { it.id }) { coupon ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text(coupon.code, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFE65100))
                                Text(
                                    if (coupon.discountPercent > 0) "${coupon.discountPercent.toInt()}% Off (Max ₹${coupon.maxDiscount.toInt()})"
                                    else "₹${coupon.flatDiscount.toInt()} Flat Off",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text("Min order ₹${coupon.minOrderAmount.toInt()} • ${coupon.description}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { viewModel.adminDeleteCoupon(coupon.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var code by remember { mutableStateOf("") }
        var percentText by remember { mutableStateOf("15") }
        var flatText by remember { mutableStateOf("0") }
        var minOrderText by remember { mutableStateOf("150") }
        var maxDiscText by remember { mutableStateOf("60") }
        var desc by remember { mutableStateOf("Special discount for Ajara foodies") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Discount Coupon") },
            text = {
                Column {
                    OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Coupon Code (e.g. AJARA15)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = percentText, onValueChange = { percentText = it }, label = { Text("Discount %") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = flatText, onValueChange = { flatText = it }, label = { Text("Flat ₹ Off") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = minOrderText, onValueChange = { minOrderText = it }, label = { Text("Min Order (₹)") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = maxDiscText, onValueChange = { maxDiscText = it }, label = { Text("Max Discount (₹)") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (code.isNotBlank()) {
                        viewModel.adminAddCoupon(
                            code = code,
                            percent = percentText.toDoubleOrNull() ?: 0.0,
                            flat = flatText.toDoubleOrNull() ?: 0.0,
                            minAmt = minOrderText.toDoubleOrNull() ?: 100.0,
                            maxDisc = maxDiscText.toDoubleOrNull() ?: 50.0,
                            desc = desc
                        )
                        showAddDialog = false
                    }
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminPayoutsView(viewModel: TTFoodViewModel) {
    val payouts by viewModel.allPayouts.collectAsStateWithLifecycle()

    if (payouts.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Money,
            title = "No payouts generated yet",
            description = "Payout liabilities to restaurants and riders generate automatically when orders are delivered."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(payouts, key = { it.id }) { payout ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                            Text(payout.beneficiaryName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${payout.role} • ${payout.referenceId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = if (payout.status == "PAID") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                payout.status,
                                color = if (payout.status == "PAID") Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Gross: ₹${"%.2f".format(payout.grossAmount)} • Platform Fee: ₹${"%.2f".format(payout.commissionDeducted)} • Net Payable: ₹${"%.2f".format(payout.netPayable)}",
                        fontSize = 12.sp
                    )

                    if (payout.status == "PENDING") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val ref = "BANK-TXN-${System.currentTimeMillis()}"
                                viewModel.adminProcessPayout(payout, ref)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("process_payout_${payout.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                        ) {
                            Text("PROCESS & MARK AS PAID")
                        }
                    }
                }
            }
        }
    }
}
