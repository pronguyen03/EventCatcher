package me.linhthengo.androiddddarchitechture.presentation.event

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import io.grpc.perfmark.PerfMark.event
import kotlinx.android.synthetic.main.fragment_list_events.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.enums.Category
import me.linhthengo.androiddddarchitechture.models.Event
import me.linhthengo.androiddddarchitechture.models.User
import me.linhthengo.androiddddarchitechture.presentation.home.HomeFragment
import me.linhthengo.androiddddarchitechture.utils.FirebaseAuthManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ListEventsFragment : BaseFragment(), OnEventItemClickListener {
    private val eventsAdapter = EventsAdapter(mutableListOf(), this)
    @Inject
    lateinit var firebaseAuthManager: FirebaseAuthManager
    private var firestoreDB: FirebaseFirestore? = null

    private lateinit var sharedPreferences: SharedPreferences
    private var listEvents: MutableList<Event> = mutableListOf()
    override fun layoutId() = R.layout.fragment_list_events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firestoreDB = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_list_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_events?.apply {
            setHasFixedSize(true)
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        getListAllEvent()
    }

    private fun bindListCardEvents(listEvents: MutableList<Event>) {
        val currentUser = this.firebaseAuthManager.getCurrentUser()!!
        val filteredList = mutableListOf<Event>()
        listEvents.forEach { event ->
            for (item in event.listInterest) {
                if (item.uid == currentUser.uid) {
                    event.isInterested = true
                }
            }
            for (item in event.listParticipant) {
                if (item.uid == currentUser.uid) {
                    event.isGoing = true
                }
            }

            if (event.isGoing || event.isInterested) {
                filteredList.add(event)
            }
        }

        (rv_events.adapter as EventsAdapter).updateEvents(filteredList)
    }

    private fun getListAllEvent() {
        firestoreDB!!.collection("event")
            .whereGreaterThanOrEqualTo(
                "endDate", Timestamp(Date())
            ).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val listEvents = mutableListOf<QueryDocumentSnapshot>()
                    task.result!!.forEach { document ->
                        listEvents.add(document)
                    }
                    bindDataEvents(listEvents)
                } else {
                    Timber.tag(HomeFragment.TAG).d(task.exception, "Error getting events ")
                }
            }
    }

    private fun bindDataEvents(querySnapshot: MutableList<QueryDocumentSnapshot>) {
        listEvents.clear()
        for (document in querySnapshot) {
            val event = Event()
            event.id = document.id
            event.name = document.data["name"].toString()
            event.image = document.data["image"].toString()
            event.description = document.data["description"].toString()
            event.locationLat = document.data["locationLat"] as Double
            event.locationLng = document.data["locationLng"] as Double
            event.location = document.data["location"].toString()
            event.locationName = document.data["locationName"].toString()
            event.category = Category.valueOf(document.data["category"].toString().toUpperCase())
            event.startDate = (document.data["startDate"] as Timestamp).toDate()
            event.endDate = (document.data["endDate"] as Timestamp).toDate()
            event.hostId = document.data["hostId"].toString()
            event.hostName = document.data["hostName"].toString()
            val listInterest =
                document.data["listInterest"] as MutableList<HashMap<String, String>>?
            listInterest?.forEach { value ->
                event.listInterest.add(
                    User(
                        value["uid"].toString(),
                        value["name"].toString(),
                        value["email"].toString()
                    )
                )
            }

            val listParticipant =
                document.data["listParticipant"] as MutableList<HashMap<String, String>>?
            listParticipant?.forEach { value ->
                event.listParticipant.add(
                    User(
                        value["uid"].toString(),
                        value["name"].toString(),
                        value["email"].toString()
                    )
                )
            }
            listEvents.add(event)
        }
        listEvents.sortedBy { event -> event.startDate }
        bindListCardEvents(listEvents)
    }

    override fun onItemClick(event: Event, position: Int) {
        val gson = Gson()
        val listEventJson  = gson.toJson(listEvents)
        val editor = sharedPreferences.edit()
        editor.putString(HomeFragment.TAG_LIST_EVENTS, listEventJson)
        editor.apply()
        val bundle = bundleOf("event" to event)
        findNavController().navigate(
            R.id.action_listEventsFragment_to_eventDetailFragment,
            bundle
        )
    }


}