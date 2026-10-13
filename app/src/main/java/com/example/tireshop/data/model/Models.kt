package com.example.tireshop.data.model

data class TireItem(
    val no: Int,
    val tireSize: String,
    val diameter: Int,
    val height: Int,
    val width: Int,
    val brand: String,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val quantity: Int,
    val minStockAlert: Int = 2,
    val madeIn: String = "",
    val carName: String = "",
    val purchaseDate: String = "",
    val sellingDate: String = "",
    val status: String = "Active", // Active, Sold, Reserved, Archived
    val notes: String = ""
) {
    val isLowStock: Boolean
        get() = quantity <= minStockAlert && status == "Active"

    val displaySize: String
        get() = if (tireSize.isNotBlank()) tireSize else "$width/${height}R$diameter"
}

data class Vehicle(
    val id: Long = 0,
    val make: String,
    val model: String,
    val year: String,
    val trim: String = "",
    val tireSize: String
) {
    val fullName: String
        get() = "$make $model $year ${if (trim.isNotBlank()) "($trim)" else ""}".trim()
}

data class SaleRecord(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val tireNo: Int,
    val tireSize: String,
    val brand: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val employeeName: String,
    val notes: String = ""
)

data class StockMovement(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val tireNo: Int,
    val tireSize: String,
    val changeType: String, // Purchase, Sale, FastSale, Adjustment
    val quantityChange: Int, // e.g. -1, +5
    val resultingStock: Int,
    val userRole: String, // Admin, Employee
    val notes: String = ""
)

enum class UserRole {
    ADMIN,
    EMPLOYEE
}

enum class AppLanguage {
    EN,
    AR
}

enum class Screen {
    HOME,
    SEARCH,
    TIRE_DETAIL,
    ADD_EDIT,
    NEW_SALE,
    VEHICLES,
    STOCK_MOVEMENTS,
    SETTINGS
}
