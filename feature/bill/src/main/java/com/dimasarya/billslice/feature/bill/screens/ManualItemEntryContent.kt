package com.dimasarya.billslice.feature.bill.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.tooling.preview.Preview
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.feature.bill.SampleBillData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.DeepInk
import com.dimasarya.billslice.core.designsystem.theme.MutedInk
import com.dimasarya.billslice.core.designsystem.theme.SubtleBorder
import com.dimasarya.billslice.core.designsystem.theme.TableEmerald
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.Rate
import com.dimasarya.billslice.feature.bill.BillFlowStep
import com.dimasarya.billslice.feature.bill.BillFlowUiEvent
import com.dimasarya.billslice.feature.bill.BillFlowUiState
import com.dimasarya.billslice.feature.bill.R
import com.dimasarya.billslice.feature.bill.components.BillFlowTopBar
import com.dimasarya.billslice.feature.bill.components.PrimaryActionButton

@Composable
fun ManualItemEntryContent(
    state: BillFlowUiState,
    onEvent: (BillFlowUiEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BillItem?>(null) }
    var showEditFeesDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
    ) {
        BillFlowTopBar(
            title = stringResource(R.string.title_enter_manually),
            onBack = onBack,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.manual_entry_headline),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight(800),
                            fontSize = 27.sp,
                            color = DeepInk,
                            lineHeight = 32.sp,
                        ),
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(R.string.manual_entry_supporting),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight(500),
                            fontSize = 14.sp,
                            color = MutedInk,
                            lineHeight = 18.sp,
                        ),
                    )
                }
            }

            item {
                // Restaurant name field
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = stringResource(R.string.restaurant_name_label),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight(600),
                            fontSize = 12.sp,
                            color = MutedInk,
                        ),
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = WarmSurface,
                        border = BorderStroke(1.dp, SubtleBorder),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            BasicTextField(
                                value = state.draft.merchantName ?: "",
                                onValueChange = { onEvent(BillFlowUiEvent.UpdateMerchantName(it)) },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight(500),
                                    color = DeepInk,
                                ),
                                cursorBrush = SolidColor(TableEmerald),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (state.draft.merchantName.isNullOrEmpty()) {
                                        Text(
                                            text = stringResource(R.string.restaurant_name_placeholder),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight(500),
                                                color = MutedInk,
                                            ),
                                        )
                                    }
                                    innerTextField()
                                },
                            )
                        }
                    }
                }
            }

            // Items list
            items(state.draft.items, key = { it.id }) { item ->
                ManualItemCard(
                    item = item,
                    onEdit = { editingItem = item },
                    onDelete = { onEvent(BillFlowUiEvent.DeleteItem(item.id)) },
                )
            }

            item {
                // Add item button matching Pencil component
                OutlinedButton(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TableEmerald),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DeepEmerald,
                    ),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = DeepEmerald,
                            modifier = Modifier.size(19.dp),
                        )
                        Text(
                            text = stringResource(R.string.add_item_button),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight(700),
                                fontSize = 14.sp,
                                color = DeepEmerald,
                            ),
                        )
                    }
                }
            }

            item {
                // Fees row (Service, Tax, Discount)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditFeesDialog = true },
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    FeeManualField(
                        label = stringResource(R.string.service_label),
                        value = state.draft.serviceRate.format(),
                        modifier = Modifier.weight(1f),
                    )
                    FeeManualField(
                        label = stringResource(R.string.tax_label),
                        value = state.draft.taxRate.format(),
                        modifier = Modifier.weight(1f),
                    )
                    FeeManualField(
                        label = stringResource(R.string.discount_label),
                        value = state.draft.discount.format(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                // Receipt total verification field
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = stringResource(R.string.receipt_total_label),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight(600),
                            fontSize = 12.sp,
                            color = MutedInk,
                        ),
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = WarmSurface,
                        border = BorderStroke(1.dp, SubtleBorder),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Rp",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight(700),
                                    fontSize = 14.sp,
                                    color = DeepEmerald,
                                ),
                            )
                            BasicTextField(
                                value = if ((state.draft.receiptTotal?.amountMinor ?: 0L) > 0L) {
                                    state.draft.receiptTotal?.amountMinor.toString()
                                } else {
                                    ""
                                },
                                onValueChange = { text ->
                                    val clean = text.filter { it.isDigit() }
                                    val amount = clean.toLongOrNull()
                                    onEvent(
                                        BillFlowUiEvent.UpdateReceiptTotal(
                                            if (amount != null && amount > 0) amount else null,
                                        ),
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight(600),
                                    color = DeepInk,
                                ),
                                cursorBrush = SolidColor(TableEmerald),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (state.draft.receiptTotal == null || state.draft.receiptTotal?.isZero == true) {
                                        Text(
                                            text = "219.450",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight(600),
                                                color = MutedInk,
                                            ),
                                        )
                                    }
                                    innerTextField()
                                },
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom CTA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = WarmSurface,
            shadowElevation = 8.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            ) {
                PrimaryActionButton(
                    text = stringResource(R.string.action_save_continue),
                    enabled = state.draft.items.isNotEmpty(),
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = {
                        onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.AddPeople))
                    },
                )
            }
        }
    }

    if (showAddItemDialog) {
        ItemFormDialog(
            title = stringResource(R.string.add_item_button),
            onDismiss = { showAddItemDialog = false },
            onConfirm = { name, price, qty ->
                onEvent(BillFlowUiEvent.AddItem(name, price, qty))
                showAddItemDialog = false
            },
        )
    }

    editingItem?.let { item ->
        ItemFormDialog(
            title = "Edit Item",
            initialName = item.name,
            initialPrice = item.unitPrice.amountMinor.toString(),
            initialQty = item.quantity.toString(),
            onDismiss = { editingItem = null },
            onConfirm = { name, price, qty ->
                onEvent(BillFlowUiEvent.UpdateItem(item.id, name, price, qty))
                editingItem = null
            },
        )
    }

    if (showEditFeesDialog) {
        FeesDialog(
            currentService = state.draft.serviceRate.basisPoints / 100,
            currentTax = state.draft.taxRate.basisPoints / 100,
            currentDiscount = state.draft.discount.amountMinor,
            onDismiss = { showEditFeesDialog = false },
            onSave = { servicePercent, taxPercent, discountMinor ->
                onEvent(BillFlowUiEvent.UpdateServiceRate(Rate.fromPercentage(servicePercent)))
                onEvent(BillFlowUiEvent.UpdateTaxRate(Rate.fromPercentage(taxPercent)))
                onEvent(BillFlowUiEvent.UpdateDiscount(discountMinor))
                showEditFeesDialog = false
            },
        )
    }
}

@Composable
private fun ManualItemCard(
    item: BillItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(8.dp),
        color = WarmSurface,
        border = BorderStroke(1.dp, SubtleBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight(700),
                        fontSize = 14.sp,
                        color = DeepInk,
                    ),
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete_item),
                        tint = MutedInk,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Price box
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.price_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight(600),
                            fontSize = 10.sp,
                            color = MutedInk,
                        ),
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = WarmCanvas,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = item.unitPrice.format(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight(650),
                                    fontSize = 13.sp,
                                    color = DeepInk,
                                ),
                            )
                        }
                    }
                }

                // Quantity box
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.quantity_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight(600),
                            fontSize = 10.sp,
                            color = MutedInk,
                        ),
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = WarmCanvas,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = item.quantity.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight(650),
                                    fontSize = 13.sp,
                                    color = DeepInk,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeeManualField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight(600),
                fontSize = 10.sp,
                color = MutedInk,
            ),
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(8.dp),
            color = WarmSurface,
            border = BorderStroke(1.dp, SubtleBorder),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 9.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight(650),
                        fontSize = 12.sp,
                        color = DeepInk,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ItemFormDialog(
    title: String,
    initialName: String = "",
    initialPrice: String = "",
    initialQty: String = "1",
    onDismiss: () -> Unit,
    onConfirm: (name: String, priceMinor: Long, quantity: Int) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var priceText by remember { mutableStateOf(initialPrice) }
    var qtyText by remember { mutableStateOf(initialQty) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepInk,
                ),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.item_name_label)) },
                    placeholder = { Text(stringResource(R.string.item_name_placeholder)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TableEmerald,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() } },
                    label = { Text(stringResource(R.string.price_label)) },
                    placeholder = { Text("40000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    prefix = { Text("Rp ") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TableEmerald,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it.filter { c -> c.isDigit() } },
                    label = { Text(stringResource(R.string.quantity_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TableEmerald,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = priceText.toLongOrNull() ?: 0L
                    val qty = qtyText.toIntOrNull() ?: 1
                    if (name.isNotBlank() && price > 0L && qty > 0) {
                        onConfirm(name.trim(), price, qty)
                    }
                },
            ) {
                Text(
                    text = "Save",
                    color = DeepEmerald,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = MutedInk)
            }
        },
    )
}

@Composable
private fun FeesDialog(
    currentService: Long,
    currentTax: Long,
    currentDiscount: Long,
    onDismiss: () -> Unit,
    onSave: (servicePercent: Long, taxPercent: Long, discountMinor: Long) -> Unit,
) {
    var serviceText by remember { mutableStateOf(currentService.toString()) }
    var taxText by remember { mutableStateOf(currentTax.toString()) }
    var discountText by remember { mutableStateOf(currentDiscount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Fees & Discount",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepInk,
                ),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = serviceText,
                    onValueChange = { serviceText = it.filter { c -> c.isDigit() } },
                    label = { Text("Service fee (%)") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TableEmerald),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = taxText,
                    onValueChange = { taxText = it.filter { c -> c.isDigit() } },
                    label = { Text("Tax rate (%)") },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TableEmerald),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = discountText,
                    onValueChange = { discountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Discount (Rp)") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TableEmerald),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val service = serviceText.toLongOrNull() ?: 0L
                    val tax = taxText.toLongOrNull() ?: 0L
                    val discount = discountText.toLongOrNull() ?: 0L
                    onSave(service, tax, discount)
                },
            ) {
                Text(text = "Save", color = DeepEmerald, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = MutedInk)
            }
        },
    )
}

@Preview(name = "Phone", showBackground = true, widthDp = 400, heightDp = 900)
@Preview(name = "Large font", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Preview(name = "Tablet", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun ManualItemEntryContentPreview() {
    BillSliceTheme {
        ManualItemEntryContent(
            state = com.dimasarya.billslice.feature.bill.BillFlowUiState(
                draft = SampleBillData.createSampleDraft(),
            ),
            onEvent = {},
            onBack = {},
        )
    }
}

