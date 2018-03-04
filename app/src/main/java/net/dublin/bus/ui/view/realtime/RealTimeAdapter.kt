package net.dublin.bus.ui.view.realtime

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.graphics.drawable.AnimationDrawable
import kotlinx.android.synthetic.main.item_list_real_time.view.*
import net.dublin.bus.R
import net.dublin.bus.model.StopData
import net.dublin.bus.ui.utilities.inflate
import java.util.*

internal class RealTimeAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<RealTimeAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<StopData> = ArrayList()

    companion object {
        private const val ITEM_MIDDLE = 2
        private const val ITEM_BOTTOM = 3
    }

    internal interface ItemClickListener {
        fun onItemClick(item: String, view: ImageView)
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

    fun replaceData(dataSet: List<StopData>) {
        setList(dataSet)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<StopData>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(inflate(parent, viewType)) {
        private var mItem: StopData? = null

        fun bind(item: StopData) = with(itemView) {
            mItem = item
            real_route_view.text = mItem?.publishedLineName
            real_route_description_view.text = mItem?.destinationName
            real_route_time_view.text = mItem?.timeRemainingFormatted()

            // itemView.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1, local_picture) } }

            live_blip.setBackgroundResource(R.drawable.live)
            val progressAnimation = live_blip.background as AnimationDrawable?
            progressAnimation?.start()
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_BOTTOM -> parent.inflate(R.layout.item_list_real_time_bottom)
            else -> parent.inflate(R.layout.item_list_real_time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == mDataSet.size - 1) {
            return ITEM_BOTTOM
        }
        return ITEM_MIDDLE
    }
}