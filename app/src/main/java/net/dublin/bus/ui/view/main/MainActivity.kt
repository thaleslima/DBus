package net.dublin.bus.ui.view.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import net.dublin.bus.*
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite
import net.dublin.bus.ui.utilities.BottomNavigationViewHelper
import net.dublin.bus.ui.view.favourite.FavouriteFragment
import net.dublin.bus.ui.view.near.NearActivity
import net.dublin.bus.ui.view.route.RouteFragment
import net.dublin.bus.ui.view.search.SearchActivity
import net.dublin.bus.ui.view.stop.StopFragment

class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        BottomNavigationViewHelper.disableShiftMode(navigation)

        main_search_view.setOnClickListener {
            SearchActivity.navigate(this)
        }

        if (savedInstanceState == null) {
            checkFavourite()
        }
    }

    private fun checkFavourite() {
        val repository = StopRepository(application)
        repository.getFavourites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    onNextData(data)
                }, {
                    onError()
                })
    }

    private fun onError() {
        navigation.selectedItemId = R.id.navigation_stop
    }

    private fun onNextData(data: List<Favourite>) {
        if (data.isEmpty()) {
            navigation.selectedItemId = R.id.navigation_stop
        } else {
            navigation.selectedItemId = R.id.navigation_favorite
        }
    }

    private fun switchContent(fragment: Fragment?) {
        if (fragment != null) {
            try {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, fragment, "fragmentBase")
                fragmentTransaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                Log.e(MainActivity::class.java.name, "Error in show view")
            }
        }
    }
}
