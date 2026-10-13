package pk.arkrealestate.app.data.model

import com.google.gson.annotations.SerializedName

enum class ListingPurpose {
    @SerializedName("FOR_SALE")
    FOR_SALE,

    @SerializedName("FOR_RENT")
    FOR_RENT
}

enum class PropertyType {
    @SerializedName("HOUSE")
    HOUSE,

    @SerializedName("APARTMENT")
    APARTMENT,

    @SerializedName("COMMERCIAL_PLOT")
    COMMERCIAL_PLOT,

    @SerializedName("RESIDENTIAL_PLOT")
    RESIDENTIAL_PLOT,

    @SerializedName("OFFICE")
    OFFICE,

    @SerializedName("SHOP")
    SHOP,

    @SerializedName("FARM_HOUSE")
    FARM_HOUSE
}

enum class AreaUnit {
    @SerializedName("MARLA")
    MARLA,

    @SerializedName("KANAL")
    KANAL,

    @SerializedName("SQ_FT")
    SQ_FT,

    @SerializedName("SQ_YARD")
    SQ_YARD
}

enum class ListingStatus {
    @SerializedName("DRAFT")
    DRAFT,

    @SerializedName("PENDING_APPROVAL")
    PENDING_APPROVAL,

    @SerializedName("APPROVED")
    APPROVED,

    @SerializedName("REJECTED")
    REJECTED,

    @SerializedName("ARCHIVED")
    ARCHIVED
}

data class ListingImageDto(
    val id: String,
    val storageKey: String,
    val url: String,
    val isFeatured: Boolean = false,
    val sortOrder: Int = 0
)

data class OwnerSummaryDto(
    val id: String,
    val fullName: String,
    val phone: String,
    val agencyName: String? = null,
    val role: UserRole = UserRole.OWNER_AGENT,
    val avatarUrl: String? = null
)

data class ListingDto(
    val id: String,
    val title: String,
    val titleUrdu: String? = null,
    val description: String,
    val descriptionUrdu: String? = null,
    val purpose: ListingPurpose,
    val propertyType: PropertyType,
    val pricePkr: String, // String representation of Decimal
    val isNegotiable: Boolean = false,
    val city: String,
    val areaLocation: String,
    val address: String? = null,
    val areaValue: String,
    val areaUnit: AreaUnit = AreaUnit.MARLA,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val carSpaces: Int? = null,
    val isFeatured: Boolean = false,
    val isVerifiedBadge: Boolean = false,
    val status: ListingStatus = ListingStatus.APPROVED,
    val createdAt: String,
    val images: List<ListingImageDto> = emptyList(),
    val owner: OwnerSummaryDto? = null,
    val savedCount: Int = 0,
    val isSavedByUser: Boolean = false
)

data class ListingDetailResponseDto(
    val listing: ListingDto
)

data class SavedListingItemDto(
    val savedAt: String,
    val listing: ListingDto
)

data class SavedListingsResponseDto(
    val savedListings: List<SavedListingItemDto>
)

data class SaveListingResponseDto(
    val isSaved: Boolean
)

data class InquiryRequest(
    val contactName: String,
    val contactPhone: String,
    val message: String
)

data class ViewingAppointmentRequest(
    val preferredDate: String, // ISO date string
    val preferredTime: String,
    val contactPhone: String,
    val notes: String? = null
)

data class CreateListingRequest(
    val title: String,
    val titleUrdu: String? = null,
    val description: String,
    val descriptionUrdu: String? = null,
    val purpose: ListingPurpose,
    val propertyType: PropertyType,
    val pricePkr: String,
    val isNegotiable: Boolean = false,
    val city: String,
    val areaLocation: String,
    val address: String? = null,
    val areaValue: String,
    val areaUnit: AreaUnit,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val carSpaces: Int? = null
)

data class MyListingDto(
    val id: String,
    val title: String,
    val titleUrdu: String? = null,
    val description: String,
    val purpose: ListingPurpose,
    val propertyType: PropertyType,
    val pricePkr: String,
    val city: String,
    val areaLocation: String,
    val areaValue: String,
    val areaUnit: AreaUnit = AreaUnit.MARLA,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val status: ListingStatus,
    val isVerifiedBadge: Boolean = false,
    val rejectionReason: String? = null,
    val createdAt: String,
    val inquiriesCount: Int = 0,
    val viewingRequestsCount: Int = 0,
    val savedCount: Int = 0,
    val images: List<ListingImageDto> = emptyList()
)

data class MyListingsResponseDto(
    val listings: List<MyListingDto>
)

data class ModerationListingsResponseDto(
    val listings: List<MyListingDto>
)

data class ModerateListingRequest(
    val action: String, // "APPROVE" or "REJECT"
    val reason: String? = null,
    val isVerifiedBadge: Boolean = true
)

