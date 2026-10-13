package com.example.tireshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.Screen
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.ui.i18n.Strings
import com.example.tireshop.ui.theme.RedLowStock
import com.example.tireshop.ui.theme.SlateBorder
import com.example.tireshop.ui.theme.SlateCard
import com.example.tireshop.ui.theme.SlateSurface
import com.example.tireshop.ui.theme.TextMuted
import com.example.tireshop.ui.theme.TextPrimary
import com.example.tireshop.ui.theme.TextSecondary
import com.example.tireshop.ui.theme.TireAmber
import com.example.tireshop.ui.theme.TireBlue

@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDiameter: Int?,
    onSelectDiameter: (Int?) -> Unit,
    selectedBrand: String?,
    onSelectBrand: (String?) -> Unit,
    onlyLowStock: Boolean,
    onToggleLowStock: () -> Unit,
    allDiameters: List<Int>,
    allBrands: List<String>,
    tires: List<TireItem>,
    language: AppLanguage,
    userRole: UserRole,
    showPurchasePrice: Boolean,
    onTogglePurchasePrice: () -> Unit,
    onQuickDecrement: (Int) -> Unit,
    onNavigate: (Screen, Int?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_screen_container")
    ) {
        // Search Input Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = Strings.get("search_hint", language),
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TireBlue
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_query_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips: Low Stock Toggle & Rim Diameters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Low stock filter chip
                item {
                    FilterChip(
                        selected = onlyLowStock,
                        onClick = onToggleLowStock,
                        label = { Text("⚠️ Low Stock", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedLowStock.copy(alpha = 0.25f),
                            selectedLabelColor = RedLowStock,
                            containerColor = SlateSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (onlyLowStock) RedLowStock else SlateBorder,
                            enabled = true,
                            selected = onlyLowStock
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_chip_low_stock")
                    )
                }

                // Diameter chips
                items(allDiameters) { d ->
                    val isSelected = selectedDiameter == d
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectDiameter(d) },
                        label = { Text("R$d\"", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TireBlue.copy(alpha = 0.25f),
                            selectedLabelColor = TireBlue,
                            containerColor = SlateSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) TireBlue else SlateBorder,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_chip_diameter_$d")
                    )
                }
            }

            // Brand filter chips
            if (allBrands.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = selectedBrand == null,
                            onClick = { onSelectBrand(null) },
                            label = { Text("All Brands", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TireAmber.copy(alpha = 0.25f),
                                selectedLabelColor = TireAmber,
                                containerColor = SlateSurface,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    items(allBrands.take(15)) { brand ->
                        val isSelected = selectedBrand.equals(brand, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectBrand(brand) },
                            label = { Text(brand.uppercase(), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TireAmber.copy(alpha = 0.25f),
                                selectedLabelColor = TireAmber,
                                containerColor = SlateSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSelected) TireAmber else SlateBorder,
                                enabled = true,
                                selected = isSelected
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_chip_brand_$brand")
                        )
                    }
                }
            }
        }

        // Result count header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${tires.size} ${Strings.get("inventory", language)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontSize = 13.sp
            )
            if (searchQuery.isNotEmpty() || selectedDiameter != null || selectedBrand != null || onlyLowStock) {
                Text(
                    text = "Clear Filters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TireBlue,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        onSearchQueryChange("")
                        onSelectDiameter(null)
                        onSelectBrand(null)
                        if (onlyLowStock) onToggleLowStock()
                    }
                )
            }
        }

        // Tires List
        if (tires.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Strings.get("no_results", language),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tires, key = { it.no }) { tire ->
                    TireCardItem(
                        tire = tire,
                        language = language,
                        userRole = userRole,
                        showPurchasePrice = showPurchasePrice,
                        onTogglePurchasePrice = onTogglePurchasePrice,
                        onQuickDecrement = { onQuickDecrement(tire.no) },
                        onClick = { onNavigate(Screen.TIRE_DETAIL, tire.no) }
                    )
                }
            }
        }
    }
}
