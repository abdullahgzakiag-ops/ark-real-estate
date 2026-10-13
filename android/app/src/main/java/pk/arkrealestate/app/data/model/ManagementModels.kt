package pk.arkrealestate.app.data.model

import com.google.gson.annotations.SerializedName

enum class LeaseStatus {
    @SerializedName("DRAFT")
    DRAFT,

    @SerializedName("ACTIVE")
    ACTIVE,

    @SerializedName("EXPIRED")
    EXPIRED,

    @SerializedName("TERMINATED")
    TERMINATED
}

enum class RentPaymentStatus {
    @SerializedName("PENDING")
    PENDING,

    @SerializedName("PAID")
    PAID,

    @SerializedName("OVERDUE")
    OVERDUE,

    @SerializedName("PARTIAL")
    PARTIAL
}

enum class PaymentMethod {
    @SerializedName("BANK_TRANSFER")
    BANK_TRANSFER,

    @SerializedName("CASH")
    CASH,

    @SerializedName("CHEQUE")
    CHEQUE,

    @SerializedName("PAY_ORDER")
    PAY_ORDER,

    @SerializedName("OTHER")
    OTHER
}

enum class ExpenseCategory {
    @SerializedName("MAINTENANCE")
    MAINTENANCE,

    @SerializedName("UTILITY")
    UTILITY,

    @SerializedName("TAX_LEGAL")
    TAX_LEGAL,

    @SerializedName("MANAGEMENT_FEE")
    MANAGEMENT_FEE,

    @SerializedName("INSURANCE")
    INSURANCE,

    @SerializedName("RENOVATION")
    RENOVATION,

    @SerializedName("OTHER")
    OTHER
}

enum class MaintenancePriority {
    @SerializedName("LOW")
    LOW,

    @SerializedName("MEDIUM")
    MEDIUM,

    @SerializedName("HIGH")
    HIGH,

    @SerializedName("URGENT")
    URGENT
}

enum class MaintenanceStatus {
    @SerializedName("OPEN")
    OPEN,

    @SerializedName("IN_PROGRESS")
    IN_PROGRESS,

    @SerializedName("RESOLVED")
    RESOLVED,

    @SerializedName("CANCELLED")
    CANCELLED
}

data class UnitDto(
    val id: String,
    val propertyId: String,
    val unitNumber: String,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val monthlyRentPkr: String,
    val isOccupied: Boolean = false
)

data class PropertyDto(
    val id: String,
    val name: String,
    val propertyType: PropertyType,
    val city: String,
    val areaLocation: String,
    val address: String,
    val totalUnits: Int = 0,
    val occupiedUnits: Int = 0,
    val occupancyRate: Int = 0,
    val totalPotentialMonthlyRent: String = "0.00",
    val units: List<UnitDto> = emptyList()
)

data class PropertiesResponseDto(
    val properties: List<PropertyDto>
)

data class PropertyDetailResponseDto(
    val property: PropertyDto
)

data class RentScheduleDto(
    val id: String,
    val leaseId: String,
    val dueDate: String,
    val amountDuePkr: String,
    val amountPaidPkr: String = "0.00",
    val status: RentPaymentStatus = RentPaymentStatus.PENDING
)

data class TenantSummaryDto(
    val id: String,
    val fullName: String,
    val phone: String
)

data class LeaseDto(
    val id: String,
    val unitId: String,
    val tenantId: String,
    val startDate: String,
    val endDate: String,
    val monthlyRentPkr: String,
    val securityDepositPkr: String,
    val status: LeaseStatus = LeaseStatus.ACTIVE,
    val rentSchedules: List<RentScheduleDto> = emptyList(),
    val tenant: TenantSummaryDto? = null
)

data class LeaseResponseDto(
    val lease: LeaseDto
)

data class ManualPaymentRecordDto(
    val id: String,
    val leaseId: String,
    val amountPaidPkr: String,
    val paymentDate: String,
    val paymentMethod: PaymentMethod,
    val referenceNumber: String? = null,
    val bankName: String? = null,
    val isVerified: Boolean = false,
    val complianceDisclosure: String? = null
)

data class ManualPaymentResponseDto(
    val paymentRecord: ManualPaymentRecordDto
)

data class RecordManualPaymentRequest(
    val leaseId: String,
    val rentScheduleId: String? = null,
    val amountPaidPkr: String,
    val paymentDate: String, // ISO date string
    val paymentMethod: PaymentMethod,
    val referenceNumber: String? = null,
    val bankName: String? = null,
    val notes: String? = null
)

data class CreatePropertyRequest(
    val name: String,
    val propertyType: PropertyType,
    val city: String,
    val areaLocation: String,
    val address: String,
    val notes: String? = null
)

data class MaintenanceRequestDto(
    val id: String,
    val unitId: String,
    val title: String,
    val description: String,
    val priority: MaintenancePriority = MaintenancePriority.MEDIUM,
    val status: MaintenanceStatus = MaintenanceStatus.OPEN
)

data class CreateMaintenanceRequest(
    val title: String,
    val description: String,
    val priority: MaintenancePriority = MaintenancePriority.MEDIUM
)

data class ExpenseItemDto(
    val id: String,
    val category: ExpenseCategory,
    val title: String,
    val amountPkr: String,
    val expenseDate: String
)

data class FinancialReportDto(
    val propertyId: String,
    val propertyName: String,
    val currency: String = "PKR",
    val totalRentDue: String,
    val totalRentCollected: String,
    val totalExpenses: String,
    val netOperatingIncomePkr: String,
    val expensesBreakdown: List<ExpenseItemDto> = emptyList()
)

data class FinancialReportResponseDto(
    val report: FinancialReportDto
)

data class ActivityAlertDto(
    val id: String,
    val title: String,
    val titleUrdu: String,
    val description: String,
    val timestamp: String,
    val type: String // "INQUIRY", "MODERATION", "PAYMENT", "SYSTEM"
)

