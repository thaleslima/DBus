package net.dublin.bus.ui.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import net.dublin.bus.R
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite
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
                switchContent(StopFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_route -> {
                switchContent(RouteFragment.newInstance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_near -> {
                //switchContent(NearFragment.newInstance())
                NearActivity.navigate(this)
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_favorite -> {
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
        BottomNavigationViewHelper.disableShiftMode(main_navigation)

        main_search_view.setOnClickListener {
            SearchActivity.navigate(this)
        }

        if (savedInstanceState == null) {
            checkFavourite()
        }
    }

    private fun initExtra() {
        checkFavourite = intent.getBooleanExtra(EXTRA_CHECK_FAVOURITE, false)
    }

    private fun checkFavourite() {
        if (checkFavourite) {
            val repository = StopRepository(application)
            repository.getFavourites()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        onNextData(data)
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

    private fun onNextData(data: List<Favourite>) {
        if (data.isEmpty()) {
            main_navigation.selectedItemId = R.id.navigation_stop
        } else {
            main_navigation.selectedItemId = R.id.navigation_favorite
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
