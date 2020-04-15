package me.linhthengo.androiddddarchitechture.presentation.home

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.core.extension.appContext
import me.linhthengo.androiddddarchitechture.core.extension.lifeCycleOwner
import me.linhthengo.androiddddarchitechture.core.platform.BaseFragment
import me.linhthengo.androiddddarchitechture.presentation.home.HomeViewModel.Companion.GPS_REQUEST_CODE
import me.linhthengo.androiddddarchitechture.presentation.home.HomeViewModel.Companion.REQUEST_LOCATION_CODE
import timber.log.Timber
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class HomeFragment : BaseFragment(), CoroutineScope, OnMapReadyCallback {
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun layoutId(): Int = R.id.homeFragment
    companion object {
        const val DEFAULT_ZOOM = 15f
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var predictionList: List<AutocompletePrediction>
    private lateinit var mapView: View
    private val homeViewModel by viewModels<HomeViewModel>(factoryProducer = { viewModelFactory })

    private fun handleAuthState(state: HomeViewModel.State) = handleSignOut(state)
    private val authStateObserver = Observer<HomeViewModel.State> { handleAuthState(it) }
    lateinit var fab_open: Animation
    lateinit var fab_close: Animation
    lateinit var rotate_cw: Animation
    lateinit var rotate_acw: Animation
    var isOpenFab: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() { // Handle the back button event
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("Exit Application")
                            .setMessage("Are you sure you want to exit the app?")
                            .setPositiveButton(
                                "Yes",
                                DialogInterface.OnClickListener { dialog, which ->
                                    activity?.finish()
                                })
                            .setNegativeButton("No", null)
                            .show()
                    };
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // Animation Floating Button
        fab_open = AnimationUtils.loadAnimation(appContext, R.anim.open_fab)
        fab_close = AnimationUtils.loadAnimation(appContext, R.anim.close_fab)
        rotate_cw = AnimationUtils.loadAnimation(appContext, R.anim.rotate_clockwise)
        rotate_acw = AnimationUtils.loadAnimation(appContext, R.anim.rotate_anticlockwise)

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
        val rootView: View = inflater.inflate(R.layout.fragment_home, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment!!.getMapAsync(this)
        mapFragment.view?.run{
            mapView = this
        }
//        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
//        val actionBar = (activity as AppCompatActivity).supportActionBar
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_main.setOnClickListener {
            if (isOpenFab) {
                fab_event.startAnimation(fab_close)
                fab.startAnimation(fab_close)
                fab_main.startAnimation(rotate_acw)
                fab_event.visibility = View.GONE
                fab.visibility = View.GONE
                fab_main.setImageResource(R.drawable.ic_add_white_24dp)
                isOpenFab = false
            } else {
                fab_event.startAnimation(fab_open)
                fab.startAnimation(fab_open)
                fab_main.startAnimation(rotate_cw)
                fab_event.visibility = View.VISIBLE
                fab.visibility = View.VISIBLE
                fab_main.setImageResource(R.drawable.ic_close_white_24dp)
                isOpenFab = true
            }
        }

        fab_event.setOnClickListener {
            Toast.makeText(appContext, "Show event Dialog", Toast.LENGTH_LONG).show()
        }

        fab.setOnClickListener {
            Toast.makeText(appContext, "Show Another Dialog", Toast.LENGTH_LONG).show()
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
                    searchBar.disableSearch();
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

                    placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val predictionsResponse = it.getResult()
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
                            Timber.tag("mytag").i("prediction fetching task unsuccessful")
                        }
                    }
                }

            }

        })

        searchBar.setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemDeleteListener(position: Int, v: View?) {

            }

            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= predictionList.size) {
                    return
                }
                val selectedPrediction = predictionList.get(position)
                val suggestion = searchBar.lastSuggestions.get(position).toString()
                searchBar.text = suggestion


                Handler().postDelayed( {
                    searchBar.clearSuggestions()
                }, 1000)

                searchBar.clearSuggestions()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.run {
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

    private fun checkLocationPermission() {
        context?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    AlertDialog.Builder(it)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_LOCATION_CODE
                            )
                        })
                        .create()
                        .show()

                } else ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (providerEnabled) {
            return true;
        } else {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("GPS Permission")
                .setMessage("GPS is required for this app to work. Please enable GPS.")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, GPS_REQUEST_CODE)
                })
                .setCancelable(true)
                .show()
        }
        return false;
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.clear() // clear old markers

        val parent = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View)
        val locationButton= parent.findViewById<View>(Integer.parseInt("2"))
        val compassButton= parent.findViewById<View>(Integer.parseInt("5"))
        val rlp= locationButton.layoutParams as (RelativeLayout.LayoutParams)
        val rlpCommpass = compassButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.setMargins(0,215,0, 0);
        rlpCommpass.setMargins(0,215,0,0)
        val googlePlex = CameraPosition.builder()
            .target(LatLng(16.138200, 108.120029))
            .zoom(15f)
            .bearing(0f)
            .tilt(45f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 500, null)
//            mMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng(16.138200, 108.120029))
//                    .icon(
//                        bitmapDescriptorFromVector(
//                            requireContext(),
//                            R.drawable.ic_event_available_black_24dp
//                        )
//                    )
//            )
    }

}



