package nonozi.freefamilytracking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nonozi.freefamilytracking.DataStoreManager
class ConfigurationActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    //  private lateinit var txtLocation: TextView
    private lateinit var edtPeriod: EditText
    private lateinit var btnSavePeriod: Button
    private lateinit var txtSavedValue: TextView
    private lateinit var txtResultValue: TextView
    val PERIOD_KEY = intPreferencesKey("period")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        val toolbar: Toolbar =findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)



        dataStoreManager = DataStoreManager(this)
        val sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Initialiser les vues
        edtPeriod = findViewById(R.id.edtPeriod)
        btnSavePeriod = findViewById(R.id.btnSavePeriod)
        txtSavedValue = findViewById(R.id.txtResultValue)

        // Lire la valeur enregistrée et l'afficher
        lifecycleScope.launchWhenStarted {
            val savedValue = dataStoreManager.readPeriod().first()
            txtSavedValue.text = "Valeur enregistrée : $savedValue"

            // Enregistrer la valeur lors du clic sur le bouton
            btnSavePeriod.setOnClickListener {
                val period = edtPeriod.text.toString().toIntOrNull()
                if (period != null) {
                    // Enregistrer la période dans le DataStore
                    lifecycleScope.launch {
                        dataStoreManager.savePeriod(period)
                       // notifyService(period)
                    } // lifecyclescope
                    txtSavedValue.text = "Valeur enregistrée : $period"
                } else {
                    // Gérer le cas où la saisie n'est pas un nombre valide
                    Toast.makeText(this@ConfigurationActivity, "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show()

                } // else-if
            } // btnSavePeriod
        } // lifecycleScope
    } // OnCreate


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