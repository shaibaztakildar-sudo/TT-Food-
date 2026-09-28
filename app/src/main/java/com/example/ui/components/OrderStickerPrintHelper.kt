package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderStickerPrintHelper {

    fun generateThermalText(order: OrderEntity, items: List<OrderItemEntity>): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(order.createdAt))

        val sb = StringBuilder()
        sb.appendLine("================================")
        sb.appendLine("       TT FOOD DELIVERY         ")
        sb.appendLine("   Fast & Fresh in Ajara (MH)   ")
        sb.appendLine("================================")
        sb.appendLine("ORDER ID: #${order.orderNumber}")
        sb.appendLine("DATE: $dateStr")
        sb.appendLine("RESTAURANT: ${order.restaurantName}")
        sb.appendLine("--------------------------------")
        sb.appendLine("CUSTOMER DETAILS:")
        sb.appendLine("Name: ${order.customerName}")
        sb.appendLine("Phone: ${order.customerPhone}")
        sb.appendLine("Address: ${order.deliveryAddress}")
        sb.appendLine("City/Pin: ${order.deliveryCity} - ${order.deliveryPincode}")
        if (!order.customerInstructions.isNullOrBlank()) {
            sb.appendLine("Note: ${order.customerInstructions}")
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("ORDER ITEMS:")
        items.forEachIndexed { index, item ->
            val vegTag = if (item.isVeg) "[VEG]" else "[NON-VEG]"
            val itemTotal = item.price * item.quantity
            sb.appendLine("${index + 1}. $vegTag ${item.productName}")
            sb.appendLine("   Qty: ${item.quantity}  x  ₹%.2f  =  ₹%.2f".format(item.price, itemTotal))
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("Subtotal:        ₹%.2f".format(order.subtotal))
        sb.appendLine("Delivery Fee:    ₹%.2f".format(order.deliveryCharge))
        if (order.discount > 0) {
            sb.appendLine("Discount:       -₹%.2f".format(order.discount))
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("TOTAL BILL:      ₹%.2f".format(order.totalAmount))
        sb.appendLine("PAYMENT METHOD:  ${order.paymentMethod.display}")
        sb.appendLine("PAYMENT STATUS:  ${order.paymentStatus.display}")
        sb.appendLine("================================")
        sb.appendLine("Thank you for ordering with us! ")
        sb.appendLine("TT Food Delivery - Ajara")
        sb.appendLine("================================")
        return sb.toString()
    }

    fun generateHtmlThermalSticker(order: OrderEntity, items: List<OrderItemEntity>): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(order.createdAt))

        val itemsHtml = items.map { item ->
            val total = item.price * item.quantity
            val badge = if (item.isVeg) "<span style='color:green; font-weight:bold;'>[VEG]</span>" else "<span style='color:brown; font-weight:bold;'>[NON-VEG]</span>"
            """
            <tr>
                <td style="padding: 4px 0;">$badge ${item.productName} <br><small>₹${"%.2f".format(item.price)} x ${item.quantity}</small></td>
                <td style="text-align: right; padding: 4px 0; vertical-align: top; font-weight: bold;">₹${"%.2f".format(total)}</td>
            </tr>
            """.trimIndent()
        }.joinToString("\n")

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Sticker #${order.orderNumber}</title>
            <style>
                @page { size: 80mm auto; margin: 0; }
                body {
                    font-family: 'Courier New', Courier, monospace, sans-serif;
                    width: 76mm;
                    margin: 0 auto;
                    padding: 8px;
                    color: #000;
                    background: #fff;
                    font-size: 13px;
                    line-height: 1.25;
                }
                .text-center { text-align: center; }
                .divider { border-top: 1px dashed #000; margin: 8px 0; }
                .double-divider { border-top: 2px solid #000; margin: 8px 0; }
                .brand { font-size: 20px; font-weight: bold; letter-spacing: 1px; }
                .order-id { font-size: 18px; font-weight: bold; margin: 4px 0; }
                table { width: 100%; border-collapse: collapse; }
                .bold { font-weight: bold; }
                .barcode {
                    height: 36px;
                    letter-spacing: 5px;
                    font-size: 22px;
                    font-family: monospace;
                    text-align: center;
                    border: 1px solid #000;
                    padding: 4px;
                    margin: 6px 0;
                }
            </style>
        </head>
        <body>
            <div class="text-center">
                <div class="brand">TT FOOD DELIVERY</div>
                <div style="font-size: 11px;">Ajara, Maharashtra</div>
                <div class="double-divider"></div>
                <div class="order-id">ORDER: #${order.orderNumber}</div>
                <div class="barcode">|| | | ||| | || ||</div>
                <div style="font-size: 11px;">$dateStr</div>
            </div>

            <div class="divider"></div>
            <div><strong>Restaurant:</strong> ${order.restaurantName}</div>
            <div class="divider"></div>
            <div><strong>Customer:</strong> ${order.customerName}</div>
            <div><strong>Mobile:</strong> ${order.customerPhone}</div>
            <div><strong>Address:</strong> ${order.deliveryAddress}</div>
            <div><strong>City:</strong> ${order.deliveryCity} (${order.deliveryPincode})</div>
            ${if (!order.customerInstructions.isNullOrBlank()) "<div><strong>Instructions:</strong> ${order.customerInstructions}</div>" else ""}

            <div class="double-divider"></div>
            <div class="bold text-center" style="margin-bottom: 4px;">ORDER ITEMS</div>
            <table>
                $itemsHtml
            </table>

            <div class="divider"></div>
            <table>
                <tr><td>Subtotal</td><td style="text-align: right;">₹${"%.2f".format(order.subtotal)}</td></tr>
                <tr><td>Delivery Fee</td><td style="text-align: right;">₹${"%.2f".format(order.deliveryCharge)}</td></tr>
                ${if (order.discount > 0) "<tr><td>Discount (${order.couponCode ?: ""})</td><td style='text-align: right;'>-₹${"%.2f".format(order.discount)}</td></tr>" else ""}
                <tr style="font-size: 16px; font-weight: bold;">
                    <td style="padding-top: 6px;">TOTAL BILL</td>
                    <td style="text-align: right; padding-top: 6px;">₹${"%.2f".format(order.totalAmount)}</td>
                </tr>
            </table>

            <div class="divider"></div>
            <div><strong>Payment Method:</strong> ${order.paymentMethod.display}</div>
            <div><strong>Payment Status:</strong> <span style="font-weight: bold;">${order.paymentStatus.display}</span></div>

            <div class="double-divider"></div>
            <div class="text-center" style="font-size: 11px;">
                *** KITCHEN / DELIVERY SLIP ***<br>
                Thank you for choosing TT Food Delivery!
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    fun printSticker(context: Context, order: OrderEntity, items: List<OrderItemEntity>) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val html = generateHtmlThermalSticker(order, items)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val printAdapter = webView.createPrintDocumentAdapter("TT_Order_${order.orderNumber}")
                val attributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A7)
                    .setResolution(PrintAttributes.Resolution("thermal", "Thermal Printer", 203, 203))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("TT_Food_Sticker_${order.orderNumber}", printAdapter, attributes)
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null)
    }

    fun shareStickerText(context: Context, order: OrderEntity, items: List<OrderItemEntity>) {
        val stickerText = generateThermalText(order, items)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "TT Food Delivery Sticker #${order.orderNumber}")
            putExtra(Intent.EXTRA_TEXT, stickerText)
        }
        context.startActivity(Intent.createChooser(intent, "Share/Print Sticker to Thermal Printer"))
    }
}
