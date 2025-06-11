package com.softklass.lazuli.data.models

/**
 * Represents the result of an operation, which can be one of the following:
 * - Error: Contains details about an error that occurred during the operation.
 * - Loading: Indicates that the operation is still in progress.
 * - Success: Contains the successful result of the operation.
 */
sealed interface Resource<out T> {
    data class Error(
        val exception: Exception? = null,
        val message: String? = null,
    ) : Resource<Nothing>

    data object Loading : Resource<Nothing>

    class Success<T>(val data: T) : Resource<T>
}

/**
 * Executes the given suspending action and wraps its result in a Resource object.
 * - On success, returns a Resource.Success containing the data returned by the action.
 * - On failure, catches the exception and returns a Resource.Error with the exception and its message.
 *
 * @param action The suspending function to execute.
 * @return A Resource representing the result of the action.
 */
suspend fun <T> action(action: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(action())
    } catch (e: Exception) {
        Resource.Error(
            exception = e,
            message = e.localizedMessage ?: "An unknown error occurred"
        )
    }
}
