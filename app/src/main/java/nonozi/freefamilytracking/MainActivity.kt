package nonozi.freefamilytracking

import android.content.pm.PackageManager
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.Timer
import com.google.android.gms.location.FusedLocationProviderClient

import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import java.text.SimpleDateFormat
import java.util.Date
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// import androidx.compose.runtime.Composable

// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.*
import nonozi.freefamilytracking.DataStoreManager

import nonozi.freefamilytracking.MyBackgroundService.Companion.EXTRA_PERIOD

const val LOCATION_PERMISSION_REQUEST = 1001

class MainActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    //private lateinit var dataStoreManager: DataStoreManager // Instance unique du DataStoreManager
    private var retrofitBuilder: RetrofitBuilder? = RetrofitBuilder.instance
    private val handler = Handler()
    private val timer = Timer()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var txtSavedValue: TextView



    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    //val PERIOD_KEY = intPreferencesKey("period")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar =findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        // Initialiser le DataStoreManager en tant que singleton
       // dataStoreManager = DataStoreManager.getInstance(this)
        dataStoreManager = DataStoreManager.getInstance(this)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //val sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE)
        //val editor = sharedPreferences.edit()
        val btnStartService = findViewById<TextView>(R.id.btnStartService)
        val btnStopService = findViewById<TextView>(R.id.btnStopService)


        //val phonename = "Arno_Code1234"
        //editor.putString("phonename", phonename)
        //editor.apply()

        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        btnStartService.setOnClickListener {
            lifecycleScope.launch {
                try {
                    // Récupérer la période depuis le DataStore
                    val savedPeriod = dataStoreManager.readPeriod().first()

                    // Démarrer le service avec l'intervalle récupéré
                    val serviceIntent = Intent(this@MainActivity, MyBackgroundService::class.java)
                    serviceIntent.putExtra(EXTRA_PERIOD, savedPeriod)
                    startService(serviceIntent)

                    // Utilisez la valeur récupérée de readPeriod() si nécessaire
                    // INTERVAL = savedPeriod.toLong()

                } catch (e: Exception) {
                    Log.e("BTNSTARTSERVICE", "Error retrieving period from DataStore: ${e.message}")
                }
            }



        }


        btnStopService.setOnClickListener {
            stopService(Intent(this, MyBackgroundService::class.java))
        }







    } // end fun OnCreate











    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée, vous pouvez maintenant accéder à la localisation
            } else {
                // La permission a été refusée, vous pouvez informer l'utilisateur ou prendre d'autres mesures
            }
        }
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                // Ouvrir l'activité correspondante à l'icône "Accueil"
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_configuration -> {
                // Ouvrir l'activité correspondante à l'icône "Parameters"
                val intent = Intent(this, ConfigurationActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_map -> {
                // Ouvrir l'activité correspondante à l'icône "Map"
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }










}
