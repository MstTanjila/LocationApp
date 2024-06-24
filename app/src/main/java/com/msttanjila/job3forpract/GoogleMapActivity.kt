package com.msttanjila.job3forpract

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.msttanjila.myapplication.R
import com.msttanjila.myapplication.databinding.ActivityMapsBinding

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var firestoreViewModel: FirestoreViewModel
    private val boundsBuilder = LatLngBounds.Builder()
    private var hasValidLocations = false
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Fetch user locations from Firestore and add markers
        firestoreViewModel.getAllUsers { userList ->
            for (user in userList) {
                val userLocation = user.location
                if (userLocation.isNotEmpty()) {
                    val latLng = parseLocation(userLocation)
                    val markerOptions = MarkerOptions().position(latLng).title(user.displayName)
                    googleMap.addMarker(markerOptions)

                }
            }

            }
        if (hasValidLocations) {
            val bounds = boundsBuilder.build()
            val padding =500
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
           googleMap.animateCamera(cameraUpdate)
        }
        }
    }

    private fun parseLocation(location: String): LatLng {
        val latLngSplit = location.split(", ")
        val latitude = latLngSplit[0].substringAfter("Lat: ").toDouble()
        val longitude = latLngSplit[1].substringAfter("Long: ").toDouble()
        return LatLng(latitude, longitude)
    }