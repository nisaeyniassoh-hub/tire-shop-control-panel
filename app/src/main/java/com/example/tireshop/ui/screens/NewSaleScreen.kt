package com.example.tireshop.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.ui.i18n.Strings
import com.example.tireshop.ui.theme.GreenStock
import com.example.tireshop.ui.theme.RedLowStock
import com.example.tireshop.ui.theme.SlateBorder
import com.example.tireshop.ui.theme.SlateCard
import com.example.tireshop.ui.theme.SlateSurface
import com.example.tireshop.ui.theme.TextMuted
import com.example.tireshop.ui.theme.TextPrimary
import com.example.tireshop.ui.theme.TextSecondary
import com.example.tireshop.ui.theme.TireAmber
import com.example.tireshop.ui.theme.TireBlue
import com.example.tireshop.ui.theme.TireBlueLight

@Composable
fun NewSaleScreen(
    tires: List<TireItem>,
    preselectedTireNo: Int?,
    language: AppLanguage,
    userRole: UserRole,
    onCompleteSale: (Int, Int, Double, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedTire by remember {
        mutableStateOf(tires.firstOrNull { it.no == preselectedTireNo } ?: tires.firstOrNull { it.quantity > 0 })
    }
    var quantity by remember { mutableIntStateOf(1) }
    var unitPriceStr by remember(selectedTire) {
        mutableStateOf(selectedTire?.sellingPrice?.toString() ?: "0.0")
    }
    var employeeName by remember { mutableStateOf("Sales Staff") }
    var customerNotes by remember { mutableStateOf("") }
    var tireFilterQuery by remember { mutableStateOf("") }

    val availableTires = remember(tireFilterQuery, tires) {
        if (tireFilterQuery.isBlank()) tires.filter { it.quantity > 0 }
        else tires.filter {
            it.quantity > 0 && (
                    it.displaySize.contains(tireFilterQuery, ignoreCase = true) ||
                            it.brand.contains(tireFilterQuery, ignoreCase = true) ||
                            it.no.toString() == tireFilterQuery
                    )
        }
    }

    val unitPrice = unitPriceStr.toDoubleOrNull() ?: 0.0
    val totalAmount = quantity * unitPrice
    val isStockInsufficient = selectedTire != null && quantity > (selectedTire?.quantity ?: 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // POS Header
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = GreenStock.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = null,
                        tint = GreenStock,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = Strings.get("new_sale", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Instant Counter POS & Stock Decrement",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Tire Selector
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "1. Select Tire from Stock",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tireFilterQuery,
                    onValueChange = { tireFilterQuery = it },
                    label = { Text("Filter available stock (e.g. 205, Dunlop, #14)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TireBlue,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableTires.take(20)) { t ->
                        val isSelected = selectedTire?.no == t.no
                        Surface(
                            color = if (isSelected) TireBlue else SlateSurface,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable {
                                    selectedTire = t
                                    unitPriceStr = t.sellingPrice.toString()
                                }
                                .testTag("select_sale_tire_${t.no}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "#${t.no} ${t.brand.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TextPrimary else TireAmber,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = t.displaySize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Stock: ${t.quantity} | ${t.sellingPrice} SAR",
                                    color = if (isSelected) TextPrimary.copy(alpha = 0.9f) else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quantity & Price Calculator
        if (selectedTire != null) {
            val tire = selectedTire!!
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "2. Quantity & Sale Amount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Selected Tire Summary
                    Surface(
                        color = SlateSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${tire.brand.uppercase()} ${tire.displaySize}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Available Units: ${tire.quantity}",
                                    color = if (tire.quantity > 2) GreenStock else RedLowStock,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Quantity Stepper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.get("quantity", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.testTag("sale_qty_decrement")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                            }

                            Text(
                                text = "$quantity",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TireBlueLight,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )

                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier.testTag("sale_qty_increment")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                            }
                        }
                    }

                    // Quick Set Buttons (1, 2, 4 = full car set)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1 to "1 Tire", 2 to "2 (Pair)", 4 to "4 (Full Set)").forEach { (q, label) ->
                            Surface(
                                color = if (quantity == q) TireBlue else SlateSurface,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { quantity = q }
                            ) {
                                Text(
                                    text = label,
                                    color = if (quantity == q) TextPrimary else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    if (isStockInsufficient) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = RedLowStock, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Warning: Requested ($quantity) exceeds available stock (${tire.quantity})!",
                                color = RedLowStock,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Unit Price Override
                    OutlinedTextField(
                        value = unitPriceStr,
                        onValueChange = { unitPriceStr = it },
                        label = { Text("Unit Price (SAR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sale_unit_price_input")
                    )

                    // Grand Total Display Card
                    Surface(
                        color = TireAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings.get("total_amount", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$totalAmount SAR",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TireAmber,
                                fontSize = 24.sp
                            )
                        }
                    }

                    // Salesperson / Vehicle Note
                    OutlinedTextField(
                        value = employeeName,
                        onValueChange = { employeeName = it },
                        label = { Text(Strings.get("customer_employee", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customerNotes,
                        onValueChange = { customerNotes = it },
                        label = { Text("Customer vehicle / Plate # / Notes") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Checkout Action
            Button(
                onClick = {
                    if (selectedTire != null && quantity > 0 && unitPrice > 0) {
                        onCompleteSale(
                            selectedTire!!.no,
                            quantity,
                            unitPrice,
                            employeeName,
                            customerNotes
                        )
                    }
                },
                enabled = !isStockInsufficient && quantity > 0,
                colors = ButtonDefaults.buttonColors(containerColor = GreenStock),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("complete_sale_button")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${Strings.get("complete_sale", language)} ($totalAmount SAR)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
