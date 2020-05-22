package me.linhthengo.androiddddarchitechture.presentation.auth.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.edit_event.*
import kotlinx.android.synthetic.main.fragment_tutorial.*
import me.linhthengo.androiddddarchitechture.R

/**
 * A simple [Fragment] subclass.
 */
class TutorialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_back.setOnClickListener{
            findNavController().navigate(R.id.action_tutorialFragment_to_homeFragment)
        }
    }
}
