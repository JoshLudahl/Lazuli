package com.softklass.lazuli.data.models

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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
 * Executes a suspendable action and emits its state as a flow of `Resource`.
 *
 * The emitted flow will go through the following states:
 * 1. `Resource.Loading`: Indicates that the operation is in progress.
 * 2. `Resource.Success`: Contains the result of the successful operation.
 * 3. `Resource.Error`: Contains error details if the operation fails.
 *
 * This function ensures that the computation is performed on the `Dispatchers.Default` dispatcher.
 *
 * Usage Example:
 * ```
 * val resultFlow = action { yourSuspendableFunction() }
 * resultFlow.collect { resource ->
 *     when (resource) {
 *         is Resource.Loading -> // Handle loading state
 *         is Resource.Success -> // Access resource.data for result
 *         is Resource.Error -> // Handle error using resource.exception or resource.message
 *     }
 * }
 * ```
 *
 * @param action A suspendable lambda that represents the operation to perform.
 * @return A flow that emits the state (`Resource`) of the operation.
 */
fun <T> action(
    action: suspend () -> T,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) = flow {
    emit(Resource.Loading)
    try {
        emit(Resource.Success(action()))
    } catch (e: Exception) {
        emit(
            Resource.Error(
                exception = e,
                message = e.localizedMessage ?: "An unknown error occurred"
            )
        )
    }
}.flowOn(coroutineDispatcher)
