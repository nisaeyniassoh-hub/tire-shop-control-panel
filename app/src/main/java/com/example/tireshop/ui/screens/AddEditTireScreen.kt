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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.tireshop.ui.theme.RedLowStock
import com.example.tireshop.ui.theme.SlateBorder
import com.example.tireshop.ui.theme.SlateCard
import com.example.tireshop.ui.theme.SlateSurface
import com.example.tireshop.ui.theme.TextPrimary
import com.example.tireshop.ui.theme.TextSecondary
import com.example.tireshop.ui.theme.TireAmber
import com.example.tireshop.ui.theme.TireBlue
import com.example.tireshop.ui.theme.TireBlueLight

@Composable
fun AddEditTireScreen(
    initialTire: TireItem?,
    suggestedNo: Int,
    allBrands: List<String>,
    language: AppLanguage,
    userRole: UserRole,
    onSave: (TireItem, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    val isEdit = initialTire != null

    var noStr by remember { mutableStateOf(initialTire?.no?.toString() ?: suggestedNo.toString()) }
    var widthStr by remember { mutableStateOf(initialTire?.width?.takeIf { it > 0 }?.toString() ?: "205") }
    var heightStr by remember { mutableStateOf(initialTire?.height?.takeIf { it > 0 }?.toString() ?: "55") }
    var diameterStr by remember { mutableStateOf(initialTire?.diameter?.takeIf { it > 0 }?.toString() ?: "16") }
    var brandStr by remember { mutableStateOf(initialTire?.brand ?: "") }
    var madeInStr by remember { mutableStateOf(initialTire?.madeIn ?: "") }
    var sellingPriceStr by remember { mutableStateOf(initialTire?.sellingPrice?.takeIf { it > 0 }?.toString() ?: "") }
    var purchasePriceStr by remember { mutableStateOf(initialTire?.purchasePrice?.takeIf { it > 0 }?.toString() ?: "") }
    var quantityStr by remember { mutableStateOf(initialTire?.quantity?.toString() ?: "4") }
    var minStockStr by remember { mutableStateOf(initialTire?.minStockAlert?.toString() ?: "2") }
    var carNameStr by remember { mutableStateOf(initialTire?.carName ?: "") }
    var statusStr by remember { mutableStateOf(initialTire?.status ?: "Active") }
    var notesStr by remember { mutableStateOf(initialTire?.notes ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Live computed size
    val computedSize = run {
        val w = widthStr.trim()
        val h = heightStr.trim()
        val d = diameterStr.trim()
        if (w.isNotBlank() && h.isNotBlank() && d.isNotBlank()) "$w/${h}R$d" else "---"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Live Preview Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isEdit) "Edit Tire #${initialTire?.no}" else "Register New Tire Record",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Computed Size:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = computedSize,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TireBlueLight
                        )
                    }
                    Surface(
                        color = TireAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (brandStr.isNotBlank()) brandStr.uppercase() else "SELECT BRAND",
                            color = TireAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Form Fields Card
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
                // Item No
                OutlinedTextField(
                    value = noStr,
                    onValueChange = { noStr = it },
                    label = { Text("Item No / Unique ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !isEdit, // Prevent changing existing ID in edit mode
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TireBlue,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tire_no")
                )

                // Dimensions (Width / Height / Diameter)
                Text(text = "Tire Size Specifications", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = widthStr,
                        onValueChange = { widthStr = it },
                        label = { Text("Width (e.g. 205)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_tire_width")
                    )
                    OutlinedTextField(
                        value = heightStr,
                        onValueChange = { heightStr = it },
                        label = { Text("Height (e.g. 55)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_tire_height")
                    )
                    OutlinedTextField(
                        value = diameterStr,
                        onValueChange = { diameterStr = it },
                        label = { Text("Rim (e.g. 16)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_tire_diameter")
                    )
                }

                // Brand Input & Quick Suggestions
                OutlinedTextField(
                    value = brandStr,
                    onValueChange = { brandStr = it },
                    label = { Text("Tire Brand (e.g. Michelin, Bridgestone, Doublestar)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tire_brand")
                )

                if (allBrands.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allBrands.take(12)) { b ->
                            Surface(
                                color = SlateSurface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { brandStr = b }
                            ) {
                                Text(
                                    text = b.uppercase(),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Made In & Compatible Vehicles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = madeInStr,
                        onValueChange = { madeInStr = it },
                        label = { Text("Country of Origin") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_tire_made_in")
                    )
                    OutlinedTextField(
                        value = carNameStr,
                        onValueChange = { carNameStr = it },
                        label = { Text("Car Models (e.g. Camry, Altima)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_tire_car_name")
                    )
                }

                // Pricing Row (Selling Price & Cost Price)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = sellingPriceStr,
                        onValueChange = { sellingPriceStr = it },
                        label = { Text("Selling Price (SAR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_selling_price")
                    )

                    OutlinedTextField(
                        value = purchasePriceStr,
                        onValueChange = { purchasePriceStr = it },
                        label = { Text("Cost Price (Admin Only)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_purchase_price")
                    )
                }

                // Quantity & Minimum Stock Threshold
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity in Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_quantity")
                    )

                    OutlinedTextField(
                        value = minStockStr,
                        onValueChange = { minStockStr = it },
                        label = { Text("Min Stock Alert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_min_stock")
                    )
                }

                // Status Selector (Active, Reserved, Sold, Archived)
                Text(text = "Inventory Status", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Active", "Reserved", "Sold", "Archived").forEach { st ->
                        val isSelected = statusStr.equals(st, ignoreCase = true)
                        Surface(
                            color = if (isSelected) TireBlue else SlateSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { statusStr = st }
                        ) {
                            Text(
                                text = st,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notesStr,
                    onValueChange = { notesStr = it },
                    label = { Text("Notes / Storage Rack / DOT Code") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_notes")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = RedLowStock,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val no = noStr.toIntOrNull()
                    val w = widthStr.toIntOrNull() ?: 0
                    val h = heightStr.toIntOrNull() ?: 0
                    val d = diameterStr.toIntOrNull() ?: 0
                    val brand = brandStr.trim()
                    val sellPrice = sellingPriceStr.toDoubleOrNull() ?: 0.0
                    val costPrice = purchasePriceStr.toDoubleOrNull() ?: 0.0
                    val qty = quantityStr.toIntOrNull() ?: 0
                    val minStock = minStockStr.toIntOrNull() ?: 2

                    if (no == null || no <= 0) {
                        errorMessage = "Please enter a valid positive Item No"
                        return@Button
                    }
                    if (brand.isBlank()) {
                        errorMessage = "Please provide the Tire Brand"
                        return@Button
                    }

                    val finalTire = TireItem(
                        no = no,
                        tireSize = if (w > 0 && h > 0 && d > 0) "$w/${h}R$d" else computedSize,
                        diameter = d,
                        height = h,
                        width = w,
                        brand = brand,
                        purchasePrice = costPrice,
                        sellingPrice = sellPrice,
                        quantity = qty,
                        minStockAlert = minStock,
                        madeIn = madeInStr.trim(),
                        carName = carNameStr.trim(),
                        status = statusStr,
                        notes = notesStr.trim()
                    )

                    onSave(finalTire, isEdit)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TireBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("submit_tire_button")
            ) {
                Text(text = Strings.get("save", language), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = Strings.get("cancel", language), color = TextSecondary)
            }
        }
    }
}
