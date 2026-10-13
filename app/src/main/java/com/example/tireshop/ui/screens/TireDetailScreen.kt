package com.example.tireshop.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.R
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.Screen
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.data.model.Vehicle
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
fun TireDetailScreen(
    tire: TireItem?,
    compatibleVehicles: List<Vehicle>,
    language: AppLanguage,
    userRole: UserRole,
    showPurchasePrice: Boolean,
    onTogglePurchasePrice: () -> Unit,
    onQuickDecrement: (Int) -> Unit,
    onAddStock: (Int, Int, String) -> Unit,
    onArchiveTire: (Int) -> Unit,
    onNavigate: (Screen, Int?) -> Unit
) {
    if (tire == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Tire record not found.", color = TextSecondary)
        }
        return
    }

    var isAddStockDialogOpen by remember { mutableStateOf(false) }
    var isArchiveConfirmOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Tire Profile Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tire_detail_hero_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = TireBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Item #${tire.no}",
                            color = TireBlueLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    val badgeColor = when {
                        tire.quantity <= 0 -> RedLowStock
                        tire.isLowStock -> TireAmber
                        else -> GreenStock
                    }
                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${tire.quantity} Units (${tire.status})",
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_tire_shop_logo),
                        contentDescription = "Tire Icon",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SlateSurface)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = tire.brand.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TireAmber,
                            fontSize = 18.sp
                        )
                        Text(
                            text = tire.displaySize,
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            fontSize = 26.sp
                        )
                        if (tire.madeIn.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${Strings.get("made_in", language)}: ${tire.madeIn}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (tire.isLowStock) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = RedLowStock.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = RedLowStock)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Low Stock Alert! Current quantity is ${tire.quantity} (Threshold: ${tire.minStockAlert})",
                                color = RedLowStock,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Pricing Card (with Admin cost security)
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Pricing Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.get("selling_price", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "${tire.sellingPrice} SAR",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TireAmber,
                            fontSize = 22.sp
                        )
                    }

                    // Cost Price (Secured Admin view)
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = Strings.get("purchase_price", language),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            if (userRole == UserRole.ADMIN) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (showPurchasePrice) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Cost Price",
                                    tint = TireBlueLight,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { onTogglePurchasePrice() }
                                        .testTag("detail_cost_price_toggle")
                                )
                            }
                        }
                        Text(
                            text = if (userRole == UserRole.ADMIN) {
                                if (showPurchasePrice) "${tire.purchasePrice} SAR" else "•••• SAR"
                            } else {
                                "[Restricted to Admin]"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (userRole == UserRole.ADMIN) TextPrimary else TextMuted
                        )
                    }
                }
            }
        }

        // Technical Specifications Breakdown
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Tire Specifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SpecItem(label = Strings.get("width", language), value = "${tire.width} mm")
                    SpecItem(label = Strings.get("height", language), value = "${tire.height}%")
                    SpecItem(label = Strings.get("diameter", language), value = "${tire.diameter} inch")
                }

                if (tire.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "${Strings.get("notes", language)}: ${tire.notes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        // Compatible Vehicles Section
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TireBlueLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("car_compatibility", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "+ Link Car",
                        color = TireBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigate(Screen.VEHICLES, null) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (tire.carName.isNotBlank()) {
                    Text(
                        text = "Notes Fitment: ${tire.carName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                if (compatibleVehicles.isEmpty() && tire.carName.isBlank()) {
                    Text(
                        text = "No specific vehicle models linked yet for this tire size.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                } else {
                    compatibleVehicles.forEach { v ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "•", color = TireBlue, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = v.fullName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row (Quick -1 Sale, + Add Stock, Edit, Archive)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onQuickDecrement(tire.no) },
                    enabled = tire.quantity > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = TireBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("detail_quick_sale_button")
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.get("quick_decrement", language), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { isAddStockDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenStock),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("detail_add_stock_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.get("add_stock_btn", language), fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(Screen.ADD_EDIT, tire.no) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("detail_edit_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.get("edit", language), color = TextPrimary)
                }

                OutlinedButton(
                    onClick = { isArchiveConfirmOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("detail_archive_button")
                ) {
                    Icon(Icons.Default.Archive, contentDescription = null, tint = RedLowStock, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Strings.get("archive", language), color = RedLowStock)
                }
            }
        }
    }

    // Add Stock Dialog
    if (isAddStockDialogOpen) {
        var addedQtyStr by remember { mutableStateOf("4") }
        var notesStr by remember { mutableStateOf("Restock shipment") }

        AlertDialog(
            onDismissRequest = { isAddStockDialogOpen = false },
            title = { Text(text = "Add Stock to #${tire.no} (${tire.displaySize})", color = TextPrimary) },
            text = {
                Column {
                    Text(text = "Current Stock: ${tire.quantity}", color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = addedQtyStr,
                        onValueChange = { addedQtyStr = it },
                        label = { Text("Quantity to Add (+)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenStock,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_stock_qty_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 4, 8).forEach { q ->
                            Surface(
                                color = SlateSurface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { addedQtyStr = q.toString() }
                            ) {
                                Text(
                                    text = "+$q",
                                    color = TireBlueLight,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notesStr,
                        onValueChange = { notesStr = it },
                        label = { Text("Movement Note / Supplier") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenStock,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val q = addedQtyStr.toIntOrNull() ?: 0
                        if (q > 0) {
                            onAddStock(tire.no, q, notesStr)
                            isAddStockDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenStock),
                    modifier = Modifier.testTag("add_stock_confirm_button")
                ) {
                    Text("Confirm (+)")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddStockDialogOpen = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SlateCard
        )
    }

    // Archive Confirmation Dialog
    if (isArchiveConfirmOpen) {
        AlertDialog(
            onDismissRequest = { isArchiveConfirmOpen = false },
            title = { Text("Archive Tire #${tire.no}?", color = TextPrimary) },
            text = {
                Text(
                    "Archiving hides this tire model from active search without deleting past sales or stock movement history.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onArchiveTire(tire.no)
                        isArchiveConfirmOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedLowStock)
                ) {
                    Text("Archive")
                }
            },
            dismissButton = {
                TextButton(onClick = { isArchiveConfirmOpen = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SlateCard
        )
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
