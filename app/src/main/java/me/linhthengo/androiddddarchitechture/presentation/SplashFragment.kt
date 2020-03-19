package me.linhthengo.androiddddarchitechture.presentation

import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment

class SplashFragment : BaseFragment() {

    override fun layoutId(): Int = R.layout.splash_fragment

    init {
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }
}