package academy.appdev.sumdu

import academy.appdev.sumdu.fragments.ContainerFragment
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val sharedPreferences: SharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, ContainerFragment())
            .commitAllowingStateLoss()
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
}

val Fragment.mainActivity: MainActivity? get() = context as? MainActivity

