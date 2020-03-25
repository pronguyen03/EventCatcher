package me.linhthengo.androiddddarchitechture.presentation.auth.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.sign_up_fragment.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment

class SignUpFragment : BaseFragment() {
    private val signUpViewModel by viewModels<SignUpViewModel>(
        factoryProducer = { viewModelFactory }
    )

    override fun layoutId(): Int = R.layout.sign_up_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_sign_up.setOnClickListener {

        }
    }
}