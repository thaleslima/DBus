package net.dublin.bus.ui.view.favorite

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import net.dublin.bus.R

class FavoriteFragment : Fragment(), FavoriteAdapter.ItemClickListener {
    override fun onItemClick(item: String, view: ImageView) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mListener: OnFragmentInteractionListener? = null
    private var mAdapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        setupRecyclerView(view)
        return view
    }

    private fun setupRecyclerView(view: View) {
        mAdapter = FavoriteAdapter(this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
        showStops()
    }

    fun showStops() {
        val locals = ArrayList<String>()
        locals.add("adasddasdasdsad")
        locals.add("dasdasdsadasdasdsad");
        locals.add("dasdsadasdasd")
        locals.add("3213123a")
        locals.add("3213123dsfsaa")
        locals.add("adasdsadasdas")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("3213123a")
        locals.add("adasdasdasd")
        mAdapter?.replaceData(locals)
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(): Fragment {
            val fragment = FavoriteFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}