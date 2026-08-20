package com.dimasarya.billslice.feature.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.domain.GetBillUseCase
import com.dimasarya.billslice.core.domain.SaveBillUseCase
import com.dimasarya.billslice.core.model.BillDraft
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ReopenBillUiState {
    data object Loading : ReopenBillUiState
    data class Loaded(val draft: BillDraft) : ReopenBillUiState
    data class Failed(val cause: Throwable) : ReopenBillUiState
}

class ReopenBillViewModel(
    private val billId: String,
    private val getBillUseCase: GetBillUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ReopenBillUiState>(ReopenBillUiState.Loading)
    val uiState: StateFlow<ReopenBillUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        retry()
    }

    fun retry() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = ReopenBillUiState.Loading
            val result = try {
                getBillUseCase(billId)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Throwable) {
                Result.failure(error)
            }
            _uiState.value = result.fold(
                onSuccess = ReopenBillUiState::Loaded,
                onFailure = ReopenBillUiState::Failed,
            )
        }
    }
}

@Composable
fun ReopenedBillEntry(
    billId: String,
    getBillUseCase: GetBillUseCase,
    saveBillUseCase: SaveBillUseCase,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModelFactory = remember(billId, getBillUseCase) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ReopenBillViewModel(billId, getBillUseCase) as T
        }
    }
    val reopenBillViewModel = viewModel<ReopenBillViewModel>(
        key = "reopen-bill-$billId",
        factory = viewModelFactory,
    )
    val state by reopenBillViewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = state) {
        ReopenBillUiState.Loading -> ReopenBillLoading(modifier = modifier)
        is ReopenBillUiState.Loaded -> ManualBillSplitFlowScreen(
            initialDraft = currentState.draft,
            initialStep = BillFlowStep.SplitResult,
            saveBillUseCase = saveBillUseCase,
            modifier = modifier,
            onBack = onBack,
        )
        is ReopenBillUiState.Failed -> ReopenBillErrorScreen(
            modifier = modifier,
            onRetry = reopenBillViewModel::retry,
            onBack = onBack,
        )
    }
}

@Composable
private fun ReopenBillLoading(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
        horizontalArrangement = Arrangement.spacedBy(
            BillSliceThemeTokens.spacing.medium,
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(color = DeepEmerald)
        Text(
            text = stringResource(R.string.reopen_bill_loading),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ReopenBillErrorScreen(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas)
            .verticalScroll(rememberScrollState())
            .padding(BillSliceThemeTokens.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            BillSliceThemeTokens.spacing.large,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.reopen_bill_error_title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = stringResource(R.string.reopen_bill_error_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.reopen_bill_retry))
        }
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.reopen_bill_back))
        }
    }
}
