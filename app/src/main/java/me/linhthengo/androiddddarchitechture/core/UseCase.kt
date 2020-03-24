package me.linhthengo.androiddddarchitechture.core

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class UseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    operator fun invoke(
        params: Params,
        coroutineScope: CoroutineScope,
        onResult: (Either<Failure, Type>) -> Unit = {}
    ) = coroutineScope.launch {
        val job = async(Dispatchers.IO) { run(params) }
        launch(Dispatchers.Main) { onResult(job.await()) }
    }
}
