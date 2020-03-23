package me.linhthengo.androiddddarchitechture.presentation.splash

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import javax.inject.Inject

class SplashFragment : BaseFragment() {

    @Inject
    lateinit var firebaseAuthManager: FirebaseAuthManager

    override fun layoutId(): Int = R.layout.splash_fragment

    init {
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            val user = firebaseAuthManager.getCurrentUser()
            Toast.makeText(context, "Loading finished", Toast.LENGTH_SHORT)
                .show()
            if (user != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
            }
        }
    }
}
