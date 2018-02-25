package net.dublin.bus.ui.view.favorite

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.item_list_favorite.view.*
import net.dublin.bus.R
import net.dublin.bus.ui.utilities.inflate
import java.util.ArrayList

internal class FavoriteAdapter(private val mListener: ItemClickListener) : RecyclerView.Adapter<FavoriteAdapter.LocalViewHolder>() {
    private val mDataSet: MutableList<String> = ArrayList()
    private val ITEM_TOP = 1
    private val ITEM_MIDDLE = 2
    private val ITEM_BOTTOM = 3

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

    fun replaceData(dataSet: List<String>) {
        setList(dataSet)
        notifyDataSetChanged()
    }

    private fun setList(dataSet: List<String>) {
        mDataSet.clear()
        mDataSet.addAll(dataSet)
    }

    internal inner class LocalViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_list_favorite)) {
        private var mItem: String? = null

        fun bind(item: String) = with(itemView) {
            mItem = item
            favorite_description_view.text = item
            favorite_description_aux_view.text = "111"

            // itemView.setOnClickListener { mItem?.let { it1 -> mListener.onItemClick(it1, local_picture) } }
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            ITEM_TOP -> parent.inflate(R.layout.item_list_favorite_top)
            ITEM_BOTTOM -> parent.inflate(R.layout.item_list_favorite_bottom)
            else -> parent.inflate(R.layout.item_list_favorite)
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