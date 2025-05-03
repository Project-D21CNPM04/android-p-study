package com.example.pstudy.data.remote.utils

import com.example.pstudy.data.remote.NetworkConfig
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safely executes an API call with proper error handling
 * @param apiCall The suspend function that executes a network request
 * @return NetworkResult wrapped around the response data
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        handleApiResponse(apiCall())
    } catch (e: HttpException) {
        NetworkResult.Error(
            message = getErrorMessageByStatusCode(e.code()),
            code = e.code(),
            exception = e
        )
    } catch (e: UnknownHostException) {
        NetworkResult.Error(
            message = NetworkConfig.NETWORK_ERROR_MESSAGE,
            exception = e
        )
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error(
            message = "Connection timed out. Please try again.",
            exception = e
        )
    } catch (e: IOException) {
        NetworkResult.Error(
            message = NetworkConfig.NETWORK_ERROR_MESSAGE,
            exception = e
        )
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.localizedMessage ?: NetworkConfig.GENERIC_ERROR_MESSAGE,
            exception = e
        )
    }
}

/**
 * Handles the API response and converts it to NetworkResult
 */
private fun <T> handleApiResponse(response: Response<T>): NetworkResult<T> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Error(
                message = "Response body is null",
                code = response.code()
            )
        }
    } else {
        val errorMsg = response.errorBody()?.string()
            ?: getErrorMessageByStatusCode(response.code())
        NetworkResult.Error(
            message = errorMsg,
            code = response.code()
        )
    }
}

/**
 * Returns a user-friendly error message based on HTTP status code
 */
private fun getErrorMessageByStatusCode(code: Int): String {
    return when (code) {
        NetworkConfig.StatusCodes.BAD_REQUEST -> "Invalid request. Please try again."
        NetworkConfig.StatusCodes.UNAUTHORIZED -> "Authentication required. Please log in again."
        NetworkConfig.StatusCodes.NOT_FOUND -> "The requested resource was not found."
        NetworkConfig.StatusCodes.SERVER_ERROR -> "Server error. Please try again later."
        else -> NetworkConfig.GENERIC_ERROR_MESSAGE
    }
}

// Extension function to log API responses consistently
fun <T> logApiResponse(tag: String, result: NetworkResult<T>) {
    when (result) {
        is NetworkResult.Success -> android.util.Log.d(tag, "Success")
        is NetworkResult.Error -> android.util.Log.e(
            tag,
            "Error - ${result.message} (${result.code})"
        )

        is NetworkResult.Loading -> android.util.Log.d(tag, "Loading")
    }
}