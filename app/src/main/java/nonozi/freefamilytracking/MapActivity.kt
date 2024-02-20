package nonozi.freefamilytracking


import androidx.appcompat.widget.Toolbar
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var retrofitBuilder: RetrofitBuilder? = RetrofitBuilder.instance
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 5 * 1000L // 15 secondes

    private fun refreshActivity() {
        // Mettez à jour vos données ou effectuez d'autres opérations nécessaires
        fetchCoordinatesFromAPI()
    }

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
        mapController.setZoom(3.0)

        // Appel à l'API pour récupérer les coordonnées
      //  fetchCoordinatesFromAPI()
        startPeriodicRefresh()
    }

    // Fonction pour démarrer la planification des actualisations
    private fun startPeriodicRefresh() {
        // Utilisez le Handler pour exécuter la fonction de rafraîchissement toutes les 15 secondes
        handler.postDelayed(object : Runnable {
            override fun run() {
                refreshActivity() // Appeler la fonction de rafraîchissement
                handler.postDelayed(this, refreshInterval) // Planifier la prochaine actualisation
            }
        }, refreshInterval)
    }


    private fun fetchCoordinatesFromAPI() {
        retrofitBuilder?.build(retrofitBuilder?.BASE_URL_POST)
        val postRequestMap = PostRequestMap("nonozi", "HQ7dJ9uw")

        val call: Call<List<PostResponseMap?>?> = retrofitBuilder!!.callApi().getCoordinates(postRequestMap)
        call!!.enqueue(object : Callback<List<PostResponseMap?>?> {
            override fun onResponse(
                call: Call<List<PostResponseMap?>?>,
                response: Response<List<PostResponseMap?>?>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Log.d("Response", "$responseBody")
                    // Traiter la réponse et afficher les marqueurs sur la carte
                    displayMarkers(responseBody)
                    centerMapOnMarkers(responseBody)
                } else {
                    Log.d("Response", "Response Erreur")
                    // Ici, vous pouvez mettre le code pour gérer l'erreur
                }
            }

            override fun onFailure(call: Call<List<PostResponseMap?>?>, t: Throwable) {
                t.printStackTrace()
                // Enregistrer l'exception dans les logs
                Log.e("MapActivity", "Erreur lors de la requête API", t)

                // Vous pouvez également afficher un message Toast pour informer l'utilisateur de l'échec de la requête
                Toast.makeText(this@MapActivity, "Erreur lors de la requête API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getMarkerColor(name: String): Int {
        return when (name) {
            "nonozi" -> Color.BLUE
            "celine" -> Color.RED
            "Clara" -> Color.MAGENTA // Rose
            else -> Color.GRAY
        }
    }
    fun createMarkerIcon(color: Int, size: Int): Drawable {
        // Créer un bitmap avec la couleur spécifiée et la taille donnée
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color

        // Dessiner un cercle dans le bitmap avec le rayon approprié
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius * 0.8f, paint) // Ajustez le rayon selon votre besoin

        // Convertir le bitmap en Drawable
        return BitmapDrawable(bitmap)
    }



    private fun displayMarkers(responseBody: List<PostResponseMap?>) {
        // Supprimer les marqueurs existants
        mapView.overlays.clear()

        // Ajouter des marqueurs pour chaque point avec des descriptifs
        for (point in responseBody) {
            val latitude = point?.latitude ?: continue
            val longitude = point?.longitude ?: continue
            val name = point?.name ?: continue
            val color = getMarkerColor(name)
            val markerIcon = createMarkerIcon(color,200)
            val marker = Marker(mapView)
            marker.position = GeoPoint(latitude, longitude)
            marker.title = name
            marker.snippet = "Latitude: $latitude, Longitude: $longitude"
            Log.i("DisplayMarkers","Marker=Name:$name et lat=$latitude - long=$longitude")
            marker.icon = markerIcon
            mapView.overlays.add(marker)

        }

        // Rafraîchir la carte pour afficher les nouveaux marqueurs
        mapView.invalidate()
    }

    fun centerMapOnMarkers(markers: List<PostResponseMap?>) {
        val mapController = mapView.controller
        val points = mutableListOf<GeoPoint>()
        var totalLat = 0.0
        var totalLon = 0.0
        // Ajoutez les marqueurs sur la carte et collectez les GeoPoint
        for (marker in markers) {
            val latitude = marker?.latitude ?: continue
            val longitude = marker.longitude ?: continue
            val geoPoint = GeoPoint(latitude, longitude)
            points.add(geoPoint)
            totalLat += latitude
            totalLon += longitude
        }

        if (points.isNotEmpty()) {
            // Calcul de la moyenne des coordonnées
            val avgLat = totalLat / points.size
            val avgLon = totalLon / points.size
            val center = GeoPoint(avgLat, avgLon)

            // Centrer la carte sur la moyenne de tous les points
            mapController.setCenter(center)

            // Détermination du niveau de zoom en fonction de l'éloignement total de tous les points
            val zoomLevel = determineZoomLevel(points)

            // Appliquer le niveau de zoom
            mapController.setZoom(zoomLevel.toDouble())

            // Ajout du marqueur pour afficher la distance totale
            val totalDistance = calculateTotalDistance(points)
            val totalDistanceMarker = Marker(mapView)
            // Définir la position du marqueur en haut à droite de la carte
            totalDistanceMarker.position = GeoPoint(mapView.boundingBox.latNorth, mapView.boundingBox.lonEast)
            totalDistanceMarker.setAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_TOP)
            totalDistanceMarker.title = "Distance totale"
            totalDistanceMarker.snippet = "Distance: $totalDistance m"
          // A RETRAVAILLER  mapView.overlays.add(totalDistanceMarker)
        } else {
            // Si aucun point n'est disponible, laisser la carte avec le centre et le zoom par défaut
            mapController.setZoom(14.0)
        }
    }






    // Calculer le centre des marqueurs
    fun calculateCenter(markers: List<PostResponseMap?>): GeoPoint {
        var totalLat = 0.0
        var totalLon = 0.0
        for (marker in markers) {
            totalLat += marker!!.latitude!!
            totalLon += marker.longitude!!
        }
        val avgLat = totalLat / markers.size
        val avgLon = totalLon / markers.size
        return GeoPoint(avgLat, avgLon)
    }

    fun determineZoomLevel(points: List<GeoPoint>): Int {
        val totalDistance = calculateTotalDistance(points)
        return when {
            totalDistance <= 1000 -> 15 // Distance totale <= 1km
            totalDistance <= 5000 -> 14
            totalDistance <= 15000 -> 13
            totalDistance <= 30000 -> 12 // OK
            totalDistance <= 50000 -> 11
            totalDistance <= 100000 -> 10

            else -> 8 // Distance totale > 10km
        }
    }

    fun calculateTotalDistance(points: List<GeoPoint>): Double {
        var totalDistance = 0.0
        for (i in 0 until points.size - 1) {
            totalDistance += points[i].distanceToAsDouble(points[i + 1])
        }
        return totalDistance
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

    override fun onDestroy() {
        super.onDestroy()
        // Arrêtez la planification des actualisations
        handler.removeCallbacksAndMessages(null)
    }

}