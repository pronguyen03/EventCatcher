package me.linhthengo.androiddddarchitechture.presentation.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.event_detail.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.models.Event
import me.linhthengo.androiddddarchitechture.models.User
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventDetailFragment: BaseFragment() {
    @Inject
    lateinit var firebaseAuthManager: FirebaseAuthManager
    private var firestoreDB: FirebaseFirestore? = null

    override fun layoutId(): Int = R.layout.event_detail

    private var eventDetail: Event? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firestoreDB = FirebaseFirestore.getInstance()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = this.arguments
        eventDetail = args?.getParcelable("event")
        eventDetail?.run {
            bindDataEvent(this)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun bindDataEvent(event: Event) {
        Picasso.get().load(event.image).into(title_image)
        val sdf = SimpleDateFormat("E, dd/MM/YY 'at' HH:mm", Locale.UK)

        val startDate = Date(event.startDate)
        title_date.text = SimpleDateFormat("dd", Locale.UK).format(startDate)
        title_month.text = SimpleDateFormat("MMM", Locale.UK).format(startDate)
        title_name.text = event.name
        title_host.text = "Host by ${event.hostName}"
        tv_start_date.text = sdf.format(startDate).toString()
        tv_location_name.text = event.locationName
        tv_location_address.text = event.location
        val currentUser = this.firebaseAuthManager.getCurrentUser()!!

        // Button Interest
        var isInterested = checkIsInterestedOrParticipated(event.listInterest, currentUser.uid)
        if (isInterested > -1) {
            setActiveInterest(true)
        } else {
            setActiveInterest(false)
        }

        tv_clickable_interest.setOnClickListener {
            tv_clickable_interest.isClickable = false
            tv_clickable_interest.isFocusable = false
            isInterested = checkIsInterestedOrParticipated(event.listInterest, currentUser.uid)

            if (isInterested > -1) {
                event.listInterest.removeAt(isInterested)
                firestoreDB!!.collection("event").document(event.id).update(
                    "listInterest", event.listInterest
                ).addOnCompleteListener {
                    setActiveInterest(false)
                }
            } else {
                event.listInterest.add(
                    User(
                        currentUser.uid,
                        currentUser.displayName,
                        currentUser.email
                    )
                )
                firestoreDB!!.collection("event").document(event.id).update(
                    "listInterest", event.listInterest
                ).addOnCompleteListener {
                    setActiveInterest(true)
                }
            }
        }


        // Button Going
        var isParticipated = checkIsInterestedOrParticipated(event.listParticipant, currentUser.uid)
        if (isParticipated > -1) {
            setActiveParticipate(true)
        } else {
            setActiveParticipate(false)
        }

        tv_clickable_going.setOnClickListener {
            tv_clickable_going.isClickable = false
            tv_clickable_going.isFocusable = false
            isParticipated = checkIsInterestedOrParticipated(event.listParticipant, currentUser.uid)

            if (isParticipated > -1) {
                event.listParticipant.removeAt(isParticipated)
                firestoreDB!!.collection("event").document(event.id).update(
                    "listParticipant", event.listParticipant
                ).addOnCompleteListener {
                    setActiveParticipate(false)
                }
            } else {
                event.listParticipant.add(
                    User(
                        currentUser.uid,
                        currentUser.displayName,
                        currentUser.email
                    )
                )
                firestoreDB!!.collection("event").document(event.id).update(
                    "listParticipant", event.listParticipant
                ).addOnCompleteListener {
                    setActiveParticipate(true)
                }
            }
        }
    }

    private fun checkIsInterestedOrParticipated(listUser: MutableList<User>, userUID: String): Int {
        for ((index,user) in listUser.withIndex()) {
            if (user.uid == userUID) {
                return index
            }
        }
        return -1
    }

    private fun setActiveInterest(boolean: Boolean) {
        if (boolean) {
            tv_clickable_interest.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.ic_star_color_primary_36dp,
                0,
                0
            )
            tv_clickable_interest.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
        } else {
            tv_clickable_interest.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_border_black_24dp,0 ,0)
            tv_clickable_interest.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        tv_clickable_interest.isClickable = true
        tv_clickable_interest.isFocusable = true
    }

    private fun setActiveParticipate(boolean: Boolean) {
        if (boolean) {
            tv_clickable_going.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.ic_check_color_primary_36dp,
                0,
                0
            )
            tv_clickable_going.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
        } else {
            tv_clickable_going.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_check_black_36dp,0 ,0)
            tv_clickable_going.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        tv_clickable_going.isClickable = true
        tv_clickable_going.isFocusable = true
    }
}