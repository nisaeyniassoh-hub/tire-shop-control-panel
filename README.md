# Tire Shop Pro (بنشر ريحان الشباب المتميز لخدمات السيارات)

**Tire Shop Pro** is a modern, responsive Android application and inventory control panel designed specifically for tire service centers and automotive repair shops.

Originally imported from Google Sheets & Google Apps Script (`tire-shop-control-panel`), this application has been rewritten into a production-grade Android application powered by Kotlin, Jetpack Compose Material 3, and offline-first persistent SQLite storage.

---

## 🌟 Key Features

### 1. Bilingual Interface (English & العربية)
- Complete Arabic localization with native Right-to-Left (RTL) layout rendering.
- Instant 1-tap language switch accessible directly from the header and settings.
- Formatted Arabic and English currency and quantity figures.

### 2. Smart Search & Vehicle Compatibility
- **Unified Fuzzy Search Bar**: Search by car name and model (e.g. `Toyota Camry`, `Camry 2016`, `Nissan Patrol`), tire size (e.g. `225/65R17`, `225/65 R17`, `225 65 17`), brand, or unique Item ID.
- **Diameter & Brand Filter Chips**: Quick filtering by rim diameters (`13"`, `14"`, `15"`, `16"`, `17"`, `18"`, `19"`, `20"`) and manufacturer brands.
- **Low Stock Filter**: 1-tap filter for items at or below minimum threshold.

### 3. Role-Based Access Control & Cost Price Security
- **Admin vs. Employee Modes**: Protects sensitive business margins.
- **Purchase/Cost Price Masking**: Cost prices are strictly restricted to Admin users and hidden behind an eye toggle icon (👁️). Employees only see retail selling prices.
- **Admin PIN Protection**: Default PIN `1234` protects administrative access and manual stock adjustments.

### 4. Fast Counter Sales (POS) & Stock Decrement
- **Quick "-1" Sale Button**: 1-tap decrement directly from the dashboard or inventory lists for counter walk-ins.
- **New Sale (Bulk POS Checkout)**: Select tire, specify pair/set quantities (1, 2, 4), override unit prices if discounted, record salesperson names, and generate customer receipts.
- **Stock Guard**: Prevents overselling when requested quantities exceed in-stock numbers.

### 5. Inventory & Add/Edit Management
- **Automatic Size Calculator**: Live `Width/Height R Diameter` preview (e.g. typing Width `205`, Height `55`, Rim `16` generates `205/55R16`).
- **Duplicate ID Prevention**: Ensures Item Numbers remain unique.
- **Soft-Archiving**: Tires can be safely archived without deleting transaction and movement histories.

### 6. Stock Movements Audit Trail
- Logs every inventory transaction: Purchases, Counter Sales, Bulk Sales, Restocks, and Adjustments with timestamps, user roles, quantity deltas, and resulting inventory levels.

### 7. Google Sheets CSV Export & Synchronization
- **One-Click CSV Export**: Exports current inventory formatted to match the original Google Sheets `Tire Database` columns (`No, Tire Size, Diameter, Height, Width, Brand, Purchase Price, Made In, Selling Price, Car Name, Purchase Date, Selling Date, Quantity, Status, Notes`).
- **Clipboard Sync**: Instantly copy formatted CSV data to paste into Google Sheets.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Database**: Android SQLite with preloaded dataset of 159 real tire records
- **Asynchronous Operations**: Kotlin Coroutines & StateFlow
- **Responsive Layout**: Adaptive support for mobile phones, foldables, tablets, and desktop/DeX mode

---

## 🔐 Credentials & Default Settings

- **Admin PIN**: `1234`
- **Default Mode**: Admin (switchable to Employee in Settings or top bar)
