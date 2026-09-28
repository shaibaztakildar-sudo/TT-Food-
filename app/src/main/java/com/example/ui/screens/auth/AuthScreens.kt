package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserRole
import com.example.viewmodel.TTFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: TTFoodViewModel,
    onAuthSuccess: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var isLoginMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // App Branding Header
        Surface(
            shape = CircleShape,
            color = Color(0xFFE23744),
            modifier = Modifier.size(68.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "TT",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "TT Food Delivery",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Serving Ajara, Maharashtra",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Role Selector Chips
        Text(
            text = "Select Portal / Role",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RoleCard(
                title = "Customer",
                icon = Icons.Default.Person,
                isSelected = selectedRole == UserRole.CUSTOMER,
                onClick = { selectedRole = UserRole.CUSTOMER },
                modifier = Modifier.weight(1f)
            )
            RoleCard(
                title = "Seller",
                icon = Icons.Default.Business,
                isSelected = selectedRole == UserRole.SELLER,
                onClick = { selectedRole = UserRole.SELLER },
                modifier = Modifier.weight(1f)
            )
            RoleCard(
                title = "Rider",
                icon = Icons.Default.DeliveryDining,
                isSelected = selectedRole == UserRole.RIDER,
                onClick = { selectedRole = UserRole.RIDER },
                modifier = Modifier.weight(1f)
            )
            RoleCard(
                title = "Admin",
                icon = Icons.Default.Security,
                isSelected = selectedRole == UserRole.ADMIN,
                onClick = {
                    selectedRole = UserRole.ADMIN
                    isLoginMode = true // Admin only has secure login
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Login / Register Tabs (Disabled for Admin)
        if (selectedRole != UserRole.ADMIN) {
            SecondaryTabRow(
                selectedTabIndex = if (isLoginMode) 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = isLoginMode,
                    onClick = { isLoginMode = true },
                    text = { Text("Sign In") },
                    modifier = Modifier.testTag("tab_signin")
                )
                Tab(
                    selected = !isLoginMode,
                    onClick = { isLoginMode = false },
                    text = {
                        Text(
                            when (selectedRole) {
                                UserRole.CUSTOMER -> "Register"
                                UserRole.SELLER -> "Register Restaurant"
                                UserRole.RIDER -> "Join as Rider"
                                else -> "Sign Up"
                            }
                        )
                    },
                    modifier = Modifier.testTag("tab_signup")
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                if (selectedRole == UserRole.ADMIN) {
                    AdminLoginForm(viewModel = viewModel, onAuthSuccess = onAuthSuccess)
                } else if (isLoginMode) {
                    LoginForm(
                        role = selectedRole,
                        viewModel = viewModel,
                        onAuthSuccess = onAuthSuccess
                    )
                } else {
                    when (selectedRole) {
                        UserRole.CUSTOMER -> CustomerRegisterForm(viewModel = viewModel, onAuthSuccess = onAuthSuccess)
                        UserRole.SELLER -> SellerRegisterForm(viewModel = viewModel, onAuthSuccess = onAuthSuccess)
                        UserRole.RIDER -> RiderRegisterForm(viewModel = viewModel, onAuthSuccess = onAuthSuccess)
                        else -> {}
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RoleCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFFE23744) else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Card(
        onClick = onClick,
        modifier = modifier
            .height(72.dp)
            .testTag("role_${title.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = contentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LoginForm(
    role: UserRole,
    viewModel: TTFoodViewModel,
    onAuthSuccess: () -> Unit
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isOtpMethod by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${role.name.lowercase().replaceFirstChar { it.uppercase() }} Sign In",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (isOtpMethod) "Using Mobile OTP" else "Using Email / Password",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = {
                isOtpMethod = !isOtpMethod
                generatedOtp = null
            }) {
                Text(if (isOtpMethod) "Switch to Password" else "Switch to OTP")
            }
        }

        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = { Text(if (isOtpMethod) "Mobile Number" else "Email or Mobile") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_identifier_input"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isOtpMethod) KeyboardType.Phone else KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isOtpMethod) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter OTP") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("login_otp_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (emailOrPhone.trim().length >= 10) {
                            val otp = (1000 + (Math.random() * 9000).toInt()).toString()
                            generatedOtp = otp
                            password = otp // Auto-fill for convenience
                            viewModel.setUiMessage("OTP Sent: $otp")
                        } else {
                            errorMessage = "Please enter valid 10-digit mobile number"
                        }
                    },
                    modifier = Modifier.testTag("send_otp_button")
                ) {
                    Text("Get OTP")
                }
            }
            if (generatedOtp != null) {
                Text(
                    text = "Verification Code: $generatedOtp",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                if (emailOrPhone.isBlank()) {
                    errorMessage = "Please enter your email or phone."
                    return@Button
                }
                viewModel.login(emailOrPhone, password) { success, msg ->
                    if (success) {
                        onAuthSuccess()
                    } else {
                        errorMessage = msg
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))
        ) {
            Text("Sign In", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomerRegisterForm(
    viewModel: TTFoodViewModel,
    onAuthSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Create Customer Account", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth().testTag("cust_reg_name"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Mobile Number") },
            modifier = Modifier.fillMaxWidth().testTag("cust_reg_phone"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth().testTag("cust_reg_email"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("cust_reg_password"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                if (name.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all details."
                    return@Button
                }
                viewModel.signupCustomer(name, phone, email, password) { success, msg ->
                    if (success) {
                        onAuthSuccess()
                    } else {
                        errorMessage = msg
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("cust_register_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))
        ) {
            Text("Register Customer", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SellerRegisterForm(
    viewModel: TTFoodViewModel,
    onAuthSuccess: () -> Unit
) {
    var restaurantName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var bankIfsc by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var docUri by remember { mutableStateOf<String?>("fssai_license_proof.pdf") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Register Restaurant Partner", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text("Requires Admin Approval before receiving orders", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = restaurantName,
            onValueChange = { restaurantName = it },
            label = { Text("Restaurant / Business Name") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_rest_name"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Owner Full Name") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_owner_name"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Business Mobile Number") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_phone"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Business Email") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_email"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_password"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Kitchen Address in Ajara") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_address"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = panNumber,
            onValueChange = { panNumber = it.uppercase() },
            label = { Text("Business PAN Number") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_pan"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = bankAccount,
                onValueChange = { bankAccount = it },
                label = { Text("Bank Account #") },
                modifier = Modifier.weight(1.2f).testTag("seller_reg_bank_acc"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = bankIfsc,
                onValueChange = { bankIfsc = it.uppercase() },
                label = { Text("IFSC") },
                modifier = Modifier.weight(0.8f).testTag("seller_reg_ifsc"),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = upiId,
            onValueChange = { upiId = it },
            label = { Text("Payout UPI ID (e.g. restaurant@upi)") },
            modifier = Modifier.fillMaxWidth().testTag("seller_reg_upi"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Business Proof / FSSAI", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text(docUri ?: "No file chosen", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick = {
                    docUri = "fssai_cert_${System.currentTimeMillis()}.pdf"
                }) {
                    Text("Attach Doc", fontSize = 11.sp)
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                if (restaurantName.isBlank() || ownerName.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all mandatory fields."
                    return@Button
                }
                viewModel.signupSeller(
                    restaurantName = restaurantName,
                    ownerName = ownerName,
                    phone = phone,
                    email = email,
                    pass = password,
                    address = address.ifBlank { "Main Road, Ajara, Maharashtra" },
                    pan = panNumber,
                    bankAcc = bankAccount,
                    ifsc = bankIfsc,
                    upi = upiId,
                    docProofUri = docUri
                ) { success, msg ->
                    if (success) {
                        onAuthSuccess()
                    } else {
                        errorMessage = msg
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("seller_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))
        ) {
            Text("Submit Seller Application", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RiderRegisterForm(
    viewModel: TTFoodViewModel,
    onAuthSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Motorcycle") }
    var vehicleNumber by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var licenseDocUri by remember { mutableStateOf<String?>("driving_license_front.jpg") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Register as Delivery Rider", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text("Earn ₹25+ per completed delivery in Ajara", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Rider Full Name") },
            modifier = Modifier.fillMaxWidth().testTag("rider_reg_name"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Mobile Number") },
            modifier = Modifier.fillMaxWidth().testTag("rider_reg_phone"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth().testTag("rider_reg_email"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("rider_reg_password"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = vehicleType,
                onValueChange = { vehicleType = it },
                label = { Text("Vehicle Type") },
                modifier = Modifier.weight(1f).testTag("rider_reg_vehicle_type"),
                singleLine = true
            )
            OutlinedTextField(
                value = vehicleNumber,
                onValueChange = { vehicleNumber = it.uppercase() },
                label = { Text("Vehicle Plate #") },
                modifier = Modifier.weight(1f).testTag("rider_reg_vehicle_num"),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = licenseNumber,
            onValueChange = { licenseNumber = it.uppercase() },
            label = { Text("Driving License Number") },
            modifier = Modifier.fillMaxWidth().testTag("rider_reg_license"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Driving License Proof", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text(licenseDocUri ?: "No file chosen", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick = {
                    licenseDocUri = "license_doc_${System.currentTimeMillis()}.jpg"
                }) {
                    Text("Attach Doc", fontSize = 11.sp)
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                if (name.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all mandatory fields."
                    return@Button
                }
                viewModel.signupRider(
                    name = name,
                    phone = phone,
                    email = email,
                    pass = password,
                    vehicleType = vehicleType,
                    vehicleNumber = vehicleNumber,
                    licenseNumber = licenseNumber,
                    licenseDocUri = licenseDocUri
                ) { success, msg ->
                    if (success) {
                        onAuthSuccess()
                    } else {
                        errorMessage = msg
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("rider_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))
        ) {
            Text("Submit Rider Application", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminLoginForm(
    viewModel: TTFoodViewModel,
    onAuthSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("admin@ttfood.com") }
    var password by remember { mutableStateOf("admin123") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFC62828))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Admin Portal Authentication", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            "Protected authorization. Restricted to verified system administrators.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Admin Email") },
            modifier = Modifier.fillMaxWidth().testTag("admin_email_input"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Master Password / Key") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("admin_password_input"),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                viewModel.login(email, password) { success, msg ->
                    if (success) {
                        onAuthSuccess()
                    } else {
                        errorMessage = msg
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("admin_login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Text("Authorize & Enter Admin Panel", fontWeight = FontWeight.Bold)
        }
    }
}
