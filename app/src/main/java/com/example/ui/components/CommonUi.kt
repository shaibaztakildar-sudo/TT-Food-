package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus

@Composable
fun VegIndicator(isVeg: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (isVeg) Color(0xFF2E7D32) else Color(0xFFC62828)
    val dotColor = borderColor
    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.ORDER_PLACED -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        OrderStatus.SELLER_ACCEPTED -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        OrderStatus.PREPARING -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
        OrderStatus.READY_FOR_PICKUP -> Pair(Color(0xFFEDE7F6), Color(0xFF512DA8))
        OrderStatus.RIDER_ASSIGNED -> Pair(Color(0xFFE0F7FA), Color(0xFF00838F))
        OrderStatus.PICKED_UP -> Pair(Color(0xFFE1F5FE), Color(0xFF0277BD))
        OrderStatus.OUT_FOR_DELIVERY -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        OrderStatus.DELIVERED -> Pair(Color(0xFFE8F5E9), Color(0xFF1B5E20))
        OrderStatus.CANCELLED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = status.display,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun ThermalStickerPreviewDialog(
    order: OrderEntity,
    items: List<OrderItemEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thermal Sticker Preview",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Realistic Thermal Paper View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .background(Color(0xFFFFFDE7), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val thermalText = OrderStickerPrintHelper.generateThermalText(order, items)
                    Text(
                        text = thermalText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color.Black,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            OrderStickerPrintHelper.shareStickerText(context, order, items)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_sticker_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = {
                            OrderStickerPrintHelper.printSticker(context, order, items)
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("print_sticker_action_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23744))
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print Sticker")
                    }
                }
            }
        }
    }
}
