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

class AuthRepository(private val tokenStorage: TokenStorage) {

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

        // 10.0.2.2 maps to local machine localhost on standard Android Emulator
        val baseUrl = "http://10.0.2.2:5000/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ArkApiService::class.java)
    }

    fun register(request: RegisterRequest): Flow<NetworkResult<UserDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                tokenStorage.saveTokens(data.tokens.accessToken, data.tokens.refreshToken)
                tokenStorage.saveUserSession(data.user.id, data.user.role.name)
                emit(NetworkResult.Success(data.user))
            } else {
                val errorMsg = response.body()?.error?.message ?: response.message() ?: "Registration failed"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network connection error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun login(request: LoginRequest): Flow<NetworkResult<UserDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                tokenStorage.saveTokens(data.tokens.accessToken, data.tokens.refreshToken)
                tokenStorage.saveUserSession(data.user.id, data.user.role.name)
                emit(NetworkResult.Success(data.user))
            } else {
                val errorMsg = response.body()?.error?.message ?: "Invalid mobile number or password"
                emit(NetworkResult.Error(errorMsg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Unable to connect to ARK server", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun getCurrentUser(): Flow<NetworkResult<UserDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(response.body()!!.data!!.user))
            } else {
                emit(NetworkResult.Error(response.body()?.error?.message ?: "Session expired", "UNAUTHORIZED"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun isUserLoggedIn(): Boolean = tokenStorage.isAuthenticated()

    fun logout() {
        tokenStorage.clearSession()
    }

    fun savePreferredLocale(locale: String) {
        tokenStorage.saveLocale(locale)
    }

    fun getPreferredLocale(): String = tokenStorage.getLocale()
}
