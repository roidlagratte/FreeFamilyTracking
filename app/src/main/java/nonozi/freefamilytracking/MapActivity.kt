package nonozi.freefamilytracking


import androidx.appcompat.widget.Toolbar
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity: AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        // Initialiser osmdroid
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Récupérer la référence de la MapView
        mapView = findViewById(R.id.mapView)

        // Configurer le centre et le zoom de la carte
        val mapController = mapView.controller
        val startPoint = GeoPoint(48.8583, 2.2944) // Coordonnées de Paris (par défaut)
        mapController.setCenter(startPoint)
        mapController.setZoom(14.0)

        // Ajouter un marqueur pour afficher le dernier point enregistré
        val lastPoint = GeoPoint(48.8583, 2.2944) // Coordonnées fictives (à remplacer avec les dernières coordonnées)
        val marker = Marker(mapView)
        marker.position = lastPoint
        mapView.overlays.add(marker)


        val retrofitBuilder = RetrofitBuilder.instance
        retrofitBuilder?.build(retrofitBuilder.BASE_URL_POST)

        val apiInterface = retrofitBuilder?.apiInterface
        // Network call
        val apiCalls = retrofitBuilder?.callApi()

        val RequestMap = PostRequestMap("examplePhone")
        apiCalls?.getCoordinates(RequestMap)?.enqueue(object : Callback<PostResponseMap?> {


            override fun onResponse(call: Call<PostResponseMap?>, response: Response<PostResponseMap?>) {
                if (response.isSuccessful) {
                    val postResponseMap = response.body()
                    postResponseMap?.let {
                        val latitude = it.latitude?.toDoubleOrNull()
                        val longitude = it.longitude?.toDoubleOrNull()
                        if (latitude != null && longitude != null) {
                            Log.d("MapActivity", "latitude=$latitude et longitude=$longitude")
                            val newPoint = GeoPoint(latitude, longitude)
                            // Mettre à jour le marqueur avec les nouvelles coordonnées
                            marker.position = newPoint
                            mapView.invalidate() // Rafraîchir la carte pour afficher le nouveau marqueur
                            // Centrer la carte sur les nouvelles coordonnées si nécessaire
                            mapView.controller.setCenter(newPoint)
                        } else {
                            Log.d("MapActivity", "Latitude et Longitude nulles")
                            // Gérer les valeurs de latitude ou de longitude nulles
                        }
                    }
                } else {
                    // Gérer les erreurs de réponse
                    // Afficher un message à l'utilisateur par exemple
                }
            } // overrride onResponse



            override fun onFailure(call: Call<PostResponseMap?>, t: Throwable) {
                // Gérer l'échec de l'appel API
            }
        })
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