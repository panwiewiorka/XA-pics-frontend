package xapics.app.auth

data class TokenResponse(
    val token: String,
    val userId: String?
)
