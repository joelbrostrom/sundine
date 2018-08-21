package com.appshack.sundine.interfaces

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.OnMapReadyCallback

/**
 * Created by joelbrostrom on 2018-05-03
 * Developed by App Shack
 */

interface MapInterface : GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {

    override fun onConnectionSuspended(p0: Int) {}

}