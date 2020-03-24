package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.sign_in_fragment.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.domain.auth.EmailAddress
import me.linhthengo.androiddddarchitechture.domain.auth.Password
import me.linhthengo.androiddddarchitechture.domain.core.ValueFailures
import timber.log.Timber

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
            Timber.tag(this::class.java.name).e("sign in clicked")
            if (tv_email.editText!!.error.isNullOrBlank() && tv_password.editText!!.error.isNullOrBlank()) {
                Timber.tag(this::class.java.name).e("sign in")
                signInViewModel.signIn(
                    tv_email.editText?.text.toString(),
                    tv_password.editText?.text.toString()
                ) {
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                }
            } else {
                handleEmail(tv_email.editText?.text.toString())
                handlePassword(tv_password.editText?.text.toString())
            }
        }

        signInViewModel.failure.observe(this.activity as LifecycleOwner, Observer {
            it?.let {
                MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.sign_in_failure))
                    .setMessage(getString(R.string.error_s).format(it.message))
                    .setPositiveButton(getString(R.string.close), null)
                    .show()
            }
        })
    }

    private fun handleError(valueFailures: ValueFailures<String>) = when (valueFailures) {
        is ValueFailures.ShortPassword<String> -> getString(R.string.password_too_short)
        is ValueFailures.InvalidEmail -> getString(R.string.invalid_email)
    }

    private fun handlePassword(input: String) = Password(input).value.fold({ valueFailures ->
        tv_password.editText?.error = handleError(valueFailures)
    }, {
        tv_password.editText?.error = null
    })

    private fun handleEmail(input: String) = EmailAddress(input).value.fold({ valueFailures ->
        tv_email.editText?.error = handleError(valueFailures)
    }, {
        tv_email.editText?.error = null
    })
}
