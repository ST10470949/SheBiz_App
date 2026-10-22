package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SheBizRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun BusinessScreen(
    repository: SheBizRepository
) {
    val businessProfile by repository.businessProfile.collectAsState()
    val products by repository.products.collectAsState()
    val sales by repository.sales.collectAsState()
    val expenses by repository.expenses.collectAsState()
    val customers by repository.customers.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0 = Dashboard, 1 = Products, 2 = Record Sale, 3 = Expenses, 4 = Analytics
    var customerDrawerOpen by remember { mutableStateOf(false) }
    var customerSearch by remember { mutableStateOf("") }
    var filterLowStockOnly by remember { mutableStateOf(false) }

    // Add Product Modal State
    var addProductOpen by remember { mutableStateOf(false) }
    var newProdName by remember { mutableStateOf("") }
    var newProdDesc by remember { mutableStateOf("") }
    var newProdPrice by remember { mutableStateOf("250") }
    var newProdStock by remember { mutableStateOf("10") }
    var newProdCategory by remember { mutableStateOf("Cakes") }

    // Restock Modal State
    var restockingProduct by remember { mutableStateOf<Product?>(null) }
    var restockAmount by remember { mutableIntStateOf(5) }

    // Record Sale Form State
    var selectedProductId by remember { mutableStateOf(products.firstOrNull()?.id ?: "") }
    var saleQuantity by remember { mutableIntStateOf(1) }
    var customerNameInput by remember { mutableStateOf("") }
    var saleSuccessNotice by remember { mutableStateOf<String?>(null) }
    var saleErrorNotice by remember { mutableStateOf<String?>(null) }

    // Add Expense Form State
    var expCategory by remember { mutableStateOf("Ingredients") }
    var expAmount by remember { mutableStateOf("") }
    var expDate by remember { mutableStateOf("2026-10-22") }
    var expNote by remember { mutableStateOf("") }
    var toastNotice by remember { mutableStateOf<String?>(null) }

    val todaySales = repository.getTodaySales()
    val monthlySales = repository.getMonthlySales()
    val monthlyExpenses = repository.getMonthlyExpenses()
    val estProfit = repository.getEstProfit()

    val lowStockCount = products.count { it.stock in 1..2 }
    val outOfStockCount = products.count { it.stock <= 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SheBizBackground)
    ) {
        // Business Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = businessProfile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SheBizHeading
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(SheBizLavender, RoundedCornerShape(100.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(businessProfile.location, fontSize = 10.sp, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Small Business Hub • South African Rand (ZAR)", fontSize = 11.sp, color = SheBizBody)
            }

            Button(
                onClick = { activeSubTab = 2 }, // Switch to Record Sale
                colors = ButtonDefaults.buttonColors(containerColor = SheBizCoral),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("header_record_sale_button")
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Record Sale", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Sub-Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = activeSubTab,
            edgePadding = 20.dp,
            containerColor = SheBizSurface,
            contentColor = SheBizPrimaryPurple,
            modifier = Modifier.fillMaxWidth()
        ) {
            val tabs = listOf("Dashboard", "Products (${products.size})", "Record Sale", "Expenses", "Analytics")
            tabs.forEachIndexed { index, tabName ->
                Tab(
                    selected = activeSubTab == index,
                    onClick = { activeSubTab = index },
                    text = {
                        Text(
                            tabName,
                            fontWeight = if (activeSubTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // Body Content by SubTab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            when (activeSubTab) {
                0 -> BusinessDashboardTab(
                    todaySales = todaySales,
                    monthlySales = monthlySales,
                    monthlyExpenses = monthlyExpenses,
                    estProfit = estProfit,
                    productCount = products.size,
                    customerCount = customers.size,
                    lowStockCount = lowStockCount,
                    onOpenCustomers = { customerDrawerOpen = true },
                    onOpenLowStock = {
                        filterLowStockOnly = true
                        activeSubTab = 1
                    },
                    onOpenRecordSale = { activeSubTab = 2 },
                    onOpenAddProduct = { addProductOpen = true },
                    onOpenAddExpense = { activeSubTab = 3 },
                    onOpenAnalytics = { activeSubTab = 4 }
                )

                1 -> ProductsTab(
                    products = if (filterLowStockOnly) products.filter { it.stock <= 2 } else products,
                    isFiltered = filterLowStockOnly,
                    onClearFilter = { filterLowStockOnly = false },
                    onAddProduct = { addProductOpen = true },
                    onRestock = { restockingProduct = it },
                    onDelete = { repository.deleteProduct(it.id) },
                    onDuplicate = {
                        repository.addProduct(
                            name = "${it.name} (Copy)",
                            description = it.description,
                            price = it.price,
                            stock = it.stock,
                            category = it.category
                        )
                    }
                )

                2 -> RecordSaleTab(
                    products = products,
                    selectedProductId = selectedProductId,
                    onSelectProduct = { selectedProductId = it },
                    quantity = saleQuantity,
                    onQuantityChange = { saleQuantity = it.coerceAtLeast(1) },
                    customerName = customerNameInput,
                    onCustomerNameChange = { customerNameInput = it },
                    successNotice = saleSuccessNotice,
                    errorNotice = saleErrorNotice,
                    onSubmitSale = {
                        val result = repository.recordSale(selectedProductId, saleQuantity, customerNameInput)
                        if (result.isSuccess) {
                            val sale = result.getOrNull()!!
                            saleSuccessNotice = "Sale recorded successfully: ${SheBizRepository.formatRand(sale.total)} (${sale.productName} x ${sale.quantity})!"
                            saleErrorNotice = null
                            customerNameInput = ""
                            saleQuantity = 1
                        } else {
                            saleErrorNotice = result.exceptionOrNull()?.message ?: "Failed to record sale"
                            saleSuccessNotice = null
                        }
                    }
                )

                3 -> ExpensesTab(
                    expenses = expenses,
                    category = expCategory,
                    onCategoryChange = { expCategory = it },
                    amount = expAmount,
                    onAmountChange = { expAmount = it },
                    date = expDate,
                    onDateChange = { expDate = it },
                    note = expNote,
                    onNoteChange = { expNote = it },
                    onSubmitExpense = {
                        val amt = expAmount.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            repository.addExpense(expCategory, amt, expDate, expNote)
                            expAmount = ""
                            expNote = ""
                            toastNotice = "Expense recorded: ${SheBizRepository.formatRand(amt)}"
                        }
                    },
                    onDeleteExpense = { repository.deleteExpense(it.id) },
                    onExportCsv = { toastNotice = "Expenses exported to CSV (Abo_Bakery_Expenses.csv)" }
                )

                4 -> AnalyticsTab(
                    monthlySales = monthlySales,
                    monthlyExpenses = monthlyExpenses,
                    estProfit = estProfit,
                    sales = sales,
                    products = products,
                    onCreatePartyBox = {
                        newProdName = "Celebration Party Box"
                        newProdDesc = "Assorted selection of 6 cupcakes, chocolate fudge brownies and mini red velvet bites"
                        newProdPrice = "450"
                        newProdStock = "8"
                        newProdCategory = "Party Boxes"
                        addProductOpen = true
                    },
                    onCheckStock = {
                        activeSubTab = 1
                    }
                )
            }
        }
    }

    // Add Product Modal
    if (addProductOpen) {
        AlertDialog(
            onDismissRequest = { addProductOpen = false },
            title = { Text("Add New Bakery Product", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newProdName,
                        onValueChange = { newProdName = it },
                        label = { Text("Product Name") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_prod_name")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newProdDesc,
                        onValueChange = { newProdDesc = it },
                        label = { Text("Description") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_prod_desc")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newProdPrice,
                            onValueChange = { newProdPrice = it },
                            label = { Text("Price (ZAR R)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("add_prod_price")
                        )
                        OutlinedTextField(
                            value = newProdStock,
                            onValueChange = { newProdStock = it },
                            label = { Text("Initial Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("add_prod_stock")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = newProdPrice.toDoubleOrNull() ?: 200.0
                        val stock = newProdStock.toIntOrNull() ?: 5
                        if (newProdName.isNotBlank()) {
                            repository.addProduct(newProdName, newProdDesc, price, stock, newProdCategory)
                            addProductOpen = false
                            newProdName = ""
                            newProdDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_product_confirm")
                ) {
                    Text("Save Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { addProductOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Restock Modal
    restockingProduct?.let { prod ->
        AlertDialog(
            onDismissRequest = { restockingProduct = null },
            title = { Text("Restock ${prod.name}") },
            text = {
                Column {
                    Text("Current stock: ${prod.stock} units")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Add inventory units:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = { if (restockAmount > 1) restockAmount-- },
                            modifier = Modifier.background(SheBizLavender, CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, null)
                        }
                        Text("$restockAmount", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        IconButton(
                            onClick = { restockAmount++ },
                            modifier = Modifier.background(SheBizLavender, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, null)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.restockProduct(prod.id, restockAmount)
                        restockingProduct = null
                        restockAmount = 5
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizGreen)
                ) {
                    Text("Confirm Restock")
                }
            },
            dismissButton = {
                TextButton(onClick = { restockingProduct = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Customers Drawer Modal
    if (customerDrawerOpen) {
        Dialog(onDismissRequest = { customerDrawerOpen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                shape = RoundedCornerShape(24.dp),
                color = SheBizSurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Customer Directory", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
                        IconButton(onClick = { customerDrawerOpen = false }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerSearch,
                        onValueChange = { customerSearch = it },
                        placeholder = { Text("Search customers...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val filteredCusts = customers.filter {
                        customerSearch.isBlank() || it.name.contains(customerSearch, ignoreCase = true)
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredCusts) { cust ->
                            Floating3DCard(modifier = Modifier.fillMaxWidth(), elevation = 1.dp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cust.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                                        Text("${cust.orderCount} orders • ${cust.phone}", fontSize = 11.sp, color = SheBizBody)
                                    }
                                    Text(
                                        SheBizRepository.formatRand(cust.totalSpent),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = SheBizGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- SUB-TAB 0: DASHBOARD ----------------------
@Composable
private fun BusinessDashboardTab(
    todaySales: Double,
    monthlySales: Double,
    monthlyExpenses: Double,
    estProfit: Double,
    productCount: Int,
    customerCount: Int,
    lowStockCount: Int,
    onOpenCustomers: () -> Unit,
    onOpenLowStock: () -> Unit,
    onOpenRecordSale: () -> Unit,
    onOpenAddProduct: () -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenAnalytics: () -> Unit
) {
    var chartPeriod by remember { mutableStateOf("Last 6 Months") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // 4 Financial Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BusinessMetricCard(
                title = "Today's Sales",
                amount = SheBizRepository.formatRand(todaySales),
                subtext = "Live transactions",
                color = SheBizPrimaryPurple,
                modifier = Modifier.weight(1f)
            )
            BusinessMetricCard(
                title = "Monthly Sales",
                amount = SheBizRepository.formatRand(monthlySales),
                subtext = "October 2026",
                color = SheBizCoral,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BusinessMetricCard(
                title = "Expenses",
                amount = SheBizRepository.formatRand(monthlyExpenses),
                subtext = "Ingredients & gas",
                color = Color(0xFFC43343),
                modifier = Modifier.weight(1f)
            )
            BusinessMetricCard(
                title = "Est. Profit",
                amount = SheBizRepository.formatRand(estProfit),
                subtext = "Sales - Expenses",
                color = SheBizGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sales Overview Line / Bar Chart
        Floating3DCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sales Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
                    Text("Rand performance trend", fontSize = 11.sp, color = SheBizBody)
                }
                Box(
                    modifier = Modifier
                        .background(SheBizLavender, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(chartPeriod, fontSize = 10.sp, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Bar Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val months = listOf("May", "Jun", "Jul", "Aug", "Sep", "Oct")
                val values = listOf(1800f, 2400f, 2100f, 3100f, 2800f, 3390f)
                val maxVal = 4000f

                val barWidth = size.width / (months.size * 2)

                months.forEachIndexed { i, month ->
                    val x = (i * 2 + 0.5f) * barWidth
                    val barHeight = (values[i] / maxVal) * size.height * 0.85f
                    val y = size.height - barHeight

                    // 3D Rounded Bar
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(SheBizPrimaryPurple, Color(0xFF431DB8))
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("May", "Jun", "Jul", "Aug", "Sep", "Oct").forEach {
                    Text(it, fontSize = 10.sp, color = SheBizBody, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Business Health Strip
        Text("Business Health", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HealthPillCard(
                title = "Products",
                count = "$productCount",
                icon = Icons.Outlined.Inventory2,
                color = SheBizPrimaryPurple,
                modifier = Modifier.weight(1f),
                onClick = {}
            )
            HealthPillCard(
                title = "Customers",
                count = "$customerCount",
                icon = Icons.Outlined.People,
                color = SheBizGreen,
                modifier = Modifier.weight(1f),
                onClick = onOpenCustomers
            )
            HealthPillCard(
                title = "Low Stock",
                count = "$lowStockCount",
                icon = Icons.Outlined.WarningAmber,
                color = SheBizGold,
                modifier = Modifier.weight(1f),
                onClick = onOpenLowStock
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions
        Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                label = "+ Product",
                icon = Icons.Default.Add,
                modifier = Modifier.weight(1f),
                onClick = onOpenAddProduct
            )
            QuickActionButton(
                label = "Record Sale",
                icon = Icons.Default.PointOfSale,
                modifier = Modifier.weight(1f),
                onClick = onOpenRecordSale
            )
            QuickActionButton(
                label = "+ Expense",
                icon = Icons.Default.ReceiptLong,
                modifier = Modifier.weight(1f),
                onClick = onOpenAddExpense
            )
            QuickActionButton(
                label = "Analytics",
                icon = Icons.Default.BarChart,
                modifier = Modifier.weight(1f),
                onClick = onOpenAnalytics
            )
        }
    }
}

@Composable
private fun BusinessMetricCard(
    title: String,
    amount: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Floating3DCard(modifier = modifier, elevation = 2.dp) {
        Text(title, fontSize = 11.sp, color = SheBizBody, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(amount, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(subtext, fontSize = 9.sp, color = SheBizBody)
    }
}

@Composable
private fun HealthPillCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SheBizHeading)
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = SheBizSurface,
        border = BorderStroke(1.dp, SheBizBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SheBizHeading)
        }
    }
}

// ---------------------- SUB-TAB 1: PRODUCTS ----------------------
@Composable
private fun ProductsTab(
    products: List<Product>,
    isFiltered: Boolean,
    onClearFilter: () -> Unit,
    onAddProduct: () -> Unit,
    onRestock: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onDuplicate: (Product) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Product Inventory", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
                Text("Track prices & live stock levels", fontSize = 12.sp, color = SheBizBody)
            }
            Button(
                onClick = onAddProduct,
                colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_product_button")
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Product", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (isFiltered) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SheBizGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Showing low / out of stock items only", fontSize = 11.sp, color = Color(0xFF9E6500), fontWeight = FontWeight.Bold)
                TextButton(onClick = onClearFilter) {
                    Text("Show all", color = SheBizPrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(products, key = { it.id }) { prod ->
                var menuExpanded by remember { mutableStateOf(false) }

                Floating3DCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product Icon Box
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SheBizLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Cake, null, tint = SheBizPrimaryPurple, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
                            }
                            Text(prod.description, fontSize = 11.sp, color = SheBizBody, maxLines = 1)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    SheBizRepository.formatRand(prod.price),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = SheBizHeading
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                StockStatusBadge(status = prod.stockStatus, count = prod.stock)
                            }
                        }

                        // Row Options Menu
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, null, tint = SheBizBody)
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Restock") },
                                    leadingIcon = { Icon(Icons.Default.AddBox, null, tint = SheBizGreen) },
                                    onClick = {
                                        menuExpanded = false
                                        onRestock(prod)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Duplicate") },
                                    leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
                                    onClick = {
                                        menuExpanded = false
                                        onDuplicate(prod)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = SheBizRed) },
                                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = SheBizRed) },
                                    onClick = {
                                        menuExpanded = false
                                        onDelete(prod)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- SUB-TAB 2: RECORD SALE ----------------------
@Composable
private fun RecordSaleTab(
    products: List<Product>,
    selectedProductId: String,
    onSelectProduct: (String) -> Unit,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    successNotice: String?,
    errorNotice: String?,
    onSubmitSale: () -> Unit
) {
    val selectedProduct = products.firstOrNull { it.id == selectedProductId } ?: products.firstOrNull()
    val unitPrice = selectedProduct?.price ?: 0.0
    val total = unitPrice * quantity
    val currentStock = selectedProduct?.stock ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Record Walk-in or Online Sale", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
        Text("Automatically decrements stock and logs cash flow", fontSize = 12.sp, color = SheBizBody)

        Spacer(modifier = Modifier.height(16.dp))

        if (successNotice != null) {
            Surface(
                color = SheBizGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = SheBizGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(successNotice, color = SheBizGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (errorNotice != null) {
            Surface(
                color = SheBizRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, null, tint = SheBizRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorNotice, color = SheBizRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Product Selector
        Text("Select Product", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            products.forEach { prod ->
                val isSelected = prod.id == selectedProduct?.id
                Surface(
                    onClick = { onSelectProduct(prod.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) SheBizLavender else SheBizSurface,
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) SheBizPrimaryPurple else SheBizBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("select_prod_${prod.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                            Text(
                                "Price: ${SheBizRepository.formatRand(prod.price)} • In stock: ${prod.stock}",
                                fontSize = 11.sp,
                                color = SheBizBody
                            )
                        }
                        StockStatusBadge(status = prod.stockStatus)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quantity Stepper
        Text("Quantity", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                modifier = Modifier.background(SheBizLavender, CircleShape)
            ) {
                Icon(Icons.Default.Remove, null, tint = SheBizPrimaryPurple)
            }
            Text("$quantity", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = SheBizHeading)
            IconButton(
                onClick = { onQuantityChange(quantity + 1) },
                modifier = Modifier.background(SheBizLavender, CircleShape)
            ) {
                Icon(Icons.Default.Add, null, tint = SheBizPrimaryPurple)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Optional Customer Name
        Text("Customer Name (Optional)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = customerName,
            onValueChange = onCustomerNameChange,
            placeholder = { Text("e.g. Thandiwe Khumalo") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("sale_customer_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Live Total Banner
        Surface(
            color = SheBizBackground,
            border = BorderStroke(1.5.dp, SheBizPrimaryPurple.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOTAL AMOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SheBizBody)
                    Text("$quantity x ${SheBizRepository.formatRand(unitPrice)}", fontSize = 12.sp, color = SheBizHeading)
                }
                Text(
                    SheBizRepository.formatRand(total),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SheBizPrimaryPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PillowButton(
            text = "Record Sale",
            onClick = onSubmitSale,
            enabled = currentStock >= quantity && quantity > 0,
            modifier = Modifier.fillMaxWidth(),
            testTag = "submit_record_sale_button"
        )
    }
}

// ---------------------- SUB-TAB 3: EXPENSES ----------------------
@Composable
private fun ExpensesTab(
    expenses: List<Expense>,
    category: String,
    onCategoryChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onSubmitExpense: () -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    onExportCsv: () -> Unit
) {
    val categories = listOf("Ingredients", "Packaging", "Transport", "Utilities", "Marketing", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Operating Expenses", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
                Text("Log raw costs and packaging receipts", fontSize = 12.sp, color = SheBizBody)
            }
            TextButton(onClick = onExportCsv) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp), tint = SheBizPrimaryPurple)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SheBizPrimaryPurple)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // New Expense Card
        Floating3DCard(modifier = Modifier.fillMaxWidth()) {
            Text("Record New Expense", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = cat == category
                    FilterChip(
                        selected = isSel,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SheBizPrimaryPurple, selectedLabelColor = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (R)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("expense_amount_input")
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = onDateChange,
                    label = { Text("Date (YYYY-MM-DD)") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Expense Details / Supplier Note") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("expense_note_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            PillowButton(
                text = "Save Expense",
                onClick = onSubmitExpense,
                modifier = Modifier.fillMaxWidth(),
                testTag = "save_expense_button"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Expense History (${expenses.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
        Spacer(modifier = Modifier.height(10.dp))

        expenses.forEach { exp ->
            Floating3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(SheBizBlush, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(exp.category, fontSize = 10.sp, color = SheBizCoral, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(exp.date, fontSize = 11.sp, color = SheBizBody)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(exp.note, fontSize = 13.sp, color = SheBizHeading, fontWeight = FontWeight.Medium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            SheBizRepository.formatRand(exp.amount),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = SheBizRed
                        )
                        IconButton(onClick = { onDeleteExpense(exp) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.DeleteOutline, null, tint = SheBizBody, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- SUB-TAB 4: ANALYTICS ----------------------
@Composable
private fun AnalyticsTab(
    monthlySales: Double,
    monthlyExpenses: Double,
    estProfit: Double,
    sales: List<Sale>,
    products: List<Product>,
    onCreatePartyBox: () -> Unit,
    onCheckStock: () -> Unit
) {
    val profitMargin = if (monthlySales > 0) ((estProfit / monthlySales) * 100).toInt() else 0
    val topSelling = sales.groupBy { it.productName }
        .mapValues { entry -> entry.value.sumOf { it.total } }
        .toList()
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Business Analytics & Growth", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SheBizHeading)
        Text("Real metrics derived from Abo's Bakery sales", fontSize = 12.sp, color = SheBizBody)

        Spacer(modifier = Modifier.height(16.dp))

        // Growth Insight Card (Generated from real sales)
        Floating3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SheBizLavender.copy(alpha = 0.5f),
            borderColor = SheBizPrimaryPurple.copy(alpha = 0.3f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SheBizPrimaryPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Growth Insight", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                    Text("Chocolate Cake leads all bakery revenue this month", fontSize = 11.sp, color = SheBizBody)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Your Belgian chocolate cake generates 52% of total turnover with high customer re-orders. Bundle it with red velvet cupcakes to capture larger event orders.",
                fontSize = 12.sp,
                color = SheBizHeading,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onCreatePartyBox,
                    colors = ButtonDefaults.buttonColors(containerColor = SheBizPrimaryPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Create Party Box", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCheckStock,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Check Stock", fontSize = 11.sp, color = SheBizPrimaryPurple, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profit Split Donut
        Floating3DCard(modifier = Modifier.fillMaxWidth()) {
            Text("Profit & Expense Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SheBizHeading)
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    val strokeW = 28f
                    // Profit slice (58%)
                    drawArc(
                        color = SheBizGreen,
                        startAngle = -90f,
                        sweepAngle = 210f,
                        useCenter = false,
                        style = Stroke(strokeW)
                    )
                    // Ingredients slice (28%)
                    drawArc(
                        color = SheBizCoral,
                        startAngle = 120f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(strokeW)
                    )
                    // Packaging & other (14%)
                    drawArc(
                        color = SheBizGold,
                        startAngle = 220f,
                        sweepAngle = 50f,
                        useCenter = false,
                        style = Stroke(strokeW)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$profitMargin%", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = SheBizGreen)
                    Text("Net Margin", fontSize = 10.sp, color = SheBizBody, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DonutLegendItem("Profit (58%)", SheBizGreen)
                DonutLegendItem("Ingredients (28%)", SheBizCoral)
                DonutLegendItem("Packaging (14%)", SheBizGold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Best-Selling Products Ranked
        Text("Best-Selling Products", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SheBizHeading)
        Spacer(modifier = Modifier.height(10.dp))

        topSelling.forEachIndexed { index, pair ->
            Floating3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "#${index + 1}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = SheBizPrimaryPurple
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pair.first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SheBizHeading)
                        Text("Ranked by revenue", fontSize = 10.sp, color = SheBizBody)
                    }
                    Text(
                        SheBizRepository.formatRand(pair.second),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = SheBizHeading
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 10.sp, color = SheBizHeading, fontWeight = FontWeight.Medium)
    }
}
