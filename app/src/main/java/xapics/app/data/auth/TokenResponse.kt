package xapics.app.data.auth

data class TokenResponse(
    val token: String,
    val userId: String?
)
