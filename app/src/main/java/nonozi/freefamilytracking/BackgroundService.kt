package nonozi.freefamilytracking

import android.Manifest
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.TextView

import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.first

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyBackgroundService : Service() {
    var timer: Timer? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var retrofitBuilder: RetrofitBuilder? = RetrofitBuilder.instance
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private val channelId = "MyForegroundServiceChannel"
    private val handler = Handler()
    private var INTERVAL = 60000L
    val dataStoreManager = DataStoreManager(this)
    private lateinit var savedName: String
    private lateinit var savedGroupName: String

    companion object {
        fun isServiceRunning(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (MyBackgroundService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }


        const val ACTION_UPDATE_PERIOD = "nonozi.freefamilytracking.UPDATE_PERIOD"
        const val EXTRA_PERIOD = "period"
        const val NAME_KEY = "name"
        const val GROUP_NAME_KEY = "groupName"

    }




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyBackgroundService", "Starting service...")
        startForeground(1, createNotification()) // Ajoutez cette ligne pour démarrer le service en mode foreground
        val savedPeriod = intent?.getIntExtra(EXTRA_PERIOD, 60000) ?: 60000 // default 60 sec
        INTERVAL = savedPeriod.toLong()


        savedName = intent?.getStringExtra(NAME_KEY) ?: "Unknown"
        savedGroupName = intent?.getStringExtra(GROUP_NAME_KEY) ?: "Unknown"

        Log.d("MyBackgroundService", "savedName=$savedName, savedGroupName=$savedGroupName")

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        if (intent != null && intent.action == ACTION_UPDATE_PERIOD) {
           var newPeriod = intent.getIntExtra(EXTRA_PERIOD, 3000)
            //var newPeriod = intent?.getIntExtra(EXTRA_PERIOD, INTERVAL)?: INTERVAL.toLong()
            handler.removeCallbacksAndMessages(null)
            Log.d("MyBackgroundService", "Update INTERVAL to newperiod = $newPeriod")
            INTERVAL = newPeriod.toLong()
        }
        startSendingLocationUpdates()
        return START_STICKY


    } // OnStartCoomand


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("MyBackgroundService","Arrêt du service demandé, destruction des différentes instances")
        super.onDestroy()
        stopSendingLocationUpdates()
        timer?.cancel()
    }

    private fun startSendingLocationUpdates() {
        val newtimer = Timer()
        newtimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.d("MyBackgroundService", "Sending location update...")
                if (checkLocationPermission()) {
                    getLastLocation { location ->
                        location?.let {
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude
                            Log.d("MyBackgroundService", "Latitude: $currentLatitude, Longitude: $currentLongitude")
                            sendPostRequest()
                        }
                    }
                }
            }
        }, 0, INTERVAL)
    }


    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun stopSendingLocationUpdates() {
        handler.removeCallbacksAndMessages(null)
    }


    private fun sendPostRequest() {
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        retrofitBuilder?.build(retrofitBuilder?.BASE_URL_POST)
        val postRequestModel = PostRequestModel(savedName, savedGroupName, currentLatitude.toString(), currentLongitude.toString(), currentDateTime)
        val call = retrofitBuilder!!.callApi().postHeros(postRequestModel)
        Log.d("ApiCall", "Appel de l'API PostRequestModel")
        call!!.enqueue(object : Callback<PostResponseModel?> {
            override fun onResponse(call: Call<PostResponseModel?>, response: Response<PostResponseModel?>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    // Ici, vous pouvez mettre le code pour traiter la réponse
                } else {
                    // Ici, vous pouvez mettre le code pour gérer l'erreur
                }
            }

            override fun onFailure(call: Call<PostResponseModel?>, t: Throwable) {
                // Ici, vous pouvez mettre le code pour gérer l'échec de la requête
            }
        })
    }

    private fun createNotification(): Notification {
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Free Family Tracking")
            .setContentText("Start service")
            .setSmallIcon(R.drawable.ic_launcher_breizh)
            .setContentIntent(pendingIntent)
            .build()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getLastLocation(callback: (Location?) -> Unit) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    callback.invoke(location)
                }
        } else {
            callback.invoke(null)
        }
    }



}




