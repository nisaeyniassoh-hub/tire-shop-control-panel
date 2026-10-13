package com.example.tireshop.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.R
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.Screen
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.data.repository.ShopStats
import com.example.tireshop.ui.i18n.Strings
import com.example.tireshop.ui.theme.GreenStock
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
fun HomeScreen(
    stats: ShopStats,
    recentTires: List<TireItem>,
    language: AppLanguage,
    userRole: UserRole,
    showPurchasePrice: Boolean,
    onSearchClick: () -> Unit,
    onNavigate: (Screen, Int?) -> Unit,
    onTogglePurchasePrice: () -> Unit,
    onQuickDecrement: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Prominent Top Search Bar
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SlateCard,
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TireBlue.copy(alpha = 0.5f))),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() }
                    .testTag("home_search_bar_clickable")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TireBlueLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = Strings.get("search_hint", language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 2. Hero Visual Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_tire_shop),
                        contentDescription = "Tire Workshop Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xEE090D16)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = Strings.get("shop_name", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Professional Inventory, Counter Sales & Fitment Control Panel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TireAmber,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3. Summary KPI Metrics (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = Strings.get("total_models", language),
                        value = "${stats.totalModels}",
                        icon = Icons.Default.Inventory,
                        color = TireBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.SEARCH, null) }
                    )
                    KpiCard(
                        title = Strings.get("in_stock_pieces", language),
                        value = "${stats.totalUnitsInStock}",
                        icon = Icons.Default.PointOfSale,
                        color = GreenStock,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.SEARCH, null) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = Strings.get("low_stock", language),
                        value = "${stats.lowStockCount}",
                        icon = Icons.Default.Warning,
                        color = if (stats.lowStockCount > 0) RedLowStock else TextSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.SEARCH, null) }
                    )
                    KpiCard(
                        title = Strings.get("today_sales", language),
                        value = "${stats.totalRevenue.toInt()} SAR",
                        icon = Icons.Default.History,
                        color = TireAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.STOCK_MOVEMENTS, null) }
                    )
                }
            }
        }

        // 4. Quick Action Buttons
        item {
            Text(
                text = Strings.get("quick_actions", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                item {
                    QuickActionButton(
                        label = Strings.get("new_sale", language),
                        icon = Icons.Default.PointOfSale,
                        color = GreenStock,
                        onClick = { onNavigate(Screen.NEW_SALE, null) }
                    )
                }
                item {
                    QuickActionButton(
                        label = Strings.get("add_tire", language),
                        icon = Icons.Default.Add,
                        color = TireBlue,
                        onClick = { onNavigate(Screen.ADD_EDIT, null) }
                    )
                }
                item {
                    QuickActionButton(
                        label = Strings.get("vehicles", language),
                        icon = Icons.Default.DirectionsCar,
                        color = TireAmber,
                        onClick = { onNavigate(Screen.VEHICLES, null) }
                    )
                }
                item {
                    QuickActionButton(
                        label = Strings.get("movements", language),
                        icon = Icons.Default.History,
                        color = TireBlueLight,
                        onClick = { onNavigate(Screen.STOCK_MOVEMENTS, null) }
                    )
                }
            }
        }

        // 5. Fast Counter Sales / Inventory Snapshot
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get("counter_sale", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${Strings.get("all", language)} (${recentTires.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TireBlue,
                    modifier = Modifier.clickable { onNavigate(Screen.SEARCH, null) }
                )
            }
        }

        items(recentTires.take(12)) { tire ->
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

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}
