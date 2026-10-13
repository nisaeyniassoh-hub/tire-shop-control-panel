package com.example.tireshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.data.model.Screen
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.ui.i18n.Strings
import com.example.tireshop.ui.i18n.toLayoutDirection
import com.example.tireshop.ui.screens.AddEditTireScreen
import com.example.tireshop.ui.screens.AdminPinDialog
import com.example.tireshop.ui.screens.AppHeader
import com.example.tireshop.ui.screens.HomeScreen
import com.example.tireshop.ui.screens.NewSaleScreen
import com.example.tireshop.ui.screens.SearchScreen
import com.example.tireshop.ui.screens.SettingsScreen
import com.example.tireshop.ui.screens.StockMovementsScreen
import com.example.tireshop.ui.screens.TireDetailScreen
import com.example.tireshop.ui.screens.VehicleFitmentScreen
import com.example.tireshop.ui.theme.AsphalBg
import com.example.tireshop.ui.theme.SlateBorder
import com.example.tireshop.ui.theme.SlateCard
import com.example.tireshop.ui.theme.TextPrimary
import com.example.tireshop.ui.theme.TextSecondary
import com.example.tireshop.ui.theme.TireBlue
import com.example.tireshop.ui.theme.TireShopTheme
import com.example.tireshop.ui.viewmodel.TireShopViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TireShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TireShopTheme {
                TireShopApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TireShopApp(viewModel: TireShopViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display user messages in Snackbar
    LaunchedEffect(uiState.userMessage) {
        val msg = uiState.userMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Support back navigation
    if (uiState.currentScreen != Screen.HOME) {
        BackHandler {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    // Provide proper RTL or LTR layout direction
    CompositionLocalProvider(LocalLayoutDirection provides uiState.language.toLayoutDirection()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(AsphalBg)) {
            val isWideScreen = maxWidth > 600.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = AsphalBg,
                topBar = {
                    AppHeader(
                        title = when (uiState.currentScreen) {
                            Screen.HOME -> Strings.get("app_title", uiState.language)
                            Screen.SEARCH -> Strings.get("search", uiState.language)
                            Screen.TIRE_DETAIL -> "Tire #${uiState.selectedTireNo ?: ""}"
                            Screen.ADD_EDIT -> Strings.get("add_tire", uiState.language)
                            Screen.NEW_SALE -> Strings.get("new_sale", uiState.language)
                            Screen.VEHICLES -> Strings.get("vehicles", uiState.language)
                            Screen.STOCK_MOVEMENTS -> Strings.get("movements", uiState.language)
                            Screen.SETTINGS -> Strings.get("settings", uiState.language)
                        },
                        language = uiState.language,
                        userRole = uiState.userRole,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onRoleClick = {
                            if (uiState.userRole == UserRole.EMPLOYEE) {
                                viewModel.verifyAdminPin("prompt")
                            } else {
                                viewModel.setUserRole(UserRole.EMPLOYEE)
                            }
                        },
                        onBack = if (uiState.currentScreen != Screen.HOME) {
                            { viewModel.navigateTo(Screen.HOME) }
                        } else null
                    )
                },
                bottomBar = {
                    if (!isWideScreen) {
                        AppBottomNavigation(
                            currentScreen = uiState.currentScreen,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWideScreen) {
                        AppNavigationRail(
                            currentScreen = uiState.currentScreen,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (uiState.currentScreen) {
                            Screen.HOME -> {
                                HomeScreen(
                                    stats = uiState.stats,
                                    recentTires = uiState.tires,
                                    language = uiState.language,
                                    userRole = uiState.userRole,
                                    showPurchasePrice = uiState.showPurchasePrice,
                                    onSearchClick = { viewModel.navigateTo(Screen.SEARCH) },
                                    onNavigate = { screen, id -> viewModel.navigateTo(screen, id) },
                                    onTogglePurchasePrice = { viewModel.togglePurchasePriceVisibility() },
                                    onQuickDecrement = { viewModel.quickDecrementStock(it) }
                                )
                            }
                            Screen.SEARCH -> {
                                SearchScreen(
                                    searchQuery = uiState.searchQuery,
                                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                                    selectedDiameter = uiState.selectedDiameter,
                                    onSelectDiameter = { viewModel.setDiameterFilter(it) },
                                    selectedBrand = uiState.selectedBrand,
                                    onSelectBrand = { viewModel.setBrandFilter(it) },
                                    onlyLowStock = uiState.onlyLowStock,
                                    onToggleLowStock = { viewModel.toggleOnlyLowStock() },
                                    allDiameters = uiState.allDiameters,
                                    allBrands = uiState.allBrands,
                                    tires = uiState.tires,
                                    language = uiState.language,
                                    userRole = uiState.userRole,
                                    showPurchasePrice = uiState.showPurchasePrice,
                                    onTogglePurchasePrice = { viewModel.togglePurchasePriceVisibility() },
                                    onQuickDecrement = { viewModel.quickDecrementStock(it) },
                                    onNavigate = { screen, id -> viewModel.navigateTo(screen, id) }
                                )
                            }
                            Screen.TIRE_DETAIL -> {
                                val currentTire = uiState.tires.firstOrNull { it.no == uiState.selectedTireNo }
                                val compatibleVehicles = currentTire?.let { t ->
                                    val cleanSize = t.displaySize.replace(" ", "").lowercase()
                                    uiState.vehicles.filter { it.tireSize.replace(" ", "").lowercase() == cleanSize }
                                } ?: emptyList()

                                TireDetailScreen(
                                    tire = currentTire,
                                    compatibleVehicles = compatibleVehicles,
                                    language = uiState.language,
                                    userRole = uiState.userRole,
                                    showPurchasePrice = uiState.showPurchasePrice,
                                    onTogglePurchasePrice = { viewModel.togglePurchasePriceVisibility() },
                                    onQuickDecrement = { viewModel.quickDecrementStock(it) },
                                    onAddStock = { id, q, n -> viewModel.addStock(id, q, n) },
                                    onArchiveTire = { viewModel.archiveTire(it) },
                                    onNavigate = { screen, id -> viewModel.navigateTo(screen, id) }
                                )
                            }
                            Screen.ADD_EDIT -> {
                                val currentTire = uiState.selectedTireNo?.let { id ->
                                    uiState.tires.firstOrNull { it.no == id }
                                }
                                AddEditTireScreen(
                                    initialTire = currentTire,
                                    suggestedNo = uiState.nextTireNo,
                                    allBrands = uiState.allBrands,
                                    language = uiState.language,
                                    userRole = uiState.userRole,
                                    onSave = { tire, isEdit -> viewModel.saveTire(tire, isEdit) },
                                    onCancel = { viewModel.navigateTo(Screen.HOME) }
                                )
                            }
                            Screen.NEW_SALE -> {
                                NewSaleScreen(
                                    tires = uiState.tires,
                                    preselectedTireNo = uiState.selectedTireNo,
                                    language = uiState.language,
                                    userRole = uiState.userRole,
                                    onCompleteSale = { id, q, p, emp, notes ->
                                        viewModel.recordSale(id, q, p, emp, notes)
                                    },
                                    onCancel = { viewModel.navigateTo(Screen.HOME) }
                                )
                            }
                            Screen.VEHICLES -> {
                                VehicleFitmentScreen(
                                    vehicles = uiState.vehicles,
                                    allTires = uiState.tires,
                                    language = uiState.language,
                                    onAddVehicle = { viewModel.addVehicle(it) },
                                    onSearchTireSize = { size ->
                                        viewModel.onSearchQueryChange(size)
                                        viewModel.navigateTo(Screen.SEARCH)
                                    }
                                )
                            }
                            Screen.STOCK_MOVEMENTS -> {
                                StockMovementsScreen(
                                    movements = uiState.movements,
                                    language = uiState.language
                                )
                            }
                            Screen.SETTINGS -> {
                                SettingsScreen(
                                    language = uiState.language,
                                    userRole = uiState.userRole,
                                    csvData = uiState.csvExportData,
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onSwitchRole = { viewModel.setUserRole(it) },
                                    onExportCsv = { viewModel.exportToCsv() },
                                    onTriggerAdminPin = { viewModel.verifyAdminPin("prompt") }
                                )
                            }
                        }
                    }
                }
            }

            // Admin PIN Verification Dialog
            AdminPinDialog(
                isOpen = uiState.isAdminPinDialogOpen,
                onDismiss = { viewModel.dismissAdminPinDialog() },
                onConfirm = { pin -> viewModel.verifyAdminPin(pin) }
            )
        }
    }
}

@Composable
fun AppBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = SlateCard,
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(Screen.HOME, Icons.Default.Home, "Home"),
            Triple(Screen.SEARCH, Icons.Default.Search, "Search"),
            Triple(Screen.NEW_SALE, Icons.Default.PointOfSale, "POS Sale"),
            Triple(Screen.VEHICLES, Icons.Default.DirectionsCar, "Vehicles"),
            Triple(Screen.SETTINGS, Icons.Default.Settings, "Settings")
        )

        navItems.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { Text(text = label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TireBlue,
                    selectedTextColor = TireBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = TireBlue.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationRail(
        containerColor = SlateCard,
        contentColor = TextPrimary
    ) {
        val navItems = listOf(
            Triple(Screen.HOME, Icons.Default.Home, "Home"),
            Triple(Screen.SEARCH, Icons.Default.Search, "Search"),
            Triple(Screen.NEW_SALE, Icons.Default.PointOfSale, "POS Sale"),
            Triple(Screen.VEHICLES, Icons.Default.DirectionsCar, "Vehicles"),
            Triple(Screen.SETTINGS, Icons.Default.Settings, "Settings")
        )

        navItems.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen
            NavigationRailItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TireBlue,
                    selectedTextColor = TireBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = TireBlue.copy(alpha = 0.2f)
                )
            )
        }
    }
}
