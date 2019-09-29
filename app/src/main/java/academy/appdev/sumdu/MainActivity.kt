package academy.appdev.sumdu

import academy.appdev.sumdu.fragments.ContainerFragment
import academy.appdev.sumdu.networking.getLists
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val sharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
    private var registeredSharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, ContainerFragment())
            .commitAllowingStateLoss()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(registeredSharedPreferencesListener)
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount <= 0) {
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(false)
                    setDisplayShowHomeEnabled(false)
                    title = getString(R.string.app_name)
                }
            }
        }
    }

    fun setSharedPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
        registeredSharedPreferencesListener = listener
    }
}

val Fragment.mainActivity: MainActivity? get() = context as? MainActivity

fun Context.makeToast(stringId: Int) {
    Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
}
