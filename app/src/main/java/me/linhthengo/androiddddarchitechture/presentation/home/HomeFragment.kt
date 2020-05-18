package me.linhthengo.androiddddarchitechture.presentation.home

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import kotlinx.android.synthetic.main.dialog_discover_event.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.extension.appContext
import me.linhthengo.androiddddarchitechture.core.extension.lifeCycleOwner
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.enums.Category
import me.linhthengo.androiddddarchitechture.models.Event
import me.linhthengo.androiddddarchitechture.models.GoogleMapDTO
import me.linhthengo.androiddddarchitechture.models.User
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class HomeFragment : BaseFragment(), CoroutineScope, OnMapReadyCallback {
    private lateinit var sharedPreferences: SharedPreferences
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun layoutId(): Int = R.layout.fragment_home

    companion object {
        const val DEFAULT_ZOOM = 15f
        const val DATE_FORMAT = "MM/dd/yyyy"
        const val TAG = "Home Fragment"
        const val EARTH_RADIUS = 6378137
        const val TAG_LIST_EVENTS = "LIST_EVENTS"
        const val TAG_LAT = "CURRENT_LAT"
        const val TAG_LNG = "CURRENT_LNG"
        const val DIRECTION_EVENT = "DIRECTION_EVENT"
        const val CURRENT_EVENT_PLACE = "CURRENT_EVENT_PLACE"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var predictionList: List<AutocompletePrediction>
    private lateinit var mapView: View
    private val homeViewModel by viewModels<HomeViewModel>(factoryProducer = { viewModelFactory })
    private val profileViewModel by viewModels<ProfileViewModel>(factoryProducer = { viewModelFactory })
    private val tutorialViewModel by viewModels<TutorialViewModel>(factoryProducer = { viewModelFactory })
    private val yourEventViewModel by viewModels<YourEventViewModel>(factoryProducer = { viewModelFactory })

    private fun handleAuthState(state: HomeViewModel.State) = handleSignOut(state)
    private fun handleViewProfile(state: ProfileViewModel.State) = handleProfile(state)
    private fun handleViewTutorial(state: TutorialViewModel.State) = handleTutorial(state)
    private fun handleViewYourEvent(state: YourEventViewModel.State) = handleYourEvent(state)

    private val authStateObserver = Observer<HomeViewModel.State> { handleAuthState(it) }
    private val viewProfileObserver = Observer<ProfileViewModel.State> { handleViewProfile(it) }
    private val viewTutorialObserver = Observer<TutorialViewModel.State> { handleViewTutorial(it) }
    private val viewYourEventObserver = Observer<YourEventViewModel.State> { handleViewYourEvent(it) }

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateCw: Animation
    private lateinit var rotateAcw: Animation
    private var isOpenFab: Boolean = false

    private var startDate = Calendar.getInstance()
    private var listEvents: MutableList<Event> = mutableListOf<Event>()
    private var filterOngoing = true
    private var filterUpcoming = false
    private var filterCategory = "All"
    private var filterScope = 40
    private lateinit var searchMarker: Marker

    //
    private var firestoreDB: FirebaseFirestore? = null
//    private var eventListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        firestoreDB = FirebaseFirestore.getInstance()
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() { // Handle the back button event
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("Exit Application")
                            .setMessage("Are you sure you want to exit the app?")
                            .setPositiveButton(
                                "Yes"
                            ) { _, _ ->
                                activity?.finish()
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // Animation Floating Button
        fabOpen = AnimationUtils.loadAnimation(appContext, R.anim.open_fab)
        fabClose = AnimationUtils.loadAnimation(appContext, R.anim.close_fab)
        rotateCw = AnimationUtils.loadAnimation(appContext, R.anim.rotate_clockwise)
        rotateAcw = AnimationUtils.loadAnimation(appContext, R.anim.rotate_anticlockwise)

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireActivity(), getString(R.string.google_maps_key))

        context?.let {
            placesClient = Places.createClient(it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment!!.getMapAsync(this)
        mapFragment.view?.run {
            mapView = this
        }
        fab_main.setOnClickListener {
            if (isOpenFab) {
                fab_event.startAnimation(fabClose)
                fab_main.startAnimation(rotateAcw)
                fab_event.visibility = View.GONE
                isOpenFab = false
            } else {
                fab_event.startAnimation(fabOpen)
                fab_main.startAnimation(rotateCw)
                fab_event.visibility = View.VISIBLE
                isOpenFab = true
            }
        }

        fab_event.setOnClickListener {
            val dialogEvent = getExploreDialog()
            dialogEvent.show()
        }

        nvView.setNavigationItemSelectedListener {
            drawer_layout?.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.log_out -> {
                    homeViewModel.signOut()
                    true
                }
                R.id.profile -> {
                    Log.d("HomeFragment", "vao profile")
                    profileViewModel.profile()
                    true
                }
                R.id.tutorial -> {
                    tutorialViewModel.tutorial()
                    true
                }
                R.id.discover_event -> {
                    yourEventViewModel.yourEvent()
                    true
                }
                else -> false
            }
        }
        homeViewModel.state.observe(lifeCycleOwner, authStateObserver)
        profileViewModel.state.observe(lifeCycleOwner, viewProfileObserver)
        tutorialViewModel.state.observe(lifeCycleOwner, viewTutorialObserver)
        yourEventViewModel.state.observe(lifeCycleOwner, viewYourEventObserver)

        val token = AutocompleteSessionToken.newInstance()
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    drawer_layout.openDrawer(GravityCompat.START)
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    searchBar.disableSearch()
                    searchBar.clearSuggestions()
                    searchBar.hideSuggestionsList()
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {
            }

            override fun onSearchConfirmed(text: CharSequence?) {
//                startSearch(text.toString(), true, null, true)
            }
        })

        searchBar.addTextChangeListener(object : TextWatcher {
            private var searchFor = ""
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor) {
                    return
                }
                searchFor = searchText

                launch {
                    delay(1000)
                    if (searchText != searchFor) {
                        return@launch
                    }
                    val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("VN")
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()

                    placesClient.findAutocompletePredictions(predictionsRequest)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val predictionsResponse = it.result
                                predictionsResponse?.run {
                                    predictionList = predictionsResponse.autocompletePredictions
                                    val suggestionsList = ArrayList<String>()
                                    predictionList.forEach { prediction ->
                                        suggestionsList.add(prediction.getFullText(null).toString())
                                    }
                                    searchBar.updateLastSuggestions(suggestionsList)
                                    if (!searchBar.isSuggestionsVisible) {
                                        searchBar.showSuggestionsList()
                                    }
                                }
                            } else {
                                Timber.tag(TAG).i("prediction fetching task unsuccessful")
                            }
                        }
                }

            }

        })

        searchBar.setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemDeleteListener(position: Int, v: View?) {
                searchBar.lastSuggestions.removeAt(position)
                searchBar.updateLastSuggestions(searchBar.lastSuggestions)
            }

            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= predictionList.size) {
                    return
                }
                val selectedPrediction = predictionList[position]
                val suggestion = searchBar.lastSuggestions[position].toString()
                searchBar.text = suggestion


                Handler().postDelayed({
                    searchBar.clearSuggestions()
                }, 1000)

                searchBar.clearSuggestions()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.run {
                    hideSoftInputFromWindow(
                        searchBar.windowToken,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                    )
                }

                val placeId = selectedPrediction.placeId
                val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG)
                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener {
                        val place = it.place
                        Timber.tag("mytag").i("Place found ${place.name}")
                        val latLngOfPlace = place.latLng
                        latLngOfPlace?.let { locationPlace ->
                            searchMarker = mMap.addMarker(
                                MarkerOptions()
                                    .position(locationPlace)
                                    .title(place.name)
                                    .icon(
                                        bitmapDescriptorFromVector(
                                            requireActivity(),
                                            R.drawable.ic_location_pin
                                        )
                                    )
                            )
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    locationPlace,
                                    DEFAULT_ZOOM
                                )
                            )
                        }
                    }
                    .addOnFailureListener {
                        if (it is ApiException) {
                            val apiException = it
                            apiException.printStackTrace()
                            val statusCode = apiException.statusCode
                            Timber.tag("mytag").i("Place not found ${it.message}")
                            Timber.tag("mytag").i("Status Code: $statusCode")
                        }
                    }
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.state.removeObserver(authStateObserver)
        profileViewModel.state.removeObserver(viewProfileObserver)
        tutorialViewModel.state.removeObserver(viewTutorialObserver)
        yourEventViewModel.state.removeObserver(viewYourEventObserver)
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int
    ): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun handleSignOut(state: HomeViewModel.State) {
        when (state) {
            is HomeViewModel.State.Failure -> {
                showErrorDialog(state.message) {
                    Toast.makeText(context, "Sign out failed", Toast.LENGTH_SHORT).show()
                }
            }
            is HomeViewModel.State.Success -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSignInFragment())
            }
        }
    }

    private fun handleProfile(state: ProfileViewModel.State) {
        when (state) {
            is ProfileViewModel.State.Failure -> {
                showErrorDialog(state.message) {
                    Toast.makeText(context, " failed", Toast.LENGTH_SHORT).show()
                }
            }
            is ProfileViewModel.State.Success -> {
                Log.println(Log.DEBUG, "HomeFragment", "success handle profile")
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            }
        }
    }

    private fun handleTutorial(state: TutorialViewModel.State) {
        when (state) {
            is TutorialViewModel.State.Failure -> {
                showErrorDialog(state.message) {
                    Toast.makeText(context, " failed", Toast.LENGTH_SHORT).show()
                }
            }
            is TutorialViewModel.State.Success -> {
//                Log.println(Log.DEBUG, "HomeFragment", "success handle profile")
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTutorialFragment())
            }
        }
    }

    private fun handleYourEvent(state: YourEventViewModel.State) {
        when (state) {
            is YourEventViewModel.State.Failure -> {
                showErrorDialog(state.message) {
                    Toast.makeText(context, " failed", Toast.LENGTH_SHORT).show()
                }
            }
            is YourEventViewModel.State.Success -> {
//                Log.println(Log.DEBUG, "HomeFragment", "success handle profile")
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToYourEventFragment())
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.clear() // clear old markers

        val parent = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View)

        val locationButton = parent.findViewById<View>(Integer.parseInt("2"))
        val compassButton = parent.findViewById<View>(Integer.parseInt("5"))
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        val rlpCommpass = compassButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.setMargins(0, 215, 0, 0)
        rlpCommpass.setMargins(0, 215, 0, 0)

        moveToDeviceLocation()

        mMap.setOnInfoWindowClickListener { marker ->
            marker?.run {
                if (this.tag is Event) {
                    val gson = Gson()
                    val listEventJson  = gson.toJson(listEvents)
                    val editor = sharedPreferences.edit()
                    editor.putString(TAG_LIST_EVENTS, listEventJson)
                    editor.apply()
                    val event = this.tag as Event
                    val bundle = bundleOf("event" to event)
                    findNavController().navigate(
                        R.id.action_homeFragment_to_eventDetailFragment,
                        bundle
                    )
                }
            }
        }

//        val listEventsJson = sharedPreferences.getString(TAG_LIST_EVENTS, "")
//        if (listEventsJson != null && listEventsJson != "") {
//            val type = object : TypeToken<MutableList<Event>>() {}.type
//            listEvents = Gson().fromJson(listEventsJson, type)
//            if (listEvents.size > 0) {
//                displayEventsToMap()
//            } else {
//                filterEvents(filterUpcoming, filterOngoing, filterCategory, filterScope, startDate)
//            }
//        } else {
        filterEvents(filterUpcoming, filterOngoing, filterCategory, filterScope, startDate)
//        }

        val args = arguments
        val eventDirection: Event? = args?.getParcelable("EVENT_DIRECTION")
        eventDirection?.run {
            Timber.tag("DIRECTION").i("Run here...")
            makeDirectionToEventPlace(this)
        }
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
                    Timber.tag(TAG).d(task.exception, "Error getting events ")
                }
            }
    }

    private fun displayEventsToMap() {
        mMap.clear()
        for (event in listEvents) {
            displayEventMarker(event)
        }
    }

    fun displayEventMarker(event: Event) {
        val markerOptions = MarkerOptions()
        //Set attribute for marker;
        markerOptions.title(event.name)
        markerOptions.snippet(event.description)
        val position = LatLng(event.locationLat, event.locationLng)
        markerOptions.position(position)

        when (event.category) {
            Category.ART -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_art
                )
            )
            Category.DRINKS -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_drink
                )
            )
            Category.FILM -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_film
                )
            )
            Category.FITNESS -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_fitness
                )
            )
            Category.FOOD -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_food
                )
            )
            Category.GAMES -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_game
                )
            )
            Category.HEALTH -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_health
                )
            )
            Category.HOME -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_home
                )
            )
            Category.MUSIC -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_music
                )
            )
            Category.SHOPPING -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_shopping
                )
            )
            Category.SPORTS -> markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.marker_sport
                )
            )
            else -> {
                markerOptions.icon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.ic_event_available_black_24dp
                    )
                )
            }
        }
        val marker = mMap.addMarker(markerOptions)
        marker.tag = event
    }

    private fun filterEvents(
        isUpcoming: Boolean,
        isOngoing: Boolean,
        category: String,
        scope: Int,
        startDate: Calendar
    ) {
        val currentLat = sharedPreferences.getString(TAG_LAT, "0")!!.toDouble()
        val currentLng = sharedPreferences.getString(TAG_LNG, "0")!!.toDouble()
        val currentDateTimestamp = Timestamp(Date())
        val queryStartDate: Timestamp
        val collection = firestoreDB!!.collection("event")

        if (isOngoing) {
            collection.whereGreaterThanOrEqualTo(
                "endDate", currentDateTimestamp
            ).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val listEvents = mutableListOf<QueryDocumentSnapshot>()
                    task.result!!.forEach { document ->
                        val eventStartDate = document.data["startDate"] as Timestamp
                        val locationLat = document.data["locationLat"] as Double
                        val locationLng = document.data["locationLng"] as Double
                        val categoryData = document.data["category"].toString()
                        if (eventStartDate < currentDateTimestamp &&
                            (getDistanceBetweenPoints(locationLat, locationLng, currentLat, currentLng) <= scope ||
                                    getDistanceBetweenPoints(currentLat, currentLng, locationLat, locationLng) <= scope) &&
                            (category === "All" || categoryData === category)
                        ) {
                            listEvents.add(document)
                        }
                    }

                    bindDataEvents(listEvents)
                } else {
                    Timber.tag(TAG).d(task.exception, "Error getting events ")
                }

            }
        }

        if (isUpcoming) {
            val startDateTimestamp = Timestamp(startDate.time)
            queryStartDate = if (startDateTimestamp >= currentDateTimestamp) {
                startDateTimestamp
            } else {
                startDateTimestamp
            }
            collection.whereGreaterThanOrEqualTo("startDate", queryStartDate).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val listEvents = mutableListOf<QueryDocumentSnapshot>()
                        task.result!!.forEach { document ->
                            val locationLat = document.data["locationLat"] as Double
                            val locationLng = document.data["locationLng"] as Double
                            val categoryData = document.data["category"].toString()
                            if ((getDistanceBetweenPoints(locationLat, locationLng, currentLat, currentLng) <= scope ||
                                getDistanceBetweenPoints(currentLat, currentLng, locationLat, locationLng) <= scope) &&
                                (category === "All" || categoryData === category)
                            ) {
                                listEvents.add(document)
                            }
                        }
                        bindDataEvents(listEvents)
                    } else {
                        Timber.tag(TAG).d(task.exception, "Error getting events ")
                    }

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
        displayEventsToMap()
    }

    private fun moveToDeviceLocation() {
        try {
            mFusedLocationProviderClient.lastLocation
                .addOnCompleteListener { task: Task<Location?> ->
                    if (task.isSuccessful && task.result != null) {
                        val currentLocation = task.result
                        saveLocation(currentLocation!!)
                        val googlePlex = CameraPosition.builder()
                            .target(LatLng(currentLocation.latitude, currentLocation.longitude))
                            .zoom(DEFAULT_ZOOM)
                            .bearing(0f)
                            .build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(googlePlex),
                            500,
                            null
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Unable to get current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } catch (e: SecurityException) {
            Timber.tag("GetDeviceLocation").e("Sercurity Exeption: %s", e.message)
        }
    }

    private fun saveLocation(currentLocation: Location) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(TAG_LAT, currentLocation.latitude.toString())
        Timber.i("moveToDeviceLocation: %s", currentLocation.latitude)
        editor.putString(TAG_LNG, currentLocation.longitude.toString())
        editor.apply()
    }

    private fun getExploreDialog(): Dialog {
        return Dialog(requireContext(), R.style.DialogTheme).apply {
            setContentView(R.layout.dialog_discover_event)
            switch_ongoing_event.isChecked = filterOngoing
            switch_upcoming_event.isChecked = filterUpcoming

            switch_ongoing_event.setOnCheckedChangeListener { _, isChecked ->
                if (filterUpcoming && isChecked) {
                    filterUpcoming = false
                    switch_upcoming_event.isChecked = filterUpcoming
                    edt_start_date.setText("")
                }
                filterOngoing = isChecked
            }

            switch_upcoming_event.setOnCheckedChangeListener { _, isChecked ->
                if (filterOngoing && isChecked) {
                    filterOngoing = false
                    switch_ongoing_event.isChecked = filterOngoing
                    val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                    edt_start_date.setText(sdf.format(Date().time))
                }
                filterUpcoming = isChecked
            }

            btn_explore_dialog.setOnClickListener {
                filterEvents(filterUpcoming, filterOngoing, filterCategory, filterScope, startDate)
                dismiss()
            }

            val listCategory = arrayOf(
                "All",
                "Art",
                "Causes",
                "Comedy",
                "Crafts",
                "Dance",
                "Drinks",
                "Film",
                "Fitness",
                "Food",
                "Games",
                "Gardening",
                "Health",
                "Home",
                "Literature",
                "Music",
                "Networking",
                "Party",
                "Religion",
                "Shopping",
                "Sports",
                "Theater",
                "Wellness",
                "Other"
            )

            spinner_choose_event.adapter = ArrayAdapter(requireContext(),R.layout.support_simple_spinner_dropdown_item, listCategory)

            spinner_choose_event.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    filterCategory = listCategory[0]
                    spinner_choose_event.setSelection(0)
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    filterCategory = listCategory[position]
                }


            }

            // Scope Bar
            sb_scope.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) =
                    if (progress >= 1) {
                        filterScope = progress
                        tv_scope_value.text = "$progress kms"
                    } else {
                        filterScope = progress
                        tv_scope_value.text = "$progress km"
                    }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            sb_scope.progress = filterScope

            // Start Date
//            val format = "MM/dd/yyyy"
//            val sdf = SimpleDateFormat(format, Locale.getDefault())
//            edt_start_date.setText(sdf.format(startDate.time))
//            val startDateCallback =
//                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//                    startDate.set(Calendar.YEAR, year)
//                    startDate.set(Calendar.MONTH, month)
//                    startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                    edt_start_date.setText(sdf.format(startDate.time))
//                }
//            edt_start_date.setOnClickListener {
//                val datePickerDialog = DatePickerDialog(
//                    requireActivity(),
//                    startDateCallback,
//                    startDate.get(Calendar.YEAR),
//                    startDate.get(Calendar.MONTH),
//                    startDate.get(Calendar.DAY_OF_MONTH)
//                )
//                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
//                datePickerDialog.show()
//            }

            btn_close_dialog.setOnClickListener {
                dismiss()
            }

            btn_explore_all.setOnClickListener {
                getListAllEvent()
                dismiss()
            }

        }
    }

    /**
     * Converts degrees to radians.
     *
     * @param degrees Number of degrees.
     */
    private fun degreesToRadians(degrees: Double): Double {
        return degrees * Math.PI / 180
    }

    private fun getDistanceBetweenPoints(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {
        // The radius of the planet earth in meters
        val dLat = degreesToRadians(lat2 - lat1)
        val dLong = degreesToRadians(lng2 - lng1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(degreesToRadians(lat1)) * cos(degreesToRadians(lat1)) * sin(dLong / 2) * sin(
            dLong / 2
        )

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS * c / 1000
    }

    private fun makeDirectionToEventPlace(event: Event) {
        val currentLat = sharedPreferences.getString(TAG_LAT, "0")!!.toDouble()
        val currentLng = sharedPreferences.getString(TAG_LNG, "0")!!.toDouble()
        val origin = LatLng(currentLat, currentLng)
        val destination = LatLng(event.locationLat, event.locationLng)
        val url = getDirectionURL(origin, destination, "driving")
        GetDirection(url).execute()

    }

    private fun getDirectionURL(origin: LatLng, destination: LatLng, directionMode: String): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${destination.latitude},${destination.longitude}";
        val mode = "mode=${directionMode}"
        val parameters = "${strOrigin}&${strDest}&${mode}"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/${output}?${parameters}&key=${getString(R.string.google_maps_key)}"
    }

    inner class GetDirection(private val url: String): AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall((request)).execute()
            val data = response.body?.string()
            val result = ArrayList<List<LatLng>>()
            try {
                Timber.tag("DIRECTION").i(data)
                val resObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()

                for (i in 0 until resObj.routes[0].legs[0].steps.size) {
//                    val startLatLng = LatLng(resObj.routes[0].legs[0].steps[i].start_location.lat.toDouble(),
//                        resObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(resObj.routes[0].legs[0].steps[i].end_location.lat.toDouble(),
//                        resObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyLine(resObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineOption = PolylineOptions()
            for (i in result.indices) {
                lineOption.addAll(result[i])
                lineOption.width(10f)
                lineOption.color(Color.BLUE)
                lineOption.geodesic(true)
            }
            mMap.addPolyline(lineOption)
        }

    }

    private fun decodePolyLine(poly: String): List<LatLng> {
        val len = poly.length
        var index = 0
        val decoded = ArrayList<LatLng>()
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            decoded.add(
                LatLng(
                    lat / 100000.0, lng / 100000.0
                )
            )
        }
        return decoded
    }

}



