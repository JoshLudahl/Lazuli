package com.softklass.lazuli.data.models

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

enum class Action {
    DATABASE, NETWORK, UNKNOWN
}

/**
 * A sealed interface representing the state of a resource, commonly used for wrapping data fetching
 * or processing results. It encompasses various states such as loading, success, and error scenarios.
 *
 * Usage Example:
 * ```
 * val resource: Resource<Int> = Resource.Success(42)
 * when (resource) {
 *     is Resource.Loading -> // Handle loading state
 *     is Resource.Success -> println("Data: ${resource.data}")
 *     is Resource.Error -> when (resource) {
 *         is Resource.Error.DataBase -> println("Database Error: ${resource.exception}")
 *         is Resource.Error.Network -> println("Network Error: ${resource.exception}")
 *         is Resource.Error.Unknown -> println("Unknown Error: ${resource.exception}")
 *     }
 * }
 * ```
 */
sealed interface Resource<out T> {

    sealed interface Error : Resource<Nothing> {
        data class DataBase(val exception: Exception) : Error
        data class Network(val exception: Exception) : Error
        data class Unknown(val exception: Exception) : Error
    }

    data object Loading : Resource<Nothing>

    class Success<T>(val data: T) : Resource<T>
}


/**
 * Executes a suspend function and emits its results wrapped in a Resource. Handles loading, success, and error states.
 *
 * Usage Example:
 * ```
 * val resourceFlow = action {
 *     // Perform a suspend operation, e.g., a network or database fetch
 *     fetchDataFromApi()
 * }
 * resourceFlow.collect { resource ->
 *     when (resource) {
 *         is Resource.Loading -> // Handle loading state
 *         is Resource.Success -> println("Data: ${resource.data}")
 *         is Resource.Error -> println("Error: ${resource.exception.message}")
 *     }
 * }
 * ```
 *
 * @param action The suspend function to be executed, returning the desired type.
 * @param coroutineDispatcher The [CoroutineDispatcher] to run the action on; defaults to [Dispatchers.Default].
 * @return A Flow emitting [Resource] states ([Resource.Loading], [Resource.Success], [Resource.Error]).
 */
fun <T> action(
    action: suspend () -> T,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
    actionType: Action = Action.UNKNOWN
) = flow {
    emit(Resource.Loading)
    try {
        emit(Resource.Success(action()))
    } catch (e: Exception) {
        emit(
            when (actionType) {
                Action.DATABASE -> Resource.Error.DataBase(e)
                Action.NETWORK -> Resource.Error.Network(e)
                else -> Resource.Error.Unknown(e)
            }
        )
    }
}.flowOn(coroutineDispatcher)
