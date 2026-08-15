package com.dimasarya.billslice.feature.bill.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.DeepInk
import com.dimasarya.billslice.core.designsystem.theme.MutedInk
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.SubtleBorder
import com.dimasarya.billslice.core.designsystem.theme.TableEmerald
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface
import com.dimasarya.billslice.core.model.BillItem
import com.dimasarya.billslice.core.model.Participant
import com.dimasarya.billslice.feature.bill.BillFlowStep
import com.dimasarya.billslice.feature.bill.BillFlowUiEvent
import com.dimasarya.billslice.feature.bill.BillFlowUiState
import com.dimasarya.billslice.feature.bill.R
import com.dimasarya.billslice.feature.bill.components.BannerType
import com.dimasarya.billslice.feature.bill.components.BillFlowTopBar
import com.dimasarya.billslice.feature.bill.components.PrimaryActionButton
import com.dimasarya.billslice.feature.bill.components.StatusBanner

@Composable
fun AssignItemsContent(
    state: BillFlowUiState,
    onEvent: (BillFlowUiEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalItems = state.draft.items.size
    val assignedCount = totalItems - state.draft.unassignedItemIds().size
    val progressFraction = if (totalItems > 0) assignedCount.toFloat() / totalItems.toFloat() else 0f
    val percent = (progressFraction * 100).toInt()
    val allAssigned = state.canCalculate

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
    ) {
        BillFlowTopBar(
            title = stringResource(R.string.title_assign_items),
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Header progress text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.assignment_headline, assignedCount, totalItems),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight(800),
                                fontSize = 24.sp,
                                color = DeepInk,
                            ),
                            modifier = Modifier.semantics { heading() },
                        )
                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight(800),
                                fontSize = 13.sp,
                                color = DeepEmerald,
                            ),
                        )
                    }

                    // Progress track & fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFDDE6E1), CircleShape),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressFraction)
                                .background(TableEmerald, CircleShape),
                        )
                    }

                    Text(
                        text = stringResource(R.string.assignment_supporting),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight(500),
                            fontSize = 13.sp,
                            color = MutedInk,
                        ),
                    )
                }
            }

            // Assignment List
            items(state.draft.items, key = { it.id }) { item ->
                val assignedParticipantId = state.draft.assignments.find { it.itemId == item.id }?.participantId
                val assignedParticipant = state.draft.participants.find { it.id == assignedParticipantId }

                ItemAssignmentCard(
                    item = item,
                    assignedParticipant = assignedParticipant,
                    allParticipants = state.draft.participants,
                    onAssign = { participantId ->
                        onEvent(BillFlowUiEvent.AssignItem(item.id, participantId))
                    },
                )
            }

            // Banner
            item {
                if (allAssigned) {
                    StatusBanner(
                        message = stringResource(R.string.status_all_assigned),
                        type = BannerType.Success,
                    )
                } else {
                    StatusBanner(
                        message = stringResource(R.string.status_unassigned_warning),
                        type = BannerType.Warning,
                    )
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
                    text = stringResource(R.string.action_continue_to_calculate),
                    enabled = allAssigned,
                    icon = Icons.Rounded.Calculate,
                    onClick = {
                        onEvent(BillFlowUiEvent.CalculateSplit)
                        onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.CalculationSummary))
                    },
                )
            }
        }
    }
}

@Composable
private fun ItemAssignmentCard(
    item: BillItem,
    assignedParticipant: Participant?,
    allParticipants: List<Participant>,
    onAssign: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = WarmSurface,
        border = BorderStroke(1.dp, SubtleBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Header: Name & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight(750),
                        fontSize = 15.sp,
                        color = DeepInk,
                    ),
                )
                Text(
                    text = item.subtotal.format(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight(750),
                        fontSize = 14.sp,
                        color = DeepInk,
                    ),
                )
            }

            // Assignee box
            Box {
                if (assignedParticipant != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = ReceiptMint,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(TableEmerald, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = assignedParticipant.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight(800),
                                            fontSize = 11.sp,
                                            color = DeepInk,
                                        ),
                                    )
                                }
                                Text(
                                    text = assignedParticipant.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight(750),
                                        fontSize = 13.sp,
                                        color = DeepEmerald,
                                    ),
                                )
                            }

                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Change assignee",
                                tint = DeepEmerald,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = WarmSurface,
                        border = BorderStroke(1.dp, SubtleBorder),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.choose_person),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight(600),
                                    fontSize = 13.sp,
                                    color = MutedInk,
                                ),
                            )
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Choose assignee",
                                tint = MutedInk,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    allParticipants.forEach { participant ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(TableEmerald, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = participant.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = DeepInk,
                                            ),
                                        )
                                    }
                                    Text(
                                        text = participant.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = DeepInk,
                                        ),
                                    )
                                }
                            },
                            onClick = {
                                onAssign(participant.id)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}
