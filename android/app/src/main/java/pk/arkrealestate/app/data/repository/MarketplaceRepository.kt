package pk.arkrealestate.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pk.arkrealestate.app.data.model.*
import pk.arkrealestate.app.data.remote.ArkApiService
import pk.arkrealestate.app.data.remote.AuthInterceptor
import pk.arkrealestate.app.data.remote.NetworkResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MarketplaceRepository(private val tokenStorage: TokenStorage) {

    private val apiService: ArkApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor { tokenStorage.getAccessToken() }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val baseUrl = "http://10.0.2.2:5000/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ArkApiService::class.java)
    }

    fun getListings(
        search: String? = null,
        city: String? = null,
        purpose: String? = null,
        propertyType: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        bedrooms: Int? = null,
        page: Int = 1,
        limit: Int = 20,
        sortBy: String = "newest"
    ): Flow<NetworkResult<List<ListingDto>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getListings(
                search = if (search.isNullOrBlank()) null else search.trim(),
                city = if (city.isNullOrBlank() || city == "All Cities") null else city.trim(),
                purpose = purpose,
                propertyType = propertyType,
                minPrice = minPrice,
                maxPrice = maxPrice,
                bedrooms = bedrooms,
                page = page,
                limit = limit,
                sortBy = sortBy
            )
            if (response.isSuccessful && response.body()?.success == true) {
                val listings = response.body()!!.data ?: emptyList()
                emit(NetworkResult.Success(listings))
            } else {
                val errorMsg = response.body()?.error?.message ?: response.message() ?: "Failed to fetch listings"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unable to connect to ARK network", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun getListingById(id: String): Flow<NetworkResult<ListingDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getListingById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val listing = response.body()!!.data!!.listing
                emit(NetworkResult.Success(listing))
            } else {
                val errorMsg = response.body()?.error?.message ?: "Listing not found or pending verification"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun submitInquiry(
        listingId: String,
        contactName: String,
        contactPhone: String,
        message: String
    ): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.createInquiry(
                id = listingId,
                request = InquiryRequest(
                    contactName = contactName.trim(),
                    contactPhone = contactPhone.trim(),
                    message = message.trim()
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(true))
            } else {
                val errorMsg = response.body()?.error?.message ?: "Failed to submit inquiry"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun scheduleViewing(
        listingId: String,
        preferredDate: String,
        preferredTime: String,
        contactPhone: String,
        notes: String? = null
    ): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.createViewingRequest(
                id = listingId,
                request = ViewingAppointmentRequest(
                    preferredDate = preferredDate,
                    preferredTime = preferredTime,
                    contactPhone = contactPhone.trim(),
                    notes = notes?.trim()
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(true))
            } else {
                val errorMsg = response.body()?.error?.message ?: "Failed to schedule viewing"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun toggleSaveListing(listingId: String, currentlySaved: Boolean): Flow<NetworkResult<Boolean>> = flow {
        try {
            val response = if (currentlySaved) {
                apiService.unsaveListing(listingId)
            } else {
                apiService.saveListing(listingId)
            }
            if (response.isSuccessful && response.body()?.success == true) {
                val isSaved = response.body()!!.data?.isSaved ?: !currentlySaved
                emit(NetworkResult.Success(isSaved))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Failed to update bookmark"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error"))
        }
    }.flowOn(Dispatchers.IO)

    fun getSavedListings(): Flow<NetworkResult<List<ListingDto>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getSavedListings()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data?.savedListings?.map { it.listing } ?: emptyList()
                emit(NetworkResult.Success(list))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Failed to load saved listings"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error"))
        }
    }.flowOn(Dispatchers.IO)

    fun submitListing(request: CreateListingRequest): Flow<NetworkResult<ListingDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.createListing(request)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(response.body()!!.data!!.listing))
            } else {
                val errorMsg = response.body()?.error?.message ?: "Failed to submit property listing"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun getMyListings(): Flow<NetworkResult<List<MyListingDto>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getMyListings()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data?.listings ?: emptyList()
                emit(NetworkResult.Success(list))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Failed to load your listings"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun getPendingModerationListings(): Flow<NetworkResult<List<MyListingDto>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getPendingModerationListings()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data?.listings ?: emptyList()
                emit(NetworkResult.Success(list))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Failed to load pending queue"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun approveListing(listingId: String): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.approveListing(listingId)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(true))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Approval failed"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error"))
        }
    }.flowOn(Dispatchers.IO)

    fun rejectListing(listingId: String, reason: String): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.rejectListing(
                id = listingId,
                request = ModerateListingRequest(action = "REJECT", reason = reason, isVerifiedBadge = false)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(true))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Rejection failed"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error"))
        }
    }.flowOn(Dispatchers.IO)
}

