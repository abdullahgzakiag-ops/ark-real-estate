package pk.arkrealestate.app.ui.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pk.arkrealestate.app.data.model.*
import pk.arkrealestate.app.data.remote.NetworkResult
import pk.arkrealestate.app.data.repository.MarketplaceRepository
import java.math.BigDecimal
import java.math.RoundingMode

data class FeedUiState(
    val listings: List<ListingDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCity: String = "All Cities",
    val selectedPurpose: ListingPurpose? = null,
    val selectedPropertyType: PropertyType? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val selectedBedrooms: Int? = null,
    val isFilterDrawerOpen: Boolean = false,
    val savedIds: Set<String> = emptySet()
)

data class DetailUiState(
    val listing: ListingDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isInquiryModalOpen: Boolean = false,
    val isViewingModalOpen: Boolean = false,
    val inquiryName: String = "",
    val inquiryPhone: String = "",
    val inquiryMessage: String = "",
    val viewingDate: String = "",
    val viewingTimeSlot: String = "Morning (10:00 - 13:00)",
    val viewingNotes: String = "",
    val isSubmittingAction: Boolean = false,
    val actionSuccessMessage: String? = null
)

data class SubmitListingUiState(
    val title: String = "",
    val description: String = "",
    val purpose: ListingPurpose = ListingPurpose.FOR_SALE,
    val propertyType: PropertyType = PropertyType.HOUSE,
    val pricePkr: String = "",
    val city: String = "Lahore",
    val areaLocation: String = "",
    val areaValue: String = "1",
    val areaUnit: AreaUnit = AreaUnit.KANAL,
    val bedrooms: Int = 4,
    val bathrooms: Int = 4,
    val carSpaces: Int = 2,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class MyListingsUiState(
    val listings: List<MyListingDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: String = "all" // "all", "active", "review", "drafts"
)

data class SavedListingsUiState(
    val listings: List<ListingDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: String = "all" // "all", "drops"
)

data class ModerationUiState(
    val pendingListings: List<MyListingDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionSuccessMessage: String? = null,
    val isRejectModalOpen: Boolean = false,
    val rejectingListingId: String? = null,
    val rejectionReason: String = "",
    val isProcessingAction: Boolean = false
)

class MarketplaceViewModel(
    private val marketplaceRepository: MarketplaceRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow(FeedUiState())
    val feedState: StateFlow<FeedUiState> = _feedState.asStateFlow()

    private val _detailState = MutableStateFlow(DetailUiState())
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    private val _submitState = MutableStateFlow(SubmitListingUiState())
    val submitState: StateFlow<SubmitListingUiState> = _submitState.asStateFlow()

    private val _myListingsState = MutableStateFlow(MyListingsUiState())
    val myListingsState: StateFlow<MyListingsUiState> = _myListingsState.asStateFlow()

    private val _savedListingsState = MutableStateFlow(SavedListingsUiState())
    val savedListingsState: StateFlow<SavedListingsUiState> = _savedListingsState.asStateFlow()

    private val _moderationState = MutableStateFlow(ModerationUiState())
    val moderationState: StateFlow<ModerationUiState> = _moderationState.asStateFlow()


    init {
        loadListings()
    }

    fun onSearchQueryChanged(query: String) {
        _feedState.update { it.copy(searchQuery = query) }
    }

    fun onCitySelected(city: String) {
        _feedState.update { it.copy(selectedCity = city) }
        loadListings()
    }

    fun onPurposeSelected(purpose: ListingPurpose?) {
        _feedState.update { it.copy(selectedPurpose = purpose) }
    }

    fun onPropertyTypeSelected(type: PropertyType?) {
        _feedState.update { it.copy(selectedPropertyType = type) }
    }

    fun onPriceRangeChanged(min: Double?, max: Double?) {
        _feedState.update { it.copy(minPrice = min, maxPrice = max) }
    }

    fun onBedroomsSelected(beds: Int?) {
        _feedState.update { it.copy(selectedBedrooms = beds) }
    }

    fun toggleFilterDrawer(isOpen: Boolean) {
        _feedState.update { it.copy(isFilterDrawerOpen = isOpen) }
    }

    fun applyFilters() {
        _feedState.update { it.copy(isFilterDrawerOpen = false) }
        loadListings()
    }

    fun resetFilters() {
        _feedState.update {
            it.copy(
                selectedCity = "All Cities",
                selectedPurpose = null,
                selectedPropertyType = null,
                minPrice = null,
                maxPrice = null,
                selectedBedrooms = null,
                searchQuery = "",
                isFilterDrawerOpen = false
            )
        }
        loadListings()
    }

    fun loadListings() {
        val state = _feedState.value
        viewModelScope.launch {
            marketplaceRepository.getListings(
                search = if (state.searchQuery.isBlank()) null else state.searchQuery,
                city = state.selectedCity,
                purpose = state.selectedPurpose?.name,
                propertyType = state.selectedPropertyType?.name,
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                bedrooms = state.selectedBedrooms
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _feedState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _feedState.update {
                            it.copy(
                                isLoading = false,
                                listings = result.data ?: emptyList(),
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _feedState.update {
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

    fun loadListingDetail(listingId: String) {
        viewModelScope.launch {
            marketplaceRepository.getListingById(listingId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _detailState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _detailState.update {
                            it.copy(
                                isLoading = false,
                                listing = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _detailState.update {
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

    fun toggleSave(listingId: String) {
        val currentSaved = _feedState.value.savedIds.contains(listingId)
        viewModelScope.launch {
            marketplaceRepository.toggleSaveListing(listingId, currentSaved).collect { result ->
                if (result is NetworkResult.Success) {
                    _feedState.update { state ->
                        val newSet = state.savedIds.toMutableSet()
                        if (result.data == true) newSet.add(listingId) else newSet.remove(listingId)
                        state.copy(savedIds = newSet)
                    }
                }
            }
        }
    }

    fun openInquiryModal() {
        _detailState.update { it.copy(isInquiryModalOpen = true, actionSuccessMessage = null) }
    }

    fun closeInquiryModal() {
        _detailState.update { it.copy(isInquiryModalOpen = false) }
    }

    fun onInquiryNameChanged(name: String) {
        _detailState.update { it.copy(inquiryName = name) }
    }

    fun onInquiryPhoneChanged(phone: String) {
        _detailState.update { it.copy(inquiryPhone = phone) }
    }

    fun onInquiryMessageChanged(message: String) {
        _detailState.update { it.copy(inquiryMessage = message) }
    }

    fun submitInquiry() {
        val state = _detailState.value
        val listingId = state.listing?.id ?: return
        if (state.inquiryName.isBlank() || state.inquiryPhone.isBlank() || state.inquiryMessage.isBlank()) {
            return
        }

        viewModelScope.launch {
            marketplaceRepository.submitInquiry(
                listingId = listingId,
                contactName = state.inquiryName,
                contactPhone = state.inquiryPhone,
                message = state.inquiryMessage
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _detailState.update { it.copy(isSubmittingAction = true) }
                    }
                    is NetworkResult.Success -> {
                        _detailState.update {
                            it.copy(
                                isSubmittingAction = false,
                                isInquiryModalOpen = false,
                                actionSuccessMessage = "Inquiry sent successfully to property owner!"
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _detailState.update {
                            it.copy(
                                isSubmittingAction = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Submit Listing Actions ---
    fun onSubmitTitleChanged(title: String) { _submitState.update { it.copy(title = title) } }
    fun onSubmitDescChanged(desc: String) { _submitState.update { it.copy(description = desc) } }
    fun onSubmitPurposeChanged(purpose: ListingPurpose) { _submitState.update { it.copy(purpose = purpose) } }
    fun onSubmitTypeChanged(type: PropertyType) { _submitState.update { it.copy(propertyType = type) } }
    fun onSubmitPriceChanged(price: String) { _submitState.update { it.copy(pricePkr = price) } }
    fun onSubmitCityChanged(city: String) { _submitState.update { it.copy(city = city) } }
    fun onSubmitAreaLocationChanged(location: String) { _submitState.update { it.copy(areaLocation = location) } }
    fun onSubmitAreaValueChanged(value: String) { _submitState.update { it.copy(areaValue = value) } }
    fun onSubmitAreaUnitChanged(unit: AreaUnit) { _submitState.update { it.copy(areaUnit = unit) } }
    fun onSubmitBedroomsChanged(beds: Int) { _submitState.update { it.copy(bedrooms = beds) } }
    fun onSubmitBathroomsChanged(baths: Int) { _submitState.update { it.copy(bathrooms = baths) } }
    fun onSubmitCarSpacesChanged(spaces: Int) { _submitState.update { it.copy(carSpaces = spaces) } }

    fun submitListing(onSuccess: () -> Unit) {
        val s = _submitState.value
        if (s.title.isBlank() || s.pricePkr.isBlank() || s.areaLocation.isBlank()) {
            _submitState.update { it.copy(errorMessage = "Please fill in all mandatory listing fields") }
            return
        }

        viewModelScope.launch {
            val req = CreateListingRequest(
                title = s.title.trim(),
                description = if (s.description.isBlank()) s.title.trim() else s.description.trim(),
                purpose = s.purpose,
                propertyType = s.propertyType,
                pricePkr = s.pricePkr.trim(),
                city = s.city.trim(),
                areaLocation = s.areaLocation.trim(),
                areaValue = s.areaValue.trim(),
                areaUnit = s.areaUnit,
                bedrooms = s.bedrooms,
                bathrooms = s.bathrooms,
                carSpaces = s.carSpaces
            )
            marketplaceRepository.submitListing(req).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _submitState.update { it.copy(isSubmitting = true, errorMessage = null) }
                    is NetworkResult.Success -> {
                        _submitState.update { it.copy(isSubmitting = false, isSuccess = true, errorMessage = null) }
                        onSuccess()
                    }
                    is NetworkResult.Error -> _submitState.update { it.copy(isSubmitting = false, errorMessage = result.message) }
                }
            }
        }
    }

    // --- My Listings Actions ---
    fun loadMyListings() {
        viewModelScope.launch {
            marketplaceRepository.getMyListings().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _myListingsState.update { it.copy(isLoading = true, errorMessage = null) }
                    is NetworkResult.Success -> _myListingsState.update { it.copy(isLoading = false, listings = result.data, errorMessage = null) }
                    is NetworkResult.Error -> _myListingsState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onMyListingsTabSelected(tab: String) {
        _myListingsState.update { it.copy(selectedTab = tab) }
    }

    // --- Saved Listings Actions ---
    fun loadSavedListings() {
        viewModelScope.launch {
            marketplaceRepository.getSavedListings().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _savedListingsState.update { it.copy(isLoading = true, errorMessage = null) }
                    is NetworkResult.Success -> _savedListingsState.update { it.copy(isLoading = false, listings = result.data, errorMessage = null) }
                    is NetworkResult.Error -> _savedListingsState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun removeSavedListing(listingId: String) {
        viewModelScope.launch {
            marketplaceRepository.toggleSaveListing(listingId, true).collect {
                loadSavedListings()
            }
        }
    }

    // --- Admin Moderation Actions ---
    fun loadPendingModerationQueue() {
        viewModelScope.launch {
            marketplaceRepository.getPendingModerationListings().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _moderationState.update { it.copy(isLoading = true, errorMessage = null) }
                    is NetworkResult.Success -> _moderationState.update { it.copy(isLoading = false, pendingListings = result.data, errorMessage = null) }
                    is NetworkResult.Error -> _moderationState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun approveListing(listingId: String) {
        viewModelScope.launch {
            marketplaceRepository.approveListing(listingId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _moderationState.update { it.copy(isProcessingAction = true) }
                    is NetworkResult.Success -> {
                        _moderationState.update { it.copy(isProcessingAction = false, actionSuccessMessage = "Listing verified & approved for public marketplace!") }
                        loadPendingModerationQueue()
                    }
                    is NetworkResult.Error -> _moderationState.update { it.copy(isProcessingAction = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun openRejectModal(listingId: String) {
        _moderationState.update { it.copy(isRejectModalOpen = true, rejectingListingId = listingId, rejectionReason = "") }
    }

    fun closeRejectModal() {
        _moderationState.update { it.copy(isRejectModalOpen = false, rejectingListingId = null) }
    }

    fun onRejectionReasonChanged(reason: String) {
        _moderationState.update { it.copy(rejectionReason = reason) }
    }

    fun rejectListing() {
        val s = _moderationState.value
        val listingId = s.rejectingListingId ?: return
        val reason = if (s.rejectionReason.isBlank()) "Does not meet ARK verification guidelines" else s.rejectionReason.trim()

        viewModelScope.launch {
            marketplaceRepository.rejectListing(listingId, reason).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _moderationState.update { it.copy(isProcessingAction = true) }
                    is NetworkResult.Success -> {
                        _moderationState.update { it.copy(isProcessingAction = false, isRejectModalOpen = false, actionSuccessMessage = "Listing flagged & rejected") }
                        loadPendingModerationQueue()
                    }
                    is NetworkResult.Error -> _moderationState.update { it.copy(isProcessingAction = false, errorMessage = result.message) }
                }
            }
        }
    }

    companion object {
        /**
         * Formats Pakistani Rupee price into human-readable Pakistani units (Crore, Lakh, Thousand)
         * E.g. 65,000,000 -> "PKR 6.5 Crore"
         * E.g. 150,000 -> "PKR 1.5 Lakh"
         */
        fun formatPkrPrice(rawPrice: String, isRent: Boolean = false): String {
            return try {
                val amount = BigDecimal(rawPrice)
                val crore = BigDecimal("10000000")
                val lakh = BigDecimal("100000")
                val thousand = BigDecimal("1000")

                val formatted = when {
                    amount >= crore -> {
                        val value = amount.divide(crore, 2, RoundingMode.HALF_UP)
                        "PKR $value Crore"
                    }
                    amount >= lakh -> {
                        val value = amount.divide(lakh, 2, RoundingMode.HALF_UP)
                        "PKR $value Lakh"
                    }
                    amount >= thousand -> {
                        val value = amount.divide(thousand, 0, RoundingMode.HALF_UP)
                        "PKR $value Thousand"
                    }
                    else -> "PKR $amount"
                }

                if (isRent) "$formatted / mo" else formatted
            } catch (e: Exception) {
                "PKR $rawPrice"
            }
        }
    }
}
