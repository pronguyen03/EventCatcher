package me.linhthengo.androiddddarchitechture.presentation.auth.signup

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.sign_up_fragment.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.domain.core.ValueFailures
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthFragment
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthViewModel

class SignUpFragment : AuthFragment() {
    override fun layoutId(): Int = R.layout.sign_up_fragment

    override val authViewModel by viewModels<SignUpViewModel>(factoryProducer = { viewModelFactory })

    override fun handleAuthState(state: AuthViewModel.State) = handleSignUpState(state)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_confirm_password.editText?.doAfterTextChanged { handleConfirmPassword(it.toString()) }

        btn_sign_up.setOnClickListener {
            if (tv_email.editText?.error.isNullOrBlank()
                && tv_password.editText?.error.isNullOrBlank()
                && tv_confirm_password.editText?.error.isNullOrBlank()
                && tv_email.editText?.text!!.isNotEmpty()
                && tv_password.editText?.text!!.isNotEmpty()
                && tv_confirm_password.editText?.text!!.isNotEmpty()
            ) {
                authViewModel.signUp(
                    email = tv_email.editText?.text.toString(),
                    password = tv_password.editText?.text.toString()
                )
            } else {
                handleEmail(tv_email.editText?.text.toString())
                handlePassword(tv_password.editText?.text.toString())
                handleConfirmPassword(tv_confirm_password.editText?.text.toString())
            }
        }

        btn_back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun handleConfirmPassword(text: String) {
        when {
            text.isEmpty() -> tv_confirm_password.error =
                handleValidatorError(ValueFailures.Empty(text))
            tv_password.editText?.text.toString() != text -> tv_confirm_password.error =
                handleValidatorError(ValueFailures.NotTheSamePassword(text))
            else -> tv_confirm_password.error = null
        }
    }

    private fun handleSignUpState(state: AuthViewModel.State) {
        when (state) {
            is AuthViewModel.State.Failure -> {
                showErrorDialog(state.message) {
                    btn_sign_up.revertAnimation()
                }
            }
            is AuthViewModel.State.Success -> {
                btn_sign_up.revertAnimation()
                Toast.makeText(context, R.string.sign_in_successfully, Toast.LENGTH_LONG).show()
                activity?.onBackPressed()
            }
            is AuthViewModel.State.Loading -> {
                btn_sign_up.startAnimation()
            }
        }
    }
}
