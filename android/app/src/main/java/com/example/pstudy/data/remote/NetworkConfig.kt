package com.example.pstudy.data.remote

/**
 * Network configuration for API calls
 */
object NetworkConfig {
    // Base URL for API endpoints
    const val BASE_URL = "https://android-p-study.onrender.com/"
    const val TIME_OUT = 120L

    // Error messages
    const val NETWORK_ERROR_MESSAGE =
        "Unable to connect to server. Please check your internet connection."
    const val GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please try again later."

    // HTTP status codes
    object StatusCodes {
        const val OK = 200
        const val CREATED = 201
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val NOT_FOUND = 404
        const val SERVER_ERROR = 500
    }
}