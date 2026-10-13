package com.example.tireshop.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.SaleRecord
import com.example.tireshop.data.model.Screen
import com.example.tireshop.data.model.StockMovement
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.data.model.Vehicle
import com.example.tireshop.data.repository.ShopStats
import com.example.tireshop.data.repository.TireShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TireShopUiState(
    val currentScreen: Screen = Screen.HOME,
    val selectedTireNo: Int? = null,
    val searchQuery: String = "",
    val selectedDiameter: Int? = null,
    val selectedBrand: String? = null,
    val selectedStatus: String? = null,
    val onlyLowStock: Boolean = false,
    val userRole: UserRole = UserRole.ADMIN,
    val showPurchasePrice: Boolean = false,
    val language: AppLanguage = AppLanguage.EN,
    val tires: List<TireItem> = emptyList(),
    val allBrands: List<String> = emptyList(),
    val allDiameters: List<Int> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val sales: List<SaleRecord> = emptyList(),
    val movements: List<StockMovement> = emptyList(),
    val stats: ShopStats = ShopStats(0, 0, 0, 0, 0.0),
    val nextTireNo: Int = 1,
    val csvExportData: String = "",
    val userMessage: String? = null,
    val isAdminPinDialogOpen: Boolean = false,
    val isLoading: Boolean = false
)

class TireShopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TireShopRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(TireShopUiState())
    val uiState: StateFlow<TireShopUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val stats = repository.getStats()
            val brands = repository.getDistinctBrands()
            val diameters = repository.getDistinctDiameters()
            val vehicles = repository.getAllVehicles()
            val sales = repository.getAllSales()
            val movements = repository.getAllMovements()
            val nextNo = repository.getNextTireNo()

            val currentQuery = _uiState.value.searchQuery
            val currentDiameter = _uiState.value.selectedDiameter
            val currentBrand = _uiState.value.selectedBrand
            val currentStatus = _uiState.value.selectedStatus
            val currentLowStock = _uiState.value.onlyLowStock

            val tires = repository.searchTires(
                query = currentQuery,
                diameterFilter = currentDiameter,
                brandFilter = currentBrand,
                statusFilter = currentStatus,
                onlyLowStock = currentLowStock
            )

            _uiState.update {
                it.copy(
                    stats = stats,
                    allBrands = brands,
                    allDiameters = diameters,
                    vehicles = vehicles,
                    sales = sales,
                    movements = movements,
                    nextTireNo = nextNo,
                    tires = tires,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshFilteredTires()
    }

    fun setDiameterFilter(diameter: Int?) {
        _uiState.update {
            it.copy(selectedDiameter = if (it.selectedDiameter == diameter) null else diameter)
        }
        refreshFilteredTires()
    }

    fun setBrandFilter(brand: String?) {
        _uiState.update {
            it.copy(selectedBrand = if (it.selectedBrand == brand) null else brand)
        }
        refreshFilteredTires()
    }

    fun setStatusFilter(status: String?) {
        _uiState.update {
            it.copy(selectedStatus = if (it.selectedStatus == status) null else status)
        }
        refreshFilteredTires()
    }

    fun toggleOnlyLowStock() {
        _uiState.update { it.copy(onlyLowStock = !it.onlyLowStock) }
        refreshFilteredTires()
    }

    private fun refreshFilteredTires() {
        viewModelScope.launch {
            val state = _uiState.value
            val tires = repository.searchTires(
                query = state.searchQuery,
                diameterFilter = state.selectedDiameter,
                brandFilter = state.selectedBrand,
                statusFilter = state.selectedStatus,
                onlyLowStock = state.onlyLowStock
            )
            _uiState.update { it.copy(tires = tires) }
        }
    }

    fun navigateTo(screen: Screen, tireNo: Int? = null) {
        _uiState.update {
            it.copy(
                currentScreen = screen,
                selectedTireNo = tireNo ?: it.selectedTireNo
            )
        }
    }

    fun togglePurchasePriceVisibility() {
        val state = _uiState.value
        if (state.userRole != UserRole.ADMIN) {
            _uiState.update {
                it.copy(
                    userMessage = "Purchase price is strictly restricted to Admin users.",
                    isAdminPinDialogOpen = true
                )
            }
            return
        }
        _uiState.update { it.copy(showPurchasePrice = !it.showPurchasePrice) }
    }

    fun setUserRole(role: UserRole) {
        _uiState.update {
            it.copy(
                userRole = role,
                showPurchasePrice = if (role == UserRole.EMPLOYEE) false else it.showPurchasePrice,
                userMessage = if (role == UserRole.ADMIN) "Admin mode enabled" else "Employee mode enabled"
            )
        }
    }

    fun verifyAdminPin(pin: String): Boolean {
        return if (pin == "1234") {
            setUserRole(UserRole.ADMIN)
            _uiState.update { it.copy(isAdminPinDialogOpen = false, showPurchasePrice = true) }
            true
        } else {
            _uiState.update { it.copy(userMessage = "Incorrect PIN. Default is 1234") }
            false
        }
    }

    fun dismissAdminPinDialog() {
        _uiState.update { it.copy(isAdminPinDialogOpen = false) }
    }

    fun toggleLanguage() {
        _uiState.update {
            val newLang = if (it.language == AppLanguage.EN) AppLanguage.AR else AppLanguage.EN
            it.copy(language = newLang)
        }
    }

    fun quickDecrementStock(tireNo: Int, employeeName: String = "Counter Staff") {
        viewModelScope.launch {
            val result = repository.decrementStock(
                tireNo = tireNo,
                employeeName = employeeName,
                userRole = _uiState.value.userRole
            )
            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(userMessage = "Sale recorded! Tire #${updated.no} stock updated to ${updated.quantity}")
                }
                loadData()
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Failed to decrement stock") }
            }
        }
    }

    fun recordSale(
        tireNo: Int,
        quantity: Int,
        unitPrice: Double,
        employeeName: String,
        notes: String
    ) {
        viewModelScope.launch {
            val result = repository.recordSale(
                tireNo = tireNo,
                quantity = quantity,
                unitPrice = unitPrice,
                employeeName = employeeName,
                notes = notes,
                userRole = _uiState.value.userRole
            )
            result.onSuccess { saleRecord ->
                _uiState.update { state ->
                    state.copy(userMessage = "Sale completed! Total: ${saleRecord.totalPrice} SAR")
                }
                loadData()
                navigateTo(Screen.HOME)
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Sale failed") }
            }
        }
    }

    fun addStock(tireNo: Int, addedQty: Int, notes: String = "") {
        viewModelScope.launch {
            val result = repository.addStock(
                tireNo = tireNo,
                addedQuantity = addedQty,
                userRole = _uiState.value.userRole,
                notes = notes
            )
            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(userMessage = "Stock added! Tire #${updated.no} stock now: ${updated.quantity}")
                }
                loadData()
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Failed to add stock") }
            }
        }
    }

    fun saveTire(tire: TireItem, isEdit: Boolean) {
        viewModelScope.launch {
            val result = if (isEdit) {
                repository.updateTire(tire, _uiState.value.userRole, "Edited in form")
            } else {
                repository.insertTire(tire, _uiState.value.userRole)
            }

            result.onSuccess {
                _uiState.update {
                    it.copy(userMessage = if (isEdit) "Tire #${tire.no} updated!" else "New tire #${tire.no} created!")
                }
                loadData()
                navigateTo(Screen.TIRE_DETAIL, tire.no)
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Failed to save tire") }
            }
        }
    }

    fun archiveTire(tireNo: Int) {
        viewModelScope.launch {
            val result = repository.archiveTire(tireNo, _uiState.value.userRole)
            result.onSuccess {
                _uiState.update { it.copy(userMessage = "Tire #${tireNo} archived.") }
                loadData()
                navigateTo(Screen.HOME)
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Failed to archive tire") }
            }
        }
    }

    fun addVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            val res = repository.addVehicle(vehicle)
            res.onSuccess {
                _uiState.update { it.copy(userMessage = "Vehicle ${vehicle.fullName} fitment added!") }
                loadData()
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Failed to add vehicle") }
            }
        }
    }

    fun exportToCsv() {
        viewModelScope.launch {
            val csv = repository.exportInventoryToCsv()
            _uiState.update {
                it.copy(
                    csvExportData = csv,
                    userMessage = "CSV exported! Ready to copy to Google Sheets."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
