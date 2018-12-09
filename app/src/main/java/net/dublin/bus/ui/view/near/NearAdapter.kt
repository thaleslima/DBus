package net.dublin.bus.ui.view.near

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_near.view.*
import net.dublin.bus.R
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.DistanceFormatter
import net.dublin.bus.ui.utilities.inflate
import java.util.*

internal class NearAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<NearAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Stop> = ArrayList()

    internal interface ItemClickListener {
        fun onItemClick(item: Stop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        return LocalViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        holder.bind(mDataSet[position])
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun replaceData(dataSet: List<Stop>) {
        setList(dataSet)
        notifyDataSetChanged()
    }

    fun clear() {
        mDataSet.clear()
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<Stop>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_near)) {
        private var mItem: Stop? = null

        fun bind(item: Stop) = with(itemView) {
            mItem = item
            near_number_stop_view.text = item.stopNumber
            near_description_stop_view.text = item.description
            near_routes_view.text = item.routes
            near_distance_view.text = DistanceFormatter.formatDistanceKilometer(item.distance)
            itemView.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1) } }
        }
    }
}