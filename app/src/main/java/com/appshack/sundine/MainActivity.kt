package com.appshack.sundine

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.DexterBuilder
import java.security.Permission

class MainActivity : FragmentActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationClient = FusedLocationProviderClient(this)

        val mapFragment = fragmentManager.findFragmentById(R.id.google_map_fragment) as MapFragment
        mapFragment.getMapAsync(this)

        val mGoeDataClient = Places.getGeoDataClient(this)
        val mPlaceDetectionClient = Places.getPlaceDetectionClient(this)
        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(gMap: GoogleMap?) {

        val permissionHandler = PermissionHandler(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION), {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                gMap?.isMyLocationEnabled = true
            }
            getLocation()
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).latitude,
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).longitude)
                    , 14f))
        })

        permissionHandler.checkPermission()


//        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
//            gMap?.isMyLocationEnabled = true
//        }


    }

    private fun getLocation() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000L, 0f, locationListener)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000L, 0f, locationListener)


            val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastLocation?.let { locationListener.onLocationChanged(it) }
                    ?: locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

        } else {
            Toast.makeText(this, "Could not get location", Toast.LENGTH_LONG).show()
        }
    }

    override fun onConnected(p0: Bundle?) {
        if (PermissionChecker.checkSelfPermission(this, ))
        var lastLocation = locationClient.lastLocation
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location?) {
            location?.let {
                Toast.makeText(this@MainActivity, "Lat: ${it.latitude}, Lng: ${it.longitude}", Toast.LENGTH_LONG).show()
//                posViewModel.loadPos(LatLng(it.latitude, it.longitude))

            } ?: Log.d("@Dev onLocationError", "Location == null")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }

}

