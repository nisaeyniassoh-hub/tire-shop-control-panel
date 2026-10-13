package com.example.tireshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.Screen
import com.example.tireshop.data.model.TireItem
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
fun VehicleFitmentScreen(
    vehicles: List<Vehicle>,
    allTires: List<TireItem>,
    language: AppLanguage,
    onAddVehicle: (Vehicle) -> Unit,
    onSearchTireSize: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    val filteredVehicles = remember(searchQuery, vehicles) {
        if (searchQuery.isBlank()) vehicles
        else vehicles.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
                    it.tireSize.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vehicle_fitment_screen")
    ) {
        // Top Action & Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get("vehicles", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Button(
                    onClick = { isAddDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TireBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_vehicle_dialog_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = Strings.get("add_vehicle_fitment", language), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search make/model (Toyota, Camry, Altima, Patrol)...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TireBlueLight) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TireBlue,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SlateSurface,
                    unfocusedContainerColor = SlateSurface
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Vehicles List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredVehicles, key = { it.id }) { v ->
                val matchingTires = remember(v.tireSize, allTires) {
                    val clean = v.tireSize.replace(" ", "").lowercase()
                    allTires.filter { it.displaySize.replace(" ", "").lowercase() == clean }
                }
                val inStockCount = matchingTires.sumOf { it.quantity }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = TireBlue.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = TireBlueLight,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${v.make} ${v.model}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${v.year} ${if (v.trim.isNotBlank()) "• ${v.trim}" else ""}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // OEM Tire Size Pill
                            Surface(
                                color = TireAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { onSearchTireSize(v.tireSize) }
                            ) {
                                Text(
                                    text = v.tireSize,
                                    color = TireAmber,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // In stock availability badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (inStockCount > 0)
                                    "✓ $inStockCount units available in stock (${matchingTires.size} models)"
                                else
                                    "⚠️ Out of stock currently",
                                color = if (inStockCount > 0) GreenStock else RedLowStock,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "View Tires →",
                                color = TireBlueLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { onSearchTireSize(v.tireSize) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Vehicle Fitment Dialog
    if (isAddDialogOpen) {
        var makeStr by remember { mutableStateOf("") }
        var modelStr by remember { mutableStateOf("") }
        var yearStr by remember { mutableStateOf("") }
        var trimStr by remember { mutableStateOf("") }
        var tireSizeStr by remember { mutableStateOf("215/55R17") }

        AlertDialog(
            onDismissRequest = { isAddDialogOpen = false },
            title = { Text(text = "Add Vehicle Fitment", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = makeStr,
                        onValueChange = { makeStr = it },
                        label = { Text("Make (e.g. Toyota, Nissan)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = modelStr,
                        onValueChange = { modelStr = it },
                        label = { Text("Model (e.g. Camry, Land Cruiser)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = yearStr,
                        onValueChange = { yearStr = it },
                        label = { Text("Year (e.g. 2018-2024)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = trimStr,
                        onValueChange = { trimStr = it },
                        label = { Text("Trim / Grade (e.g. V6 / AWD / SE)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tireSizeStr,
                        onValueChange = { tireSizeStr = it },
                        label = { Text("Compatible Tire Size (e.g. 235/45R18)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (makeStr.isNotBlank() && modelStr.isNotBlank() && tireSizeStr.isNotBlank()) {
                            onAddVehicle(
                                Vehicle(
                                    make = makeStr.trim(),
                                    model = modelStr.trim(),
                                    year = yearStr.trim(),
                                    trim = trimStr.trim(),
                                    tireSize = tireSizeStr.trim()
                                )
                            )
                            isAddDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TireBlue)
                ) {
                    Text("Save Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddDialogOpen = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SlateCard
        )
    }
}
