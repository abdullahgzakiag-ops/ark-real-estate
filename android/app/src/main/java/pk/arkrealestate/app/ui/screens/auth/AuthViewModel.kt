package pk.arkrealestate.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pk.arkrealestate.app.data.model.LoginRequest
import pk.arkrealestate.app.data.model.RegisterRequest
import pk.arkrealestate.app.data.model.UserDto
import pk.arkrealestate.app.data.model.UserRole
import pk.arkrealestate.app.data.remote.NetworkResult
import pk.arkrealestate.app.data.repository.AuthRepository

data class LoginUiState(
    val phone: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class RegisterUiState(
    val fullName: String = "",
    val phone: String = "",
    val password: String = "",
    val role: UserRole = UserRole.BUYER_RENTER,
    val agencyName: String = "",
    val licenseNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class SessionUiState(
    val isAuthenticated: Boolean = false,
    val user: UserDto? = null,
    val isUrdu: Boolean = false,
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _sessionState = MutableStateFlow(
        SessionUiState(
            isAuthenticated = authRepository.isUserLoggedIn(),
            isUrdu = authRepository.getPreferredLocale() == "ur"
        )
    )
    val sessionState: StateFlow<SessionUiState> = _sessionState.asStateFlow()

    init {
        if (authRepository.isUserLoggedIn()) {
            fetchCurrentUser()
        }
    }

    fun onLoginPhoneChanged(phone: String) {
        val sanitized = phone.filter { it.isDigit() }.take(10)
        _loginState.update { it.copy(phone = sanitized, errorMessage = null) }
    }

    fun onLoginPasswordChanged(password: String) {
        _loginState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onRegisterFullNameChanged(name: String) {
        _registerState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onRegisterPhoneChanged(phone: String) {
        val sanitized = phone.filter { it.isDigit() }.take(10)
        _registerState.update { it.copy(phone = sanitized, errorMessage = null) }
    }

    fun onRegisterPasswordChanged(password: String) {
        _registerState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onRegisterRoleChanged(role: UserRole) {
        _registerState.update { it.copy(role = role) }
    }

    fun onRegisterAgencyNameChanged(agency: String) {
        _registerState.update { it.copy(agencyName = agency) }
    }

    fun onRegisterLicenseNumberChanged(license: String) {
        _registerState.update { it.copy(licenseNumber = license) }
    }

    fun toggleLanguage() {
        val newIsUrdu = !_sessionState.value.isUrdu
        val newLocale = if (newIsUrdu) "ur" else "en"
        authRepository.savePreferredLocale(newLocale)
        _sessionState.update { it.copy(isUrdu = newIsUrdu) }
    }

    fun clearLoginError() {
        _loginState.update { it.copy(errorMessage = null) }
    }

    fun clearRegisterError() {
        _registerState.update { it.copy(errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _loginState.value
        if (state.phone.length < 10) {
            _loginState.update { it.copy(errorMessage = "Please enter a valid 10-digit mobile number") }
            return
        }
        if (state.password.length < 8) {
            _loginState.update { it.copy(errorMessage = "Password must be at least 8 characters") }
            return
        }

        viewModelScope.launch {
            val formattedPhone = if (state.phone.startsWith("0")) {
                "+92${state.phone.substring(1)}"
            } else if (state.phone.startsWith("+92")) {
                state.phone
            } else {
                "+92${state.phone}"
            }

            authRepository.login(
                LoginRequest(
                    phone = formattedPhone,
                    password = state.password
                )
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _loginState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _loginState.update { it.copy(isLoading = false, isSuccess = true) }
                        _sessionState.update {
                            it.copy(
                                isAuthenticated = true,
                                user = result.data
                            )
                        }
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        _loginState.update {
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

    fun register(onSuccess: () -> Unit) {
        val state = _registerState.value
        if (state.fullName.trim().length < 2) {
            _registerState.update { it.copy(errorMessage = "Full name must be at least 2 characters") }
            return
        }
        if (state.phone.length < 10) {
            _registerState.update { it.copy(errorMessage = "Please enter a valid 10-digit mobile number") }
            return
        }
        if (state.password.length < 8) {
            _registerState.update { it.copy(errorMessage = "Password must be at least 8 characters") }
            return
        }

        viewModelScope.launch {
            val formattedPhone = if (state.phone.startsWith("0")) {
                "+92${state.phone.substring(1)}"
            } else if (state.phone.startsWith("+92")) {
                state.phone
            } else {
                "+92${state.phone}"
            }

            val request = RegisterRequest(
                fullName = state.fullName.trim(),
                phone = formattedPhone,
                password = state.password,
                role = state.role,
                agencyName = if (state.agencyName.isNotBlank()) state.agencyName.trim() else null,
                licenseNumber = if (state.licenseNumber.isNotBlank()) state.licenseNumber.trim() else null
            )

            authRepository.register(request).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _registerState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _registerState.update { it.copy(isLoading = false, isSuccess = true) }
                        _sessionState.update {
                            it.copy(
                                isAuthenticated = true,
                                user = result.data
                            )
                        }
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        _registerState.update {
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

    fun fetchCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _sessionState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _sessionState.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                user = result.data
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _sessionState.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = false,
                                user = null
                            )
                        }
                        authRepository.logout()
                    }
                }
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        _sessionState.update {
            it.copy(
                isAuthenticated = false,
                user = null
            )
        }
        _loginState.update { LoginUiState() }
        _registerState.update { RegisterUiState() }
        onLoggedOut()
    }
}
