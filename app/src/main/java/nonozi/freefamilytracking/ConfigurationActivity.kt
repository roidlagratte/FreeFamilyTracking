package nonozi.freefamilytracking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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

    // image
    private lateinit var btnChooseImage: Button
    private lateinit var imgSelectedImage: ImageView




    val PERIOD_KEY = intPreferencesKey("period")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)


        btnChooseImage = findViewById(R.id.btnChooseImage)


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











    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }


}