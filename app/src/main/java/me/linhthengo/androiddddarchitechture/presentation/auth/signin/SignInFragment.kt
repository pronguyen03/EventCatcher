package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.sign_in_fragment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthFragment
import me.linhthengo.androiddddarchitechture.presentation.auth.AuthViewModel
import me.linhthengo.androiddddarchitechture.presentation.auth.signin.SignInViewModel.Companion.GOOGLE_SIGN_IN_REQUEST_CODE


class SignInFragment : AuthFragment() {

    lateinit var googleSignInClient: GoogleSignInClient

    override fun layoutId(): Int = R.layout.sign_in_fragment

    override fun handleAuthState(state: AuthViewModel.State) = handleSignIn(state)

    override val authViewModel by viewModels<SignInViewModel>(factoryProducer = { viewModelFactory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        googleSignInClient = authViewModel.getGoogleSignInClient()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_sign_up.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        btn_sign_in.setOnClickListener {
            if (tv_email.editText!!.error.isNullOrBlank() && tv_password.editText!!.error.isNullOrBlank()) {
                authViewModel.signIn(
                    tv_email.editText?.text.toString(),
                    tv_password.editText?.text.toString()
                )
            } else {
                handleEmail(tv_email.editText?.text.toString())
                handlePassword(tv_password.editText?.text.toString())
            }
        }

        btn_sign_in_with_google.setOnClickListener {
            btn_sign_in_with_google.startAnimation()
            val signInIntent = googleSignInClient.signInIntent
            signInIntent?.run {
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            runBlocking {
                try {
                    val googleSignInAccount = authViewModel.getSignedInAccountFromIntent(data).await()
                    googleSignInAccount?.let { authViewModel.signInWithGoogle(it) }
                } catch (e: Exception) {
                    btn_sign_in_with_google.revertAnimation()
                }
            }
        }
    }

    private fun handleSignIn(state: AuthViewModel.State) {
        when (state) {
            is AuthViewModel.State.Failure -> {
                showErrorDialog(state.message) {
                    btn_sign_in.revertAnimation()
                }
            }
            is AuthViewModel.State.Success -> {
                btn_sign_in.revertAnimation()
                lifecycleScope.launch {
                    btn_sign_in.doneLoadingAnimation(
                        R.color.white,
                        getDrawable(
                            this@SignInFragment.requireContext(),
                            R.drawable.ic_check_white_24dp
                        )!!.toBitmap()
                    )
                    delay(500)
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                }
            }
            is AuthViewModel.State.Loading -> {
                btn_sign_in.startAnimation()
            }
        }
    }
}
