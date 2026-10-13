package com.example.tireshop.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tireshop.data.model.AppLanguage
import com.example.tireshop.data.model.UserRole
import com.example.tireshop.ui.i18n.Strings
import com.example.tireshop.ui.theme.GreenStock
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
fun SettingsScreen(
    language: AppLanguage,
    userRole: UserRole,
    csvData: String,
    onToggleLanguage: () -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    onExportCsv: () -> Unit,
    onTriggerAdminPin: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Selector Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = TireAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("language", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isEn = language == AppLanguage.EN
                    Surface(
                        color = if (isEn) TireBlue else SlateSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isEn) onToggleLanguage() }
                            .testTag("lang_en_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isEn) Icon(Icons.Default.Check, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "English", fontWeight = FontWeight.Bold, color = if (isEn) TextPrimary else TextSecondary)
                        }
                    }

                    val isAr = language == AppLanguage.AR
                    Surface(
                        color = if (isAr) TireBlue else SlateSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isAr) onToggleLanguage() }
                            .testTag("lang_ar_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isAr) Icon(Icons.Default.Check, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "العربية (RTL)", fontWeight = FontWeight.Bold, color = if (isAr) TextPrimary else TextSecondary)
                        }
                    }
                }
            }
        }

        // Role & Security Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TireBlueLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Role & Access Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Admin role unlocks cost prices (purchase prices), inventory deletion, and manual stock overrides. Employees have safe POS counter access only.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isAdmin = userRole == UserRole.ADMIN
                    Surface(
                        color = if (isAdmin) TireBlue else SlateSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isAdmin) onTriggerAdminPin() }
                            .testTag("role_select_admin")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = if (isAdmin) TextPrimary else TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Admin Mode", fontWeight = FontWeight.Bold, color = if (isAdmin) TextPrimary else TextSecondary)
                        }
                    }

                    val isEmp = userRole == UserRole.EMPLOYEE
                    Surface(
                        color = if (isEmp) TireBlue else SlateSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSwitchRole(UserRole.EMPLOYEE) }
                            .testTag("role_select_employee")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = if (isEmp) TextPrimary else TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Employee Mode", fontWeight = FontWeight.Bold, color = if (isEmp) TextPrimary else TextSecondary)
                        }
                    }
                }
            }
        }

        // Google Sheets Sync & CSV Export Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TableChart, contentDescription = null, tint = GreenStock)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Sheets & Database Export",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = Strings.get("export_desc", language),
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onExportCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenStock),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_csv_button")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = Strings.get("export_csv", language), fontWeight = FontWeight.Bold)
                }

                if (csvData.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Tire Inventory CSV", csvData)
                            clipboard.setPrimaryClip(clip)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Copy CSV Content to Clipboard", color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = SlateSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = csvData.lines().take(6).joinToString("\n") + "\n...",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // About & Shop Branding Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = Strings.get("shop_name", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TireAmber
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tire Shop Pro Version 1.0 • Imported & Cleaned Architecture from GitHub nisaeyniassoh-hub/tire-shop-control-panel",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
