package nonozi.freefamilytracking

import android.content.pm.PackageManager
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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


const val LOCATION_PERMISSION_REQUEST = 1001

class MainActivity : AppCompatActivity() {


    private var retrofitBuilder: RetrofitBuilder? = RetrofitBuilder.instance
    private val handler = Handler()
    private val timer = Timer()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var dataStoreManager: DataStoreManager
  //  private lateinit var txtLocation: TextView
    private lateinit var edtPeriod: EditText
    private lateinit var btnSavePeriod: Button
    private lateinit var txtSavedValue: TextView



    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    val PERIOD_KEY = intPreferencesKey("period")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dataStoreManager = DataStoreManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
     //   val edtPeriod = findViewById<EditText>(R.id.edtPeriod)
     //   val btnSavePeriod = findViewById<Button>(R.id.btnSavePeriod) // Bouton qui enregistre les préférences concernant l'intervalle entre chaque requête
        val btnPostRequest = findViewById<Button>(R.id.btnPostRequest) // Bouton qui requete les coordonnées et les envoi au serveur
        val txtResultValue = findViewById<TextView>(R.id.txtResultValue)
        val txtPeriod = findViewById<TextView>(R.id.txtPeriod)
        val txtLocation = findViewById<TextView>(R.id.txtLocation)
        val btnStartService = findViewById<TextView>(R.id.btnStartService)
        val btnStopService = findViewById<TextView>(R.id.btnStopService)

       /* btnSavePeriod.setOnClickListener {
            val enteredPeriod = edtPeriod.text.toString().toIntOrNull()
            if (enteredPeriod != null) {
                editor.putInt("period", enteredPeriod)
                editor.apply()
            }*/



        val phonename = "Arno_Code1234"
        editor.putString("phonename", phonename)
        editor.apply()

        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        btnPostRequest.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    sendPostRequest(txtResultValue, "ExamplePhone")
                    Log.d(
                        "FusedLocationClient",
                        "Latitude: $currentLatitude, Longitude: $currentLongitude"
                    )
                } else {
                    Toast.makeText(
                        this,
                        "Impossible de récupérer la position actuelle.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("FusedLocationClient", "Impossible d'afficher la localisation GPS")
                } // end else
            } // end fusedlocationclient
        }// end btnPostRequest


        btnStartService.setOnClickListener {
            val serviceIntent = Intent(this, MyBackgroundService::class.java)
            startService(serviceIntent)
            lifecycleScope.launchWhenStarted {
                val savedValue = dataStoreManager.readPeriod().first()


            notifyService(savedValue)}
        }


        btnStopService.setOnClickListener {
            stopService(Intent(this, MyBackgroundService::class.java))
        }



        // Initialiser les vues
        edtPeriod = findViewById(R.id.edtPeriod)
        btnSavePeriod = findViewById(R.id.btnSavePeriod)
        txtSavedValue = findViewById(R.id.txtResultValue)

        // Lire la valeur enregistrée et l'afficher
        lifecycleScope.launchWhenStarted {
            val savedValue = dataStoreManager.readPeriod().first()
            txtSavedValue.text = "Valeur enregistrée : $savedValue"
        }

        // Enregistrer la valeur lors du clic sur le bouton
        btnSavePeriod.setOnClickListener {
            val period = edtPeriod.text.toString().toIntOrNull()
            if (period != null) {
                // Enregistrer la période dans le DataStore
                lifecycleScope.launch {
                    dataStoreManager.savePeriod(period)
                    notifyService(period)


                }
                txtSavedValue.text = "Valeur enregistrée : $period"
            } else {
                // Gérer le cas où la saisie n'est pas un nombre valide
                Toast.makeText(this, "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show()
            }
        } // btnSavePeriod
    } // end fun OnCreate


    private fun sendPostRequest(txtResulValue: TextView, phonename: String) {
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        retrofitBuilder?.build(retrofitBuilder?.BASE_URL_POST)
        val postRequestModel = PostRequestModel(phonename, currentLatitude.toString(), currentLongitude.toString(),currentDateTime)

        val call = retrofitBuilder!!.callApi().postHeros(postRequestModel)
        Log.d("ApiCall", "Appel de l'API PostRequestModel")
        call!!.enqueue(object : Callback<PostResponseModel?> {
            override fun onResponse(
                call: Call<PostResponseModel?>,
                response: Response<PostResponseModel?>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    txtResulValue.text = responseBody.toString()
                } else {
                    txtResulValue.text = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<PostResponseModel?>, t: Throwable) {
                txtResulValue.text = "Error: ${t.message}"
            }
        })
    }



    private fun notifyService(period: Int) {
        val intent = Intent(this, MyBackgroundService::class.java)
        intent.action = MyBackgroundService.ACTION_UPDATE_PERIOD
        intent.putExtra(MyBackgroundService.EXTRA_PERIOD, period)
        startService(intent)
    }





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
}
