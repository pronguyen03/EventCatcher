package me.linhthengo.androiddddarchitechture.presentation.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.aplication.home.GetEvent
import me.linhthengo.androiddddarchitechture.aplication.home.SignOut
import me.linhthengo.androiddddarchitechture.core.Failure
import me.linhthengo.androiddddarchitechture.core.platform.BaseViewModel
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInViewModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val signOut: SignOut
//    private val getEvent: GetEvent,
//    private val getListAllEvent: GetListAllEvent
) : BaseViewModel() {
    sealed class State {
        data class Failure(val message: String) : State()
        object Success : State()
    }

    var state = MutableLiveData<State>()

    fun signOut() = viewModelScope.launch {
        signOut(null,this) {
            it.fold(::handleFailure, ::handleSuccess)
        }
    }

    private fun handleFailure(failure: Failure) {
        Timber.tag(SignInViewModel.TAG).e(failure.message)
    }

    private fun handleSuccess(unit: Unit) {
        this.state.postValue(State.Success)
    }

    companion object {
        const val REQUEST_LOCATION_CODE = 101
        const val GPS_REQUEST_CODE = 9003
    }

    fun getEvent(isUpcoming: Boolean, isOngoing: Boolean, location: Location, scope: Int, startDate: Calendar) = viewModelScope.launch {
//        getEvent(GetEvent.Params(isUpcoming, isOngoing, location, scope, startDate.timeInMillis))
    }

//    fun getListAllEvent() = viewModelScope.async {
//        getListAllEvent(null, this)
//    }

}