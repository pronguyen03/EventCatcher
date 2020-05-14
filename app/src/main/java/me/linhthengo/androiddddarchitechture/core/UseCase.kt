package me.linhthengo.androiddddarchitechture.core

import arrow.core.Either
import kotlinx.coroutines.*

abstract class UseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    operator fun invoke(
        params: Params,
        coroutineScope: CoroutineScope,
        computeDispatcher: CoroutineDispatcher = Dispatchers.IO,
        resultDispatcher: CoroutineDispatcher = Dispatchers.Main,
        onResult: (Either<Failure, Type>) -> Unit = {}
    ) = coroutineScope.launch {
        val job = async(computeDispatcher) { run(params) }
        launch(resultDispatcher) { onResult(job.await()) }
    }
}
