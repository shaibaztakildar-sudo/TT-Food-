package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.customer.CartScreen
import com.example.ui.screens.customer.CustomerHomeScreen
import com.example.ui.screens.customer.CustomerOrdersHistoryScreen
import com.example.ui.screens.customer.CustomerProfileScreen
import com.example.ui.screens.customer.OrderTrackingScreen
import com.example.ui.screens.rider.RiderDashboardScreen
import com.example.ui.screens.seller.SellerDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TTFoodViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TTFoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot(viewModel = viewModel)
            }
        }
    }
}

enum class CustomerScreenState {
    EXPLORE,
    CART,
    ORDERS,
    PROFILE,
    TRACKING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppRoot(viewModel: TTFoodViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var customerScreen by remember { mutableStateOf(CustomerScreenState.EXPLORE) }
    var trackingOrderId by remember { mutableStateOf<String?>(null) }
    var showAccountSwitcher by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    // Back button handling
    BackHandler(enabled = currentUser != null && customerScreen != CustomerScreenState.EXPLORE) {
        if (customerScreen == CustomerScreenState.TRACKING || customerScreen == CustomerScreenState.CART) {
            customerScreen = CustomerScreenState.EXPLORE
        } else {
            customerScreen = CustomerScreenState.EXPLORE
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentUser != null) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE23744),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("TT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "TT Food Delivery",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ajara, Maharashtra",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Current Role Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (currentUser!!.role) {
                                UserRole.CUSTOMER -> Color(0xFFE3F2FD)
                                UserRole.SELLER -> Color(0xFFFFF3E0)
                                UserRole.RIDER -> Color(0xFFE0F2F1)
                                UserRole.ADMIN -> Color(0xFFE8F5E9)
                            }
                        ) {
                            Text(
                                text = currentUser!!.role.name,
                                color = when (currentUser!!.role) {
                                    UserRole.CUSTOMER -> Color(0xFF1565C0)
                                    UserRole.SELLER -> Color(0xFFE65100)
                                    UserRole.RIDER -> Color(0xFF00796B)
                                    UserRole.ADMIN -> Color(0xFF1B5E20)
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        // Switch Portal / User
                        IconButton(
                            onClick = { showAccountSwitcher = true },
                            modifier = Modifier.testTag("switch_account_btn")
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Portal")
                        }

                        // Notifications
                        IconButton(onClick = { showNotificationsDialog = true }) {
                            BadgedBox(
                                badge = {
                                    val unread = notifications.count { !it.isRead }
                                    if (unread > 0) {
                                        Badge { Text("$unread") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }

                        // Logout
                        IconButton(
                            onClick = {
                                viewModel.logout()
                                customerScreen = CustomerScreenState.EXPLORE
                            },
                            modifier = Modifier.testTag("logout_btn")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (currentUser?.role == UserRole.CUSTOMER) {
                NavigationBar {
                    NavigationBarItem(
                        selected = customerScreen == CustomerScreenState.EXPLORE,
                        onClick = { customerScreen = CustomerScreenState.EXPLORE },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Explore") },
                        modifier = Modifier.testTag("nav_explore")
                    )
                    NavigationBarItem(
                        selected = customerScreen == CustomerScreenState.CART,
                        onClick = { customerScreen = CustomerScreenState.CART },
                        icon = {
                            BadgedBox(
                                badge = {
                                    val count = cartItems.sumOf { it.quantity }
                                    if (count > 0) {
                                        Badge { Text("$count") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart") },
                        modifier = Modifier.testTag("nav_cart")
                    )
                    NavigationBarItem(
                        selected = customerScreen == CustomerScreenState.ORDERS,
                        onClick = { customerScreen = CustomerScreenState.ORDERS },
                        icon = { Icon(Icons.Default.Receipt, contentDescription = "Orders") },
                        label = { Text("Orders") },
                        modifier = Modifier.testTag("nav_orders")
                    )
                    NavigationBarItem(
                        selected = customerScreen == CustomerScreenState.PROFILE,
                        onClick = { customerScreen = CustomerScreenState.PROFILE },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            if (currentUser == null) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        customerScreen = CustomerScreenState.EXPLORE
                    }
                )
            } else {
                when (currentUser!!.role) {
                    UserRole.CUSTOMER -> {
                        when (customerScreen) {
                            CustomerScreenState.EXPLORE -> {
                                CustomerHomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToCart = { customerScreen = CustomerScreenState.CART },
                                    onNavigateToOrderDetails = { orderId ->
                                        trackingOrderId = orderId
                                        customerScreen = CustomerScreenState.TRACKING
                                    }
                                )
                            }
                            CustomerScreenState.CART -> {
                                CartScreen(
                                    viewModel = viewModel,
                                    onOrderPlaced = { order ->
                                        trackingOrderId = order.id
                                        customerScreen = CustomerScreenState.TRACKING
                                    },
                                    onNavigateBack = {
                                        customerScreen = CustomerScreenState.EXPLORE
                                    }
                                )
                            }
                            CustomerScreenState.ORDERS -> {
                                CustomerOrdersHistoryScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetails = { orderId ->
                                        trackingOrderId = orderId
                                        customerScreen = CustomerScreenState.TRACKING
                                    }
                                )
                            }
                            CustomerScreenState.PROFILE -> {
                                CustomerProfileScreen(
                                    viewModel = viewModel,
                                    onLogout = {
                                        customerScreen = CustomerScreenState.EXPLORE
                                    }
                                )
                            }
                            CustomerScreenState.TRACKING -> {
                                OrderTrackingScreen(
                                    orderId = trackingOrderId ?: "",
                                    viewModel = viewModel,
                                    onBackToHome = {
                                        customerScreen = CustomerScreenState.EXPLORE
                                    }
                                )
                            }
                        }
                    }
                    UserRole.SELLER -> {
                        SellerDashboardScreen(viewModel = viewModel)
                    }
                    UserRole.RIDER -> {
                        RiderDashboardScreen(viewModel = viewModel)
                    }
                    UserRole.ADMIN -> {
                        AdminDashboardScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Account & Role Switcher Dialog (Allows instant testing of all four roles!)
    if (showAccountSwitcher) {
        val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

        Dialog(onDismissRequest = { showAccountSwitcher = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Switch Role / Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { showAccountSwitcher = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Text(
                        "Easily switch between roles to test the complete order workflow in Ajara:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allUsers, key = { it.id }) { u ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.switchUser(u.id)
                                        customerScreen = CustomerScreenState.EXPLORE
                                        showAccountSwitcher = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (currentUser?.id == u.id) Color(0xFFE23744).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(u.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${u.role.name} • ${u.phone.ifBlank { u.email }}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (currentUser?.id == u.id) {
                                        Text("ACTIVE", color = Color(0xFFE23744), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            viewModel.logout()
                            showAccountSwitcher = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Login / Register Another Account")
                    }
                }
            }
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        Dialog(onDismissRequest = { showNotificationsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { showNotificationsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    if (notifications.isEmpty()) {
                        Text(
                            "No notifications yet. Status updates for placed, accepted, prepared, and delivered orders will appear here.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(notifications) { notif ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(notif.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
