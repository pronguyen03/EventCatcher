package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.domain.auth.EmailAddress
import me.linhthengo.androiddddarchitechture.domain.auth.Password
import me.linhthengo.androiddddarchitechture.domain.core.ValueFailures

class SignInFragment : BaseFragment() {
    override fun layoutId(): Int = R.layout.sign_in_fragment

    private val signInViewModel by viewModels<SignInViewModel>(
        factoryProducer = { viewModelFactory }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_sign_up.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        tv_email.editText?.doAfterTextChanged {
            handleEmail(it.toString())
        }

        tv_password.editText?.doAfterTextChanged {
            handlePassword(it.toString())
        }

        btn_sign_in.setOnClickListener {
            if (tv_email.editText!!.error.isNullOrBlank() && tv_password.editText!!.error.isNullOrBlank()) {
                signInViewModel.signIn(
                    tv_email.editText?.text.toString(),
                    tv_password.editText?.text.toString()
                )
            } else {
                handleEmail(tv_email.editText?.text.toString())
                handlePassword(tv_password.editText?.text.toString())
            }
        }

        signInViewModel.state.observe(this.activity as LifecycleOwner, Observer {
            it?.let { handleSignIn(it) }
        })
    }

    private fun handleSignIn(state: SignInViewModel.State) {
        when (state) {
            is SignInViewModel.State.SignInFailure -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.sign_in_failure)
                    .setMessage(getString(R.string.error_s).format(state.message))
                    .setPositiveButton(R.string.close) { _, _ ->
                        btn_sign_in.revertAnimation()
                    }
                    .show()
            }
            is SignInViewModel.State.SignInSuccess -> {
                lifecycleScope.launch {
                    btn_sign_in.doneLoadingAnimation(
                        R.color.white,
                        getDrawable(
                            this@SignInFragment.requireContext(),
                            R.drawable.ic_check_white_24dp
                        )!!.toBitmap()
                    )
                    delay(500)
                    activity?.onBackPressed()
                }
            }
            is SignInViewModel.State.SignInLoading -> {
                btn_sign_in.startAnimation()
            }
        }
    }

    private fun handleValidatorError(valueFailures: ValueFailures<String>) = when (valueFailures) {
        is ValueFailures.ShortPassword<String> -> getString(R.string.password_too_short)
        is ValueFailures.InvalidEmail -> getString(R.string.invalid_email)
    }

    private fun handlePassword(input: String) = Password(input).value.fold({ valueFailures ->
        tv_password.editText?.error = handleValidatorError(valueFailures)
    }, {
        tv_password.editText?.error = null
    })

    private fun handleEmail(input: String) = EmailAddress(input).value.fold({ valueFailures ->
        tv_email.editText?.error = handleValidatorError(valueFailures)
    }, {
        tv_email.editText?.error = null
    })
}
