package pk.arkrealestate.app.data.model

import com.google.gson.annotations.SerializedName

enum class UserRole {
    @SerializedName("BUYER_RENTER")
    BUYER_RENTER,

    @SerializedName("OWNER_AGENT")
    OWNER_AGENT,

    @SerializedName("PROPERTY_MANAGER")
    PROPERTY_MANAGER,

    @SerializedName("ADMIN")
    ADMIN
}

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: ApiErrorResponse?
)

data class ApiErrorResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: List<ApiErrorDetail>?
)

data class ApiErrorDetail(
    @SerializedName("field") val field: String?,
    @SerializedName("issue") val issue: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: UserRole,
    @SerializedName("agencyName") val agencyName: String?,
    @SerializedName("preferredLocale") val preferredLocale: String
)

data class AuthTokensDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class AuthDataDto(
    @SerializedName("user") val user: UserDto,
    @SerializedName("tokens") val tokens: AuthTokensDto
)

data class RefreshTokensDataDto(
    @SerializedName("tokens") val tokens: AuthTokensDto
)

data class UserProfileDataDto(
    @SerializedName("user") val user: UserDto
)

data class RegisterRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: UserRole = UserRole.BUYER_RENTER,
    @SerializedName("agencyName") val agencyName: String? = null,
    @SerializedName("preferredLocale") val preferredLocale: String = "en"
)

data class LoginRequest(
    @SerializedName("phoneOrEmail") val phoneOrEmail: String,
    @SerializedName("password") val password: String
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)
