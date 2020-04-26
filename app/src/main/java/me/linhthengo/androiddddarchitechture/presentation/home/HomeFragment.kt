package me.linhthengo.androiddddarchitechture.presentation.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Toast
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import kotlinx.android.synthetic.main.dialog_discover_event.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.extension.appContext
import me.linhthengo.androiddddarchitechture.core.extension.lifeCycleOwner
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.enums.Category
import me.linhthengo.androiddddarchitechture.models.Event
import me.linhthengo.androiddddarchitechture.models.User
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext


class HomeFragment : BaseFragment(), CoroutineScope, OnMapReadyCallback {
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun layoutId(): Int = R.layout.fragment_home

    companion object {
        const val DEFAULT_ZOOM = 15f
    }

    private val TAG = "Home Fragment"
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var predictionList: List<AutocompletePrediction>
    private lateinit var mapView: View
    private val homeViewModel by viewModels<HomeViewModel>(factoryProducer = { viewModelFactory })

    private fun handleAuthState(state: HomeViewModel.State) = handleSignOut(state)
    private val authStateObserver = Observer<HomeViewModel.State> { handleAuthState(it) }
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateCw: Animation
    private lateinit var rotateAcw: Animation
    private var isOpenFab: Boolean = false

    private var startDate = Calendar.getInstance()
    private val listEvents = mutableListOf<Event>();
    private var filterUpcoming = true
    private var filterOngoing = true
    private var filterScope = 40
    private lateinit var searchMarker: Marker
//
    private var firestoreDB: FirebaseFirestore? = null
//    private var eventListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            val dialogEvent = Dialog(requireContext(), R.style.DialogTheme).apply {
                setContentView(R.layout.dialog_discover_event)
                btn_close_dialog_explore.setOnClickListener {
                    filterEvents(filterUpcoming, filterOngoing, filterScope, startDate)
                    dismiss()
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
                val format = "MM/dd/yyyy"
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                edt_start_date.setText(sdf.format(startDate.time))
                val startDateCallback =
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        startDate.set(Calendar.YEAR, year)
                        startDate.set(Calendar.MONTH, month)
                        startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        edt_start_date.setText(sdf.format(startDate.time))
                    }
                edt_start_date.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(
                        requireActivity(),
                        startDateCallback,
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePickerDialog.show()
                }

                switch_upcoming_event.isChecked = filterUpcoming
                switch_upcoming_event.setOnCheckedChangeListener { _, isChecked ->
                    filterUpcoming = isChecked
                }

                switch_ongoing_event.isChecked = filterOngoing
                switch_ongoing_event.setOnCheckedChangeListener { _, isChecked ->
                    filterOngoing = isChecked
                }

                btn_close_dialog.setOnClickListener {
                    dismiss()
                }
            }
            dialogEvent.show()
        }

        nvView.setNavigationItemSelectedListener {
            drawer_layout?.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.log_out -> {
                    homeViewModel.signOut()
                    true
                }
                else -> false
            }
        }
        homeViewModel.state.observe(lifeCycleOwner, authStateObserver)

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
                        latLngOfPlace?.let {
                            searchMarker = mMap.addMarker(MarkerOptions()
                                .position(it)
                                .title(place.name)
                                .icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_location_pin)))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM))

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.clear() // clear old markers

        val parent = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View)
        val locationButton = parent.findViewById<View>(Integer.parseInt("2"))
        val compassButton = parent.findViewById<View>(Integer.parseInt("5"))
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        val rlpCommpass = compassButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.setMargins(0, 215, 0, 0)
        rlpCommpass.setMargins(0, 215, 0, 0)


         mFusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.result != null) {
                val currentLocation = it.result
                val currentLat: Double
                val currentLng: Double
                if (currentLocation != null) {
                    currentLat = currentLocation.latitude
                    currentLng = currentLocation.longitude
                } else {
                    currentLat = 16.138200
                    currentLng = 108.120029
                }
                val googlePlex = CameraPosition.builder()
                    .target(LatLng(currentLat, currentLng))
                    .zoom(15f)
                    .bearing(0f)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 500, null)
            }
        }



        mMap.setOnInfoWindowClickListener { marker ->
            marker?.run {
                if (this.tag is Event) {
                    val event = this.tag as Event
                    val bundle = bundleOf("event" to event)
                    findNavController().navigate(R.id.action_homeFragment_to_eventDetailFragment, bundle)
                }
            }
        }

        getListAllEvent()
    }

    private fun getListAllEvent() {
        firestoreDB!!.collection("event").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listEvents.clear()
                for (document in task.result!!) {
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
                    event.startDate = (document.data["startDate"] as Timestamp).seconds
                    event.endDate = (document.data["endDate"] as Timestamp).seconds
                    event.hostId = document.data["hostId"].toString()
                    event.hostName = document.data["hostName"].toString()
                    val listInterest = document.data["listInterest"] as MutableList<HashMap<String, String>>?
                    listInterest?.forEach { value ->
                        event.listInterest.add(User(
                            value["uid"].toString(),
                            value["name"].toString(),
                            value["email"].toString()
                        ))
                    }

                    val listParticipant = document.data["listParticipant"] as MutableList<HashMap<String, String>>?
                    listParticipant?.forEach { value ->
                        event.listParticipant.add(User(
                            value["uid"].toString(),
                            value["name"].toString(),
                            value["email"].toString()
                        ))
                    }


                    listEvents.add(event)
                }
                displayEventsToMap()
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
        if (event.category == Category.MUSIC) {
            markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.ic_concert
                )
            )
        } else {
            markerOptions.icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    R.drawable.ic_location_pin
                )
            )
        }
        val marker = mMap.addMarker(markerOptions)
        marker.tag = event
    }

    fun onInfoWindowClick(marker: Marker?) {
//        if (marker?.tag !is null)
//        {
//            if (marker.getTag() instanceof  Report) {
//                Report report = (Report) marker.getTag();
//                boolean isVoted = false;
//                tvTitleInfoReport.setText(report.getTitle());
//                tvDescriptionInfoReport.setText(report.getContent());
//                StringBuilder builder = new StringBuilder();
//                builder.append(report.getRemainingTime()/3600);
//                builder.append(" ");
//                builder.append(getString(R.string.mins));
//                tvRemainingTimeInfoReport.setText(builder.toString());
//                tvReporterNameInfoReport.setText(report.getReporter().getUsername());
//                tvUpVoteInfoReport.setText(String.valueOf(report.getUpVote()));
//                tvDownVoteInfoReport.setText(String.valueOf(report.getDownVote()));
//                tvPostedDateInfoReport.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(report.getPostDate()));
//                ivDownVoteInfoReport.setOnClickListener(l -> {
//                    int currentUpVote = report.getUpVote();
//                    int currentDownVote = report.getDownVote();
//                    if (ivDownVoteInfoReport.isEnabled()) {
//                        if (currentUpVote > 0) {
//                            report.setUpVote(--currentUpVote);
//                            tvUpVoteInfoReport.setText(String.valueOf(currentUpVote));
//                        }
//                        report.setDownVote(++currentDownVote);
//                        tvDownVoteInfoReport.setText(String.valueOf(currentDownVote));
//                        ivDownVoteInfoReport.setEnabled(false);
//                        ivUpVoteInfoReport.setEnabled(true);
//                        Map<String, Object> vote = new HashMap<>();
//                        vote.put("downVote", currentDownVote);
//                        vote.put("upVote", currentUpVote);
//                        databaseReference.child("reports")
//                            .child(report.getId())
//                            .updateChildren(vote)
//                            .addOnCompleteListener(task -> {
//                            showToastMessage("Vote down successfully!");
//                        });
//                    }
//                });
//                ivUpVoteInfoReport.setOnClickListener(l -> {
//                    int currentUpVote = report.getUpVote();
//                    int currentDownVote = report.getDownVote();
//                    if (ivUpVoteInfoReport.isEnabled()) {
//                        if (currentDownVote > 0) {
//                            report.setDownVote(--currentDownVote);
//                            tvDownVoteInfoReport.setText(String.valueOf(currentDownVote));
//                        }
//                        report.setUpVote(++currentUpVote);
//                        tvUpVoteInfoReport.setText(String.valueOf(currentUpVote));
//                        ivUpVoteInfoReport.setEnabled(false);
//                        ivDownVoteInfoReport.setEnabled(true);
//                        Map<String, Object> vote = new HashMap<>();
//                        vote.put("downVote", currentDownVote);
//                        vote.put("upVote", currentUpVote);
//                        databaseReference.child("reports")
//                            .child(report.getId())
//                            .updateChildren(vote)
//                            .addOnCompleteListener(task -> {
//                            showToastMessage("Vote up successfully!");
//                        });
//                    }
//                });
//                Log.e("size of report image: ",report.getImageName().size()+"");
//                downloadImageAdapter.setImageUrl(report.getImageName());
//                downloadImageAdapter.notifyDataSetChanged();
//                dialogInfoReport.show();
//            }
//        }
    }

    private fun filterEvents(isUpcoming: Boolean, isOngoing: Boolean, scope: Int, startDate: Calendar) {
//        val currentLocation = mFusedLocationProviderClient.lastLocation.result
//        listEvents = homeViewModel.getEvent(isUpcoming, isOngoing, currentLocation, scope, startDate)
//        displayEventsToMap()
    }

}



