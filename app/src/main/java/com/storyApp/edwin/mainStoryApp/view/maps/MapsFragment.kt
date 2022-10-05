package com.storyApp.edwin.mainStoryApp.view.maps

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.storyApp.edwin.mainStoryApp.R
import com.storyApp.edwin.mainStoryApp.model.Story
import com.storyApp.edwin.mainStoryApp.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MapsFragment(token: String) : Fragment(R.layout.fragment_maps), OnMapReadyCallback {

    private val _token = token
    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        getFindLocation(_token)
    }

    private fun getFindLocation(token: String) {
        val loc = 1;
        val client = RetrofitClient.create(token).getFindLocation(loc)
        client.enqueue(object: Callback<Story> {
            override fun onResponse(call: Call<Story>, response: Response<Story>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res != null) {
                        marker(res)
                    }
                }
            }

            override fun onFailure(call: Call<Story>, t: Throwable) {
                Log.d(FAILURE, "${t.message}")
            }
        })
    }

    private fun marker(res: Story) {
        res.listStory.forEach { data ->
            val latLng = LatLng(data.lat, data.lon)
            val addressName = getAddressName(data.lat, data.lon)
            mMap.addMarker(MarkerOptions().position(latLng).title(data.name).snippet(addressName))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(context?.let { MapStyleOptions.loadRawResourceStyle(it, R.raw.map_style) })
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    companion object {
        private const val TAG = "MapsFragment"
        private const val FAILURE = "Failure"
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Add a marker in DIY and move the camera
        val diy = LatLng(-7.800296, 110.361329)
        mMap.addMarker(MarkerOptions().position(diy).title("Marker in Yogyakarta City, Indonesia"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(diy))
        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(vectorToBitmap(R.drawable.ic_android, Color.parseColor("#3DDC84")))
            )
        }
        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        setMapStyle()
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}