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

class ManagementRepository(private val tokenStorage: TokenStorage) {

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

    fun getProperties(): Flow<NetworkResult<List<PropertyDto>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getProperties()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data?.properties ?: emptyList()
                emit(NetworkResult.Success(list))
            } else {
                val msg = response.body()?.error?.message ?: "Failed to retrieve properties"
                emit(NetworkResult.Error(msg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun getPropertyById(id: String): Flow<NetworkResult<PropertyDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getPropertyById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val property = response.body()!!.data!!.property
                emit(NetworkResult.Success(property))
            } else {
                val msg = response.body()?.error?.message ?: "Failed to retrieve property details"
                emit(NetworkResult.Error(msg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun recordManualPayment(request: RecordManualPaymentRequest): Flow<NetworkResult<ManualPaymentRecordDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.recordManualPayment(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val record = response.body()!!.data!!.paymentRecord
                emit(NetworkResult.Success(record))
            } else {
                val msg = response.body()?.error?.message ?: "Failed to log manual payment"
                emit(NetworkResult.Error(msg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun createMaintenanceRequest(unitId: String, title: String, description: String, priority: MaintenancePriority): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.createMaintenanceRequest(
                unitId = unitId,
                request = CreateMaintenanceRequest(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                emit(NetworkResult.Success(true))
            } else {
                val msg = response.body()?.error?.message ?: "Failed to log maintenance request"
                emit(NetworkResult.Error(msg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)

    fun getFinancialReport(propertyId: String): Flow<NetworkResult<FinancialReportDto>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getFinancialReport(propertyId)
            if (response.isSuccessful && response.body()?.success == true) {
                val report = response.body()!!.data!!.report
                emit(NetworkResult.Success(report))
            } else {
                val msg = response.body()?.error?.message ?: "Failed to generate financial yield report"
                emit(NetworkResult.Error(msg, response.body()?.error?.code))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Network error", "NETWORK_ERROR"))
        }
    }.flowOn(Dispatchers.IO)
}

