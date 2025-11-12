package hr.foi.air.giveaway.model

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val message: String? = null
)
