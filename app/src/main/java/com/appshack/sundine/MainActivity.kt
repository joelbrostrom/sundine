package com.appshack.sundine

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.PermissionChecker
import android.util.Log
import com.appshack.sundine.enums.PermissionCodes
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class MainActivity : FragmentActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private val TAG = "@Dev ${javaClass.simpleName}"

    /**
     *  gMap: The visible map you see
     *  isResumed: Used to set location only if its the first location since resumed activity
     *  fusedLocationProviderClient: Googles version of locationClient. Used to fetch location data
     *  googleApiClient: Handles api connections, most importantly connect and disconnect
     *  LocationRequest: Callback function passed when fetching data with fusedLocationProviderClient
     */
    private lateinit var gMap: GoogleMap
    private var isResumed: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: LocationRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = (object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                Log.i(TAG, "LocationCallbackResult: $p0")
            }
        })

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000)  // 1 second, in milliseconds

        /**
         * Call getMapAsync to get a google map.
         * This call will return a GoogleMap in onMapReady, which we implement from OnMapReadyCallback
         */
        val mapFragment = fragmentManager.findFragmentById(R.id.google_map_fragment) as MapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * We check permission to fetch location via our custom permission handler and pass a function
     * as the callback parameter
     * If we have/get permission we save the new googleMap and set isMyLocationEnabled to true,
     * enabling tracking
     */
    override fun onMapReady(googleMap: GoogleMap?) {

        val permissionHandler = PermissionHandler(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION), {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                googleMap?.let {
                    gMap = googleMap
                    gMap.isMyLocationEnabled = true
                }
            }
        })

        permissionHandler.checkPermission()

    }

    /**
     * Set isResumed to true
     * Activates "go to user location" only the first time the app is resumed
     * Connect the api client to te google services
     */
    override fun onResume() {
        super.onResume()
        isResumed = true
//      setupMapIfNeeded()
        mGoogleApiClient.connect()
    }

    /**
     * If connected when the app pauses (or are terminated) we disconnect and stop listening for
     * location updates
     */
    override fun onPause() {
        super.onPause()

        if (mGoogleApiClient.isConnected) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            mGoogleApiClient.disconnect()
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
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                gMap.animateCamera(cameraUpdate)
                isResumed = false
            }
        }
        Log.d(TAG, location.toString())
    }

    /**
     * Create a JoelPermissionHandler (TM) and pass a callback that set a request for location
     * updates and in turn a callback overriding onLocationResult which pass the current location
     * along to handleNewLocation
     */
    override fun onConnected(p0: Bundle?) {
        PermissionHandler(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION), {

            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {

                fusedLocationClient.requestLocationUpdates(mLocationRequest, (object : LocationCallback() {

                    override fun onLocationResult(p0: LocationResult?) {
                        super.onLocationResult(p0)
                        p0?.let {
                            handleNewLocation(it.lastLocation)
                        }
                    }

                }), null)


            }
        }).checkPermission()

    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (p0.hasResolution()) {
            p0.startResolutionForResult(this, PermissionCodes.CONNECTION_FAILURE_RESOLUTION_REQUEST.id)
        } else {
            Log.i(TAG, "Location services connection failed with code ${p0.errorMessage}")
        }

    }

}