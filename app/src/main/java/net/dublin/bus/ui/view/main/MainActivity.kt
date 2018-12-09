package net.dublin.bus.ui.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import net.dublin.bus.R
import net.dublin.bus.common.Analytics
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.data.sync.SyncUtils
import net.dublin.bus.ui.utilities.BottomNavigationViewHelper
import net.dublin.bus.ui.view.favourite.FavouriteFragment
import net.dublin.bus.ui.view.near.NearActivity
import net.dublin.bus.ui.view.route.RouteFragment
import net.dublin.bus.ui.view.search.SearchActivity
import net.dublin.bus.ui.view.stop.StopFragment

class MainActivity : AppCompatActivity() {
    private var checkFavourite: Boolean = false

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_stop -> {
                Analytics.trackScreenStops(this)
                switchContent(StopFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_route -> {
                Analytics.trackScreenRoutes(this)
                switchContent(RouteFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_near -> {
                NearActivity.navigate(this)
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_favorite -> {
                Analytics.trackScreenFavourites(this)
                switchContent(FavouriteFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    companion object {
        private const val EXTRA_CHECK_FAVOURITE = "route_number"

        fun navigate(context: Context, checkFavourite: Boolean) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_CHECK_FAVOURITE, checkFavourite)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initExtra()

        main_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //BottomNavigationViewHelper.disableShiftMode(main_navigation)

        main_search_view.setOnClickListener {
            SearchActivity.navigate(this)
        }

        if (savedInstanceState == null) {
            checkFavourite()
        }

        SyncUtils.initialize(this)
    }

    private fun initExtra() {
        checkFavourite = intent.getBooleanExtra(EXTRA_CHECK_FAVOURITE, false)
    }

    @SuppressLint("CheckResult")
    private fun checkFavourite() {
        if (checkFavourite) {
            val repository = StopRepository(application)
            repository.getQtdStops()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        Analytics.sendFavouritesQtdProperty(this, data)
                        onNextData(data > 0)
                    }, {
                        onError()
                    })
        } else {
            main_navigation.selectedItemId = R.id.navigation_favorite
        }
    }

    private fun onError() {
        main_navigation.selectedItemId = R.id.navigation_stop
    }

    private fun onNextData(data: Boolean) {
        if (data) {
            main_navigation.selectedItemId = R.id.navigation_favorite
        } else {
            main_navigation.selectedItemId = R.id.navigation_stop
        }
    }

    private fun switchContent(fragment: Fragment?) {
        if (fragment != null) {
            try {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_container, fragment, "fragmentBase")
                fragmentTransaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                Log.e(MainActivity::class.java.name, "Error in show view")
            }
        }
    }
}
