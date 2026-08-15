package com.dimasarya.billslice.feature.bill

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.dimasarya.billslice.core.model.BillDraft
import com.dimasarya.billslice.feature.bill.screens.AddPeopleContent
import com.dimasarya.billslice.feature.bill.screens.AssignItemsContent
import com.dimasarya.billslice.feature.bill.screens.CalculationSummaryContent
import com.dimasarya.billslice.feature.bill.screens.ManualItemEntryContent
import com.dimasarya.billslice.feature.bill.screens.SharePreviewContent
import com.dimasarya.billslice.feature.bill.screens.SplitResultContent
import java.util.UUID

@Composable
fun ManualBillSplitFlowScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialDraft: BillDraft? = null,
    viewModel: BillFlowViewModel = remember {
        if (initialDraft != null) {
            BillFlowViewModel(initialDraft = initialDraft)
        } else {
            BillFlowViewModel(initialDraft = BillDraft(id = UUID.randomUUID().toString()))
        }
    },
) {
    val state by viewModel.uiState.collectAsState()

    val handleBack: () -> Unit = {
        when (state.step) {
            BillFlowStep.ManualEntry -> onBack()
            BillFlowStep.AddPeople -> viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.ManualEntry))
            BillFlowStep.AssignItems -> viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.AddPeople))
            BillFlowStep.CalculationSummary -> viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.AssignItems))
            BillFlowStep.SplitResult -> viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.CalculationSummary))
            BillFlowStep.SharePreview -> viewModel.onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.SplitResult))
        }
    }

    BackHandler(onBack = handleBack)

    when (state.step) {
        BillFlowStep.ManualEntry -> {
            ManualItemEntryContent(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = handleBack,
                modifier = modifier,
            )
        }
        BillFlowStep.AddPeople -> {
            AddPeopleContent(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = handleBack,
                modifier = modifier,
            )
        }
        BillFlowStep.AssignItems -> {
            AssignItemsContent(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = handleBack,
                modifier = modifier,
            )
        }
        BillFlowStep.CalculationSummary -> {
            CalculationSummaryContent(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = handleBack,
                modifier = modifier,
            )
        }
        BillFlowStep.SplitResult -> {
            SplitResultContent(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = handleBack,
                modifier = modifier,
            )
        }
        BillFlowStep.SharePreview -> {
            SharePreviewContent(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = handleBack,
                onFinishFlow = onBack,
                modifier = modifier,
            )
        }
    }
}
