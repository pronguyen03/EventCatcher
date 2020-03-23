package me.linhthengo.androiddddarchitechture.presentation.auth.signin

import androidx.fragment.app.viewModels
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment

class SignInFragment : BaseFragment() {
    override fun layoutId(): Int = R.layout.sign_in_fragment

    private val signInViewModel by viewModels<SignInViewModel>(
        factoryProducer = { viewModelFactory }
    )

}
