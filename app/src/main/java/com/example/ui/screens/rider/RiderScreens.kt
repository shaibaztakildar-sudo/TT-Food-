package com.example.ui.screens.rider

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.OrderStatusBadge
import com.example.viewmodel.TTFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderDashboardScreen(
    viewModel: TTFoodViewModel
) {
    val riderProfile by viewModel.currentRiderProfile.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    if (riderProfile == null) {
        EmptyStateView(
            icon = Icons.Default.DeliveryDining,
            title = "Rider profile not found",
            description = "Please register or sign in as a delivery partner."
        )
        return
    }

    val rider = riderProfile!!

    if (rider.status != "APPROVED") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (rider.status == "PENDING") Color(0xFFFFF3E0) else Color(0xFFFFEBEE),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (rider.status == "PENDING") Icons.Default.HourglassEmpty else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (rider.status == "PENDING") Color(0xFFE65100) else Color(0xFFC62828),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (rider.status == "PENDING") "Rider Approval Pending" else "Application ${rider.status}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (rider.status == "PENDING") {
                    "Hi ${rider.name}, your delivery rider application is currently under review by TT Food Delivery Admin. You will be activated soon to start accepting orders in Ajara."
                } else {
                    "Your rider profile is currently ${rider.status.lowercase()}. Please contact support."
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
                    Text("Vehicle: ${rider.vehicleType} (${rider.vehicleNumber})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("License #: ${rider.licenseNumber}", fontSize = 12.sp)
                    Text("Phone: ${rider.phone}", fontSize = 12.sp)
                }
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color(0xFFE0F2F1), modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Motorcycle, contentDescription = null, tint = Color(0xFF00796B))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Active Rider • ${rider.vehicleNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ONLINE", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Deliveries") },
                icon = { Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("rider_tab_deliveries")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Earnings") },
                icon = { Icon(Icons.Default.Money, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("rider_tab_earnings")
            )
        }

        when (selectedTab) {
            0 -> RiderDeliveriesView(viewModel = viewModel)
            1 -> RiderEarningsView(viewModel = viewModel)
        }
    }
}

@Composable
fun RiderDeliveriesView(viewModel: TTFoodViewModel) {
    val assignedOrders by viewModel.assignedRiderOrders.collectAsStateWithLifecycle()
    val availableOrders by viewModel.availableRiderOrders.collectAsStateWithLifecycle()
    val activeDelivery = assignedOrders.find { it.orderStatus != OrderStatus.DELIVERED && it.orderStatus != OrderStatus.CANCELLED }

    var otpDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Delivery Task Section
        if (activeDelivery != null) {
            item {
                Text("Current Active Delivery", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFFE23744))
            }
            item {
                ActiveDeliveryCard(
                    order = activeDelivery,
                    viewModel = viewModel,
                    onOpenOtpDialog = { otpDialogOrder = activeDelivery },
                    onNavigateToMap = { address ->
                        val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode("$address, Ajara, Maharashtra"))
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        try {
                            context.startActivity(mapIntent)
                        } catch (_: Exception) {
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode("$address, Ajara, Maharashtra")))
                            context.startActivity(webIntent)
                        }
                    },
                    onCallCustomer = { phone ->
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        context.startActivity(intent)
                    }
                )
            }
        }

        // Available Ready Orders
        item {
            Text("Orders Ready for Pickup in Ajara (${availableOrders.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }

        if (availableOrders.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.Receipt,
                    title = "No orders waiting for pickup",
                    description = "When restaurant kitchens mark orders as 'Ready for Pickup', they will appear here instantly."
                )
            }
        } else {
            items(availableOrders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("available_order_${order.id}"),
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
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                Text("EARN ₹25.00", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Pickup: ${order.restaurantName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        Text("Drop: ${order.deliveryAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Bill: ₹${"%.2f".format(order.totalAmount)} (${order.paymentMethod.display})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.riderAcceptOrder(order.id) },
                            modifier = Modifier.fillMaxWidth().testTag("rider_accept_btn_${order.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744)),
                            enabled = activeDelivery == null // Only one delivery at a time
                        ) {
                            Text(if (activeDelivery == null) "ACCEPT THIS DELIVERY" else "COMPLETE CURRENT ORDER FIRST")
                        }
                    }
                }
            }
        }
    }

    // Delivery OTP Dialog
    if (otpDialogOrder != null) {
        DeliveryOtpVerificationDialog(
            order = otpDialogOrder!!,
            onDismiss = { otpDialogOrder = null },
            onVerify = { otp, onResult ->
                viewModel.riderDeliverOrder(otpDialogOrder!!.id, otp) { success, msg ->
                    onResult(success, msg)
                    if (success) {
                        otpDialogOrder = null
                    }
                }
            }
        )
    }
}

@Composable
fun ActiveDeliveryCard(
    order: OrderEntity,
    viewModel: TTFoodViewModel,
    onOpenOtpDialog: () -> Unit,
    onNavigateToMap: (String) -> Unit,
    onCallCustomer: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("active_delivery_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE23744)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pickup & Drop
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Pickup From: ${order.restaurantName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Ajara Market Area", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Deliver To: ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${order.deliveryAddress}, ${order.deliveryCity}", fontSize = 12.sp)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Collect Amount: ${if (order.paymentStatus.name.contains("COD")) "₹%.2f (CASH)".format(order.totalAmount) else "PAID ONLINE"}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (order.paymentStatus.name.contains("COD")) Color(0xFFE65100) else Color(0xFF2E7D32))
                    Text("Customer Phone: ${order.customerPhone}", fontSize = 12.sp)
                }

                Row {
                    IconButton(onClick = { onCallCustomer(order.customerPhone) }) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF2E7D32))
                    }
                    IconButton(onClick = { onNavigateToMap(order.deliveryAddress) }) {
                        Icon(Icons.Default.Map, contentDescription = "Map", tint = Color(0xFF1565C0))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Transitions
            when (order.orderStatus) {
                OrderStatus.RIDER_ASSIGNED -> {
                    Button(
                        onClick = { viewModel.riderPickUpOrder(order.id) },
                        modifier = Modifier.fillMaxWidth().testTag("rider_pickup_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("CONFIRM PICKUP FROM RESTAURANT")
                    }
                }
                OrderStatus.PICKED_UP -> {
                    Button(
                        onClick = { viewModel.riderOutForDelivery(order.id) },
                        modifier = Modifier.fillMaxWidth().testTag("rider_out_for_delivery_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                    ) {
                        Text("START RIDE / OUT FOR DELIVERY")
                    }
                }
                OrderStatus.OUT_FOR_DELIVERY -> {
                    Button(
                        onClick = onOpenOtpDialog,
                        modifier = Modifier.fillMaxWidth().testTag("rider_deliver_order_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DELIVER ORDER (ENTER OTP)")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun DeliveryOtpVerificationDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onVerify: (otp: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var enteredOtp by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verify Delivery OTP") },
        text = {
            Column {
                Text(
                    "Ask customer ${order.customerName} for the 4-digit Delivery OTP displayed on their order screen.",
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = enteredOtp,
                    onValueChange = {
                        if (it.length <= 4) enteredOtp = it
                    },
                    label = { Text("4-Digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("rider_otp_input"),
                    singleLine = true
                )
                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Security Notice: No bypass allowed. Accurate OTP verification guarantees payment credit.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredOtp.length != 4) {
                        errorText = "Please enter 4-digit OTP"
                        return@Button
                    }
                    isVerifying = true
                    errorText = null
                    onVerify(enteredOtp) { success, msg ->
                        isVerifying = false
                        if (!success) {
                            errorText = msg
                        }
                    }
                },
                enabled = !isVerifying,
                modifier = Modifier.testTag("verify_otp_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(if (isVerifying) "Verifying..." else "Verify & Complete Delivery")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RiderEarningsView(viewModel: TTFoodViewModel) {
    val earnings by viewModel.riderEarnings.collectAsStateWithLifecycle()
    val totalEarned = earnings.sumOf { it.netPayable }
    val pendingPayout = earnings.filter { it.status == "PENDING" }.sumOf { it.netPayable }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Rider Delivery Earnings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Earned", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${"%.2f".format(totalEarned)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pending Payout", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${"%.2f".format(pendingPayout)}", fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Delivery Trips Completed (${earnings.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (earnings.isEmpty()) {
            Text("No delivery trips completed yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            earnings.forEach { trip ->
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
                            Text(trip.referenceId ?: "Trip Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Delivery Fee Credit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+₹${"%.2f".format(trip.netPayable)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF2E7D32))
                            Text(trip.status, fontSize = 10.sp, color = if (trip.status == "PAID") Color(0xFF2E7D32) else Color(0xFFE65100))
                        }
                    }
                }
            }
        }
    }
}
