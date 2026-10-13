package pk.arkrealestate.app.data.remote

import pk.arkrealestate.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ArkApiService {

    // Health
    @GET("v1/health")
    suspend fun checkHealth(): Response<ApiResponse<Map<String, Any>>>

    // Auth
    @POST("v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthDataDto>>

    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthDataDto>>

    @POST("v1/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<RefreshTokensDataDto>>

    @GET("v1/auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserProfileDataDto>>

    // Marketplace Listings
    @GET("v1/listings")
    suspend fun getListings(
        @Query("search") search: String? = null,
        @Query("city") city: String? = null,
        @Query("areaLocation") areaLocation: String? = null,
        @Query("purpose") purpose: String? = null,
        @Query("propertyType") propertyType: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("bedrooms") bedrooms: Int? = null,
        @Query("bathrooms") bathrooms: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sortBy") sortBy: String = "newest"
    ): Response<ApiResponse<List<ListingDto>>>

    @GET("v1/listings/{id}")
    suspend fun getListingById(
        @Path("id") id: String
    ): Response<ApiResponse<ListingDetailResponseDto>>

    @POST("v1/listings")
    suspend fun createListing(
        @Body request: CreateListingRequest
    ): Response<ApiResponse<ListingDetailResponseDto>>

    @POST("v1/listings/{id}/inquiries")
    suspend fun createInquiry(
        @Path("id") id: String,
        @Body request: InquiryRequest
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("v1/listings/{id}/viewings")
    suspend fun createViewingRequest(
        @Path("id") id: String,
        @Body request: ViewingAppointmentRequest
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("v1/listings/{id}/save")
    suspend fun saveListing(
        @Path("id") id: String
    ): Response<ApiResponse<SaveListingResponseDto>>

    @DELETE("v1/listings/{id}/save")
    suspend fun unsaveListing(
        @Path("id") id: String
    ): Response<ApiResponse<SaveListingResponseDto>>

    @GET("v1/listings/user/saved")
    suspend fun getSavedListings(): Response<ApiResponse<SavedListingsResponseDto>>

    @GET("v1/listings/user/my-listings")
    suspend fun getMyListings(): Response<ApiResponse<MyListingsResponseDto>>

    // Admin Moderation
    @GET("v1/moderation/listings/pending")
    suspend fun getPendingModerationListings(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<ModerationListingsResponseDto>>

    @PATCH("v1/moderation/listings/{id}/approve")
    suspend fun approveListing(
        @Path("id") id: String,
        @Body request: ModerateListingRequest = ModerateListingRequest(action = "APPROVE", isVerifiedBadge = true)
    ): Response<ApiResponse<ListingDetailResponseDto>>

    @PATCH("v1/moderation/listings/{id}/reject")
    suspend fun rejectListing(
        @Path("id") id: String,
        @Body request: ModerateListingRequest
    ): Response<ApiResponse<ListingDetailResponseDto>>

    // Property Management & Tenancy
    @GET("v1/properties")
    suspend fun getProperties(): Response<ApiResponse<PropertiesResponseDto>>

    @GET("v1/properties/{id}")
    suspend fun getPropertyById(
        @Path("id") id: String
    ): Response<ApiResponse<PropertyDetailResponseDto>>

    @POST("v1/properties")
    suspend fun createProperty(
        @Body request: CreatePropertyRequest
    ): Response<ApiResponse<PropertyDetailResponseDto>>

    @POST("v1/properties/payments/manual")
    suspend fun recordManualPayment(
        @Body request: RecordManualPaymentRequest
    ): Response<ApiResponse<ManualPaymentResponseDto>>

    @POST("v1/properties/units/{unitId}/maintenance")
    suspend fun createMaintenanceRequest(
        @Path("unitId") unitId: String,
        @Body request: CreateMaintenanceRequest
    ): Response<ApiResponse<Map<String, Any>>>

    @GET("v1/properties/{propertyId}/financial-report")
    suspend fun getFinancialReport(
        @Path("propertyId") propertyId: String
    ): Response<ApiResponse<FinancialReportResponseDto>>
}

