package xapics.app.data.auth

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val userName: String?
)
