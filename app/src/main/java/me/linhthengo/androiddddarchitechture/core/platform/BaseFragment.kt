package me.linhthengo.androiddddarchitechture.core.platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.extension.appContext
import me.linhthengo.androiddddarchitechture.core.extension.viewContainer
import me.linhthengo.androiddddarchitechture.domain.core.ValueFailures
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {
    abstract fun layoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutId(), container, false)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    internal fun notify(@StringRes message: Int) =
        Snackbar.make(viewContainer, message, Snackbar.LENGTH_SHORT).show()

    internal fun notifyWithAction(
        @StringRes message: Int,
        @StringRes actionText: Int,
        action: () -> Any
    ) {
        val snackBar = Snackbar.make(viewContainer, message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction(actionText) { _ -> action.invoke() }
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorTextPrimary
            )
        )
        snackBar.show()
    }

    internal fun showErrorDialog(message: String, onCloseClicked: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.sign_in_failure)
            .setMessage(getString(R.string.error_s).format(message))
            .setPositiveButton(R.string.close) { _, _ ->
                onCloseClicked()
            }
            .show()
    }

    internal fun handleValidatorError(valueFailures: ValueFailures<String>) = when (valueFailures) {
        is ValueFailures.ShortPassword<String> -> getString(R.string.password_too_short)
        is ValueFailures.InvalidEmail -> getString(R.string.invalid_email)
        is ValueFailures.NotTheSamePassword -> getString(R.string.password_is_not_the_same)
        is ValueFailures.Empty -> getString(R.string.empty)
    }
}
