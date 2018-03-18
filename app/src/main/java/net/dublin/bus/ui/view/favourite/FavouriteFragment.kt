package net.dublin.bus.ui.view.favourite

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_favourite.*
import net.dublin.bus.R
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite
import net.dublin.bus.ui.utilities.Utility
import net.dublin.bus.ui.view.realtime.RealTimeActivity

class FavouriteFragment : Fragment(), FavouriteAdapter.ItemClickListener {
    private lateinit var model: FavouriteViewModel

    private lateinit var mAdapter: FavouriteAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favourite, container, false)
        setupRecyclerView(view)
        initialize()

        return view
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = FavouriteAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.favourite_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
    }

    private fun initialize() {
        val factory = FavouriteViewModelFactory(StopRepository(activity.application))
        model = ViewModelProviders.of(activity, factory).get(FavouriteViewModel::class.java)
        model.getStops().observe(activity, Observer<List<Favourite>> {
            it?.let {
                onData(it)
            }
        })
    }

    private fun onData(data: List<Favourite>) {
        hideNoData()
        showData(data)
        if (data.isEmpty()) {
            showNoData()
        }
    }

    override fun onResume() {
        super.onResume()
        mAdapter.updateRealData()
    }

    fun isNetworkAvailable(): Boolean {
        return Utility.isNetworkAvailable(activity)
    }

    fun showData(data: List<Favourite>) {
        mAdapter.replaceData(data)
    }

    private fun showNoData() {
        favorite_message_empty?.visibility = View.VISIBLE
    }

    private fun hideNoData() {
        favorite_message_empty?.visibility = View.GONE
    }

    override fun onItemClick(item: Favourite) {
        RealTimeActivity.navigate(activity, item)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = FavouriteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}