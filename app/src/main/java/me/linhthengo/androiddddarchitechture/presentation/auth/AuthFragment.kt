package me.linhthengo.androiddddarchitechture.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.extension.lifeCycleOwner
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.domain.auth.EmailAddress
import me.linhthengo.androiddddarchitechture.domain.auth.Password

abstract class AuthFragment : BaseFragment() {
    lateinit var tvEmail: TextInputLayout

    lateinit var tvPassword: TextInputLayout

    internal abstract val authViewModel: AuthViewModel

    private val authStateObserver = Observer<AuthViewModel.State> { handleAuthState(it) }

    internal abstract fun handleAuthState(state: AuthViewModel.State)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.state.observe(lifeCycleOwner, authStateObserver)

        tvEmail = view.findViewById<TextInputLayout>(R.id.tv_email).apply {
            editText?.doAfterTextChanged {
                handleEmail(it.toString())
            }
        }

        tvPassword = view.findViewById<TextInputLayout>(R.id.tv_password).apply {
            editText?.doAfterTextChanged {
                handlePassword(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.state.removeObserver(authStateObserver)
    }

    internal fun handlePassword(input: String) = Password(input).value.fold({ valueFailures ->
        tvPassword.error = handleValidatorError(valueFailures)
    }, {
        tvPassword.error = null
    })

    internal fun handleEmail(input: String) = EmailAddress(input).value.fold({ valueFailures ->
        tvEmail.error = handleValidatorError(valueFailures)
    }, {
        tvEmail.error = null
    })
}
