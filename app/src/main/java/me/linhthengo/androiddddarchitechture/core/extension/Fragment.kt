package me.linhthengo.androiddddarchitechture.core.extension

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.activity_main.*
import me.linhthengo.androiddddarchitechture.core.platform.BaseActivity
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

//inline fun <reified T : ViewModel> Fragment.viewModel(factory: Factory, body: T.() -> Unit): T {
//    val vm = ViewModelProvider.of(this, factory)[T::class.java]
//    vm.body()
//    return vm
//}

fun BaseFragment.close() = activity?.supportFragmentManager?.popBackStack()

val BaseFragment.viewContainer: Fragment get() = (activity as BaseActivity).nav_host

val BaseFragment.appContext: Context get() = activity?.applicationContext!!

val BaseFragment.lifeCycleOwner: LifecycleOwner get() = activity as LifecycleOwner
