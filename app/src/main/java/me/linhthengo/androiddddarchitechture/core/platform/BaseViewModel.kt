package me.linhthengo.androiddddarchitechture.core.platform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.linhthengo.androiddddarchitechture.core.Failure
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    var failure: MutableLiveData<Failure> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        Timber.tag(this::class.java.name).e(failure.message)
        this.failure.value = failure
    }
}
