package com.example.ui.screens.customer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.viewmodel.TTFoodViewModel

@Composable
fun CustomerProfileScreen(
    viewModel: TTFoodViewModel,
    onLogout: () -> Unit
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val addresses by viewModel.customerAddresses.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && user != null) {
            viewModel.updateProfile(user!!.name, user!!.phone, uri.toString())
        }
    }

    if (user == null) return

    val currentUser = user!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Avatar with gallery picker
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE23744),
                modifier = Modifier.size(90.dp)
            ) {
                if (!currentUser.profileImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = currentUser.profileImageUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            currentUser.name.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("${currentUser.phone} • ${currentUser.email}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showEditProfileDialog = true },
            modifier = Modifier.height(34.dp).testTag("edit_profile_button")
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Edit Profile", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Saved Delivery Addresses Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Addresses (${addresses.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    TextButton(onClick = { showAddAddressDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New")
                    }
                }

                if (addresses.isEmpty()) {
                    Text("No saved addresses in Ajara yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    addresses.forEachIndexed { index, addr ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE23744), modifier = Modifier.padding(top = 2.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("${addr.recipientName} (${addr.label})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("${addr.addressLine}, ${addr.landmark}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${addr.city} (${addr.pincode})", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteAddress(addr.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                            }
                        }
                        if (index < addresses.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("customer_logout_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }
    }

    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(currentUser.name) }
        var newPhone by remember { mutableStateOf(currentUser.phone) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank() && newPhone.isNotBlank()) {
                        viewModel.updateProfile(newName, newPhone, currentUser.profileImageUri)
                        showEditProfileDialog = false
                    }
                }) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismiss = { showAddAddressDialog = false },
            onSave = { label, rName, rPhone, line, landmark, city, pin, isDef ->
                viewModel.addCustomerAddress(label, rName, rPhone, line, landmark, city, pin, isDef)
                showAddAddressDialog = false
            }
        )
    }
}
