package academy.appdev.sumdu

import academy.appdev.sumdu.databinding.ActivityMainBinding
import academy.appdev.sumdu.fragments.ContainerFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureToolbar()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, ContainerFragment())
            .commitAllowingStateLoss()
    }

    private fun configureToolbar() {
        setSupportActionBar(binding.toolbar)

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

