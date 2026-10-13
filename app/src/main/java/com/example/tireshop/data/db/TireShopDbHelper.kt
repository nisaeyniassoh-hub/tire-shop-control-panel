package com.example.tireshop.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.tireshop.data.model.SaleRecord
import com.example.tireshop.data.model.StockMovement
import com.example.tireshop.data.model.TireItem
import com.example.tireshop.data.model.Vehicle
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class TireShopDbHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "tireshop.db"
        const val DATABASE_VERSION = 1
        private const val TAG = "TireShopDbHelper"

        // Tires Table
        const val TABLE_TIRES = "tires"
        const val COL_TIRE_NO = "no"
        const val COL_TIRE_SIZE = "tire_size"
        const val COL_DIAMETER = "diameter"
        const val COL_HEIGHT = "height"
        const val COL_WIDTH = "width"
        const val COL_BRAND = "brand"
        const val COL_PURCHASE_PRICE = "purchase_price"
        const val COL_SELLING_PRICE = "selling_price"
        const val COL_QUANTITY = "quantity"
        const val COL_MIN_STOCK = "min_stock"
        const val COL_MADE_IN = "made_in"
        const val COL_CAR_NAME = "car_name"
        const val COL_PURCHASE_DATE = "purchase_date"
        const val COL_SELLING_DATE = "selling_date"
        const val COL_STATUS = "status"
        const val COL_NOTES = "notes"

        // Vehicles Table
        const val TABLE_VEHICLES = "vehicles"
        const val COL_VEHICLE_ID = "id"
        const val COL_MAKE = "make"
        const val COL_MODEL = "model"
        const val COL_YEAR = "year"
        const val COL_TRIM = "trim"
        const val COL_VEHICLE_TIRE_SIZE = "tire_size"

        // Sales Table
        const val TABLE_SALES = "sales"
        const val COL_SALE_ID = "id"
        const val COL_SALE_TIMESTAMP = "timestamp"
        const val COL_SALE_TIRE_NO = "tire_no"
        const val COL_SALE_TIRE_SIZE = "tire_size"
        const val COL_SALE_BRAND = "brand"
        const val COL_SALE_QUANTITY = "quantity"
        const val COL_SALE_UNIT_PRICE = "unit_price"
        const val COL_SALE_TOTAL_PRICE = "total_price"
        const val COL_SALE_EMPLOYEE = "employee_name"
        const val COL_SALE_NOTES = "notes"

        // Stock Movements Table
        const val TABLE_MOVEMENTS = "stock_movements"
        const val COL_MOV_ID = "id"
        const val COL_MOV_TIMESTAMP = "timestamp"
        const val COL_MOV_TIRE_NO = "tire_no"
        const val COL_MOV_TIRE_SIZE = "tire_size"
        const val COL_MOV_TYPE = "change_type"
        const val COL_MOV_QTY_CHANGE = "qty_change"
        const val COL_MOV_RESULTING_STOCK = "resulting_stock"
        const val COL_MOV_USER = "user_role"
        const val COL_MOV_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_TIRES (
                $COL_TIRE_NO INTEGER PRIMARY KEY,
                $COL_TIRE_SIZE TEXT NOT NULL,
                $COL_DIAMETER INTEGER NOT NULL,
                $COL_HEIGHT INTEGER NOT NULL,
                $COL_WIDTH INTEGER NOT NULL,
                $COL_BRAND TEXT NOT NULL,
                $COL_PURCHASE_PRICE REAL NOT NULL,
                $COL_SELLING_PRICE REAL NOT NULL,
                $COL_QUANTITY INTEGER NOT NULL,
                $COL_MIN_STOCK INTEGER NOT NULL DEFAULT 2,
                $COL_MADE_IN TEXT,
                $COL_CAR_NAME TEXT,
                $COL_PURCHASE_DATE TEXT,
                $COL_SELLING_DATE TEXT,
                $COL_STATUS TEXT NOT NULL DEFAULT 'Active',
                $COL_NOTES TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_VEHICLES (
                $COL_VEHICLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MAKE TEXT NOT NULL,
                $COL_MODEL TEXT NOT NULL,
                $COL_YEAR TEXT NOT NULL,
                $COL_TRIM TEXT,
                $COL_VEHICLE_TIRE_SIZE TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_SALES (
                $COL_SALE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SALE_TIMESTAMP INTEGER NOT NULL,
                $COL_SALE_TIRE_NO INTEGER NOT NULL,
                $COL_SALE_TIRE_SIZE TEXT NOT NULL,
                $COL_SALE_BRAND TEXT NOT NULL,
                $COL_SALE_QUANTITY INTEGER NOT NULL,
                $COL_SALE_UNIT_PRICE REAL NOT NULL,
                $COL_SALE_TOTAL_PRICE REAL NOT NULL,
                $COL_SALE_EMPLOYEE TEXT NOT NULL,
                $COL_SALE_NOTES TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_MOVEMENTS (
                $COL_MOV_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MOV_TIMESTAMP INTEGER NOT NULL,
                $COL_MOV_TIRE_NO INTEGER NOT NULL,
                $COL_MOV_TIRE_SIZE TEXT NOT NULL,
                $COL_MOV_TYPE TEXT NOT NULL,
                $COL_MOV_QTY_CHANGE INTEGER NOT NULL,
                $COL_MOV_RESULTING_STOCK INTEGER NOT NULL,
                $COL_MOV_USER TEXT NOT NULL,
                $COL_MOV_NOTES TEXT
            )
            """.trimIndent()
        )

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TIRES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VEHICLES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SALES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVEMENTS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        try {
            val jsonString = context.assets.open("initial_tires.json").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            }
            val jsonArray = JSONArray(jsonString)
            db.beginTransaction()
            try {
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val values = ContentValues().apply {
                        put(COL_TIRE_NO, obj.getInt("no"))
                        put(COL_TIRE_SIZE, obj.optString("tireSize", ""))
                        put(COL_DIAMETER, obj.optInt("diameter", 0))
                        put(COL_HEIGHT, obj.optInt("height", 0))
                        put(COL_WIDTH, obj.optInt("width", 0))
                        put(COL_BRAND, obj.optString("brand", "").trim())
                        val purchasePrice = obj.optDouble("purchasePrice", 0.0)
                        put(COL_PURCHASE_PRICE, purchasePrice)
                        // If selling price was 0 in spreadsheet, default to markup or 0
                        val sellingPrice = obj.optDouble("sellingPrice", 0.0)
                        val finalSellingPrice = if (sellingPrice > 0) sellingPrice else if (purchasePrice > 0) purchasePrice * 1.25 else 0.0
                        put(COL_SELLING_PRICE, finalSellingPrice)
                        put(COL_QUANTITY, obj.optInt("quantity", 1))
                        put(COL_MIN_STOCK, 2)
                        put(COL_MADE_IN, obj.optString("madeIn", ""))
                        put(COL_CAR_NAME, obj.optString("carName", ""))
                        put(COL_PURCHASE_DATE, obj.optString("purchaseDate", ""))
                        put(COL_SELLING_DATE, obj.optString("sellingDate", ""))
                        put(COL_STATUS, obj.optString("status", "Active"))
                        put(COL_NOTES, obj.optString("notes", ""))
                    }
                    db.insert(TABLE_TIRES, null, values)
                }
                db.setTransactionSuccessful()
                Log.d(TAG, "Successfully seeded ${jsonArray.length()} tires into database.")
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding initial tires from assets", e)
        }

        // Seed popular vehicle compatibility records
        seedInitialVehicles(db)
    }

    private fun seedInitialVehicles(db: SQLiteDatabase) {
        val initialVehicles = listOf(
            Vehicle(make = "Toyota", model = "Camry", year = "2012-2017", trim = "LE / SE", tireSize = "215/60R16"),
            Vehicle(make = "Toyota", model = "Camry", year = "2018-2024", trim = "XLE / XSE", tireSize = "235/45R18"),
            Vehicle(make = "Toyota", model = "Corolla", year = "2014-2023", trim = "Standard", tireSize = "195/65R15"),
            Vehicle(make = "Toyota", model = "Corolla", year = "2019-2024", trim = "SE", tireSize = "205/55R16"),
            Vehicle(make = "Toyota", model = "Land Cruiser", year = "2010-2021", trim = "V8 / GXR", tireSize = "285/60R18"),
            Vehicle(make = "Toyota", model = "Prado", year = "2012-2023", trim = "TX / TXL", tireSize = "265/65R17"),
            Vehicle(make = "Toyota", model = "Hilux", year = "2015-2024", trim = "4x4", tireSize = "265/65R17"),
            Vehicle(make = "Toyota", model = "Hilux", year = "2012-2020", trim = "4x2", tireSize = "205/70R15"),
            Vehicle(make = "Nissan", model = "Altima", year = "2013-2018", trim = "SV / SL", tireSize = "215/60R16"),
            Vehicle(make = "Nissan", model = "Altima", year = "2019-2024", trim = "SR / Platinum", tireSize = "235/40R19"),
            Vehicle(make = "Nissan", model = "Sunny", year = "2012-2022", trim = "1.5L", tireSize = "175/70R14"),
            Vehicle(make = "Nissan", model = "Patrol", year = "2010-2024", trim = "Y62 Platinum", tireSize = "275/60R20"),
            Vehicle(make = "Honda", model = "Accord", year = "2013-2017", trim = "EX", tireSize = "225/50R17"),
            Vehicle(make = "Honda", model = "Accord", year = "2018-2023", trim = "Sport", tireSize = "235/40R19"),
            Vehicle(make = "Honda", model = "Civic", year = "2016-2022", trim = "EX / LX", tireSize = "215/55R16"),
            Vehicle(make = "Hyundai", model = "Elantra", year = "2016-2023", trim = "GLS", tireSize = "205/55R16"),
            Vehicle(make = "Hyundai", model = "Sonata", year = "2015-2022", trim = "Limited", tireSize = "215/55R17"),
            Vehicle(make = "Hyundai", model = "Tucson", year = "2016-2023", trim = "AWD", tireSize = "225/60R17"),
            Vehicle(make = "Hyundai", model = "Accent", year = "2012-2021", trim = "Sedan", tireSize = "175/70R14"),
            Vehicle(make = "Kia", model = "Optima / K5", year = "2016-2023", trim = "LX / EX", tireSize = "215/55R17"),
            Vehicle(make = "Kia", model = "Sportage", year = "2016-2022", trim = "FWD / AWD", tireSize = "225/60R17"),
            Vehicle(make = "Ford", model = "F-150", year = "2015-2023", trim = "XLT", tireSize = "275/65R18"),
            Vehicle(make = "Chevrolet", model = "Tahoe", year = "2015-2022", trim = "LT / Premier", tireSize = "275/55R20"),
            Vehicle(make = "Small Compact", model = "City Cars", year = "Universal", trim = "R13 Economy", tireSize = "185/70R13"),
            Vehicle(make = "Compact Sedan", model = "Budget Sedans", year = "Universal", trim = "R14 Classic", tireSize = "175/65R14")
        )

        db.beginTransaction()
        try {
            for (v in initialVehicles) {
                val cv = ContentValues().apply {
                    put(COL_MAKE, v.make)
                    put(COL_MODEL, v.model)
                    put(COL_YEAR, v.year)
                    put(COL_TRIM, v.trim)
                    put(COL_VEHICLE_TIRE_SIZE, v.tireSize)
                }
                db.insert(TABLE_VEHICLES, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
