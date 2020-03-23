package me.linhthengo.androiddddarchitechture.core.extension

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

//inline fun <reified T : ViewModel> Fragment.viewModel(factory: Factory, body: T.() -> Unit): T {
//    val vm = ViewModelProvider.of(this, factory)[T::class.java]
//    vm.body()
//    return vm
//}

fun BaseFragment.close() = fragmentManager?.popBackStack()

//val BaseFragment.viewContainer: View get() = (activity as BaseActivity).fragmentContainer

val BaseFragment.appContext: Context get() = activity?.applicationContext!!