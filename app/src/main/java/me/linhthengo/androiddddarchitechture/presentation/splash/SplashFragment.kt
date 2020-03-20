package me.linhthengo.androiddddarchitechture.presentation.splash

import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment

class SplashFragment : BaseFragment() {

//    @Inject
//    private lateinit var firebaseAuthManager: FirebaseAuthManager

    override fun layoutId(): Int = R.layout.splash_fragment

    init {
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(3000)
//            val user = firebaseAuthManager.getCurrentUser()
//            if (user != null) {
//                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
//            } else {
//                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
//            }
//        }
    }
}