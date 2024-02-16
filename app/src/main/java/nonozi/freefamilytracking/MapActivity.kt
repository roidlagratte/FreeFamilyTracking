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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import nonozi.freefamilytracking.PostRequestMap

class MapActivity: AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var marker: Marker
    private var retrofitBuilder: RetrofitBuilder? = RetrofitBuilder.instance
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


        /*
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
*/


        retrofitBuilder?.build(retrofitBuilder?.BASE_URL_POST)
        val postRequestMap = PostRequestMap("nonozi", "HQ7dJ9uw")

        val call = retrofitBuilder!!.callApi().getCoordinates(postRequestMap)
        Log.d("ApiCall", "Appel de l'API PostRequestModel")
        call!!.enqueue(object : Callback<PostResponseMap?> {
            override fun onResponse(
                call: Call<PostResponseMap?>,
                response: Response<PostResponseMap?>
            ) {
                val responseBody = response.body()
                Log.d("Response", "OnResponse")
                if (response.isSuccessful && responseBody != null) {
                    Log.d("Response", "Response is $responseBody")
                    // Ici, vous pouvez mettre le code pour traiter la réponse
                } else {
                    Log.d("Response", "Response Erreur")
                    // Ici, vous pouvez mettre le code pour gérer l'erreur
                }
            }

            override fun onFailure(call: Call<PostResponseMap?>, t: Throwable) {
                t.printStackTrace()

                // Enregistrer l'exception dans les logs
                Log.e("MapActivity", "Erreur lors de la requête API", t)

                // Vous pouvez également afficher un message Toast pour informer l'utilisateur de l'échec de la requête
                Toast.makeText(this@MapActivity, "Erreur lors de la requête API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        /*
        val retrofitBuilder = RetrofitBuilder.instance
        retrofitBuilder?.build(retrofitBuilder.BASE_URL_POST)
        val apiInterface = retrofitBuilder?.apiInterface
        val apiCalls = retrofitBuilder?.callApi()
        val RequestMap = PostRequestMap("nonozi","HQ7dJ9uw")
        apiCalls?.getCoordinates(RequestMap)?.enqueue(object : Callback<PostResponseMap?> {
            override fun onResponse(call: Call<PostResponseMap?>, response: Response<PostResponseMap?>) {
                if (response.isSuccessful) {
                    val postResponseMap = response.body()
                    postResponseMap?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
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
        }) */
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