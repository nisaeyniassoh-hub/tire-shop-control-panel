package com.example.tireshop.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.tireshop.data.db.TireShopDbHelper
import com.example.tireshop.data.model.SaleRecord
import com.example.tireshop.data.model.StockMovement
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.data.model.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ShopStats(
    val totalModels: Int,
    val totalUnitsInStock: Int,
    val lowStockCount: Int,
    val totalSalesCount: Int,
    val totalRevenue: Double
)

class TireShopRepository(context: Context) {
    private val dbHelper = TireShopDbHelper(context)

    suspend fun getAllTires(): List<TireItem> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<TireItem>()
        val cursor = db.query(
            TireShopDbHelper.TABLE_TIRES,
            null,
            null,
            null,
            null,
            null,
            "${TireShopDbHelper.COL_TIRE_NO} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToTire(it))
            }
        }
        list
    }

    suspend fun getTireByNo(no: Int): TireItem? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TireShopDbHelper.TABLE_TIRES,
            null,
            "${TireShopDbHelper.COL_TIRE_NO} = ?",
            arrayOf(no.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToNext()) cursorToTire(it) else null
        }
    }

    suspend fun searchTires(
        query: String,
        diameterFilter: Int? = null,
        brandFilter: String? = null,
        statusFilter: String? = null,
        onlyLowStock: Boolean = false
    ): List<TireItem> = withContext(Dispatchers.IO) {
        val all = getAllTires()
        val cleanQuery = query.trim().lowercase()

        // Normalize tire size pattern, e.g. "225 65 17" -> "225/65r17" or "225/65 r17" -> "225/65r17"
        val normalizedQuery = cleanQuery
            .replace(" ", "")
            .replace("-", "")

        // Check if query matches any vehicle make/model/year in vehicles table
        val vehicleMatchedSizes = if (cleanQuery.isNotBlank()) {
            findTireSizesForVehicleQuery(cleanQuery)
        } else {
            emptySet()
        }

        all.filter { item ->
            // Filter by Diameter
            if (diameterFilter != null && item.diameter != diameterFilter) {
                return@filter false
            }
            // Filter by Brand
            if (brandFilter != null && brandFilter.isNotBlank() && !item.brand.equals(brandFilter, ignoreCase = true)) {
                return@filter false
            }
            // Filter by Status
            if (statusFilter != null && statusFilter.isNotBlank() && !item.status.equals(statusFilter, ignoreCase = true)) {
                return@filter false
            }
            // Filter low stock
            if (onlyLowStock && !item.isLowStock) {
                return@filter false
            }

            if (cleanQuery.isBlank()) return@filter true

            val itemSizeNormalized = item.displaySize.lowercase().replace(" ", "")

            // 1. Exact or partial match on No
            val matchesNo = item.no.toString() == cleanQuery
            // 2. Match on Brand
            val matchesBrand = item.brand.lowercase().contains(cleanQuery)
            // 3. Match on Tire Size directly or normalized
            val matchesSize = itemSizeNormalized.contains(normalizedQuery) ||
                    item.displaySize.lowercase().contains(cleanQuery)
            // 4. Match on Width / Height / Diameter numbers
            val matchesSpecs = "${item.width}/${item.height}".contains(cleanQuery) ||
                    "${item.width}".contains(cleanQuery)
            // 5. Match on Car Name / notes / country
            val matchesCar = item.carName.lowercase().contains(cleanQuery) ||
                    item.madeIn.lowercase().contains(cleanQuery) ||
                    item.notes.lowercase().contains(cleanQuery)
            // 6. Match via vehicle database fitment
            val matchesVehicleFitment = vehicleMatchedSizes.contains(item.displaySize) ||
                    vehicleMatchedSizes.contains(item.tireSize)

            matchesNo || matchesBrand || matchesSize || matchesSpecs || matchesCar || matchesVehicleFitment
        }
    }

    private fun findTireSizesForVehicleQuery(query: String): Set<String> {
        val db = dbHelper.readableDatabase
        val sizes = mutableSetOf<String>()
        val words = query.split(" ").filter { it.isNotBlank() }
        if (words.isEmpty()) return sizes

        val cursor = db.query(
            TireShopDbHelper.TABLE_VEHICLES,
            arrayOf(
                TireShopDbHelper.COL_MAKE,
                TireShopDbHelper.COL_MODEL,
                TireShopDbHelper.COL_YEAR,
                TireShopDbHelper.COL_TRIM,
                TireShopDbHelper.COL_VEHICLE_TIRE_SIZE
            ),
            null,
            null,
            null,
            null,
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val make = it.getString(0) ?: ""
                val model = it.getString(1) ?: ""
                val year = it.getString(2) ?: ""
                val trim = it.getString(3) ?: ""
                val tireSize = it.getString(4) ?: ""
                val fullVehicle = "$make $model $year $trim".lowercase()

                val allWordsMatch = words.all { w -> fullVehicle.contains(w) }
                if (allWordsMatch && tireSize.isNotBlank()) {
                    sizes.add(tireSize)
                }
            }
        }
        return sizes
    }

    suspend fun getDistinctBrands(): List<String> = withContext(Dispatchers.IO) {
        val list = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT ${TireShopDbHelper.COL_BRAND} FROM ${TireShopDbHelper.TABLE_TIRES} WHERE ${TireShopDbHelper.COL_BRAND} != '' ORDER BY ${TireShopDbHelper.COL_BRAND} ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val b = it.getString(0)
                if (!b.isNullOrBlank()) list.add(b)
            }
        }
        list
    }

    suspend fun getDistinctDiameters(): List<Int> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Int>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT ${TireShopDbHelper.COL_DIAMETER} FROM ${TireShopDbHelper.TABLE_TIRES} WHERE ${TireShopDbHelper.COL_DIAMETER} > 0 ORDER BY ${TireShopDbHelper.COL_DIAMETER} ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.getInt(0))
            }
        }
        list
    }

    suspend fun getNextTireNo(): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MAX(${TireShopDbHelper.COL_TIRE_NO}) FROM ${TireShopDbHelper.TABLE_TIRES}", null)
        var maxNo = 0
        cursor.use {
            if (it.moveToNext()) {
                maxNo = it.getInt(0)
            }
        }
        maxNo + 1
    }

    suspend fun insertTire(tire: TireItem, userRole: UserRole): Result<Unit> = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        // Check if ID already exists
        val existing = getTireByNo(tire.no)
        if (existing != null) {
            return@withContext Result.failure(Exception("Tire ID #${tire.no} already exists! Use Edit/Update instead."))
        }

        val values = ContentValues().apply {
            put(TireShopDbHelper.COL_TIRE_NO, tire.no)
            put(TireShopDbHelper.COL_TIRE_SIZE, tire.displaySize)
            put(TireShopDbHelper.COL_DIAMETER, tire.diameter)
            put(TireShopDbHelper.COL_HEIGHT, tire.height)
            put(TireShopDbHelper.COL_WIDTH, tire.width)
            put(TireShopDbHelper.COL_BRAND, tire.brand.trim())
            put(TireShopDbHelper.COL_PURCHASE_PRICE, tire.purchasePrice)
            put(TireShopDbHelper.COL_SELLING_PRICE, tire.sellingPrice)
            put(TireShopDbHelper.COL_QUANTITY, tire.quantity)
            put(TireShopDbHelper.COL_MIN_STOCK, tire.minStockAlert)
            put(TireShopDbHelper.COL_MADE_IN, tire.madeIn)
            put(TireShopDbHelper.COL_CAR_NAME, tire.carName)
            put(TireShopDbHelper.COL_PURCHASE_DATE, tire.purchaseDate)
            put(TireShopDbHelper.COL_SELLING_DATE, tire.sellingDate)
            put(TireShopDbHelper.COL_STATUS, tire.status)
            put(TireShopDbHelper.COL_NOTES, tire.notes)
        }

        val res = db.insert(TireShopDbHelper.TABLE_TIRES, null, values)
        if (res == -1L) {
            return@withContext Result.failure(Exception("Failed to insert tire record into database"))
        }

        // Log movement
        logMovement(
            tireNo = tire.no,
            tireSize = tire.displaySize,
            changeType = "Initial Entry",
            qtyChange = tire.quantity,
            resultingStock = tire.quantity,
            userRole = userRole.name,
            notes = "New tire model registered"
        )

        Result.success(Unit)
    }

    suspend fun updateTire(tire: TireItem, userRole: UserRole, reason: String = "Update"): Result<Unit> = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val oldTire = getTireByNo(tire.no) ?: return@withContext Result.failure(Exception("Tire #${tire.no} not found"))

        val values = ContentValues().apply {
            put(TireShopDbHelper.COL_TIRE_SIZE, tire.displaySize)
            put(TireShopDbHelper.COL_DIAMETER, tire.diameter)
            put(TireShopDbHelper.COL_HEIGHT, tire.height)
            put(TireShopDbHelper.COL_WIDTH, tire.width)
            put(TireShopDbHelper.COL_BRAND, tire.brand.trim())
            put(TireShopDbHelper.COL_PURCHASE_PRICE, tire.purchasePrice)
            put(TireShopDbHelper.COL_SELLING_PRICE, tire.sellingPrice)
            put(TireShopDbHelper.COL_QUANTITY, tire.quantity)
            put(TireShopDbHelper.COL_MIN_STOCK, tire.minStockAlert)
            put(TireShopDbHelper.COL_MADE_IN, tire.madeIn)
            put(TireShopDbHelper.COL_CAR_NAME, tire.carName)
            put(TireShopDbHelper.COL_PURCHASE_DATE, tire.purchaseDate)
            put(TireShopDbHelper.COL_SELLING_DATE, tire.sellingDate)
            put(TireShopDbHelper.COL_STATUS, tire.status)
            put(TireShopDbHelper.COL_NOTES, tire.notes)
        }

        val count = db.update(
            TireShopDbHelper.TABLE_TIRES,
            values,
            "${TireShopDbHelper.COL_TIRE_NO} = ?",
            arrayOf(tire.no.toString())
        )

        if (count > 0 && oldTire.quantity != tire.quantity) {
            val delta = tire.quantity - oldTire.quantity
            logMovement(
                tireNo = tire.no,
                tireSize = tire.displaySize,
                changeType = reason,
                qtyChange = delta,
                resultingStock = tire.quantity,
                userRole = userRole.name,
                notes = "Quantity updated from ${oldTire.quantity} to ${tire.quantity}"
            )
        }

        Result.success(Unit)
    }

    suspend fun decrementStock(
        tireNo: Int,
        employeeName: String,
        userRole: UserRole
    ): Result<TireItem> = withContext(Dispatchers.IO) {
        val tire = getTireByNo(tireNo) ?: return@withContext Result.failure(Exception("Tire not found"))
        if (tire.quantity <= 0) {
            return@withContext Result.failure(Exception("Cannot decrement: Tire #${tireNo} is out of stock!"))
        }

        val newQuantity = tire.quantity - 1
        val updated = tire.copy(
            quantity = newQuantity,
            status = if (newQuantity == 0) "Sold" else tire.status
        )

        updateTire(updated, userRole, "Quick Counter Sale")

        // Record sale
        val db = dbHelper.writableDatabase
        val saleCv = ContentValues().apply {
            put(TireShopDbHelper.COL_SALE_TIMESTAMP, System.currentTimeMillis())
            put(TireShopDbHelper.COL_SALE_TIRE_NO, tire.no)
            put(TireShopDbHelper.COL_SALE_TIRE_SIZE, tire.displaySize)
            put(TireShopDbHelper.COL_SALE_BRAND, tire.brand)
            put(TireShopDbHelper.COL_SALE_QUANTITY, 1)
            put(TireShopDbHelper.COL_SALE_UNIT_PRICE, tire.sellingPrice)
            put(TireShopDbHelper.COL_SALE_TOTAL_PRICE, tire.sellingPrice)
            put(TireShopDbHelper.COL_SALE_EMPLOYEE, employeeName.ifBlank { "Staff" })
            put(TireShopDbHelper.COL_SALE_NOTES, "Quick -1 Sale Button")
        }
        db.insert(TireShopDbHelper.TABLE_SALES, null, saleCv)

        Result.success(updated)
    }

    suspend fun recordSale(
        tireNo: Int,
        quantity: Int,
        unitPrice: Double,
        employeeName: String,
        notes: String,
        userRole: UserRole
    ): Result<SaleRecord> = withContext(Dispatchers.IO) {
        if (quantity <= 0) return@withContext Result.failure(Exception("Quantity must be greater than zero"))
        val tire = getTireByNo(tireNo) ?: return@withContext Result.failure(Exception("Tire not found"))

        if (tire.quantity < quantity) {
            return@withContext Result.failure(Exception("Insufficient stock! Available: ${tire.quantity}, Requested: $quantity"))
        }

        val newQuantity = tire.quantity - quantity
        val updated = tire.copy(
            quantity = newQuantity,
            status = if (newQuantity == 0) "Sold" else tire.status
        )

        updateTire(updated, userRole, "Sale")

        val totalPrice = quantity * unitPrice
        val db = dbHelper.writableDatabase
        val saleCv = ContentValues().apply {
            put(TireShopDbHelper.COL_SALE_TIMESTAMP, System.currentTimeMillis())
            put(TireShopDbHelper.COL_SALE_TIRE_NO, tire.no)
            put(TireShopDbHelper.COL_SALE_TIRE_SIZE, tire.displaySize)
            put(TireShopDbHelper.COL_SALE_BRAND, tire.brand)
            put(TireShopDbHelper.COL_SALE_QUANTITY, quantity)
            put(TireShopDbHelper.COL_SALE_UNIT_PRICE, unitPrice)
            put(TireShopDbHelper.COL_SALE_TOTAL_PRICE, totalPrice)
            put(TireShopDbHelper.COL_SALE_EMPLOYEE, employeeName.ifBlank { "Staff" })
            put(TireShopDbHelper.COL_SALE_NOTES, notes)
        }
        val id = db.insert(TireShopDbHelper.TABLE_SALES, null, saleCv)

        val record = SaleRecord(
            id = id,
            tireNo = tire.no,
            tireSize = tire.displaySize,
            brand = tire.brand,
            quantity = quantity,
            unitPrice = unitPrice,
            totalPrice = totalPrice,
            employeeName = employeeName,
            notes = notes
        )

        Result.success(record)
    }

    suspend fun addStock(
        tireNo: Int,
        addedQuantity: Int,
        userRole: UserRole,
        notes: String = ""
    ): Result<TireItem> = withContext(Dispatchers.IO) {
        if (addedQuantity <= 0) return@withContext Result.failure(Exception("Added quantity must be positive"))
        val tire = getTireByNo(tireNo) ?: return@withContext Result.failure(Exception("Tire not found"))

        val newQuantity = tire.quantity + addedQuantity
        val updated = tire.copy(
            quantity = newQuantity,
            status = if (tire.status == "Sold") "Active" else tire.status
        )

        updateTire(updated, userRole, "Restock / +$addedQuantity")
        Result.success(updated)
    }

    suspend fun archiveTire(tireNo: Int, userRole: UserRole): Result<Unit> = withContext(Dispatchers.IO) {
        val tire = getTireByNo(tireNo) ?: return@withContext Result.failure(Exception("Tire not found"))
        val updated = tire.copy(status = "Archived")
        updateTire(updated, userRole, "Archived")
        Result.success(Unit)
    }

    private fun logMovement(
        tireNo: Int,
        tireSize: String,
        changeType: String,
        qtyChange: Int,
        resultingStock: Int,
        userRole: String,
        notes: String
    ) {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(TireShopDbHelper.COL_MOV_TIMESTAMP, System.currentTimeMillis())
            put(TireShopDbHelper.COL_MOV_TIRE_NO, tireNo)
            put(TireShopDbHelper.COL_MOV_TIRE_SIZE, tireSize)
            put(TireShopDbHelper.COL_MOV_TYPE, changeType)
            put(TireShopDbHelper.COL_MOV_QTY_CHANGE, qtyChange)
            put(TireShopDbHelper.COL_MOV_RESULTING_STOCK, resultingStock)
            put(TireShopDbHelper.COL_MOV_USER, userRole)
            put(TireShopDbHelper.COL_MOV_NOTES, notes)
        }
        db.insert(TireShopDbHelper.TABLE_MOVEMENTS, null, cv)
    }

    suspend fun getAllMovements(): List<StockMovement> = withContext(Dispatchers.IO) {
        val list = mutableListOf<StockMovement>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TireShopDbHelper.TABLE_MOVEMENTS,
            null,
            null,
            null,
            null,
            null,
            "${TireShopDbHelper.COL_MOV_TIMESTAMP} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    StockMovement(
                        id = it.getLong(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_TIMESTAMP)),
                        tireNo = it.getInt(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_TIRE_NO)),
                        tireSize = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_TIRE_SIZE)) ?: "",
                        changeType = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_TYPE)) ?: "",
                        quantityChange = it.getInt(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_QTY_CHANGE)),
                        resultingStock = it.getInt(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_RESULTING_STOCK)),
                        userRole = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_USER)) ?: "",
                        notes = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MOV_NOTES)) ?: ""
                    )
                )
            }
        }
        list
    }

    suspend fun getAllSales(): List<SaleRecord> = withContext(Dispatchers.IO) {
        val list = mutableListOf<SaleRecord>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TireShopDbHelper.TABLE_SALES,
            null,
            null,
            null,
            null,
            null,
            "${TireShopDbHelper.COL_SALE_TIMESTAMP} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    SaleRecord(
                        id = it.getLong(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_TIMESTAMP)),
                        tireNo = it.getInt(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_TIRE_NO)),
                        tireSize = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_TIRE_SIZE)) ?: "",
                        brand = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_BRAND)) ?: "",
                        quantity = it.getInt(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_QUANTITY)),
                        unitPrice = it.getDouble(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_UNIT_PRICE)),
                        totalPrice = it.getDouble(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_TOTAL_PRICE)),
                        employeeName = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_EMPLOYEE)) ?: "",
                        notes = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_SALE_NOTES)) ?: ""
                    )
                )
            }
        }
        list
    }

    suspend fun getAllVehicles(): List<Vehicle> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Vehicle>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TireShopDbHelper.TABLE_VEHICLES,
            null,
            null,
            null,
            null,
            null,
            "${TireShopDbHelper.COL_MAKE} ASC, ${TireShopDbHelper.COL_MODEL} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Vehicle(
                        id = it.getLong(it.getColumnIndexOrThrow(TireShopDbHelper.COL_VEHICLE_ID)),
                        make = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MAKE)) ?: "",
                        model = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_MODEL)) ?: "",
                        year = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_YEAR)) ?: "",
                        trim = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_TRIM)) ?: "",
                        tireSize = it.getString(it.getColumnIndexOrThrow(TireShopDbHelper.COL_VEHICLE_TIRE_SIZE)) ?: ""
                    )
                )
            }
        }
        list
    }

    suspend fun addVehicle(vehicle: Vehicle): Result<Unit> = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(TireShopDbHelper.COL_MAKE, vehicle.make.trim())
            put(TireShopDbHelper.COL_MODEL, vehicle.model.trim())
            put(TireShopDbHelper.COL_YEAR, vehicle.year.trim())
            put(TireShopDbHelper.COL_TRIM, vehicle.trim.trim())
            put(TireShopDbHelper.COL_VEHICLE_TIRE_SIZE, vehicle.tireSize.trim())
        }
        db.insert(TireShopDbHelper.TABLE_VEHICLES, null, cv)
        Result.success(Unit)
    }

    suspend fun getCompatibleVehiclesForTireSize(tireSize: String): List<Vehicle> = withContext(Dispatchers.IO) {
        val cleanSize = tireSize.trim().replace(" ", "").lowercase()
        val allVehicles = getAllVehicles()
        allVehicles.filter { v ->
            v.tireSize.trim().replace(" ", "").lowercase() == cleanSize
        }
    }

    suspend fun getStats(): ShopStats = withContext(Dispatchers.IO) {
        val tires = getAllTires()
        val sales = getAllSales()

        val totalModels = tires.size
        val totalUnits = tires.filter { it.status == "Active" }.sumOf { it.quantity }
        val lowStock = tires.count { it.isLowStock }
        val totalRevenue = sales.sumOf { it.totalPrice }

        ShopStats(
            totalModels = totalModels,
            totalUnitsInStock = totalUnits,
            lowStockCount = lowStock,
            totalSalesCount = sales.size,
            totalRevenue = totalRevenue
        )
    }

    suspend fun exportInventoryToCsv(): String = withContext(Dispatchers.IO) {
        val tires = getAllTires()
        val sb = StringBuilder()
        // Header matching Google Sheets Tire Database exactly
        sb.append("No,Tire Size,Diameter,Height,Width,Brand,Purchase Price,Made In,Selling Price,Car Name,Purchase Date,Selling Date,Quantity,Status,Notes\n")
        for (t in tires) {
            sb.append("${t.no},")
            sb.append("\"${t.displaySize}\",")
            sb.append("${t.diameter},")
            sb.append("${t.height},")
            sb.append("${t.width},")
            sb.append("\"${t.brand.replace("\"", "\"\"")}\",")
            sb.append("${t.purchasePrice},")
            sb.append("\"${t.madeIn}\",")
            sb.append("${t.sellingPrice},")
            sb.append("\"${t.carName.replace("\"", "\"\"")}\",")
            sb.append("\"${t.purchaseDate}\",")
            sb.append("\"${t.sellingDate}\",")
            sb.append("${t.quantity},")
            sb.append("\"${t.status}\",")
            sb.append("\"${t.notes.replace("\"", "\"\"")}\"\n")
        }
        sb.toString()
    }

    private fun cursorToTire(cursor: Cursor): TireItem {
        return TireItem(
            no = cursor.getInt(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_TIRE_NO)),
            tireSize = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_TIRE_SIZE)) ?: "",
            diameter = cursor.getInt(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_DIAMETER)),
            height = cursor.getInt(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_HEIGHT)),
            width = cursor.getInt(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_WIDTH)),
            brand = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_BRAND)) ?: "",
            purchasePrice = cursor.getDouble(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_PURCHASE_PRICE)),
            sellingPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_SELLING_PRICE)),
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_QUANTITY)),
            minStockAlert = cursor.getInt(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_MIN_STOCK)),
            madeIn = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_MADE_IN)) ?: "",
            carName = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_CAR_NAME)) ?: "",
            purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_PURCHASE_DATE)) ?: "",
            sellingDate = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_SELLING_DATE)) ?: "",
            status = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_STATUS)) ?: "Active",
            notes = cursor.getString(cursor.getColumnIndexOrThrow(TireShopDbHelper.COL_NOTES)) ?: ""
        )
    }
}
