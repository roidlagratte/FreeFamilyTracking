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
    private lateinit var txtName: TextView
    private lateinit var txtGroupName: TextView
    private lateinit var edtName: EditText
    private lateinit var edtGroupName: EditText

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
        txtSavedValue = findViewById(R.id.txtSavedValue)
        txtName = findViewById(R.id.txtName)
        txtGroupName = findViewById(R.id.txtGroupName)
        edtName = findViewById(R.id.edtName) // Initialiser le champ name
        edtGroupName = findViewById(R.id.edtGroupName)

        // Lire les valeurs enregistrées et les afficher
        lifecycleScope.launchWhenStarted {


            val savedValue = dataStoreManager.readPeriod().first()
            txtSavedValue.text = "$savedValue"
            val savedName = dataStoreManager.readName().first()
            val savedGroupName = dataStoreManager.readGroupName().first()

            // Mise à jour des TextView avec les valeurs lues
            txtName.text = savedName
            txtGroupName.text = savedGroupName






            btnSavePeriod.setOnClickListener {
                val period = edtPeriod.text.toString().toIntOrNull()
                val name = edtName.text.toString()
                val groupName = edtGroupName.text.toString()

                // Vérifiez la validité de chaque champ individuellement
                val isPeriodValid = period != null
                val isNameValid = name.isNotEmpty()
                val isGroupNameValid = groupName.isNotEmpty()

                // Si au moins un champ requis est vide, affichez un message d'erreur
                if (!(isPeriodValid || isNameValid || isGroupNameValid)) {
                    Toast.makeText(this@ConfigurationActivity, "Veuillez remplir au moins un champ correctement", Toast.LENGTH_SHORT).show()
                } else {
                    // Si tous les champs requis sont remplis, procédez à la mise à jour
                    lifecycleScope.launch {
                        if (isPeriodValid) {
                            dataStoreManager.savePeriod(period!!)
                            txtSavedValue.text = "$period"
                        }
                        if (isNameValid) {
                            dataStoreManager.saveName(name)
                            txtName.text = name
                        }
                        if (isGroupNameValid) {
                            dataStoreManager.saveGroupName(groupName)
                            txtGroupName.text = groupName
                        }
                    }
                }
            }


            // Enregistrer la valeur lors du clic sur le bouton
/*            btnSavePeriod.setOnClickListener {
                val period = edtPeriod.text.toString().toIntOrNull()
                val name = edtName.text.toString()
                val groupName = edtGroupName.text.toString()
                if (period != null && name.isNotEmpty() && groupName.isNotEmpty()) {
                    // Enregistrer la période dans le DataStore
                    lifecycleScope.launch {
                            dataStoreManager.saveConfiguration(period, name, groupName)
                       // notifyService(period)
                    } // lifecyclescope
                    txtSavedValue.text = "$period"
                    txtName.text = "$name"
                    txtGroupName.text = "$groupName"
                } else {
                    // Gérer le cas où la saisie n'est pas un nombre valide
                    Toast.makeText(this@ConfigurationActivity, "Veuillez remplir tous les champs correctement", Toast.LENGTH_SHORT).show()

                } // else-if
            } // btnSavePeriod
            */

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