package com.example.kotlinwetherapp

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.example.kotlinwetherapp.Common.Common
import com.example.kotlinwetherapp.Common.Helper
import com.example.kotlinwetherapp.Models.OpenWeatherMap
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener  {
    val PEMISSION_REQUEST_CODE = 1001
    val PLAY_SERVICE_RESOLUTION_REQUEST = 1000

    var mGoogleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    internal var openWeatherMap = OpenWeatherMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
        if(checkPlayService())
            buildGoogleApiClient()
    }

    private fun requestPermission(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),PEMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PEMISSION_REQUEST_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayService())
                    {
                        buildGoogleApiClient()
                    }
                }
            }
        }
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener (this)
            .addApi(LocationServices.API).build()
    }

    private fun checkPlayService(): Boolean {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RESOLUTION_REQUEST).show()
            } else {
                Toast.makeText(applicationContext, "this device is not supported", Toast.LENGTH_SHORT).show()
                finish()
            }
            return false
        }
        return true
    }

    override fun onConnected(p0: Bundle?) {
        createLocationRequest();
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.interval = 10000
        mLocationRequest!!.fastestInterval = 500000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("ERROR","connection Failed" + p0.errorCode)
    }

    override fun onLocationChanged(location: Location?) {
        GetWeather().execute(Common.apiRequest(location!!.latitude.toString(), location.longitude.toString()))
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null)
            mGoogleApiClient!!.connect()
    }

    override fun onDestroy() {
        mGoogleApiClient!!.disconnect()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        checkPlayService()
    }

    private inner class GetWeather: AsyncTask<String, Void, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var stream:String? = null
            val urlString= params[0]

            val http = Helper()
            stream = http.getHTTPSData(urlString)
            return stream
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.contains("Error: City Not Found")){
                return
            }
            val gson = Gson()
            val mType = object: TypeToken<OpenWeatherMap>(){}.type

            openWeatherMap = gson.fromJson<OpenWeatherMap>(result,mType)


            textCity.text = "${openWeatherMap.name}, ${openWeatherMap.sys!!.country}"
            textLastUpdated.text = "Correct for: ${Common.dateNow}"
            textDescription.text = "${openWeatherMap.weather!![0].description}"
            textSunrise.text = "Sunrise at ${Common.unixTimeStampToDateTime(openWeatherMap.sys!!.sunrise)}"
            textSunset.text = "Sunset at ${Common.unixTimeStampToDateTime(openWeatherMap.sys!!.sunset)}"
            textHumidity.text = "Humidity: ${openWeatherMap.main!!.humidity}%"
            textCelsius.text = "${openWeatherMap.main!!.temp} °C"
            Picasso.with(this@MainActivity)
                .load(Common.getImage(openWeatherMap.weather!![0].icon!!))
                .into(imageView)
        }
    }
}
