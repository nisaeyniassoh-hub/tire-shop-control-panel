package com.example.tireshop.ui.i18n

import androidx.compose.ui.unit.LayoutDirection
import com.example.tireshop.data.model.AppLanguage

object Strings {
    fun get(key: String, lang: AppLanguage): String {
        val entry = map[key] ?: return key
        return if (lang == AppLanguage.AR) entry.second else entry.first
    }

    val map = mapOf(
        "app_title" to Pair("Tire Shop Pro", "تاير شوب برو"),
        "shop_name" to Pair("Rayhan Al-Shabab Automotive Services", "بنشر ريحان الشباب المتميز لخدمات السيارات"),
        "dashboard" to Pair("Dashboard", "لوحة التحكم"),
        "search" to Pair("Search Tires", "بحث الإطارات"),
        "inventory" to Pair("Inventory", "المخزون"),
        "new_sale" to Pair("New Sale", "عملية بيع جديدة"),
        "add_tire" to Pair("Add Tire", "إضافة إطار جديد"),
        "vehicles" to Pair("Vehicle Fitments", "توافق السيارات"),
        "movements" to Pair("Stock Movements", "حركات المخزون"),
        "settings" to Pair("Settings", "الإعدادات"),
        "total_models" to Pair("Tire Models", "أنواع الإطارات"),
        "in_stock_pieces" to Pair("Units in Stock", "القطع المتوفرة"),
        "low_stock" to Pair("Low Stock Alerts", "تنبيهات نقص المخزون"),
        "today_sales" to Pair("Total Sales", "إجمالي المبيعات"),
        "search_hint" to Pair("Search car model, size (225/65R17), brand, or ID...", "ابحث بموديل السيارة، المقاس (225/65R17)، الماركة، أو الرقم..."),
        "quick_actions" to Pair("Quick Actions", "الإجراءات السريعة"),
        "low_stock_warning" to Pair("Low Stock Warning", "تحذير: مخزون منخفض"),
        "in_stock" to Pair("In Stock", "متوفر بالمخزون"),
        "out_of_stock" to Pair("Out of Stock", "نفد المخزون"),
        "selling_price" to Pair("Selling Price", "سعر البيع"),
        "purchase_price" to Pair("Cost Price (Admin Only)", "سعر الشراء (للمدير فقط)"),
        "quick_decrement" to Pair("Quick -1 Sale", "بيع سريع -1"),
        "add_stock_btn" to Pair("+ Add Stock", "+ إضافة مخزون"),
        "brand" to Pair("Brand", "الماركة"),
        "diameter" to Pair("Rim Diameter", "قطر الجنط"),
        "height" to Pair("Profile / Height", "الارتفاع / النسبة"),
        "width" to Pair("Width (mm)", "العرض (ملم)"),
        "size" to Pair("Tire Size", "مقاس الإطار"),
        "made_in" to Pair("Country of Origin", "بلد الصنع"),
        "quantity" to Pair("Quantity", "الكمية"),
        "status" to Pair("Status", "الحالة"),
        "notes" to Pair("Notes / Location", "ملاحظات / الرف"),
        "car_compatibility" to Pair("Compatible Vehicles", "السيارات المتوافقة"),
        "admin_role" to Pair("Admin Mode", "وضع المدير"),
        "employee_role" to Pair("Employee Mode", "وضع الموظف"),
        "enter_pin" to Pair("Enter Admin PIN", "أدخل رمز المدير"),
        "pin_hint" to Pair("Default PIN: 1234", "الرمز الافتراضي: 1234"),
        "cancel" to Pair("Cancel", "إلغاء"),
        "confirm" to Pair("Confirm", "تأكيد"),
        "save" to Pair("Save Record", "حفظ السجل"),
        "edit" to Pair("Edit", "تعديل"),
        "archive" to Pair("Archive", "أرشفة"),
        "all" to Pair("All", "الكل"),
        "active" to Pair("Active", "نشط"),
        "sold" to Pair("Sold", "تم البيع"),
        "reserved" to Pair("Reserved", "محجوز"),
        "archived" to Pair("Archived", "مؤرشف"),
        "counter_sale" to Pair("Fast Counter Sale", "بيع كاونتر سريع"),
        "complete_sale" to Pair("Complete Sale", "إتمام البيع"),
        "customer_employee" to Pair("Salesperson / Staff", "الموظف البائع"),
        "unit_price" to Pair("Unit Price", "سعر القطعة"),
        "total_amount" to Pair("Total Amount", "المبلغ الإجمالي"),
        "export_csv" to Pair("Export to Google Sheets CSV", "تصدير إلى Google Sheets CSV"),
        "export_desc" to Pair("Export all tire database records in Google Sheets compatible CSV format.", "تصدير كافة بيانات قاعدة بيانات الإطارات بتنسيق متوافق مع جداول بيانات Google."),
        "copied" to Pair("Copied to clipboard!", "تم النسخ إلى الحافظة!"),
        "language" to Pair("Language", "اللغة"),
        "currency_sar" to Pair("SAR", "ر.س"),
        "no_results" to Pair("No tires found matching your criteria.", "لم يتم العثور على إطارات مطابقة للبحث."),
        "add_vehicle_fitment" to Pair("Add Vehicle Fitment", "إضافة توافق سيارة"),
        "make" to Pair("Make (e.g. Toyota)", "الشركة الصانعة (مثل: تويوتا)"),
        "model" to Pair("Model (e.g. Camry)", "الموديل (مثل: كامري)"),
        "year" to Pair("Year (e.g. 2016-2022)", "سنة الصنع (مثل: 2016-2022)"),
        "trim" to Pair("Trim / Variant", "الفئة / الطراز"),
        "audit_log" to Pair("Stock Audit Trail", "سجل تدقيق حركة المخزون"),
        "change_type" to Pair("Movement Type", "نوع الحركة"),
        "date_time" to Pair("Date & Time", "التاريخ والوقت")
    )
}

fun AppLanguage.toLayoutDirection(): LayoutDirection {
    return if (this == AppLanguage.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
}
