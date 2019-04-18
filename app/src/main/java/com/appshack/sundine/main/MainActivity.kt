package com.appshack.sundine.main

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.PermissionChecker
import android.util.Log
import com.appshack.sundine.PermissionHandler
import com.appshack.sundine.R
import com.appshack.sundine.enums.PermissionCodes
import com.appshack.sundine.extensions.debugTrace
import com.appshack.sundine.extensions.toast
import com.appshack.sundine.interfaces.MapInterface
import com.appshack.sundine.interfaces.SunCanvasMVP
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import com.appshack.sundine.suncanvas.SunCanvasPresenter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), MapInterface, SunCanvasMVP.View {

    /**
     *  gMap: The visible map you see
     *  isResumed: Used to set location only if its the first location since resumed activity
     *  fusedLocationProviderClient: Google version of locationClient. Used to fetch location data
     *  googleApiClient: Handles api connections, most importantly connect and disconnect
     *  LocationRequest: Callback function passed when fetching data with fusedLocationProviderClient
     */
    private lateinit var gMap: GoogleMap
    private var isResumed: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: LocationRequest
    private val sunCanvasPresenter = SunCanvasPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mMapFragment = fragmentManager.findFragmentById(R.id.google_map_fragment) as MapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this)

        locationCallback = locationResultCallback

        mGoogleApiClient = mClient

        mLocationRequest = mIntervalFast

        /**
         * Call getMapAsync to get a google map.
         * This call will return a GoogleMap in onMapReady, which we implement from OnMapReadyCallback
         */
        mMapFragment.getMapAsync(this)

    }

    override fun onResume() {
        super.onResume()
        isResumed = true
        mGoogleApiClient.connect()
    }

    override fun onPause() {
        super.onPause()

        if (mGoogleApiClient.isConnected) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            mGoogleApiClient.disconnect()
        }
    }

    private fun getNearbyPlaces() {

        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PermissionChecker.PERMISSION_GRANTED) {
            val placeResults = mPlaceDetectionClient.getCurrentPlace(null)
            placeResults.addOnCompleteListener(placeResultCompleteListener)
        }
    }

    private fun addMarkers(places: List<PlaceLikelihood>) {
        places.forEach {
            gMap.addMarker(MarkerOptions()
                    .title(it.place.name.toString())
                    .position(it.place.latLng))
        }
    }

    private fun filterFoodPlaces(likelyPlaces: PlaceLikelihoodBufferResponse?): List<PlaceLikelihood> {
        return likelyPlaces?.filter { placeLikelihood ->
            placeLikelihood.place.placeTypes.any {
                it == Place.TYPE_RESTAURANT ||
                        it == Place.TYPE_CAFE ||
                        it == Place.TYPE_BAR
            }
        } ?: listOf()
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        val permissionHandler = PermissionHandler(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                googleMap?.let { googleMap ->
                    gMap = googleMap
                    gMap.isMyLocationEnabled = true
                    gMap.setOnCameraIdleListener {
                        updateCanvas(sunCanvasPresenter.getSunPaths(gMap.cameraPosition))
                    }
                }
            }
        }

        permissionHandler.checkPermission()

    }

    private fun updateCanvas(sunPathDataModels: LinkedHashMap<String, SunPathDataModel>) {

        runOnUiThread {
            //sunPathDataModels.forEach { setupCanvasArcData(it) }
            sun_canvas_map_view.updateCanvas(sunPathDataModels)
            sun_canvas_map_view.invalidate()
        }
    }

    override fun onLocationChanged(p0: Location?) {
        handleNewLocation(p0)
    }

    /**
     * If location exists and it's the first location since last resume we get the location and
     * animates the camera to goto that location and zoom in to zoom level 14
     */
    private fun handleNewLocation(location: Location?) {

        location?.let {
            if (isResumed) {
                val latLng = LatLng(it.latitude, it.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 6f)
                getNearbyPlaces()
                gMap.animateCamera(cameraUpdate)
                isResumed = false
            }

        }
    }

    override fun onConnected(p0: Bundle?) {
        PermissionHandler(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION)) {

            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {

                val mLastLocation = fusedLocationClient.lastLocation
                mLastLocation.addOnCompleteListener { task -> handleNewLocation(task.result) }

                fusedLocationClient.requestLocationUpdates(mLocationRequest, newLocationCallback, null)

            }
        }.checkPermission()

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (p0.hasResolution()) {
            p0.startResolutionForResult(this, PermissionCodes.CONNECTION_FAILURE_RESOLUTION_REQUEST.id)
        } else {
            Log.i(debugTrace(), "Location services connection failed with code ${p0.errorMessage}")
        }

    }

    override fun toastMessage(message: String) {
        toast(message)
    }

    private val newLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            p0?.let {
                handleNewLocation(it.lastLocation)
            }
        }

    }

    private val placeResultCompleteListener = OnCompleteListener<PlaceLikelihoodBufferResponse> {

        val likelyPlaces: PlaceLikelihoodBufferResponse? = it.result
        val foodPlaces = filterFoodPlaces(likelyPlaces)

        addMarkers(foodPlaces)
        //foodPlaces.forEach { Log.i(debugTrace(), "[Place/Likelihood/Types]:   ${it.place.name} : ${it.likelihood} : ${it.place.placeTypes}\n") }
        //likelyPlaces.forEach { Log.i(debugTrace(), "[Place/Likelihood/Types]:   ${it.place.name} : ${it.likelihood} : ${it.place.placeTypes}") }
        likelyPlaces?.release()
    }

    private val locationResultCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            Log.i(debugTrace(), "LocationCallbackResult: $p0")
        }
    }

    private var mClient: GoogleApiClient
        get() = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        set(_) = Unit

    private val mIntervalFast = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
            .setFastestInterval(1 * 1000)  // 1 second, in milliseconds

}