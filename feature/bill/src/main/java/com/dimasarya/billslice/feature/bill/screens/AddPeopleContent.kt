package com.dimasarya.billslice.feature.bill.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
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
fun AddPeopleContent(
    state: BillFlowUiState,
    onEvent: (BillFlowUiEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var newPersonName by remember { mutableStateOf("") }

    val handleAddPerson = {
        if (newPersonName.isNotBlank()) {
            onEvent(BillFlowUiEvent.AddParticipant(newPersonName.trim()))
            newPersonName = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
    ) {
        BillFlowTopBar(
            title = stringResource(R.string.title_add_people),
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
                        text = stringResource(R.string.people_headline),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight(800),
                            fontSize = 28.sp,
                            color = DeepInk,
                            lineHeight = 32.sp,
                        ),
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(R.string.people_supporting),
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
                // Person Name Entry row (Input + Green Button with person_add)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
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
                                value = newPersonName,
                                onValueChange = { newPersonName = it },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(500),
                                    color = DeepInk,
                                ),
                                cursorBrush = SolidColor(TableEmerald),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (newPersonName.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.person_input_placeholder),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 14.sp,
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

                    Button(
                        onClick = handleAddPerson,
                        modifier = Modifier
                            .size(width = 58.dp, height = 52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TableEmerald,
                            contentColor = DeepInk,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PersonAdd,
                            contentDescription = stringResource(R.string.add_person_button),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            // People List
            items(state.draft.participants, key = { it.id }) { participant ->
                PersonRow(
                    participant = participant,
                    onRemove = { onEvent(BillFlowUiEvent.RemoveParticipant(participant.id)) },
                )
            }

            // People Ready State banner
            if (state.draft.participants.isNotEmpty()) {
                item {
                    StatusBanner(
                        message = stringResource(
                            R.string.people_ready_text,
                            state.draft.participants.size,
                        ),
                        type = BannerType.Success,
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
                    text = stringResource(R.string.action_continue),
                    enabled = state.draft.participants.isNotEmpty(),
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = {
                        onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.AssignItems))
                    },
                )
            }
        }
    }
}

@Composable
private fun PersonRow(
    participant: Participant,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = WarmSurface,
        border = BorderStroke(1.dp, SubtleBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Person Chip
            Surface(
                shape = CircleShape,
                color = ReceiptMint,
            ) {
                Row(
                    modifier = Modifier.padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(TableEmerald, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = participant.name.take(1).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight(800),
                                fontSize = 12.sp,
                                color = DeepInk,
                            ),
                        )
                    }
                    Text(
                        text = participant.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight(700),
                            fontSize = 13.sp,
                            color = DeepEmerald,
                        ),
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove",
                    tint = MutedInk,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
