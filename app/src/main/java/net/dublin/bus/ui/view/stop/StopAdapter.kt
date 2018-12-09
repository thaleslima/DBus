package net.dublin.bus.ui.view.stop

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list_stop.view.*
import net.dublin.bus.R
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.utilities.inflate
import java.util.*

internal class StopAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<StopAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<Stop> = ArrayList()

    companion object {
        private const val ITEM_TOP = 1
        private const val ITEM_MIDDLE = 2
        private const val ITEM_BOTTOM = 3
    }

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

    private fun setList(dataSet: List<Stop>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(inflate(parent, viewType)) {
        private var mItem: Stop? = null

        fun bind(item: Stop) = with(itemView) {
            mItem = item
            stop_description_view.text = item.description
            stop_number_view.text = item.stopNumber
            itemView.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1) } }
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_TOP -> parent.inflate(R.layout.item_list_stop_top)
            ITEM_BOTTOM -> parent.inflate(R.layout.item_list_stop_bottom)
            else -> parent.inflate(R.layout.item_list_stop)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ITEM_TOP
        }
        if (position == mDataSet.size - 1) {
            return ITEM_BOTTOM
        }
        return ITEM_MIDDLE
    }
}