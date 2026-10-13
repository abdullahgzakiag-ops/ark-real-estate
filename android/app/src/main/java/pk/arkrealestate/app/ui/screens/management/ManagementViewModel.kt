package pk.arkrealestate.app.ui.screens.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pk.arkrealestate.app.data.model.*
import pk.arkrealestate.app.data.remote.NetworkResult
import pk.arkrealestate.app.data.repository.ManagementRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PortfolioUiState(
    val properties: List<PropertyDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalPortfolioUnits: Int = 0,
    val totalOccupiedUnits: Int = 0,
    val overallOccupancyRate: Int = 0,
    val totalRentRollPkr: String = "0.00"
)

data class ManualPaymentUiState(
    val isModalOpen: Boolean = false,
    val leaseId: String = "",
    val rentScheduleId: String? = null,
    val amountPaidPkr: String = "",
    val paymentDate: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()),
    val paymentMethod: PaymentMethod = PaymentMethod.BANK_TRANSFER,
    val referenceNumber: String = "",
    val bankName: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

data class MaintenanceUiState(
    val isModalOpen: Boolean = false,
    val unitId: String = "",
    val title: String = "",
    val description: String = "",
    val priority: MaintenancePriority = MaintenancePriority.MEDIUM,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

data class FinancialReportUiState(
    val report: FinancialReportDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


class ManagementViewModel(
    private val managementRepository: ManagementRepository
) : ViewModel() {

    private val _portfolioState = MutableStateFlow(PortfolioUiState())
    val portfolioState: StateFlow<PortfolioUiState> = _portfolioState.asStateFlow()

    private val _paymentState = MutableStateFlow(ManualPaymentUiState())
    val paymentState: StateFlow<ManualPaymentUiState> = _paymentState.asStateFlow()

    private val _maintenanceState = MutableStateFlow(MaintenanceUiState())
    val maintenanceState: StateFlow<MaintenanceUiState> = _maintenanceState.asStateFlow()

    private val _financialReportState = MutableStateFlow(FinancialReportUiState())
    val financialReportState: StateFlow<FinancialReportUiState> = _financialReportState.asStateFlow()


    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            managementRepository.getProperties().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _portfolioState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        val properties = result.data ?: emptyList()
                        val totalUnits = properties.sumOf { it.totalUnits }
                        val occupiedUnits = properties.sumOf { it.occupiedUnits }
                        val rate = if (totalUnits > 0) Math.round((occupiedUnits.toDouble() / totalUnits) * 100).toInt() else 0
                        val totalRent = properties.sumOf { it.totalPotentialMonthlyRent.toDoubleOrNull() ?: 0.0 }

                        _portfolioState.update {
                            it.copy(
                                isLoading = false,
                                properties = properties,
                                totalPortfolioUnits = totalUnits,
                                totalOccupiedUnits = occupiedUnits,
                                overallOccupancyRate = rate,
                                totalRentRollPkr = String.format(Locale.US, "%.2f", totalRent),
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _portfolioState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun openManualPaymentModal(leaseId: String, rentScheduleId: String? = null, suggestedAmount: String = "") {
        _paymentState.update {
            it.copy(
                isModalOpen = true,
                leaseId = leaseId,
                rentScheduleId = rentScheduleId,
                amountPaidPkr = suggestedAmount,
                successMessage = null,
                errorMessage = null
            )
        }
    }

    fun closeManualPaymentModal() {
        _paymentState.update { it.copy(isModalOpen = false) }
    }

    fun onPaymentAmountChanged(amount: String) {
        val sanitized = amount.filter { it.isDigit() || it == '.' }
        _paymentState.update { it.copy(amountPaidPkr = sanitized) }
    }

    fun onPaymentMethodChanged(method: PaymentMethod) {
        _paymentState.update { it.copy(paymentMethod = method) }
    }

    fun onReferenceNumberChanged(ref: String) {
        _paymentState.update { it.copy(referenceNumber = ref) }
    }

    fun onBankNameChanged(bank: String) {
        _paymentState.update { it.copy(bankName = bank) }
    }

    fun onPaymentNotesChanged(notes: String) {
        _paymentState.update { it.copy(notes = notes) }
    }

    fun submitManualPayment() {
        val state = _paymentState.value
        if (state.amountPaidPkr.isBlank() || (state.amountPaidPkr.toDoubleOrNull() ?: 0.0) <= 0.0) {
            _paymentState.update { it.copy(errorMessage = "Please enter a valid payment amount") }
            return
        }

        viewModelScope.launch {
            managementRepository.recordManualPayment(
                RecordManualPaymentRequest(
                    leaseId = state.leaseId,
                    rentScheduleId = state.rentScheduleId,
                    amountPaidPkr = state.amountPaidPkr,
                    paymentDate = state.paymentDate,
                    paymentMethod = state.paymentMethod,
                    referenceNumber = state.referenceNumber.ifBlank { null },
                    bankName = state.bankName.ifBlank { null },
                    notes = state.notes.ifBlank { null }
                )
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _paymentState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _paymentState.update {
                            it.copy(
                                isLoading = false,
                                isModalOpen = false,
                                successMessage = "Manual payment recorded successfully! (Pending custodian clearance)",
                                amountPaidPkr = "",
                                referenceNumber = "",
                                bankName = ""
                            )
                        }
                        loadProperties()
                    }
                    is NetworkResult.Error -> {
                        _paymentState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun openMaintenanceModal(unitId: String) {
        _maintenanceState.update {
            it.copy(
                isModalOpen = true,
                unitId = unitId,
                title = "",
                description = "",
                successMessage = null,
                errorMessage = null
            )
        }
    }

    fun closeMaintenanceModal() {
        _maintenanceState.update { it.copy(isModalOpen = false) }
    }

    fun onMaintenanceTitleChanged(title: String) {
        _maintenanceState.update { it.copy(title = title) }
    }

    fun onMaintenanceDescriptionChanged(desc: String) {
        _maintenanceState.update { it.copy(description = desc) }
    }

    fun onMaintenancePriorityChanged(priority: MaintenancePriority) {
        _maintenanceState.update { it.copy(priority = priority) }
    }

    fun submitMaintenanceRequest() {
        val state = _maintenanceState.value
        if (state.title.isBlank() || state.description.isBlank()) {
            _maintenanceState.update { it.copy(errorMessage = "Title and description are required") }
            return
        }

        viewModelScope.launch {
            managementRepository.createMaintenanceRequest(
                unitId = state.unitId,
                title = state.title,
                description = state.description,
                priority = state.priority
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _maintenanceState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _maintenanceState.update {
                            it.copy(
                                isLoading = false,
                                isModalOpen = false,
                                successMessage = "Maintenance request submitted to property management!"
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _maintenanceState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadFinancialReport(propertyId: String) {
        viewModelScope.launch {
            managementRepository.getFinancialReport(propertyId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _financialReportState.update { it.copy(isLoading = true, errorMessage = null) }
                    is NetworkResult.Success -> _financialReportState.update { it.copy(isLoading = false, report = result.data, errorMessage = null) }
                    is NetworkResult.Error -> _financialReportState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}

